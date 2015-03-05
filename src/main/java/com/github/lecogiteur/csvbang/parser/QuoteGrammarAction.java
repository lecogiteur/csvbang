/**
 *  com.github.lecogiteur.csvbang.parser.QuoteGrammarAction
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


/**
 * Quote action
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class QuoteGrammarAction extends FieldGrammarAction {
	
	

	/**
	 * Is chuck
	 * @since 1.0.0
	 */
	private boolean isChuck = true;


	/**
	 * True if it's not the end of content quote
	 * @since 1.0.0
	 */
	private boolean isNotEndQuote = true;

	/**
	 * Constructor
	 * @param capacity the initial capacity of field
	 * @since 1.0.0
	 */
	public QuoteGrammarAction(final int capacity) {
		super(capacity);
	}
	
	

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.FieldGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.QUOTE;
	}



	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.FieldGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(CsvGrammarActionType next) {
		return !(CsvGrammarActionType.NOTHING_TO_DO.equals(next) || CsvGrammarActionType.UNDEFINED.equals(next));
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.FieldGrammarAction#isChuck(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType, byte[])
	 * @since 1.0.0
	 */
	@Override
	public boolean isChuck(CsvGrammarActionType next, byte[] keyword) {
		isChuck = isChuck && isNotEndQuote;
		isNotEndQuote = !CsvGrammarActionType.QUOTE.equals(next);
		return isChuck;
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.FieldGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) {
		if (isNotEndQuote){
			super.add(b);
		}
	}
	
}
