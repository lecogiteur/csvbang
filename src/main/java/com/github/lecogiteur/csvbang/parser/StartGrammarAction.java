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
import com.github.lecogiteur.csvbang.util.CsvbangUti;

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
	 * Constructor
	 * @param beanClass type of CSV bean
	 * @param conf configuration of CSV bean
	 * @since 1.0.0
	 */
	public StartGrammarAction(final Class<T> beanClass, final CsvBangConfiguration conf){
		this.beanClass = beanClass;
		this.conf = conf;
	}
	
	/**
	 * Initialize the delegated action
	 * @param actionType the action type
	 * @since 1.0.0
	 */
	private void initDelegatedAction(final CsvGrammarActionType actionType){
		switch(actionType){
		case COMMENT:
			delegatedAction = new CommentGrammarAction(100);
			((CommentGrammarAction)delegatedAction).setIsFieldComment(CsvbangUti.isCollectionNotEmpty(conf.commentsBefore));
			break;
		case RECORD:
			delegatedAction = new RecordGrammarAction<T>(beanClass, conf);
			break;
		default:
			return;
		}
		delegatedAction.setStartOffset(0);
		if (endOffset > 0){
			delegatedAction.setEndOffset(endOffset);
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
			if (conf.commentCharacter == (char)b){
				//it's a comment
				initDelegatedAction(CsvGrammarActionType.COMMENT);
				return;
			}else{
				//it's a record
				initDelegatedAction(CsvGrammarActionType.RECORD);
			}
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
			if (CsvGrammarActionType.FIELD.equals(word.getType())){
				//we try to add a field. So the start action is a record
				initDelegatedAction(CsvGrammarActionType.RECORD);
				delegatedAction.add(word);
				return true;
			}
			return false;
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
}
