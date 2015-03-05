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
public class CommentGrammarAction implements CsvGrammarAction<String> {
	
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
	 * True if it's the last comment of file and it's terminated. Warning: if file have a footer, this variable is at false.
	 * @since 1.0.0
	 */
	private boolean isTerminated = false;
	
	
	/**
	 * Content of comment
	 * @since 1.0.0
	 */
	private final StringBuilder comment;
	
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
		this.comment = new StringBuilder(initialContentSize);
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
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) throws CsvBangException {
		comment.append((char)b);
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
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isLastAction()
	 * @since 1.0.0
	 */
	@Override
	public boolean isLastAction() {
		return isTerminated;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public String execute() throws CsvBangException {
		return comment.length() > 0?comment.toString():null;
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
