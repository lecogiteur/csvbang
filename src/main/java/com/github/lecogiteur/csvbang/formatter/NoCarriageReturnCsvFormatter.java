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

import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Delete all carriage return of a value and replace them by a pattern (By default a space). 
 * @author Tony EMMA
 * @version 1.0.0
 *
 */
public class NoCarriageReturnCsvFormatter implements CsvFormatter{
	
	/**
	 * Pattern of carriage return
	 * @since 0.0.1
	 */
	private static final Pattern CARRIAGE_RETURN = Pattern.compile("\r?\n");
	
	/**
	 * Pattern of carriage return
	 * @since 1.0.0
	 */
	private Pattern patterReplaceCarriageReturn;
	
	/**
	 * Pattern of replacement of carriage return
	 * @since 1.0.0
	 */
	private Pattern patterReplaceCarriageReturnReplacement;
	
	/**
	 * String which replaces carriage return
	 * @since 0.0.1
	 */
	private String replaceByString = " ";
	
	/**
	 * carriage return
	 * @since 0.0.1
	 */
	private String replaceByEndLine = "\n";

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * @since 0.0.1
	 */
	@Override
	public void init() {
		if (replaceByString != null || !"".equals(replaceByString)){
			
			
			final String[] tab = replaceByString.split("-->");
			if (tab.length == 1){
				patterReplaceCarriageReturn = CARRIAGE_RETURN;
			}else{
				replaceByEndLine = tab[0];
				replaceByString = tab[1];
				patterReplaceCarriageReturn = Pattern.compile(Pattern.quote(replaceByEndLine));
			}
			
			
			if (CsvbangUti.isStringBlank(replaceByString)){
				patterReplaceCarriageReturnReplacement = Pattern.compile("(" + replaceByString + ") ");
				replaceByEndLine += "$1";
			}else{
				patterReplaceCarriageReturnReplacement = Pattern.compile(Pattern.quote(replaceByString));
			}
		}
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
		
		final Matcher matcher = patterReplaceCarriageReturn.matcher(o.toString());
		
		return matcher.replaceAll(replaceByString);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#parse(java.lang.String, java.lang.Class)
	 * @since 1.0.0
	 */
	@Override
	public Object parse(final String value, final Class<?> typeOfReturn) {
		if (value == null || patterReplaceCarriageReturnReplacement == null){
			return value;
		}
		Matcher matcher = patterReplaceCarriageReturnReplacement.matcher(value);
		

		if (CsvbangUti.isStringBlank(replaceByString)){
			String s = matcher.replaceAll(replaceByEndLine);
			matcher = patterReplaceCarriageReturnReplacement.matcher(s);
			while (matcher.find()){
				s = matcher.replaceAll(replaceByEndLine);
				matcher = patterReplaceCarriageReturnReplacement.matcher(s);
			}
		}
		return matcher.replaceAll(replaceByEndLine);
	}

	
}
