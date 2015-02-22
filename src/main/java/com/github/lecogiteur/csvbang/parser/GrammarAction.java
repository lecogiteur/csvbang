/**
 *  com.github.lecogiteur.csvbang.parser.GrammarAction
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
 * Defined an action in CSV grammar. We can create new record, field or comment, ...
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public interface GrammarAction<T> {
	
	/**
	 * Get type of action to do.
	 * @return the type of action
	 * @since 1.0.0
	 */
	public CsvGrammarActionType getType();
	
	/**
	 * Add a byte
	 * @param b a byte
	 * @throws CsvBangException if a problem occurred when we add a byte for action
	 * @since 1.0.0
	 */
	public void add(final byte b) throws CsvBangException;
	
	/**
	 * Add another word to action. This word is generated by the action.
	 * @param word an action which will be generated a word.
	 * @return True if word is added to the current action. Else the word is not added because, this action is complete.
	 * @throws CsvBangException if a problem occurred when we add a word for action
	 * @since 1.0.0
	 */
	public boolean add(final GrammarAction<?> word) throws CsvBangException;
	
	/**
	 * Verify if the action is completed
	 * @param next the next action
	 * @return True if the action is completed and ready for execution
	 * @since 1.0.0
	 */
	public boolean isActionCompleted(final CsvGrammarActionType next);
	
	/**
	 * Execute the action
	 * @return the result of action. Could be a word or another thing
	 * @throws CsvBangException if a problem occurred when we execute action
	 * @since 1.0.0
	 */
	public T execute() throws CsvBangException;

}
