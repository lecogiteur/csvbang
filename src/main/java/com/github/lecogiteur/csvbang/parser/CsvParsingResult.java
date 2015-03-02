/**
 *  com.github.lecogiteur.csvbang.parser.CsvParsingResult
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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the result of CSV parsing
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsvParsingResult<T> {

	/**
	 * List of CSV beans
	 * @since 1.0.0
	 */
	private final Collection<T> csvBeans;
	
	/**
	 * List of comments
	 * @since 1.0.0
	 */
	private final Collection<String> comments;

	/**
	 * Constructor
	 * @since 1.0.0
	 */
	public CsvParsingResult() {
		super();
		this.csvBeans = new ArrayList<T>(100);
		this.comments = new ArrayList<String>();
	}

	/**
	 * Get the csvBeans
	 * @return the csvBeans
	 * @since 1.0.0
	 */
	public Collection<T> getCsvBeans() {
		return csvBeans;
	}

	/**
	 * Get the comments
	 * @return the comments
	 * @since 1.0.0
	 */
	public Collection<String> getComments() {
		return comments;
	}
}
