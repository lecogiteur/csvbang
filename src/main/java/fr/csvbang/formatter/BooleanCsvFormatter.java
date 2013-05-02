/**
 *  fr.csvbang.formatter.BooleanCsvFormatter
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
package fr.csvbang.formatter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Tony EMMA
 *
 */
public class BooleanCsvFormatter implements CsvFormatter {
	
	private static final Map<String, String[]> simplemap = new HashMap<String, String[]>();
	
	private static final Map<Locale, String[]> LocaleMapLitteral = new HashMap<Locale, String[]>();
	
	private static final Map<Locale, String[]> LocaleMaplitteral = new HashMap<Locale, String[]>();
	
	private static final Map<Locale, String[]> LocaleMapLITTERAL = new HashMap<Locale, String[]>();
	
	private static final Map<String, Integer> revert = new HashMap<String, Integer>();
	
	static{
		simplemap.put("boolean", new String[]{"true", "false"});
		simplemap.put("Boolean", new String[]{"True", "False"});
		simplemap.put("BOOLEAN", new String[]{"TRUE", "FALSE"});
		simplemap.put("B", new String[]{"T", "F"});
		simplemap.put("b", new String[]{"t", "f"});
		simplemap.put("integer", new String[]{"1", "0"});
		simplemap.put("letterYN", new String[]{"y", "n"});
		simplemap.put("LetterYN", new String[]{"Y", "N"});
		simplemap.put("letterON", new String[]{"o", "n"});
		simplemap.put("LetterON", new String[]{"O", "N"});
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
	
	private String pattern;
	
	private Locale locale;
	
	private String[] destination;

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#init()
	 */
	public void init() {
		if (pattern.equals("litteral")){
			destination = LocaleMaplitteral.get(locale);
			if (destination == null){
				destination = LocaleMaplitteral.get(null);
			}
		}else if (pattern.equals("Litteral")){
			destination = LocaleMapLitteral.get(locale);
			if (destination == null){
				destination = LocaleMapLitteral.get(null);
			}
		}else if (pattern.equals("LITTERAL")){
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
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 */
	public void setLocal(Locale locale) {
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 */
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		
		Integer index = revert.get(o.toString().toLowerCase());
		if (index == null && o instanceof Number){
			index = ((Number) o).intValue();
			if (index.intValue() > 0){
				index = 1;
			}
		}
		
		if (index == null){
			return "unkowningBolean";
		}
		return destination[index];
	}

}
