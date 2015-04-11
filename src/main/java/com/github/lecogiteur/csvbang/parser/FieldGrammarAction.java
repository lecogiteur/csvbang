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

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Generate a field
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FieldGrammarAction extends AbstractStringGrammarAction {
	
	/**
	 * Constructor
	 * @param conf the CsvBang configuration
	 * @param capacity initial capacity of content of field
	 * @since 1.0.0
	 */
	public FieldGrammarAction(final CsvBangConfiguration conf, final int capacity){
		super(conf, capacity);
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
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(com.github.lecogiteur.csvbang.parser.CsvGrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(CsvGrammarAction<?> word) throws CsvBangException {
		if (word != null){
			switch (word.getType()) {
			case RECORD:
				isTerminated = isTerminated || word.isLastAction();
				return false;
			case ESCAPE_CHARACTER:
			case QUOTE:
				isTerminated = isTerminated || word.isLastAction();
				addToResult(word.execute());
				endOffset = word.getEndOffset();
				return true;
			case END:
				isTerminated = isTerminated || word.isLastAction();
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
		return isTerminated || 
				(next != null && 
				!(CsvGrammarActionType.QUOTE.equals(next) 
						|| CsvGrammarActionType.ESCAPE_CHARACTER.equals(next) 
						|| CsvGrammarActionType.NOTHING_TO_DO.equals(next) 
						|| CsvGrammarActionType.UNDEFINED.equals(next)));
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isChuck(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType, byte[])
	 * @since 1.0.0
	 */
	@Override
	public boolean isChuck(final CsvGrammarActionType next, final byte[] keyword) {
		return false;
	}
	
	/**
	 * Delete the last character
	 * @return the last character
	 * @throws CsvBangException if a problem has occurred when we flush byte buffer
	 * @since 1.0.0
	 */
	public Character deleteLastChar() throws CsvBangException{
		flushByteBuffer();
		if (result.length() > 0){
			final int idx = result.length() - 1;
			final char a = result.charAt(idx);
			result.deleteCharAt(idx);
			return a;
		}
		return null;
	}
	

}
