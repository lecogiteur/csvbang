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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.ByteStreamBuffer;

/**
 * Undefined action
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class UndefinedGrammarAction extends AbstractGrammarAction<ByteStreamBuffer> {
	
	/**
	 * The logger
	 * @since 1.0.0
	 */
	private static final Logger LOGGER = Logger.getLogger(UndefinedGrammarAction.class.getName());
	
	/**
	 * Content of undefined action
	 * @since 1.0.0
	 */
	private ByteStreamBuffer buffer;
	
	
	/**
	 * Constructor
	 * @param capacity initial capacity of the undefined word
	 * @since 1.0.0
	 */
	public UndefinedGrammarAction(final int capacity) {
		super();
		if (LOGGER.isLoggable(Level.FINEST)){
			LOGGER.finest(String.format("Capacity required of undefined action %s bytes.", capacity));
		}
		buffer = new ByteStreamBuffer(capacity);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.UNDEFINED;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(final byte b) throws CsvBangException {
		buffer.add(b);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(com.github.lecogiteur.csvbang.parser.CsvGrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(final CsvGrammarAction<?> word) throws CsvBangException {
		if (word != null){
			switch (word.getType()) {
			case UNDEFINED:
				final UndefinedGrammarAction undefinedAction = (UndefinedGrammarAction)word;
				final ByteStreamBuffer wordBuffer = undefinedAction.execute();
				if (wordBuffer.isEmpty()){
					isTerminated = isTerminated || word.isLastAction();
					return true;
				}
				if (word.getEndOffset() == startOffset){
					buffer.addBefore(wordBuffer);
					startOffset = word.getStartOffset();
					isTerminated = isTerminated || word.isLastAction();
					return true;
				}else if (word.getStartOffset() == endOffset){
					buffer.addAfter(wordBuffer);
					endOffset = word.getEndOffset();
					isTerminated = isTerminated || word.isLastAction();
					return true;
				}
				return false;
			case NOTHING_TO_DO:
				isTerminated = isTerminated || word.isLastAction();
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
	public boolean isActionCompleted(final CsvGrammarActionType next) {
		return isTerminated || (next != null && !CsvGrammarActionType.NOTHING_TO_DO.equals(next));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public ByteStreamBuffer execute() throws CsvBangException {
		return buffer;
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

}
