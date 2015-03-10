/**
 *  com.github.lecogiteur.csvbang.parser.StartGrammarAction
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
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

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Action to do at the start of a CSV file
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class StartGrammarAction<T> implements CsvGrammarAction<CsvGrammarAction<?>>{
	
	/**
	 * When we read file, we doesn't know what action we must to execute. 
	 * So, start action defines and delegate works to another specific action.
	 * @since 1.0.0
	 */
	private CsvGrammarAction<?> delegatedAction;
	
	/**
	 * Type of CSV bean
	 * @since 1.0.0
	 */
	private final Class<T> beanClass;
	
	/**
	 * The configuration of CSV bean
	 * @since 1.0.0
	 */
	private final CsvBangConfiguration conf;
	
	/**
	 * The end offset of start action
	 * @since 1.0.0
	 */
	private long endOffset = -1;
	
	/**
	 * byte content
	 * @since 1.0.0
	 */
	private byte[] buffer;
	
	/**
	 * Current length of buffer
	 * @since 1.0.0
	 */
	private int index = 0;
	
	/**
	 * True if the CSV file has header
	 * @since 1.0.0
	 */
	private final boolean hasHeader;
	
	/**
	 * Constructor
	 * @param beanClass type of CSV bean
	 * @param conf configuration of CSV bean
	 * @param initialCapacity initial capacity of buffer
	 * @since 1.0.0
	 */
	public StartGrammarAction(final Class<T> beanClass, final CsvBangConfiguration conf, final int initialCapacity){
		this.beanClass = beanClass;
		this.conf = conf;
		this.buffer = new byte[initialCapacity];
		this.hasHeader = conf.header != null && conf.header.length()>0;
	}
	
	/**
	 * Initialize the delegated action
	 * @param actionType the action type
	 * @param action an init action
	 * @throws CsvBangException When we add buffer to action
	 * @since 1.0.0
	 */
	//TODO enlever le paramètre action
	private void initDelegatedAction(final CsvGrammarActionType actionType, final CsvGrammarAction<?> action) throws CsvBangException{
		final CsvGrammarActionType type = action == null?actionType:action.getType();
		//set the delegated action
		switch(type){
		case COMMENT:
			delegatedAction = new CommentGrammarAction(conf, 100);
			break;
		case RECORD:
			delegatedAction = new RecordGrammarAction<T>(beanClass, conf);
			break;
		case HEADER:
			delegatedAction = new HeaderGrammarAction(conf, buffer.length);
			break;
		default:
			return;
		}
		//start offset
		delegatedAction.setStartOffset(0);
		delegatedAction.setEndOffset(endOffset);
		
		//add buffer to action
		for (int i=0; i<index; i++){
			delegatedAction.add(buffer[i]);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.START;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(final byte b) throws CsvBangException {
		if (delegatedAction == null){
			if (!hasHeader && index == 0 && conf.commentCharacter == (char)b){
				//it's a comment
				initDelegatedAction(CsvGrammarActionType.COMMENT, null);
			}else{
				if (index >= buffer.length){
					final byte[] tmp = new byte[buffer.length * 2];
					System.arraycopy(buffer, 0, tmp, 0, buffer.length);
					buffer = tmp;
				}
				buffer[index++] = b;
			}
			return;
		}
		delegatedAction.add(b);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(com.github.lecogiteur.csvbang.parser.CsvGrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(final CsvGrammarAction<?> word) throws CsvBangException {
		if (word != null && delegatedAction == null){
			switch (word.getType()) {
			case HEADER:
				initDelegatedAction(CsvGrammarActionType.HEADER, word);
				break;
			case END:
				if (conf.fields.size() > 1){
					//a record has multiple fields, so the buffer is a header
					initDelegatedAction(CsvGrammarActionType.HEADER, null);
				}else{
					//we don't know if the buffer is a record or a header
					//so we try the record type
					initDelegatedAction(CsvGrammarActionType.RECORD, null);
				}
				break;
			case RECORD:
			case COMMENT:
				if (conf.fields.size() > 1){
					//a record has multiple fields, so the buffer is a header
					initDelegatedAction(CsvGrammarActionType.HEADER, null);
				}else{
					//perhaps the buffer is a record
					if (conf.endRecord.equals(conf.defaultEndLineCharacter.toString())){
						//we don't know if the buffer is a record or a header
						//so we try the record type
						initDelegatedAction(CsvGrammarActionType.RECORD, null);
						return false;
					}else{
						//the buffer is a header
						initDelegatedAction(CsvGrammarActionType.HEADER, null);
					}
				}
				break;
			case QUOTE: 
			case ESCAPE_CHARACTER: 
			case FIELD:
				//we try to add a field. So the start action is a record
				initDelegatedAction(CsvGrammarActionType.RECORD, null);
				break;
			default:
				return false;
			}
		}
		return delegatedAction.add(word);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(final CsvGrammarActionType next) {
		return delegatedAction != null && delegatedAction.isActionCompleted(next);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarAction<?> execute() throws CsvBangException {
		return delegatedAction;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getStartOffset()
	 * @since 1.0.0
	 */
	@Override
	public long getStartOffset() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getEndOffset()
	 * @since 1.0.0
	 */
	@Override
	public long getEndOffset() {
		if (delegatedAction == null){
			return endOffset;
		}
		return delegatedAction.getEndOffset();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#setStartOffset(long)
	 * @since 1.0.0
	 */
	@Override
	public void setStartOffset(long offset) {
		//nothing to do
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#setEndOffset(long)
	 * @since 1.0.0
	 */
	@Override
	public void setEndOffset(long offset) {
		if (delegatedAction != null){
			delegatedAction.setEndOffset(offset);
			this.endOffset = -1;
		}else{
			this.endOffset = offset;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isLastAction()
	 * @since 1.0.0
	 */
	@Override
	public boolean isLastAction() {
		return delegatedAction != null && delegatedAction.isLastAction();
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isChuck(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType, byte[])
	 * @since 1.0.0
	 */
	@Override
	public boolean isChuck(final CsvGrammarActionType next, final byte[] keyword) throws CsvBangException {
		if (delegatedAction != null){
			return delegatedAction.isChuck(next, keyword);
		}
		
		if (CsvGrammarActionType.HEADER.equals(next)){
			return false;
		}
		
		if (hasHeader){
			if  (index < conf.header.length()){
				return true;
			}
			
			final byte[] header = conf.header.getBytes(conf.charset);
			final int max=index-header.length;
			int maxHeader = header.length;
			
			final int idx = header.length - keyword.length;
			if (idx >= 0){
				int i=0;
				for (; i<keyword.length; i++){
					if (header[i+idx] != keyword[i]){
						break;
					}
				}
				if (i == keyword.length){
					maxHeader = idx;
				}
			}
			
			for (int i=0; i < max; i++){
				if (header[0] == buffer[i]){
					int j=1;
					for (; j < maxHeader; j++){
						if (header[j] != buffer[i+j]){
							break;
						}
					}
					if (j == maxHeader){
						initDelegatedAction(CsvGrammarActionType.HEADER, null);
						return false;
					}
				}
			}
			
			return true;
			
		}
		
		return false;
	}
}
