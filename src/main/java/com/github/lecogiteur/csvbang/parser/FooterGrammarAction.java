/**
 *  com.github.lecogiteur.csvbang.parser.FooterGrammarAction
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
 * Footer parser
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FooterGrammarAction extends AbstractStringGrammarAction {
	
	/**
	 * True if the content of footer is set
	 * @since 1.0.0
	 */
	private boolean isSet = false;

	/**
	 * Constructor
	 * @param capacity initial capacity of footer
	 * @since 1.0.0
	 */
	public FooterGrammarAction(int capacity) {
		super(capacity);
		isTerminated = true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.FOOTER;
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
			case END:
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(CsvGrammarActionType next) {
		return isTerminated && isSet;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isChuck(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType, byte[])
	 * @since 1.0.0
	 */
	@Override
	public boolean isChuck(CsvGrammarActionType next, byte[] keyword)
			throws CsvBangException {
		return true;
	}
	
	/**
	 * Add this content to footer content
	 * @param content the content to add
	 * @since 1.0.0
	 */
	public void addBefore(final Object content){
		this.content.insert(0, content);
	}
	
	/**
	 * Indicates to this action, that footer content is completely set.
	 * @since 1.0.0
	 */
	public void terminateSetFooterContent(){
		isSet = false;
	}

}
