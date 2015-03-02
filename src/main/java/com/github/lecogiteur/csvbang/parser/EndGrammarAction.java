/**
 *  com.github.lecogiteur.csvbang.parser.EndGrammarAction
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
 * End file action
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class EndGrammarAction implements CsvGrammarAction<Void> {
	
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
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.END;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) throws CsvBangException {
		throw new CsvBangException("We can't add byte to the end action");
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(com.github.lecogiteur.csvbang.parser.CsvGrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(CsvGrammarAction<?> word) throws CsvBangException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(CsvGrammarActionType next) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public Void execute() throws CsvBangException {
		return null;
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
		return true;
	}



}
