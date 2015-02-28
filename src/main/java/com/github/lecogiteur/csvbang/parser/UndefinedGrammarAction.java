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
public class UndefinedGrammarAction implements CsvGrammarAction<byte[]> {
	
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
	 * Is terminate
	 * @since 1.0.0
	 */
	private boolean isTerminate = false;
	
	
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
		if (index >= content.length){
			byte[] copy = new byte[content.length>0?content.length * 2:100];
			System.arraycopy(content, 0, copy, 0, content.length);
			content = copy;
		}
		content[index++] = b;
		ismycontent();
	}
	
	//TODO a supprimer
	private boolean ismycontent(){
		byte[] toto = new byte[]{10, 54, 53, 46, 55, 56, 54};
		if (toto.length > index){
			return false;
		}
		for (int i=0; i<toto.length; i++){
			if (content[i] != toto[i]){
				return false;
			}
		}
		return true;
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
				final byte[] table = undefinedAction.execute();
				if (table == null || table.length == 0){
					isTerminate = isTerminate || word.isLastAction();
					return true;
				}
				if (word.getEndOffset() == startOffset){
					byte[] tmp = new byte[table.length + content.length];
					System.arraycopy(table, 0, tmp, 0, table.length);
					System.arraycopy(content, 0, tmp, table.length, content.length);
					index += table.length;
					content = tmp;
					startOffset = word.getStartOffset();
					isTerminate = isTerminate || word.isLastAction();
					return true;
				}else if (word.getStartOffset() == endOffset){
					byte[] tmp = new byte[table.length + content.length];
					System.arraycopy(content, 0, tmp, 0, index);
					System.arraycopy(table, 0, tmp, index, table.length);
					index += table.length;
					content = tmp;
					endOffset = word.getEndOffset();
					isTerminate = isTerminate || word.isLastAction();
					return true;
				}
				return false;
			case NOTHING_TO_DO:
				isTerminate = isTerminate || word.isLastAction();
				return true;
			case END:
				isTerminate = isTerminate || word.isLastAction();
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
		return isTerminate || (next != null && !CsvGrammarActionType.NOTHING_TO_DO.equals(next));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public byte[] execute() throws CsvBangException {
		if (index == content.length){
			return content;
		}
		return index == 0?null:Arrays.copyOf(content, index);
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
		return isTerminate;
	}

}
