/**
 *  com.github.lecogiteur.csvbang.parser.CsvParser
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
 *
 *  This file is part of Csvbang.
 *  
 *  Csvbang is a comma-separated values ( CSV ) API, written in JAVA and thread-safe.
 *
 *  Csvbang is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *   
 *  Csvbang is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with Csvbang. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.lecogiteur.csvbang.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvDatagram;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Parser of CSV content. This parser is thread safe.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsvParser<T> {
	
	/**
	 * The logger
	 * @since 1.0.0
	 */
	private static final Logger LOGGER = Logger.getLogger(CsvParser.class.getName());
	
	/**
	 * The class of CSV bean
	 * @since 1.0.0
	 */
	private final Class<T> classOfCSVBean;
	
	/**
	 * CsvBang configuration of CSV bean
	 * @since 1.0.0
	 */
	private final CsvBangConfiguration conf;
	
	/**
	 * table of CSV keywords
	 * @since 1.0.0
	 */
	private final byte[][] keywordTable;

	/**
	 * table of action. Each index matching to the index of table of CSV keywords. So one CSV keyword matches to one action. 
	 * @since 1.0.0
	 */
	private final CsvGrammarActionType actions[];
	
	/**
	 * Unterminated stack of actions by offset. Stack are ordered by offset in file
	 * @since 1.0.0
	 */
	private final ConcurrentSkipListMap<ActionKey, Deque<CsvGrammarAction<?>>> stackByOffset;
	
	/**
	 * Constructor
	 * @param classOfCSVBean the class of CSV bean to parse
	 * @param conf the configuration of CSV bean
	 * @throws CsvBangException if a problem has occurred when we generate the CSV grammar
	 * @since 1.0.0
	 */
	public CsvParser(Class<T> classOfCSVBean, CsvBangConfiguration conf) throws CsvBangException {
		super();
		this.classOfCSVBean = classOfCSVBean;
		this.conf = conf;
		this.stackByOffset = new ConcurrentSkipListMap<ActionKey, Deque<CsvGrammarAction<?>>>();
		final List<CsvGrammarActionType> actionList = new ArrayList<CsvGrammarActionType>();
		
		//generate CSV grammar
		this.keywordTable = initGrammar(conf, actionList);
		this.actions = new CsvGrammarActionType[actionList.size() + 1];
		for (int i=0; i<actionList.size(); i++){
			this.actions[i] = actionList.get(i);
		}
	}
	
	
	/**
	 * Parse CSV content to CSV bean.
	 * @param csvDatagram CSV datagram from a CSV file
	 * @return list of CSV bean
	 * @throws CsvBangException if a problem has occurred when CsvBang parsed the CSV file
	 * @since 1.0.0
	 */
	public Collection<T> parse(final CsvDatagram csvDatagram) throws CsvBangException{
		return internalParse(csvDatagram.getContent(), new ArrayDeque<CsvGrammarAction<?>>(10), 
				csvDatagram.getOffset(), csvDatagram.getFileHashCode(), csvDatagram.isLastDatagram());
	}

	/**
	 * Algorithm of CSV parsing. We search some CSV keyword and generate a corresponding action. When a new action is creating, 
	 * if the previous action is not terminated we stock it in a stack.
	 * @param csvContent content from CSV file to parse
	 * @param stack stack of CSV action to execute. The head of stack is the last action to execute. The tail of stack is the first action to execute.
	 * @param startOffset start of file offset in CSV file of CSV datagram 
	 * @param fileID file ID
	 * @param isLastDatagram True, if it's the last CSV datagram from file.
	 * @return the list of CSV bean from content
	 * @throws CsvBangException if a problem has occurred when CsvBang parsed the CSV file
	 * @see {@link com.github.lecogiteur.csvbang.parser.CsvGrammarAction}
	 * @since 1.0.0
	 */
	public Collection<T> internalParse(final byte[] csvContent, final Deque<CsvGrammarAction<?>> stack, final long startOffset, 
			final int fileID, final boolean isLastDatagram) throws CsvBangException{
		if (stack.isEmpty() && (csvContent == null || csvContent.length == 0)){
			//no data to analyze
			return null;
		}
		
		if (LOGGER.isLoggable(Level.FINEST)){
			LOGGER.finest(String.format("Parse offset [%s]", startOffset));
		}
		
		
		//init
		byte[] content = csvContent;
		long fileOffset = startOffset;
		long backupFileOffset = -1;
		boolean mustContinue = false;
		boolean isLast = isLastDatagram;
		
		//list of CSV beans which are completed.
		final Collection<T> listOfBeans = new ArrayList<T>(100);
		try{
			do{
				//init
				boolean isContentNull = content == null || content.length == 0;

				//load stack
				initStack(stack, fileID, fileOffset);

				//TODO à supprimer
				for (CsvGrammarAction<?> aze:stack){
					if (3353 == aze.getStartOffset() && 3355 == aze.getEndOffset()){
						System.out.println("INIT Stack : start = " + stack.getFirst().getStartOffset() + " - end = " + stack.getLast().getEndOffset() );
					}
				}
				

				//has inital stack?
				final boolean hasInitialStack = !stack.isEmpty();

				//init the current action
				CsvGrammarAction<?> action = initAction(stack, fileOffset, isContentNull?0:content.length);

				//init content to analyze
				while (hasInitialStack && action != null && CsvGrammarActionType.UNDEFINED.equals(action.getType())){
					//retrieve last undefined action
					final UndefinedGrammarAction a = (UndefinedGrammarAction) action;
					//get the byte content
					final byte[] result = a.execute();
					if (result == null || result.length == 0){
						//if no content in the undefined action we search the next action
						action =  stack.pollLast();
						continue;
					}
					if (isContentNull){
						//no initial content, so we take the content of the undefined action
						content = result;
					}else{
						final byte[] tmp =  new byte[result.length +  content.length];
						System.arraycopy(result, 0, tmp, 0, result.length);
						System.arraycopy(content, 0, tmp, result.length, content.length);
						content = tmp;
					}
					isContentNull = content == null || content.length == 0;
					fileOffset = a.getStartOffset();
					action =  stack.pollLast();
				}
				if (action == null){
					action =  initAction(stack, fileOffset, content==null?0:content.length);
				}

				//offset of start action which can be generated by content
				long startActionOffset = fileOffset;

				if (!isContentNull){
					for (int contentOffset=0; contentOffset < content.length;){

						//we verify if it is a keyword of CSV grammar
						final int indexTableGrammar = isKeyword(contentOffset, content, isLastDatagram);

						if (indexTableGrammar < 0){
							//not a character of CSV keyword
							action.add(content[contentOffset++]);
							++fileOffset;
							continue;
						}

						//retrieve the action we must to execute for the new keyword
						final CsvGrammarActionType newAction = actions[indexTableGrammar];
						final boolean isUndefinedAction = CsvGrammarActionType.UNDEFINED.equals(newAction);
						final int keywordLength = isUndefinedAction?0:keywordTable[indexTableGrammar].length;

						//we defined that index "contentOffset" a keyword start, so we move the index after the keyword 
						startActionOffset = fileOffset;
						contentOffset += keywordLength;
						fileOffset+=keywordLength;

						//generate the new action
						final CsvGrammarAction<?> a = generateAction(newAction, content.length > contentOffset?content.length-contentOffset:0);
						a.setStartOffset(startActionOffset);
						action.setEndOffset(startActionOffset);
						if (isUndefinedAction){
							a.add(content[contentOffset++]);
							++fileOffset;
						}

						//create stack of actions
						if (action != null && !action.isActionCompleted(newAction)){
							// the previous action is not terminated, so we stock it in the stack
							stack.add(action);
						}else if (!stack.isEmpty()){
							//the previous action is terminated and the stack is not empty.
							//We try to clear the stack
							tryClearStack(listOfBeans, stack, action, newAction);
						}else{
							//the action is terminated and there is not action in stack. 
							//We execute the action
							executeAction(listOfBeans, stack, action);
						}

						//we set the new current action
						action = a;
					}

					//the action is not terminated, we put it in stack of action not terminated
					action.setEndOffset(fileOffset);
				}else if (action.getEndOffset() > 0){
					fileOffset = action.getEndOffset();
				}

				//the action is not terminated, we put it in stack of action not terminated
				stack.add(action);

				if (isLast && !CsvGrammarActionType.END.equals(action.getType())){
					//it's the last datagramm for this file
					if (!stack.isEmpty()){
						tryClearStack(listOfBeans, stack, stack.pollLast(), CsvGrammarActionType.END);
					}
					action = generateAction(CsvGrammarActionType.END, 0);
					action.setStartOffset(fileOffset);
					action.setEndOffset(fileOffset);
					stack.add(action);
				}

				//TODO à supprimer
				for (CsvGrammarAction<?> aze:stack){
					if (3353 == aze.getStartOffset() && 3355 == aze.getEndOffset()){
						System.out.println("Stack : start = " + stack.getFirst().getStartOffset() + " - end = " + stack.getLast().getEndOffset() );
					}
				}
				
			/*	tryToGroupAndClearStack(listOfBeans, stack, fileID, fileOffset);

				if (!stack.isEmpty()){
					if (backupFileOffset != fileOffset && CsvGrammarActionType.UNDEFINED.equals(stack.getLast().getType())){							//perhaps we can analyze another part of the stack, we try
							final UndefinedGrammarAction a = (UndefinedGrammarAction)action;
							content = a.execute();
							fileOffset = a.getStartOffset();
							backupFileOffset = content != null?fileOffset + content.length:fileOffset; //in order to not loop
							mustContinue = true;
					}else{
						//stack is not empty, save it
						stackByOffset.put(generateID(fileID, stack.getFirst().getStartOffset(), stack.getLast().getEndOffset()), stack);
					}
				}*/
				
				mustContinue = false;
				if (!stack.isEmpty()){
					
					Deque<CsvGrammarAction<?>> stackToSave = new ArrayDeque<CsvGrammarAction<?>>(stack.size());
					if (!CsvGrammarActionType.UNDEFINED.equals(stack.getFirst().getType())){
						action = stack.removeFirst();
						long start = action.getStartOffset();
						while (!stack.isEmpty()){
							if (action.isLastAction()){
								tryClearStack(listOfBeans, stackToSave, action, CsvGrammarActionType.END);
								action = null;
								break;
							}else if (action.isActionCompleted(stack.getFirst().getType())){
								tryClearStack(listOfBeans, stackToSave, action, stack.getFirst().getType());
								action = stack.removeFirst();
							}else{
								stackToSave.addLast(action);
								action = stack.removeFirst();
							}
						}
						if (action != null){
							if (action.isLastAction()){
								tryClearStack(listOfBeans, stackToSave, action, CsvGrammarActionType.END);
							}else{
								stackToSave.addLast(action);
							}
						}else if (stackToSave.isEmpty() || (stackToSave.size() == 1 && CsvGrammarActionType.END.equals(stackToSave.getFirst().getType()))){
							action = generateAction(CsvGrammarActionType.END, 0);
							action.setStartOffset(start);
							action.setEndOffset(start);
							stackToSave.clear();
							stackToSave.add(action);
							initStack(stackToSave, fileID, start);
						}
					
						stack.clear();
						stack.addAll(stackToSave);
					}
					
					
					
					stackToSave = new ArrayDeque<CsvGrammarAction<?>>(stack.size());
					action = stack.removeLast();
					while (!stack.isEmpty() && !CsvGrammarActionType.UNDEFINED.equals(action.getType())){
						stackToSave.addFirst(action);
						action = stack.removeLast();
					}
					
				/*	if (!stackToSave.isEmpty()){
						Iterator<CsvGrammarAction<?>> iterator = stackToSave.iterator();
						final Deque<CsvGrammarAction<?>> stackToSave2 = new ArrayDeque<CsvGrammarAction<?>>(stackToSave.size());
						stackToSave2.add(iterator.next());
						CsvGrammarAction<?> a1 = iterator.hasNext()?iterator.next():null;
						if (a1 != null && a1.isLastAction()){
							tryClearStack(listOfBeans, stackToSave2, a1, CsvGrammarActionType.END);
						}
						while (iterator.hasNext()){
							CsvGrammarAction<?> aa = iterator.next();
							if (a1.isActionCompleted(aa.getType())){
								tryClearStack(listOfBeans, stackToSave2, a1, aa.getType());
								a1 = aa;
							}else if (a1.isLastAction()){
								tryClearStack(listOfBeans, stackToSave2, a1, CsvGrammarActionType.END);
								a1 = aa;
							}else{
								stackToSave2.addLast(a1);
								a1 = aa;
							}
						}
						if (a1 != null){
							stackToSave2.addLast(a1);
						}
						stackToSave = stackToSave2;
					}*/
					
					if (fileOffset != action.getEndOffset() && !stack.isEmpty()){ //do not loop
						final UndefinedGrammarAction a = (UndefinedGrammarAction)action;
						content = a.execute();
						fileOffset = a.getStartOffset();
						mustContinue = true;
						isLast = a.isLastAction();
						//stack is not empty, save it
						if (!stackToSave.isEmpty()){
							stackByOffset.put(generateID(fileID, stackToSave.getFirst().getStartOffset(), stackToSave.getLast().getEndOffset()), stackToSave);
						}
					}else if (fileOffset != action.getEndOffset() && stack.isEmpty()){
						stackToSave.addFirst(action);
						stackByOffset.put(generateID(fileID, stackToSave.getFirst().getStartOffset(), stackToSave.getLast().getEndOffset()), stackToSave);
					}else if (stackToSave.isEmpty()){
						//stack is not empty, save it
						stack.addLast(action);
						stackByOffset.put(generateID(fileID, stack.getFirst().getStartOffset(), stack.getLast().getEndOffset()), stack);
					}else{
						//stack is not empty, save it
						stackByOffset.put(generateID(fileID, stackToSave.getFirst().getStartOffset(), stackToSave.getLast().getEndOffset()), stackToSave);
					}
				}
			}while (mustContinue);
		}catch(Exception e){
			throw new CsvBangException(String.format("A problem has occurred when we parse CSV file [%s] at offset [%s].", 
					fileID, startOffset), e);
		}
		return listOfBeans;
	}
	
	/**
	 * Flush content. Becareful, this method is blocking until all content is parsed.
	 * @return the list of CSV bean
	 * @throws CsvBangException if a problem has occurred when CsvBang parsed the CSV file
	 * @since 1.0.0
	 */
	public Collection<T> flush() throws CsvBangException{
		
		//list of CSV beans which are completed.
		final Collection<T> listOfBeans = new ArrayList<T>(100);
		
		//TODO à supprimer
		for (final Entry<ActionKey, Deque<CsvGrammarAction<?>>> entry:stackByOffset.entrySet()){
			for (CsvGrammarAction<?> action:entry.getValue()){
				if (3353 == action.getStartOffset() && 3355 == action.getEndOffset()){
					System.out.println("Stack : start = " + entry.getKey().startOffset + " - end = " + entry.getKey().endOffset);
				}
			}
		}
		
		//for each stack which are not terminated
		while (!stackByOffset.isEmpty()){
			//init
			byte[] content = null;
			
			//retrieve the last stack. (stack has been ordered by offset in files
			final Entry<ActionKey, Deque<CsvGrammarAction<?>>> stack = stackByOffset.pollLastEntry();
			
			if (CsvbangUti.isCollectionEmpty(stack.getValue())){
				//no stack we continue
				continue;
			}
			
			//true, if the last action of stack is the last action to execute in file
			boolean isLast = stack.getValue().getLast().isLastAction();
			if (CsvGrammarActionType.END.equals(stack.getValue().getLast().getType())){
				//it's the last action
				final CsvGrammarAction<?> end = stack.getValue().removeLast();
				if (CsvbangUti.isCollectionEmpty(stack.getValue())){
					if (stackByOffset.isEmpty()){
						//end
						break;
					}
					continue;
				}else{
					stack.getValue().getLast().add(end);
				}
			}
			
			//start offset
			long startOffset = stack.getKey().startOffset;
		
			if (CsvGrammarActionType.UNDEFINED.equals(stack.getValue().getLast().getType())){
				//the action is undefined, we retrieve the last content
				final UndefinedGrammarAction action = (UndefinedGrammarAction) stack.getValue().removeLast();
				content = action.execute();
				startOffset = action.getStartOffset();
			}
			
			//parse content
			final Collection<T> list = internalParse(content, stack.getValue(), startOffset, 
					stack.getKey().fileId, true);
			
			//add result
			if (CsvbangUti.isCollectionNotEmpty(list)){
				listOfBeans.addAll(list);
			}
		}
		return listOfBeans;
	}

	/**
	 * We try to execute and clear all actions in stack
	 * @param csvBeans list of CSV bean which can be created
	 * @param stack stack of action
	 * @param fileID file ID 
	 * @param fileOffset offset in file
	 * @throws CsvBangException if a problem has occurred when we try to execute action 
	 * @since 1.0.0
	 */
	private void tryToGroupAndClearStack2(final Collection<T> csvBeans, final Deque<CsvGrammarAction<?>> stack, 
			final int fileID, final long fileOffset) throws CsvBangException{
		if (CsvbangUti.isCollectionEmpty(stack)){
			return;
		}
		
		if (stack.getLast().isLastAction()){
			//the last action is terminated
			tryClearStack(csvBeans, stack, stack.pollLast(), CsvGrammarActionType.END);
		}
		
		//try to clear the initial stack
		final Deque<CsvGrammarAction<?>> currentStack = new ArrayDeque<CsvGrammarAction<?>>(stack);
		stack.clear();
		CsvGrammarAction<?> lastAction = null;
		while(!currentStack.isEmpty()){
			final CsvGrammarAction<?> a = currentStack.removeFirst();
			if (lastAction != null && lastAction.isActionCompleted(a.getType())){
				tryClearStack(csvBeans, stack, stack.pollLast(), a.getType());
			}else if(a.isLastAction()){
				tryClearStack(csvBeans, stack, a, CsvGrammarActionType.END);
				lastAction = a;	
				continue;
			}
			stack.addLast(a);
			lastAction = a;				
		}
		
		//init - we search stack after the specified stack
		ActionKey key = generateID(fileID, fileOffset, -1);
		Deque<CsvGrammarAction<?>> stackToAdd = stackByOffset.remove(key);
		
		while (CsvbangUti.isCollectionNotEmpty(stackToAdd)){
			//we add all elements of the stack after the specified stack
			final Iterator<CsvGrammarAction<?>> iterator = stackToAdd.iterator();
			while(iterator.hasNext()){
				final CsvGrammarAction<?> action = iterator.next();
				if (stack.getLast().isActionCompleted(action.getType())){
					tryClearStack(csvBeans, stack, stack.pollLast(), action.getType());
					stack.addLast(action);
				}else if (action.isLastAction()){
					tryClearStack(csvBeans, stack, action, CsvGrammarActionType.END);
				}else{
					stack.addLast(action);
				}
			}
			//search next stack
			key = generateID(fileID, stack.getLast().getEndOffset(), -1);
			stackToAdd = stackByOffset.remove(key);
		}
	}
	
	/**
	 * We try to execute and clear all actions in stack
	 * @param csvBeans list of CSV bean which can be created
	 * @param stack stack of action
	 * @param fileID file ID 
	 * @param fileOffset offset in file
	 * @throws CsvBangException if a problem has occurred when we try to execute action 
	 * @since 1.0.0
	 */
	private void tryToGroupAndClearStack(final Collection<T> csvBeans, final Deque<CsvGrammarAction<?>> stack, 
			final int fileID, final long fileOffset) throws CsvBangException{
		if (CsvbangUti.isCollectionEmpty(stack)){
			return;
		}
		
		if (stack.getLast().isLastAction()){
			//the last action is terminated
			tryClearStack(csvBeans, stack, stack.pollLast(), CsvGrammarActionType.END);
		}
		
		//init - we search stack after the specified stack
		Deque<CsvGrammarAction<?>> stackToAdd = new ArrayDeque<CsvGrammarAction<?>>(stack);
		stack.clear();
		
		while (CsvbangUti.isCollectionNotEmpty(stackToAdd)){
			//we add all elements of the stack after the specified stack
			while(!stackToAdd.isEmpty()){
				final CsvGrammarAction<?> action = stackToAdd.removeFirst();
				if (stack.isEmpty()){
					stack.add(action);
				}else if (CsvGrammarActionType.UNDEFINED.equals(action.getType())){
					stack.addLast(action);
					if (!stackToAdd.isEmpty()){
						stackByOffset.put(generateID(fileID, stackToAdd.getFirst().getStartOffset(), 
								stackToAdd.getLast().getEndOffset()), stackToAdd);
					}
					return;
				}else if (stack.getLast().isActionCompleted(action.getType())){
					tryClearStack(csvBeans, stack, stack.pollLast(), action.getType());
					stack.addLast(action);
				}else if (action.isLastAction()){
					tryClearStack(csvBeans, stack, action, CsvGrammarActionType.END);
				}else{
					stack.addLast(action);
				}
			}
			//search next stack
			final ActionKey key = generateID(fileID, stack.getLast().getEndOffset(), -1);
			stackToAdd = stackByOffset.remove(key);
		}
	}
	
	/**
	 * Initialize the first action before to analyze CSV content 
	 * @param stack the stack of actions which are not terminated 
	 * @param fileOffset the offset in file
	 * @param contentLength length of content
	 * @return the initial action
	 * @since 1.0.0
	 */
	private CsvGrammarAction<?> initAction(final Deque<CsvGrammarAction<?>> stack, final long fileOffset, final int contentLength){
		if (!stack.isEmpty()){
			//stack is not empty, so we take the last action
			return stack.pollLast();
		}
		
		//generate a new action.
		final CsvGrammarAction<?> action = generateAction(fileOffset == 0?CsvGrammarActionType.START:CsvGrammarActionType.UNDEFINED, contentLength);
		action.setStartOffset(fileOffset);
		return action;
	}
	
	/**
	 * Initialize the stack of actions. We search if stack already exists before analyze CSV content.
	 * @param stack the current stack
	 * @param fileID file ID
	 * @param fileOffset offset of CSV content in file
	 * @since 1.0.0
	 */
	private void initStack(final Deque<CsvGrammarAction<?>> stack, final int fileID, final long fileOffset){
		//search a stack before the file offset
		Deque<CsvGrammarAction<?>> stackToAdd = stackByOffset.remove(generateID(fileID, 
				-1, stack.isEmpty()?fileOffset:stack.getFirst().getStartOffset()));
		
		while (stackToAdd != null){
			//we add all actions of the stack in the current stack
			final Iterator<CsvGrammarAction<?>> iterator = stackToAdd.descendingIterator();
			while (iterator.hasNext()){
				stack.addFirst(iterator.next());
			}
			stackToAdd = stackByOffset.remove(generateID(fileID, -1, stack.getFirst().getStartOffset()));
		}
	}
	
	/**
	 * Create the table of keywords and table of action
	 * @param conf the Csvbang configuration of CSV bean
	 * @param actionList the list of action. The index matches with the index of table keywords
	 * @return the table of keywords
	 * @throws CsvBangException if the index of action list is different of list of keyword.
	 * @since 1.0.0
	 */
	private byte[][] initGrammar(final CsvBangConfiguration conf, final List<CsvGrammarActionType> actionList) 
			throws CsvBangException{
		final byte[][] table = new byte[10][];
		final List<String> keywordSortedByLength = new ArrayList<String>(); 
		boolean hasEndRecord = CsvbangUti.isStringNotBlank(conf.endRecord);
		final boolean hasCommentChar = CsvbangUti.isStringNotBlank(conf.commentCharacter + "");
		int index = 0;
		
		//init table
		for (int i=0; i<table.length; i++){
			actionList.add(CsvGrammarActionType.NOTHING_TO_DO);
		}
		
		//sort keyword by length
		//delimiter
		keywordSortedByLength.add(conf.delimiter);
		
		//end record
		if (hasEndRecord && !keywordSortedByLength.contains(conf.endRecord)){
			keywordSortedByLength.add(conf.endRecord);
		}
		
		//start record
		if (CsvbangUti.isStringNotBlank(conf.startRecord) && !keywordSortedByLength.contains(conf.startRecord)){
			keywordSortedByLength.add(conf.startRecord);
		}
		
		//the footer
		if (CsvbangUti.isStringNotBlank(conf.footer) && !keywordSortedByLength.contains(conf.footer)){
			keywordSortedByLength.add(conf.footer);
		}
		
		//the header
		if (CsvbangUti.isStringNotBlank(conf.header) && !keywordSortedByLength.contains(conf.header)){
			keywordSortedByLength.add(conf.header);
		}
		
		//a comment
		final StringBuilder comment = new StringBuilder(5);
		comment.append(conf.defaultEndLineCharacter.toString()).append(conf.commentCharacter);
		if (hasCommentChar && !keywordSortedByLength.contains(comment.toString())){
			keywordSortedByLength.add(comment.toString());
		}
		
		//sort by length. Put in first the longest string 
		Collections.sort(keywordSortedByLength);
		Collections.reverse(keywordSortedByLength);
		
		//#########################################################
		//create tables
		//delimiter
		index = keywordSortedByLength.indexOf(conf.delimiter);
		table[index] = conf.delimiter.getBytes(conf.charset);
		actionList.set(index, CsvGrammarActionType.FIELD);
		
		//end record
		addKeyword(keywordSortedByLength, table, actionList, conf.endRecord, CsvGrammarActionType.RECORD);
		
		//start record
		addKeyword(keywordSortedByLength, table, actionList, 
				conf.startRecord, hasEndRecord?CsvGrammarActionType.NOTHING_TO_DO:CsvGrammarActionType.RECORD);
		
		//the footer
		addKeyword(keywordSortedByLength, table, actionList, conf.footer, CsvGrammarActionType.NOTHING_TO_DO);
		
		//the header
		addKeyword(keywordSortedByLength, table, actionList, conf.header, CsvGrammarActionType.NOTHING_TO_DO);
		
		//a start comment
		if (hasCommentChar){
			addKeyword(keywordSortedByLength, table, actionList, comment.toString(), CsvGrammarActionType.COMMENT);
		}

		index = keywordSortedByLength.size();
		/*table[index] = conf.defaultEndLineCharacter.toBytes(conf.charset);
		actionList.get(index++).add(CsvGrammarActionType.NOTHING_TO_DO);*/
		
		//TODO à revoir la partie des quotes
		final List<Character> characters = new ArrayList<Character>();
		table[index] = new byte[]{(byte)conf.escapeQuoteCharacter};
		//TODO en fonction de l'activation des quotes mettre la bonne action
		actionList.set(index++, conf.quote != null?CsvGrammarActionType.NOTHING_TO_DO:CsvGrammarActionType.NOTHING_TO_DO);
		characters.add(conf.escapeQuoteCharacter);
		
		if (conf.quote != null){ 
			int in = characters.indexOf(conf.quote);
			if (in < 0){
				table[index] = new byte[]{(byte)conf.quote.charValue()};
				in = index++;
			}
			actionList.set(in, CsvGrammarActionType.NOTHING_TO_DO);
		}
		
		//genrate the table of keyword
		final byte[][] dest = new byte[index][];
		System.arraycopy(table, 0, dest, 0, index);
		
		//genrate the table action. One keyword one action
		for (int i=actionList.size()-1; i>=index; i--){
			actionList.remove(i);
		}
		if (actionList.size() != dest.length){
			throw new CsvBangException(String.format("A problem has occurred when we generate the CSV grammar of CSBV bean [%s]. One action for one keyword. Number of action: %s - Number of keyword: %s", classOfCSVBean, actionList.size(), dest.length));
		}
		
		//Add the undefined action
		actionList.add(CsvGrammarActionType.UNDEFINED);
		
		return dest;
	}
	
	/**
	 * Add a keyword to the keyword table and action list
	 * @param keywordSortedByLength the list of keyword sorted by length
	 * @param tableKeyword the table of keyword
	 * @param actionList the list of action for each action
	 * @param keyword the keyword to add
	 * @param action the action corresponding to the keyword
	 * @throws CsvBangException if another action is already defined for this keyword
	 * @since 1.0.0
	 */
	private void addKeyword(final List<String> keywordSortedByLength, final byte[][] tableKeyword, 
			final List<CsvGrammarActionType> actionList, final String keyword, final CsvGrammarActionType action) 
					throws CsvBangException{
		int index = keywordSortedByLength.indexOf(keyword);
		if (index >= 0){
			tableKeyword[index] = keyword.getBytes(conf.charset);
			if (!actionList.get(index).equals(CsvGrammarActionType.NOTHING_TO_DO)){
				throw new CsvBangException(String.format("The CSv grammar contains already an action [%s] with the same keyword [%s]. We can't add action [%s]. We can't read this CSV file", 
						actionList.get(index), keyword, action));
			}
			actionList.set(index, action);
		}
	}
	
	/**
	 * Generate an ID for a CSV action 
	 * @param fileID file ID
	 * @param startOffset offset of content action start
	 * @param endOffset offset of content action end
	 * @return the ID of action
	 * @since 1.0.0
	 */
	private ActionKey generateID(final int fileID, final long startOffset, final long endOffset){
		ActionKey key = new ActionKey();
		key.startOffset = startOffset;
		key.endOffset = endOffset;
		key.fileId = fileID;
		return key;
	}
	
	/**
	 * Verify if bytes which start to current index is a CSV keyword
	 * @param index the current index
	 * @param content the content
	 * @param isEnd True if it's the last CSV content from file
	 * @return the index of action in the action table or -1 if it's no a keyword
	 * @since 1.0.0
	 * @see #words
	 * @see #actions
	 */
	private int isKeyword(final int index, final byte[] content, boolean isEnd){
		for (int i=0; i<keywordTable.length; i++){
			if (keywordTable[i][0] == content[index]){
				boolean isKeyword = !(isEnd && keywordTable[i].length > 1);
				if (content.length > index +1){
					isKeyword = true;
					for (int j=1,k=index + 1; j<keywordTable[i].length; j++){
						if (keywordTable[i][j] != content[k]){
							isKeyword = false;
							break;
						}
					}
				}else if (!isEnd && keywordTable[i].length > 1){
					//TODO faire une constante pour l'action undefined
					return keywordTable.length;
				}
				if (isKeyword){
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Generate a CSV action in function the selected type
	 * @param action the type of action to create. 
	 * @param contentLength remaining content length
	 * @return the new CSV action
	 * @since 1.0.0
	 */
	private CsvGrammarAction<?> generateAction(final CsvGrammarActionType action, final int contentLength){
		switch (action) {
		case FIELD:
			return new FieldGrammarAction(contentLength);
		case RECORD:
			return new RecordGrammarAction<T>(classOfCSVBean, conf);
		case END:
			return new EndGrammarAction();
		case START:
			return new StartGrammarAction(classOfCSVBean, conf);
		default:
			//undefined action
			return new UndefinedGrammarAction(contentLength);
		}
	}
	
	/**
	 * Try to clean the stack of actions which are not terminated
	 * @param listOfBeans list of beans which are completed
	 * @param stack stack of action which are not terminated
	 * @param action current action which is terminated
	 * @param actionType current new action type
	 * @throws CsvBangException if a problem has occurred.
	 * @since 1.0.0
	 */
	private void tryClearStack2(final Collection<T> listOfBeans, final Deque<CsvGrammarAction<?>> stack, 
			final CsvGrammarAction<?> action, final CsvGrammarActionType actionType) throws CsvBangException{
		CsvGrammarAction<?> a = action;
		boolean isEmptyStack = stack.isEmpty();
		CsvGrammarActionType currentActionType = actionType;
		//we take the last action in stack
		CsvGrammarAction<?> lastAction = stack.pollLast();
		while (!isEmptyStack && lastAction != null){
			//we add the current action which is terminated to the previous action
			if (!lastAction.add(a)){
				stack.addLast(lastAction);
				if (executeAction(listOfBeans, stack, a)){
					currentActionType = a.getType();
					a = stack.pollLast();
					lastAction = stack.pollLast();
					isEmptyStack = lastAction == null;
					continue;
				}else{
					break;
				}
			}else if (lastAction.isActionCompleted(currentActionType)){
				//the previous action is now terminated. So we add it to its previous action
				a = lastAction;
				lastAction = stack.pollLast();
			}else{
				//the previous action is not terminated so we put it again in stack.
				//not necessary to continue to empty the stack. 
				//We must to wait that new sub action is completed in order to try to terminate this action
				stack.add(lastAction);
				lastAction = null;
			}
			isEmptyStack = stack.isEmpty();
		}

		//the action is terminated
		if (isEmptyStack){
			executeAction(listOfBeans, stack, a);
		}
	}
	
	/**
	 * Try to clean the stack of actions which are not terminated
	 * @param listOfBeans list of beans which are completed
	 * @param stack stack of action which are not terminated
	 * @param action current action which is terminated
	 * @param actionType current new action type
	 * @throws CsvBangException if a problem has occurred.
	 * @since 1.0.0
	 */
	private void tryClearStack(final Collection<T> listOfBeans, final Deque<CsvGrammarAction<?>> stack, 
			final CsvGrammarAction<?> action, final CsvGrammarActionType actionType) throws CsvBangException{
		boolean isEmptyStack = stack.isEmpty();
		if (isEmptyStack){
			//case where is no stack we verify if this action is terminated
			executeAction(listOfBeans, stack, action);
			return;
		}
		
		CsvGrammarAction<?> current = action;
		CsvGrammarActionType currentActionType = actionType;
		//we take the last action in stack
		CsvGrammarAction<?> lastAction = stack.peekLast();
		while (!isEmptyStack && lastAction != null){
			//we add the current action which is terminated to the previous action
			if (!lastAction.add(current)){
				if (executeAction(listOfBeans, stack, current)){
					currentActionType = current.getType();
					current = stack.pollLast();
					lastAction = stack.peekLast();
					isEmptyStack = lastAction == null;
					continue;
				}else{
					isEmptyStack = false;
					break;
				}
			}else if (lastAction.isActionCompleted(currentActionType)){
				//the previous action is now terminated. So we add it to its previous action
				current = stack.pollLast();
				lastAction = stack.peekLast();
			}else{
				//the previous action is not terminated so we put it again in stack.
				//not necessary to continue to empty the stack. 
				//We must to wait that new sub action is completed in order to try to terminate this action
				lastAction = null;
			}
			isEmptyStack = stack.isEmpty();
		}

		//the action is terminated
		if (isEmptyStack){
			executeAction(listOfBeans, stack, current);
		}
	}
	
	
	/**
	 * Execute and process the result in function of type of action. If the action can't be executed we add it to the stack.
	 * @param listOfBeans the list of CSV beans which are completed 
	 * @param stack stack of action
	 * @param action the action which is terminated
	 * @return True, if the action has been executed, false if the action is not terminated and stock in stack.
	 * @throws CsvBangException if a problem has occurred when we execute the action.
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	private boolean executeAction(final Collection<T> listOfBeans, final Deque<CsvGrammarAction<?>> stack, 
			final CsvGrammarAction<?> action) throws CsvBangException{
		switch (action.getType()) {
		case RECORD:
			final T o = ((RecordGrammarAction<T>)action).execute();
			if (o != null){
				listOfBeans.add(o);
			}
			if (action.isLastAction()){
				//add end action if the record is the last of file
				final CsvGrammarAction<?> end = generateAction(CsvGrammarActionType.END, 0);
				end.setEndOffset(action.getStartOffset());
				end.setStartOffset(action.getStartOffset());
				stack.add(end);
			}
			return true;
		case START:
			return executeAction(listOfBeans, stack, ((StartGrammarAction)action).execute());
		default:
			stack.add(action);
			return false;
		}
		
	}
	
	
	/**
	 * Key in order to identify an action or a stack of action
	 * @author Tony EMMA
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ActionKey implements Comparable<ActionKey>{
		/**
		 * Start offset of action (include)
		 * @since 1.0.0
		 */
		public long startOffset = -1;
		
		/**
		 * file ID. Action belongs to a specific file
		 * @since 1.0.0
		 */
		public int fileId = -1;
		
		/**
		 * End offset of action (exclude)
		 * @since 1.0.0
		 */
		public long endOffset = -1;

		/**
		 * {@inheritDoc}
		 * @see java.lang.Object#equals(java.lang.Object)
		 * @since 1.0.0
		 */
		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			ActionKey other = (ActionKey) obj;
			return (startOffset < 0 || startOffset == other.startOffset || other.startOffset < 0)
					&& (endOffset < 0 || endOffset == other.endOffset || other.endOffset < 0)
					&& (fileId == other.fileId);
		}

		/**
		 * {@inheritDoc}
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 * @since 1.0.0
		 */
		@Override
		public int compareTo(ActionKey o) {
			if (equals(o)){
				return 0;
			}
			if (fileId < o.fileId){
				return -1;
			}
			if (fileId > o.fileId){
				return 1;
			}
			if (startOffset < 0 || o.startOffset < 0){
				return endOffset<o.endOffset?-1:1;
			}
			if (endOffset < 0 || o.endOffset < 0){
				return startOffset<o.startOffset?-1:1;
			}
			if (o.endOffset <= startOffset){
				return 1;
			}
			if (endOffset <= o.startOffset){
				return -1;
			}
			return startOffset<o.startOffset?-1:1;
		}

	
		
	}
}
