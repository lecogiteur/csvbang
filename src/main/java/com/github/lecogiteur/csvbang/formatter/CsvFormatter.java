/**
 *  com.github.lecogiteur.csvbang.formatter.CsvFormat
 * 
 *  Copyright (C) 2013  Tony EMMA
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
package com.github.lecogiteur.csvbang.formatter;

import java.util.Locale;


/**
 * Format a value. One instance of this class is created by field
 * @author Tony EMMA
 * @version 0.0.1
 */
public interface CsvFormatter {
	
	/**
	 * Initialize the formatter
	 * 
	 * @since 0.0.1
	 */
	void init();
	
	/**
	 * set pattern linked to this formatter
	 * @param pattern the pattern
	 * 
	 * @since 0.0.1
	 */
	void setPattern(final String pattern);
	
	/**
	 * Locale to set
	 * @param locale the locale
	 * 
	 * @since 0.0.1
	 */
	void setLocal(final Locale locale);
	
	/**
	 * Format a value
	 * @param o the value
	 * @param defaultIfNull String if value is null defined in {@link com.github.lecogiteur.csvbang.annotation.CsvField}
	 * @return the value formatted
	 * 
	 * @since 0.0.1
	 */
	String format(final Object o, final String defaultIfNull);
	
	/**
	 * Parse value 
	 * @param value the value from CSV file
	 * @param typeOfReturn the type of return
	 * @return the value parsed.
	 * @since 1.0.0
	 */
	Object parse(final String value, final Class<?> typeOfReturn);

}
