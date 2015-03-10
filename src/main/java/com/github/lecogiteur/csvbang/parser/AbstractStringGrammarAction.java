/**
 *  com.github.lecogiteur.csvbang.parser.AbstractStringGrammarAction
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
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractStringGrammarAction extends AbstractGrammarAction<String> {
	
	/**
	 * Content of action
	 * @since 1.0.0
	 */
	protected final StringBuilder content;
	
	

	/**
	 * Constructor
	 * @param capacity the initial capacity of content
	 * @since 1.0.0
	 */
	public AbstractStringGrammarAction(final int capacity) {
		super();
		this.content = new StringBuilder(capacity);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) {
		content.append((char)b);
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public String execute() {
		return content.length() == 0?null:content.toString();
	}
}
