/**
 *  com.github.lecogiteur.csvbang.parser.HeaderGrammarAction
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
//TODO la taille du header doit être inférieur à la taille d'un datagram. Mettre une erreur lors de la création d'un reader.
/**
 * Generate header of CSV file
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class HeaderGrammarAction extends AbstractStringGrammarAction{
	
	/**
	 * CsvBang configuration
	 * @since 1.0.0
	 */
	private final CsvBangConfiguration conf;

	/**
	 * Constructor
	 * @param conf CsvBang configuration
	 * @param capacity initial capacity of content
	 * @since 1.0.0
	 */
	public HeaderGrammarAction(final CsvBangConfiguration conf, final int capacity) {
		super(conf, capacity);
		this.conf = conf;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.HEADER;
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
			case HEADER:
				isTerminated = word.isLastAction();
				endOffset = word.getEndOffset();
				addContent((AbstractStringGrammarAction)word);
				break;
			case COMMENT:
				isTerminated = word.isLastAction();
				endOffset = word.getEndOffset();
				addContent((AbstractStringGrammarAction)word);
				break;
			case END:
				isTerminated = true;
				endOffset = word.getEndOffset();
				break;
			default:
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Add result of action to this action
	 * @param action action
	 * @throws CsvBangException if a problem has occurred when we add action
	 * @since 1.0.0
	 */
	private void addContent(final AbstractStringGrammarAction action) throws CsvBangException{
		if (action.result.length() == 0){
			this.buffer.addAfter(action.buffer);
		}else{
			action.flushByteBuffer();
			addToResult(action.result);					
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(final CsvGrammarActionType next) {
		return isTerminated || !(CsvGrammarActionType.NOTHING_TO_DO.equals(next) || CsvGrammarActionType.UNDEFINED.equals(next));
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
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.AbstractStringGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public String execute() throws CsvBangException {
		final String c = super.execute();
		if (c == null){
			return conf.header;
		}else if (conf.header == null || c.endsWith(conf.header)){
			return c;
		}else{
			return c + conf.header;
		}
	}

}
