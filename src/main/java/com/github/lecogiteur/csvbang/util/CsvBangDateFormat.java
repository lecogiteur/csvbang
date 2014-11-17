/**
 *  com.github.lecogiteur.csvbang.util.CsvBangDateFormat
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
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
package com.github.lecogiteur.csvbang.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Synchronized SimpleDateFormat
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class CsvBangDateFormat extends ThreadLocal<SimpleDateFormat> {
	
	/**
	 * Pattern of Date
	 * @since 0.1.0
	 */
	private final String pattern;
	
	/**
	 * Local of date
	 * @since 0.1.0
	 */
	private Locale locale;

	/**
	 * Constructor
	 * 
	 * @param pattern pattern of date
	 * @since 0.1.0
	 */
	public CsvBangDateFormat(final String pattern) {
		super();
		this.pattern = pattern;
	}
	
	

	/**
	 * Constructor
	 * @param pattern pattern of date
	 * @param locale the locale of date
	 * @since 0.1.0
	 */
	public CsvBangDateFormat(String pattern, Locale locale) {
		super();
		this.pattern = pattern;
		this.locale = locale;
	}



	/**
	 * {@inheritDoc}
	 * @see java.lang.ThreadLocal#initialValue()
	 * @since 0.1.0
	 */
	@Override
	protected SimpleDateFormat initialValue() {
		if (locale != null){
			return new SimpleDateFormat(pattern, locale);
		}
		return new SimpleDateFormat(pattern);
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.ThreadLocal#get()
	 * @since 0.1.0
	 */
	@Override
	public SimpleDateFormat get() {
		return super.get();
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.ThreadLocal#set(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void set(SimpleDateFormat value) {
		super.set(value);
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.ThreadLocal#remove()
	 * @since 0.1.0
	 */
	@Override
	public void remove() {
		super.remove();
	}
}
