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
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvDatagram;
import com.github.lecogiteur.csvbang.util.ByteStreamBuffer;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.ReflectionUti;

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
	 * Total number of field which can be deleted if there are null
	 * @since 1.0.0
	 */
	private int totalNbFieldWhichCanBeDeleted = 0;
	
	/**
	 * True if CSV bean has field of Collection (or array) type
	 * @since 1.0.0
	 */
	private boolean hasFieldCollection = false;
	
	/**
	 * Number of request to flush parser
	 * @since 1.0.0
	 */
	private AtomicInteger requestOfFlush = new AtomicInteger(0);
	
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
	public CsvParsingResult<T> parse(final CsvDatagram csvDatagram) throws CsvBangException{
		if (csvDatagram == null){
			return null;
		}
		return internalParse(csvDatagram.getContent(), new ArrayDeque<CsvGrammarAction<?>>(10), 
				csvDatagram.getOffset(), csvDatagram.getFileHashCode(), csvDatagram.isLastDatagram(), csvDatagram.isLastDatagram());
	}

	/**
	 * Algorithm of CSV parsing. We search some CSV keyword and generate a corresponding action. When a new action is creating, 
	 * if the previous action is not terminated we stock it in a stack.
	 * @param csvContent content from CSV file to parse
	 * @param stack stack of CSV action to execute. The head of stack is the last action to execute. The tail of stack is the first action to execute.
	 * @param startOffset start of file offset in CSV file of CSV datagram 
	 * @param fileID file ID
	 * @param isLastDatagram True, if it's the last CSV datagram from file.
	 * @param canAddFooter true if we can add the footer action. (It's must be the last datagram).
	 * @return the list of CSV bean from content
	 * @throws CsvBangException if a problem has occurred when CsvBang parsed the CSV file
	 * @see {@link com.github.lecogiteur.csvbang.parser.CsvGrammarAction}
	 * @since 1.0.0
	 */
	public CsvParsingResult<T> internalParse(final byte[] csvContent, final Deque<CsvGrammarAction<?>> stack, final long startOffset, 
			final int fileID, final boolean isLastDatagram, final boolean canAddFooter) throws CsvBangException{
		if (stack.isEmpty() && (csvContent == null || csvContent.length == 0)){
			//no data to analyze
			return null;
		}
		
		if (LOGGER.isLoggable(Level.FINEST)){
			LOGGER.finest(String.format("Parse offset [%s] - stack contains %s actions- file ID: %s - Last datagram: %s", 
					startOffset, stack.size(), fileID, isLastDatagram ));
		}
		
		//init
		final ByteStreamBuffer buffer = new ByteStreamBuffer(csvContent);
		long fileOffset = startOffset;
		boolean mustContinue = false;
		boolean isLast = isLastDatagram;
		boolean addFooter = canAddFooter;
		
		//list of CSV beans which are completed.
		final CsvParsingResult<T> resultParsing = new CsvParsingResult<T>();
		try{
			do{
				//load stack
				initStack(stack, fileID, fileOffset);

				//has inital stack?
				final boolean hasInitialStack = !stack.isEmpty();

				//init the current action
				CsvGrammarAction<?> action = initAction(stack, fileOffset, buffer.length());

				//init content to analyze
				while (hasInitialStack && action != null && CsvGrammarActionType.UNDEFINED.equals(action.getType())){
					//retrieve last undefined action
					final UndefinedGrammarAction a = (UndefinedGrammarAction) action;
					//get the byte content
					final ByteStreamBuffer result = a.execute();
					if (result.isEmpty()){
						//if no content in the undefined action we search the next action
						action =  stack.pollLast();
						continue;
					}
					buffer.addBefore(result);
					fileOffset = a.getStartOffset();
					action =  stack.pollLast();
				}
				if (action == null){
					action =  initAction(stack, fileOffset, buffer.length());
				}

				//offset of start action which can be generated by content
				long startActionOffset = fileOffset;
				addFooter = addFooter && !CsvGrammarActionType.FOOTER.equals(action.getType());

				if (!buffer.isEmpty()){
					//for (int contentOffset=0; contentOffset < content.length;){
					buffer.reset();
					while (!buffer.isReadWholeBuffer()){

						if (LOGGER.isLoggable(Level.FINEST)){
							LOGGER.finest(String.format("Parse offset [%s] - stack contains %s actions- file ID: %s - Last datagram: %s. Read byte: %s", 
									startOffset, stack.size(), fileID, isLastDatagram, buffer.readPosition() ));
						}
						
						//read the current
						final byte currentByte = buffer.read();
						
						//we verify if it is a keyword of CSV grammar
						final int indexTableGrammar = isKeyword(currentByte, buffer, isLastDatagram);

						if (indexTableGrammar < 0){
							//not a character of CSV keyword
							action.add(currentByte);
							++fileOffset;
							continue;
						}

						//retrieve the action we must to execute for the new keyword
						final CsvGrammarActionType newAction = actions[indexTableGrammar];
						final boolean isUndefinedAction = CsvGrammarActionType.UNDEFINED.equals(newAction);
						final int keywordLength = isUndefinedAction?0:keywordTable[indexTableGrammar].length;

						//we defined that index "contentOffset" a keyword start, so we move the index after the keyword 
						startActionOffset = fileOffset;
						fileOffset+=keywordLength;

						//chuck management
						if (!isUndefinedAction && action.isChuck(newAction, isUndefinedAction?null:keywordTable[indexTableGrammar])){
							action.add(currentByte);
							for (int i=1; i < keywordLength && !buffer.isReadWholeBuffer(); i++){
								action.add(buffer.read());
							}
							//it's not an action but content
							continue;
						}
						
						if (CsvGrammarActionType.NOTHING_TO_DO.equals(newAction)){
							//nothing to do so we continue to the next element
							buffer.setPosition(keywordLength + buffer.readPosition() - 1);
							continue;
						}
						
						//generate the new action
						final CsvGrammarAction<?> a = generateAction(newAction, buffer.length() - buffer.readPosition());
						a.setStartOffset(startActionOffset);
						if (isUndefinedAction){
							//if the action is undefined we add the keyword to content of action
							a.add(currentByte);
							++fileOffset;
							while (!buffer.isReadWholeBuffer()){
								a.add(buffer.read());
								++fileOffset;
							}
						}else{
							//set the position of read cursor after the keyword
							buffer.setPosition(keywordLength + buffer.readPosition() - 1);
						}

						//add action to the the stack
						action.setEndOffset(startActionOffset);
						stack.add(action);
						
						//we set the new current action
						action = a;
						addFooter = addFooter && !CsvGrammarActionType.FOOTER.equals(action.getType());
					}

					//the action is not terminated, we put it in stack of action not terminated
					action.setEndOffset(fileOffset);
				}else if (action.getEndOffset() > 0){
					fileOffset = action.getEndOffset();
				}

				//the action is not terminated, we put it in stack of action not terminated
				stack.add(action);

				if (isLast && !CsvGrammarActionType.END.equals(action.getType())){
					if (addFooter){
						action = generateAction(CsvGrammarActionType.FOOTER, 0);
						action.setStartOffset(fileOffset);
						action.setEndOffset(fileOffset++);
						stack.add(action);
					}else{
						//it's the last datagramm for this file
						action = generateAction(CsvGrammarActionType.END, 0);
						action.setStartOffset(fileOffset);
						action.setEndOffset(fileOffset);
						stack.add(action);
					}
				}
				
				mustContinue = false;
				if (!stack.isEmpty()){
					
					//try to execute all action in stack
					tryExecuteStack(stack, resultParsing, fileID);
					
					//retrieve the last undefined action in order to try to execute again the undefined action
					final Deque<CsvGrammarAction<?>> stackToSave = new ArrayDeque<CsvGrammarAction<?>>(stack.size());
					action = stack.removeLast();
					while (!stack.isEmpty() && !CsvGrammarActionType.UNDEFINED.equals(action.getType())){
						stackToSave.addFirst(action);
						action = stack.removeLast();
					}
					
					if (fileOffset != action.getEndOffset() && !stack.isEmpty()){ //do not loop
						//we find an undefined action. We try to parse again its content
						final UndefinedGrammarAction a = (UndefinedGrammarAction)action;
						buffer.clear();
						buffer.addAfter(a.execute());
						fileOffset = a.getStartOffset();
						mustContinue = true;
						isLast = a.isLastAction();
						if (!stackToSave.isEmpty()){
							//a part of stack (after the undefined action) is not empty, save it
							stackByOffset.put(generateID(fileID, stackToSave.getFirst().getStartOffset(), stackToSave.getLast().getEndOffset()), stackToSave);
						}
					}else if (fileOffset != action.getEndOffset() && stack.isEmpty()){
						//stack is not empty, save it
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
		return resultParsing;
	}
	
	/**
	 * Try to execute all actions of the stack
	 * @param stack the stack
	 * @param result list of CSV bean generated
	 * @param fileID the file ID
	 * @throws CsvBangException if a problem has occurred when we execute an action
	 * @since 1.0.0
	 */
	private void tryExecuteStack(final Deque<CsvGrammarAction<?>> stack, final CsvParsingResult<T> result, final int fileID) 
			throws CsvBangException{
		//init stack
		final Deque<CsvGrammarAction<?>> stackToSave = new ArrayDeque<CsvGrammarAction<?>>(stack.size());
		
		//retrieve first action of stack
		CsvGrammarAction<?> action = stack.removeFirst();
		final long startOffset = action.getStartOffset();
		while (!stack.isEmpty()){
			if (action.isLastAction()){
				//It's the last action
				tryClearStack(result, stackToSave, action, CsvGrammarActionType.END);
				action = null;
				break;
			}else if (action.isActionCompleted(stack.getFirst().getType())){
				//the action is terminated
				tryClearStack(result, stackToSave, action, stack.getFirst().getType());
				action = stack.removeFirst();
			}else{
				//the action is not terminated, so we get the next action 
				stackToSave.addLast(action);
				action = stack.removeFirst();
			}
		}

		stack.clear();
		if (action != null){
			//
			if (action.isLastAction()){
				tryClearStack(result, stackToSave, action, CsvGrammarActionType.END);
			}else{
				stackToSave.addLast(action);
			}
		}else if (stackToSave.isEmpty() || (stackToSave.size() == 1 && CsvGrammarActionType.END.equals(stackToSave.getFirst().getType()))){
			//all stack is Empty and the last action is the last action of file, so we add END action
			action = generateAction(CsvGrammarActionType.END, 0);
			action.setStartOffset(startOffset);
			action.setEndOffset(startOffset);
			stack.add(action);
			return;
		}

		stack.addAll(stackToSave);
	}
	
	/**
	 * Flush content. Becareful, this method is blocking until all content is parsed.
	 * @return the list of CSV bean
	 * @throws CsvBangException if a problem has occurred when CsvBang parsed the CSV file
	 * @since 1.0.0
	 */
	public Collection<CsvParsingResult<T>> flush() throws CsvBangException{
		int request = requestOfFlush.incrementAndGet();
		if (request > 1){
			return null;
		}
		
		//list of CSV beans which are completed.
		final Collection<CsvParsingResult<T>> result = new ArrayList<CsvParsingResult<T>>();
		
		while (request > 0){
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

				/*if (CsvGrammarActionType.UNDEFINED.equals(stack.getValue().getLast().getType())){
				//the action is undefined, we retrieve the last content
				final UndefinedGrammarAction action = (UndefinedGrammarAction) stack.getValue().removeLast();
				content = action.execute();
				startOffset = action.getStartOffset();
			}*/

				//parse content
				final CsvParsingResult<T> r = internalParse(content, stack.getValue(), startOffset, 
						stack.getKey().fileId, true, false);

				//add result
				if (r != null){
					result.add(r);
				}
			}
			request = requestOfFlush.decrementAndGet();
		}
		
		return result;
	}
	
	/**
	 * Initialize the first action before to analyze CSV content 
	 * @param stack the stack of actions which are not terminated 
	 * @param fileOffset the offset in file
	 * @param contentLength length of content
	 * @return the initial action
	 * @throws CsvBangException if a problem has occurred when initialize an action
	 * @since 1.0.0
	 */
	private CsvGrammarAction<?> initAction(final Deque<CsvGrammarAction<?>> stack, final long fileOffset, final int contentLength) 
			throws CsvBangException{
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
		final boolean hasQuoteChar = CsvbangUti.isStringNotBlank(conf.quote + "");
		final boolean hasGeneratedHeader = conf.header != null && conf.header.length() > 0;
		int index = 0;
		
		//init table
		for (int i=0; i<table.length; i++){
			actionList.add(CsvGrammarActionType.NOTHING_TO_DO);
		}
		
		//sort keyword by length
		//delimiter
		keywordSortedByLength.add(conf.delimiter);
		
		//end record
		sortKeyword(keywordSortedByLength, conf.endRecord);
		
		//start record
		sortKeyword(keywordSortedByLength, conf.startRecord);
		
		//the footer
		sortKeyword(keywordSortedByLength, conf.footer);
		

		final StringBuilder startComment = new StringBuilder(5);
		final StringBuilder endComment = new StringBuilder(5);
		if (hasCommentChar){
			//start comment
			startComment.append(conf.defaultEndLineCharacter.toString()).append(conf.commentCharacter);
			sortKeyword(keywordSortedByLength, startComment.toString());

			//END comment
			endComment.append(conf.defaultEndLineCharacter.toString()).append(conf.startRecord);
			sortKeyword(keywordSortedByLength, endComment.toString());
		}
		

		
		final StringBuilder quote = new StringBuilder(5);
		final StringBuilder escapequote = new StringBuilder(5);
		if (hasQuoteChar){
			//a quote
			quote.append(conf.quote);
			sortKeyword(keywordSortedByLength, quote.toString());
			
			escapequote.append(conf.escapeQuoteCharacter).append(conf.quote);
			sortKeyword(keywordSortedByLength, escapequote.toString());
		}
		
		final StringBuilder header = new StringBuilder();
		if (hasGeneratedHeader){
			//header
			header.append(conf.header);
			while (header.toString().endsWith(hasEndRecord?conf.endRecord:conf.startRecord)){
				final int size = header.length() - (hasEndRecord?conf.endRecord.length():conf.startRecord.length());
				header.delete(size, header.length() );
			}
			sortKeyword(keywordSortedByLength, header.toString());
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
		addKeyword(keywordSortedByLength, table, actionList, conf.footer, CsvGrammarActionType.FOOTER);
		
		//comment
		if (hasCommentChar){
			addKeyword(keywordSortedByLength, table, actionList, startComment.toString(), CsvGrammarActionType.COMMENT);
			addKeyword(keywordSortedByLength, table, actionList, endComment.toString(), CsvGrammarActionType.RECORD);
		}
		
		//quote
		if (hasQuoteChar){
			addKeyword(keywordSortedByLength, table, actionList, quote.toString(), CsvGrammarActionType.QUOTE);
			addKeyword(keywordSortedByLength, table, actionList, escapequote.toString(), CsvGrammarActionType.ESCAPE_CHARACTER);
		}
		
		//header from configuration
		if (hasGeneratedHeader){
			addKeyword(keywordSortedByLength, table, actionList, header.toString(), CsvGrammarActionType.HEADER);
		}

		index = keywordSortedByLength.size();
		
		//generate the table of keyword
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
		
		
		//######################################################"

		
		for (CsvFieldConfiguration field:conf.fields){
			if (field.isDeleteFieldIfNull){
				totalNbFieldWhichCanBeDeleted++;
			}
			hasFieldCollection = hasFieldCollection || field.typeOfSetter.isArray() || ReflectionUti.isCollection(field.typeOfSetter);
		}
		
		return dest;
	}
	
	/**
	 * Sort keyword
	 * @param sortedKeyword list of sorted keywords
	 * @param keyword the keyword to sort
	 * @since 1.0.0
	 */
	private void sortKeyword(final List<String> sortedKeywords, final String keyword){
		if (CsvbangUti.isStringNotBlank(keyword) && !sortedKeywords.contains(keyword)){
			sortedKeywords.add(keyword);
		}
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
			if (!actionList.get(index).equals(CsvGrammarActionType.NOTHING_TO_DO) && !actionList.get(index).equals(action)){
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
	 * @throws CsvBangException if a problem has occurred when we read buffer.
	 * @since 1.0.0
	 * @see #words
	 * @see #actions
	 */
	private int isKeyword(final byte currentByte, final ByteStreamBuffer content, boolean isEnd) throws CsvBangException{
		for (int i=0; i<keywordTable.length; i++){
			//If the first character of a keyword match with the current content
			if (keywordTable[i][0] == currentByte){
				if (keywordTable[i].length == 1){
					//the keyword is only one character, so we can return the index
					return i;
				}else{
					//the length of keyword is more than one character. 
					//We check characters after the first character
					boolean isKeyword = true;
					int j=1;
					int remaining = content.length() - content.readPosition();
					for (int k=0; j<keywordTable[i].length && k < remaining ; j++,k++){
						if (keywordTable[i][j] != content.readFromPosition(k)){
							//it's not the keyword. we check another keyword
							isKeyword = false;
							break;
						}
					}
					if (isKeyword){
						//it's a keyword or a part of keyword
						if (!isEnd && j < keywordTable[i].length){
							//Not enough content in order to know if it's a keyword
							//so unknowing content
							return keywordTable.length;
						}
						//it's keyword
						return i;
					}
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
	 * @throws CsvBangException if a problem has occurred when initialize an action
	 * @since 1.0.0
	 */
	private CsvGrammarAction<?> generateAction(final CsvGrammarActionType action, final int contentLength) throws CsvBangException{
		switch (action) {
		case FIELD:
			return new FieldGrammarAction(conf, contentLength);
		case RECORD:
			return new RecordGrammarAction<T>(classOfCSVBean, conf, totalNbFieldWhichCanBeDeleted, hasFieldCollection);
		case COMMENT:
			return new CommentGrammarAction(conf, contentLength);
		case QUOTE:
			return new QuoteGrammarAction(conf, contentLength);
		case ESCAPE_CHARACTER:
			return new EscapeQuoteGrammarAction(contentLength, conf);
		case END:
			return new EndGrammarAction();
		case START:
			return new StartGrammarAction<T>(classOfCSVBean, conf, totalNbFieldWhichCanBeDeleted, hasFieldCollection, contentLength);
		case HEADER:
			return new HeaderGrammarAction(conf, contentLength);
		case FOOTER:
			return new FooterGrammarAction(conf, contentLength);
		default:
			//undefined action
			return new UndefinedGrammarAction(contentLength);
		}
	}
	
	/**
	 * Try to clean the stack of actions which are not terminated
	 * @param result list of beans which are completed
	 * @param stack stack of action which are not terminated
	 * @param action current action which is terminated
	 * @param actionType current new action type
	 * @throws CsvBangException if a problem has occurred.
	 * @since 1.0.0
	 */
	private void tryClearStack(final CsvParsingResult<T> result, final Deque<CsvGrammarAction<?>> stack, 
			final CsvGrammarAction<?> action, final CsvGrammarActionType actionType) throws CsvBangException{
		boolean isEmptyStack = stack.isEmpty();
		if (isEmptyStack){
			//case where is no stack we verify if this action is terminated
			executeAction(result, stack, action);
			return;
		}
		
		CsvGrammarAction<?> current = action;
		CsvGrammarActionType currentActionType = actionType;
		//we take the last action in stack
		CsvGrammarAction<?> lastAction = stack.peekLast();
		while (!isEmptyStack && lastAction != null){
			//we add the current action which is terminated to the previous action
			if (!lastAction.add(current)){
				if (executeAction(result, stack, current)){
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
			executeAction(result, stack, current);
		}
	}
	
	
	/**
	 * Execute and process the result in function of type of action. If the action can't be executed we add it to the stack.
	 * @param result the list of CSV beans which are completed 
	 * @param stack stack of action
	 * @param action the action which is terminated
	 * @return True, if the action has been executed, false if the action is not terminated and stock in stack.
	 * @throws CsvBangException if a problem has occurred when we execute the action.
	 * @since 1.0.0
	 */
	@SuppressWarnings({ "unchecked" })
	private boolean executeAction(final CsvParsingResult<T> result, final Deque<CsvGrammarAction<?>> stack, 
			final CsvGrammarAction<?> action) throws CsvBangException{
		switch (action.getType()) {
		case RECORD:
			final T o = ((RecordGrammarAction<T>)action).execute();
			if (o != null){
				result.getCsvBeans().add(o);
			}
			addEndAction(stack, action);
			return true;
		case COMMENT:
			//get comment action
			final String comment = ((CommentGrammarAction)action).execute();
			if (comment != null){
				//add the comment
				result.getComments().add(comment);
			}
			addEndAction(stack, action);
			return true;
		case HEADER:
			final String header = ((HeaderGrammarAction)action).execute();
			if (header != null){
				//add the header
				result.setHeader(header);
			}
			addEndAction(stack, action);
			return true;
		case START:
			return executeAction(result, stack, ((StartGrammarAction<T>)action).execute());
		case FOOTER:
			final String footer = ((FooterGrammarAction)action).execute();
			if (footer != null){
				//add the footer
				result.setFooter(footer);
			}
			addEndAction(stack, action);
			return true;
		default:
			stack.add(action);
			return false;
		}
		
	}
	
	/**
	 * Add end action to the stack, if action is the last of file
	 * @param stack stack of action
	 * @param action an action
	 * @throws CsvBangException if a problem has occurred when initialize an action
	 * @since 1.0.0
	 */
	private void addEndAction(final Deque<CsvGrammarAction<?>> stack, final CsvGrammarAction<?> action) throws CsvBangException{
		if (action.isLastAction()){
			//add end action if the comment is the last of file
			final CsvGrammarAction<?> end = generateAction(CsvGrammarActionType.END, 0);
			end.setEndOffset(action.getStartOffset());
			end.setStartOffset(action.getStartOffset());
			stack.add(end);
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
