/**
 *  com.github.lecogiteur.csvbang.parser.UndefinedGrammarAction
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

import java.util.Arrays;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Undefined action
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class UndefinedGrammarAction implements GrammarAction<byte[]> {
	
	/**
	 * Content of undefined action
	 * @since 1.0.0
	 */
	private byte[] content;
	
	/**
	 * Current index
	 * @since 1.0.0
	 */
	private int index = 0;
	
	
	/**
	 * Constructor
	 * @param capacity initial capacity of the undefined word
	 * @since 1.0.0
	 */
	public UndefinedGrammarAction(int capacity) {
		super();
		content = new byte[capacity];
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.UNDEFINED;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(final byte b) throws CsvBangException {
		if (index >= content.length){
			byte[] copy = new byte[content.length * 2];
			System.arraycopy(content, 0, copy, 0, content.length);
			content = copy;
		}
		content[index++] = b;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#add(com.github.lecogiteur.csvbang.parser.GrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(final GrammarAction<?> word) throws CsvBangException {
		throw new CsvBangException(String.format("We can't add action [%s] to an undefined action", word.getType()));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(final CsvGrammarActionType next) {
		return next != null && !CsvGrammarActionType.NOTHING_TO_DO.equals(next);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public byte[] execute() throws CsvBangException {
		if (index == content.length){
			return content;
		}
		return index == 0?null:Arrays.copyOf(content, index);
	}

}
