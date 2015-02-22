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
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

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
	 * table of keywords
	 * @since 1.0.0
	 */
	private final byte[][] keywordTable;

	/**
	 * table of action. Each index matching to the index of table of keywords
	 * @since 1.0.0
	 */
	private final CsvGrammarActionType actions[];
	
	/**
	 * Unterminated stack of actions by offset
	 * @since 1.0.0
	 */
	private final ConcurrentSkipListMap<ActionKey, Deque<GrammarAction<?>>> stackByOffset;
	
	/**
	 * Undefined actions by offset
	 * @since 1.0.0
	 */
	private final ConcurrentSkipListMap<ActionKey, UndefinedGrammarAction> undefinedByOffset;
	
	/**
	 * Constructor
	 * @param classOfCSVBean
	 * @param conf
	 * @since 1.0.0
	 */
	public CsvParser(Class<T> classOfCSVBean, CsvBangConfiguration conf) {
		super();
		this.classOfCSVBean = classOfCSVBean;
		this.conf = conf;
		this.stackByOffset = new ConcurrentSkipListMap<ActionKey, Deque<GrammarAction<?>>>();
		this.undefinedByOffset = new ConcurrentSkipListMap<ActionKey, UndefinedGrammarAction>();
		final List<CsvGrammarActionType> actionList = new ArrayList<CsvGrammarActionType>();
		this.keywordTable = initGrammar(conf, actionList);
		this.actions = actionList.toArray(new CsvGrammarActionType[actionList.size()]);
	}

	/**
	 * Parse a CSV datagram from a CSV file.
	 * @param data the data
	 * @return List of CSV actions which are terminated
	 * @throws CsvBangException If a problem has occurred during parsing.
	 * @since 1.0.0
	 */
	public Collection<T> parse(final CsvDatagram data) throws CsvBangException{
		
		//stack of action. when an action is not terminated we stock it in the stack. 
		final Deque<GrammarAction<?>> stack = new ArrayDeque<GrammarAction<?>>(30);
		long currentStackOffset = initStack(stack, data.getOffset(), data.getFileHashCode(), -1);
		
		//parse content
		return internalParse(data.getContent(), data.getOffset(), currentStackOffset, 
				data.getFileHashCode(), stack, data.isLastDatagram());
	}
	
	/**
	 * Flush content. Becareful, this method is blocking until all content is parsed.
	 * @return last record.
	 * @throws CsvBangException if problem has occurred when we parse the file content
	 * @since 1.0.0
	 */
	public Collection<T> flush() throws CsvBangException{
		
		//list of CSV beans which are completed.
		final Collection<T> listOfBeans = new ArrayList<T>(100);
		
		while (!stackByOffset.isEmpty() || !undefinedByOffset.isEmpty()){
			
			if (stackByOffset.isEmpty()){
				//there is no stack, so we terminated to parse all content
				final Entry<ActionKey, UndefinedGrammarAction> entryContent = undefinedByOffset.pollFirstEntry();
				if (entryContent != null){
					final ActionKey key = entryContent.getKey();
					final Collection<T> list = internalParse(entryContent.getValue().execute(), key.startOffset, -1, 
							key.fileId, new ArrayDeque<GrammarAction<?>>(5), false);
					if (CsvbangUti.isCollectionNotEmpty(list)){
						listOfBeans.addAll(list);
					}
				}
				continue;
			}
			
			
			//we take the last stack
			final Entry<ActionKey, Deque<GrammarAction<?>>> entryStack = stackByOffset.pollLastEntry();
			if (entryStack != null){
				final Deque<GrammarAction<?>> stack = entryStack.getValue();
				final ActionKey key = entryStack.getKey();
				if (undefinedByOffset.isEmpty()){
					//if no content we try clear stack
					final long stackStartOffset = initStack(stack, key.startOffset, key.fileId, key.startOffset);
					key.startOffset = stackStartOffset;
					final GrammarAction<?> action = stack.pollLast();
					if (!stack.isEmpty()){
						tryClearStack(listOfBeans, stack, action, null);
						if (!stack.isEmpty()){
							stackByOffset.put(key, stack);
						}
					}else{
						executeAction(listOfBeans, stack, action, key);
					}
					continue;
				}
				//we retrieve the undefined data byte after end offset of this stack in order to try to clear the stack
				final ActionKey keyU = generateID(entryStack.getKey().fileId, entryStack.getKey().endOffset, -1);
				final UndefinedGrammarAction content = undefinedByOffset.remove(keyU);
				if (content != null){
					//if we find data byte, we try to parse it again and generate new record
					final Collection<T> list = internalParse(content.execute(), key.endOffset,
							key.startOffset, key.fileId, stack, false);
					if (CsvbangUti.isCollectionNotEmpty(list)){
						listOfBeans.addAll(list);
					}
					continue;
				}
				
				//no content find in order to try to clear the stack. Perhaps, we don't have terminated to read file. We must wait.
				stackByOffset.put(entryStack.getKey(), entryStack.getValue());
				continue;
			}
		}
		
		return listOfBeans;
	}
	
	/**
	 * Create the table of keywords and table of action
	 * @param conf the Csvbang configuration of CSV bean
	 * @param actionList the list of action. The index matches with the index of table keywords
	 * @return the table of keywords
	 * @since 1.0.0
	 */
	private byte[][] initGrammar(final CsvBangConfiguration conf, final List<CsvGrammarActionType> actionList){
		final byte[][] table = new byte[10][];
		boolean hasEndRecord = CsvbangUti.isStringNotBlank(conf.endRecord);
		int index = 0;
		//TODO faire les vérifications lorsque 2 mots clé sont égaux. voir lastIndexOf
		//sort keyword by length
		final List<String> keywordSortedByLength = new ArrayList<String>(); 
		keywordSortedByLength.add(conf.delimiter);
		if (hasEndRecord){
			keywordSortedByLength.add(conf.endRecord);
		}
		if (CsvbangUti.isStringNotBlank(conf.startRecord)){
			keywordSortedByLength.add(conf.startRecord);
		}
		if (CsvbangUti.isStringNotBlank(conf.footer)){
			keywordSortedByLength.add(conf.footer);
		}
		if (CsvbangUti.isStringNotBlank(conf.header)){
			keywordSortedByLength.add(conf.header);
		}
		Collections.sort(keywordSortedByLength);
		Collections.reverse(keywordSortedByLength);
		
		//create tables
		index = keywordSortedByLength.indexOf(conf.delimiter);
		table[index] = conf.delimiter.getBytes(conf.charset);
		actionList.add(index, CsvGrammarActionType.FIELD);
		
		index = keywordSortedByLength.indexOf(conf.endRecord);
		if (index >= 0){
			table[index] = conf.endRecord.getBytes(conf.charset);
			actionList.add(index, CsvGrammarActionType.RECORD);
		}
		
		index = keywordSortedByLength.indexOf(conf.startRecord);
		if (index >= 0){
			table[index] = conf.startRecord.getBytes(conf.charset);
			actionList.add(index, hasEndRecord?CsvGrammarActionType.NOTHING_TO_DO:CsvGrammarActionType.RECORD);
		}
		
		index = keywordSortedByLength.indexOf(conf.footer);
		if (index >= 0){
			table[index] = conf.footer.getBytes(conf.charset);
			actionList.add(index, CsvGrammarActionType.NOTHING_TO_DO);
		}
		
		index = keywordSortedByLength.indexOf(conf.header);
		if (index >= 0){
			table[index] = conf.header.getBytes(conf.charset);
			actionList.add(index, CsvGrammarActionType.NOTHING_TO_DO);
		}

		index = keywordSortedByLength.size();
		table[index++] = conf.defaultEndLineCharacter.toBytes(conf.charset);
		actionList.add(CsvGrammarActionType.NOTHING_TO_DO);
		
		table[index++] = new byte[]{(byte)conf.escapeQuoteCharacter};
		actionList.add(CsvGrammarActionType.NOTHING_TO_DO);
		
		if (conf.quote != null){ 
			table[index++] = new byte[]{(byte)conf.quote.charValue()};
			actionList.add(CsvGrammarActionType.NOTHING_TO_DO);
		}
		
		table[index++] = new byte[]{(byte)conf.commentCharacter};
		actionList.add(CsvGrammarActionType.NOTHING_TO_DO);
		
		
		final byte[][] dest = new byte[index][];
		System.arraycopy(table, 0, dest, 0, index);
		return dest;
	}
	
	/**
	 * Internal algorithm of CSV parsing.
	 * @param intialContent initial byte content from CSV file
	 * @param startOffset the start offset of content
	 * @param initialStackStartOffset the initial stack start offset
	 * @param fileID the ID of CSV file
	 * @param stack the stack of action which are not terminated
	 * @param isLastDatagram true if it is the last part of CSV file
	 * @return CSV bean generated. Can be null.
	 * @throws CsvBangException if a problem has occurred when we parse part of file
	 * @since 1.0.0
	 */
	private Collection<T> internalParse(final byte[] intialContent, final long startOffset, final long initialStackStartOffset, 
			final int fileID, final Deque<GrammarAction<?>> stack, final boolean isLastDatagram) throws CsvBangException{
		//init
		byte[] content = intialContent;
		long currentOffset = startOffset;
		long startActionOffset = startOffset;
		boolean continu = false;
		
		//initialize the stack with previous action (if they exist)
		long currentStackOffset = initStack(stack, initialStackStartOffset, fileID, initialStackStartOffset);

		//init current action
		GrammarAction<?> action = null;
		if (!stack.isEmpty()){
			action = stack.pollLast();
		}else{
			action = generateAction(startOffset == 0?CsvGrammarActionType.START:CsvGrammarActionType.UNDEFINED,
					content.length);
		}
		
		//list of CSV beans which are completed.
		final Collection<T> listOfBeans = new ArrayList<T>(100);

		do{
			for (int i=0; i<content.length;){
				//we verify if it is a keyword of CSV grammar
				final int index = isKeyword(i, content);
				if (index >= 0){
					//it's a keyword
					//retrieve type of action that we must to do
					final CsvGrammarActionType actionType = actions[index];

					//we defined that index "i" a keyword start, so we move the index after the keyword 
					long lastStartActionOffset = startActionOffset;
					startActionOffset = currentOffset++;
					i += keywordTable[index].length;

					//generate the new action
					final GrammarAction<?> a = generateAction(actionType, content.length - i);

					//create tree of actions
					if (action != null && !action.isActionCompleted(actionType)){
						// the previous action is not terminated, so we stock it in the stack
						if (stack.isEmpty()){
							//we move the start offset of stack to the current offset
							currentStackOffset = startActionOffset;
						}
						stack.add(action);
					}else if (!stack.isEmpty()){
						//the previous action is terminated and the stack is not empty.
						//We try to clear the stack
						tryClearStack(listOfBeans, stack, action, actionType);
						if (stack.isEmpty()){
							//the stack is cleared, so we invalidated the start offset
							currentStackOffset = -1;
						}
					}else{
						//the action is terminated and there is not action in stack. 
						//We execute the action
						executeAction(listOfBeans, stack, action, generateID(fileID, lastStartActionOffset, currentOffset));
						if (!stack.isEmpty()){
							currentStackOffset = lastStartActionOffset;
						}
					}

					//we set the new current action
					action = a;
					continue;
				}else if (action != null){
					//Not a keyword, so it is some content of an action
					action.add(content[i++]);
					++currentOffset;
					continue;
				}

				//not possible so we throw an exception
				throw new CsvBangException(String.format("A problem has occurred when we parse a file from offset [%s] to [%s].", 
						startOffset, currentOffset));
			}

			final ActionKey id = generateID(fileID, currentOffset, -1);

			//verify if CSV data exist after the last byte offset
			final UndefinedGrammarAction newAction = undefinedByOffset.remove(id);
			continu = newAction != null;
			if (continu){
				content =  newAction.execute();
			}

		}while (continu);

		if (action != null){
			//the last action is not terminated
			if (stack.isEmpty()){
				//we move the start offset of stack to the start offset of action
				currentStackOffset = startActionOffset;
			}
			stack.add(action);
		}

		if (isLastDatagram){
			//it's the last datagramm for this file
			if (stack.isEmpty()){
				//we move the start offset of stack to the current offset
				currentStackOffset = currentOffset;
			}
			stack.add(generateAction(CsvGrammarActionType.END, 0));
		}
		
		if (!stack.isEmpty()){
			//stack is not empty, save it
			stackByOffset.put(generateID(fileID, currentStackOffset, currentOffset), stack);
		}
		return listOfBeans;
	}

	/**
	 * Initialize the stack. We retrieve previous stack compared to the start offset of CSV datagram
	 * @param result the current stack
	 * @param startOffset the start offset of CSV datagram
	 * @param fileID fileID of the CSV datagram
	 * @param defaultStartOffset start offset to return, if we don't find previous stack
	 * @return the start offset of stack. if we use previous stack, the start offset is moved to the start offset of previous stack. We retrun -1 if the stack is empty.
	 * @since 1.0.0
	 */
	private long initStack(final Deque<GrammarAction<?>> result, final long startOffset, final int fileID, final long defaultStartOffset){
		//init key
		ActionKey currentKey = new ActionKey();
		currentKey.fileId = fileID;
		currentKey.endOffset = startOffset;
		
		//find previous stack
		//retrieve the key map
		ActionKey keyMap = stackByOffset.floorKey(currentKey);
		while (currentKey.equals(keyMap)){
			//if it's the current key we get the stack
			final Deque<GrammarAction<?>> stack = stackByOffset.remove(keyMap);
			if (stack == null){
				//another thread take it, sorry.
				break;
			}
			while (stack.size() > 0){
				//we add all element of the previous stack in current stack
				final GrammarAction<?> action = stack.pollLast();
				result.addFirst(action);
			}
			//get the next previous key 
			currentKey = keyMap;
			keyMap = stackByOffset.floorKey(currentKey);
		}
		
		//the current start offset of the current stack
		return currentKey.startOffset < 0?defaultStartOffset:currentKey.startOffset;
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
	 * @return the index of action in the action table or -1 if it's no a keyword
	 * @since 1.0.0
	 * @see #words
	 * @see #actions
	 */
	private int isKeyword(final int index, final byte[] content){
		for (int i=0; i<keywordTable.length; i++){
			if (keywordTable[i][0] == content[index]){
				boolean isKeyword = true;
				for (int j=1; j<keywordTable[i].length; j++){
					if (keywordTable[i][j] != content[index +1]){
						isKeyword = false;
						break;
					}
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
	 * @param action the type of action to create
	 * @param contentLength remaining content length
	 * @return the new CSV action
	 * @since 1.0.0
	 */
	private GrammarAction<?> generateAction(final CsvGrammarActionType action, final int contentLength){
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
	private void tryClearStack(final Collection<T> listOfBeans, final Deque<GrammarAction<?>> stack, 
			final GrammarAction<?> action, final CsvGrammarActionType actionType) throws CsvBangException{
		GrammarAction<?> a = action;
		boolean isEmptyStack = stack.isEmpty();
		CsvGrammarActionType currentActionType = actionType;
		//we take the last action in stack
		GrammarAction<?> lastAction = stack.pollLast();
		while (!isEmptyStack && lastAction != null){
			//we add the current action which is terminated to the previous action
			if (!lastAction.add(a)){
				executeAction(listOfBeans, stack, a, null);
				final GrammarAction<?> tmp = stack.pollLast();
				if (tmp != null && tmp.equals(a)){
					stack.add(tmp);
					break;
				}else{
					currentActionType = a.getType();
					a = lastAction;
					lastAction = tmp;
					isEmptyStack = lastAction == null;
					continue;
				}
			}else if (lastAction.isActionCompleted(currentActionType)){
				//the previous action is now terminated. So we add it to its previous action
				a = lastAction;
				lastAction = stack.pollLast();
			}else{
				//the previous action is not terminated so we put it again in stack.
				//not necessary to continue to empty the stack.
				stack.add(lastAction);
				lastAction = null;
			}
			isEmptyStack = stack.isEmpty();
		}

		//the action is terminated
		if (isEmptyStack){
			executeAction(listOfBeans, stack, a, null);
		}
	}
	
	/**
	 * Execute and process the result in function of type of action
	 * @param listOfBeans the list of CSV beans which are completed 
	 * @param stack stack of action
	 * @param action the action which is terminated
	 * @param id ID of action
	 * @throws CsvBangException if a problem has occurred when we execute the action.
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	private void executeAction(final Collection<T> listOfBeans, final Deque<GrammarAction<?>> stack, 
			final GrammarAction<?> action, final ActionKey id) throws CsvBangException{
		switch (action.getType()) {
		case RECORD:
			final T o = ((RecordGrammarAction<T>)action).execute();
			if (o != null){
				listOfBeans.add(((RecordGrammarAction<T>)action).execute());
			}
			break;
		case UNDEFINED:
			undefinedByOffset.put(id, (UndefinedGrammarAction)action);
			break;
		case START:
			executeAction(listOfBeans, stack, ((StartGrammarAction)action).execute(), id);
		case END:
			break;
		default:
			stack.add(action);
			break;
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
		public int compareTo(CsvParser<T>.ActionKey o) {
			if (equals(o)){
				return 0;
			}
			if (fileId < o.fileId){
				return -1;
			}
			if (fileId > o.fileId){
				return 1;
			}
			if (o.endOffset <= startOffset){
				return -1;
			}
			if (endOffset <= o.startOffset){
				return 1;
			}
			return startOffset<o.startOffset?1:-1;
		}

	
		
	}
}
