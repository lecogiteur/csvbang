/**
 *  com.github.lecogiteur.csvbang.formatter.BooleanCsvFormatter
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Format boolean value
 * @author Tony EMMA
 * @version 0.0.1
 */
public class BooleanCsvFormatter implements CsvFormatter {
	
	/**
	 * simple boolean translation
	 * @since 0.0.1
	 */
	private static final Map<String, String[]> simplemap = new HashMap<String, String[]>();
	
	/**
	 * Literal boolean translation (lower case)
	 * @since 0.0.1
	 */
	private static final Map<Locale, String[]> LocaleMapLitteral = new HashMap<Locale, String[]>();
	
	/**
	 * Literal boolean translation 
	 * @since 0.0.1
	 */
	private static final Map<Locale, String[]> LocaleMaplitteral = new HashMap<Locale, String[]>();
	
	/**
	 * Literal boolean translation (upper case)
	 * @since 0.0.1
	 */
	private static final Map<Locale, String[]> LocaleMapLITTERAL = new HashMap<Locale, String[]>();
	
	/**
	 * Reverse translation
	 * @since 0.0.1
	 */
	private static final Map<String, Integer> revert = new HashMap<String, Integer>();
	
	static{
		simplemap.put("boolean", new String[]{"true", "false"});
		simplemap.put("Boolean", new String[]{"True", "False"});
		simplemap.put("BOOLEAN", new String[]{"TRUE", "FALSE"});
		simplemap.put("B", new String[]{"T", "F"});
		simplemap.put("b", new String[]{"t", "f"});
		simplemap.put("integer", new String[]{"1", "0"});
		simplemap.put("y/n", new String[]{"y", "n"});
		simplemap.put("Y/N", new String[]{"Y", "N"});
		simplemap.put("o/n", new String[]{"o", "n"});
		simplemap.put("O/N", new String[]{"O", "N"});
		simplemap.put("on/off", new String[]{"on", "off"});
		simplemap.put("On/Off", new String[]{"On", "Off"});
		simplemap.put("ON/OFF", new String[]{"ON", "OFF"});
		LocaleMapLitteral.put(Locale.FRANCE, new String[]{"Oui", "Non"});
		LocaleMapLitteral.put(Locale.FRENCH, new String[]{"Oui", "Non"});
		LocaleMapLitteral.put(null, new String[]{"Yes", "No"});
		LocaleMapLITTERAL.put(Locale.FRANCE, new String[]{"OUI", "NON"});
		LocaleMapLITTERAL.put(Locale.FRENCH, new String[]{"OUI", "NON"});
		LocaleMapLITTERAL.put(null, new String[]{"YES", "NO"});
		LocaleMaplitteral.put(Locale.FRANCE, new String[]{"oui", "non"});
		LocaleMaplitteral.put(Locale.FRENCH, new String[]{"oui", "non"});
		LocaleMaplitteral.put(null, new String[]{"yes", "no"});
		
		revert.put("true", 0);
		revert.put("t", 0);
		revert.put("1", 0);
		revert.put("oui", 0);
		revert.put("yes", 0);
		revert.put("o", 0);
		revert.put("y", 0);
		revert.put("on", 0);
		revert.put("false", 1);
		revert.put("f", 1);
		revert.put("0", 1);
		revert.put("non", 1);
		revert.put("no", 1);
		revert.put("n", 1);
		revert.put("off", 1);
	}
	
	/**
	 * Pattern used for translation
	 * @since 0.0.1
	 */
	private String pattern;
	
	/**
	 * Locale used for literal translation
	 * @since 0.0.1
	 */
	private Locale locale;
	
	/**
	 * the translation used
	 * @since 0.0.1
	 */
	private String[] destination;

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * @since 0.0.1
	 */
	public void init() {
		if ("litteral".equals(pattern)){
			destination = LocaleMaplitteral.get(locale);
			if (destination == null){
				destination = LocaleMaplitteral.get(null);
			}
		}else if ("Litteral".equals(pattern)){
			destination = LocaleMapLitteral.get(locale);
			if (destination == null){
				destination = LocaleMapLitteral.get(null);
			}
		}else if ("LITTERAL".equals(pattern)){
			destination = LocaleMapLITTERAL.get(locale);
			if (destination == null){
				destination = LocaleMapLITTERAL.get(null);
			}
		}else{
			destination = simplemap.get(pattern);
		}
		
		if (destination == null){
			destination = simplemap.get("boolean");
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * List of pattern:
	 * <ul>
	 * 	<li><b>boolean</b>: true or false</li>
	 * 	<li><b>Boolean</b>: True or False</li>
	 * 	<li><b>BOOLEAN</b>: TRUE or FALSE</li>
	 * 	<li><b>B</b>: T or F</li>
	 * 	<li><b>b</b>: t or f</li>
	 * 	<li><b>integer</b>: 1 or 0</li>
	 * 	<li><b>y/n</b>: y or n</li>
	 * 	<li><b>Y/N</b>: Y or N</li>
	 * 	<li><b>o/n</b>: o or n</li>
	 * 	<li><b>O/N</b>: O or N</li>
	 * 	<li><b>on/off</b>: on or off</li>
	 * 	<li><b>On/Off</b>: On or Off</li>
	 * 	<li><b>ON/OFF</b>: ON or OFF</li>
	 * 	<li><b>litteral</b>: yes or no in function locale</li>
	 * 	<li><b>Litteral</b>: Yes or No in function locale</li>
	 * 	<li><b>LITTERAL</b>: YES or NO in function locale</li>
	 * </ul>
	 * <p>
	 * 	The default pattern is "boolean"
	 * </p>
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
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
		this.locale = locale;
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
		
		Integer index = revert.get(o.toString().toLowerCase());
		if (index == null && o instanceof Number && ((Number) o).intValue() != 0){
			index = 0;
		}
		
		if (index == null){
			return "unkowningBoolean";
		}
		return destination[index];
	}

}
