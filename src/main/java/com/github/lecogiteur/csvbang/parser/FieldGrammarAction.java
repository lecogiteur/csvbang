/**
 *  com.github.lecogiteur.csvbang.parser.FieldGrammarAction
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

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Generate a field
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FieldGrammarAction implements CsvGrammarAction<String> {
	
	/**
	 * Start offset of this action in CSV file
	 * @since 1.0.0
	 */
	private long startOffset = -1;
	
	/**
	 * End offset of this action in CSV file
	 * @since 1.0.0
	 */
	private long endOffset = -1;
	
	/**
	 * Content of field
	 * @since 1.0.0
	 */
	private final StringBuilder content;
	
	/**
	 * True if it's the last field of file and it's terminated. Warning: if file have a footer or comment after last field, this variable is at false.
	 * @since 1.0.0
	 */
	private boolean isFieldTerminated = false;
	
	/**
	 * Constructor
	 * @param capacity initial capacity of content of field
	 * @since 1.0.0
	 */
	public FieldGrammarAction(final int capacity){
		content = new StringBuilder(capacity);
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.FIELD;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) {
		content.append((char)b);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(com.github.lecogiteur.csvbang.parser.CsvGrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(CsvGrammarAction<?> word) throws CsvBangException {
		if (word != null){
			switch (word.getType()) {
			case RECORD:
				isFieldTerminated = isFieldTerminated || word.isLastAction();
				return false;
			case END:
				isFieldTerminated = isFieldTerminated || word.isLastAction();
				return true;
			default:
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(CsvGrammarActionType next) {
		return isFieldTerminated || (next != null && !(CsvGrammarActionType.NOTHING_TO_DO.equals(next) || CsvGrammarActionType.UNDEFINED.equals(next)));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public String execute() {
		return content.length() == 0?null:content.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getStartOffset()
	 * @since 1.0.0
	 */
	@Override
	public long getStartOffset() {
		return startOffset;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getEndOffset()
	 * @since 1.0.0
	 */
	@Override
	public long getEndOffset() {
		return endOffset;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#setStartOffset(long)
	 * @since 1.0.0
	 */
	@Override
	public void setStartOffset(long offset) {
		startOffset = offset;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#setEndOffset(long)
	 * @since 1.0.0
	 */
	@Override
	public void setEndOffset(long offset) {
		endOffset = offset;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isLastAction()
	 * @since 1.0.0
	 */
	@Override
	public boolean isLastAction() {
		return isFieldTerminated;
	}



}
