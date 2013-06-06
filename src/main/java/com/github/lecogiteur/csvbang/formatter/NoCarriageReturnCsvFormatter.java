/**
 *  com.github.lecogiteur.csvbang.formatter.NoCarriageReturnCsvFormatter
 * 
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Delete all carriage return of a value and replace them by a pattern (By default a space). 
 * @author Tony EMMA
 * @version 0.0.4
 *
 */
public class NoCarriageReturnCsvFormatter implements CsvFormatter{
	
	/**
	 * Pattern of carriage return
	 * @since 0.0.1
	 */
	private static final Pattern CARRIAGE_RETURN = Pattern.compile("\r?\n");
	
	/**
	 * String which replaces carriage return
	 * @since 0.0.1
	 */
	private String replaceByString = " ";

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * @since 0.0.1
	 */
	@Override
	public void init() {
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * @since 0.0.1
	 */
	@Override
	public void setPattern(String pattern) {
		if (pattern != null){
			replaceByString = pattern;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 * @since 0.0.1
	 */
	@Override
	public void setLocal(Locale locale) {
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 * @since 0.0.1
	 */
	@Override
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		
		final Matcher matcher = CARRIAGE_RETURN.matcher(o.toString());
		
		return matcher.replaceAll(replaceByString);
	}

	
}
