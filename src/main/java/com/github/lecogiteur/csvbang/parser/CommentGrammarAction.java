/**
 *  com.github.lecogiteur.csvbang.parser.CommentGrammarAction
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
 * Manage comment action
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommentGrammarAction extends AbstractStringGrammarAction {
	
	/**
	 * Csvbang configuration of CSV bean
	 * @since 1.0.0
	 */
	private final CsvBangConfiguration conf;

	/**
	 * Constructor
	 * @param conf Csvbang configuration of CSV bean
	 * @param initialContentSize initial content size for comment
	 * @since 1.0.0
	 */
	public CommentGrammarAction(final CsvBangConfiguration conf, final int initialContentSize) {
		super(conf, initialContentSize);
		this.conf = conf;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.COMMENT;
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
		return isTerminated || CsvGrammarActionType.RECORD.equals(next) || CsvGrammarActionType.END.equals(next);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isChuck(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType, byte[])
	 * @since 1.0.0
	 */
	@Override
	public boolean isChuck(final CsvGrammarActionType next, final byte[] keyword) {
		if (CsvGrammarActionType.COMMENT.equals(next)){
			return false;
		}
		if (!CsvGrammarActionType.RECORD.equals(next)){
			return true;
		}
		final byte[] endLine = conf.defaultEndLineCharacter.toBytes(conf.charset);
		if (endLine.length > keyword.length){
			return true;
		}
		for (int i=0; i<endLine.length; i++){
			if (endLine[i] != keyword[i]){
				return true;
			}
		}
		return false;
	}
}
