/**
 *  com.github.lecogiteur.csvbang.parser.EscapeQuoteGrammarAction
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
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class EscapeQuoteGrammarAction extends QuoteGrammarAction {
	
	/**
	 * Constructor
	 * @param capacity initial capacity of field
	 * @param conf configuration of CSV bean
	 * @throws CsvBangException if we can't add the quote character
	 * @since 1.0.0
	 */
	public EscapeQuoteGrammarAction(final int capacity, final CsvBangConfiguration conf) throws CsvBangException {
		super(conf, capacity);
		add((byte)conf.quote.charValue());
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.ESCAPE_CHARACTER;
	}

}