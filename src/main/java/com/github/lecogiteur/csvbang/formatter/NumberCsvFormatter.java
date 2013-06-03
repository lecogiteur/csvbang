/**
 *  com.github.lecogiteur.csvbang.formatter.NumberCsvFormatter
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

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Format and parse number. This class is based on {@link DecimalFormat}.
 * @author Tony EMMA
 * @version 0.0.1
 *
 */
public class NumberCsvFormatter implements CsvFormatter {
	
	/**
	 * Number format
	 * @since 0.0.1
	 */
	private DecimalFormat format;
	
	/**
	 * the pattern of number
	 * @since 0.0.1
	 */
	private String pattern;

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * @since 0.0.1
	 */
	public void init() {
		format = new DecimalFormat(pattern);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * @see java.text.DecimalFormat
	 * @since 0.0.1
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 * @since 0.0.1
	 */
	public void setLocal(Locale locale) {
		//do nothing
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 * @since 0.0.1
	 */
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		return format.format(o);
	}

}
