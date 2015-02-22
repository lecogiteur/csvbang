/**
 *  com.github.lecogiteur.csvbang.parser.CsvGrammarActionType
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
 * Enumeration of all action possible in CSV
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public enum CsvGrammarActionType {

	/**
	 * Create and generate new record
	 * @since 1.0.0
	 */
	RECORD,
	
	/**
	 * Create and generate new field
	 * @since 1.0.0
	 */
	FIELD,
	
	/**
	 * Action not defined
	 * @since 1.0.0
	 */
	UNDEFINED,
	
	/**
	 * Start of file
	 * @since 1.0.0
	 */
	START,
	
	/**
	 * End of file
	 * @since 1.0.0
	 */
	END,
	
	/**
	 * Footer of file
	 * @since 1.0.0
	 */
	FOOTER,
	
	/**
	 * Header of file
	 * @since 1.0.0
	 */
	HEADER,
	
	/**
	 * Comment action
	 * @since 1.0.0
	 */
	COMMENT,
	
	/**
	 * Action in order to escape content of field
	 * @since 1.0.0
	 */
	ESCAPE,
	
	/**
	 * Action escape special character
	 * @since 1.0.0
	 */
	ESCAPE_CHARACTER,
	
	/**
	 * No action defined for a word.
	 * @since 1.0.0
	 */
	NOTHING_TO_DO
}
