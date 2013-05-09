/**
 *  fr.csvbang.util.CsvbangUti
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
package fr.csvbang.util;

/**
 * 
 * General Utility class
 * @author Tony EMMA
 *
 */
public class CsvbangUti {

	/**
	 * Verify if a String is blank (null, empty or white space)
	 * @param s a String
	 * @return true if the String is blank
	 * 
	 * @author Tony EMMA
	 */
	public static final boolean isStringBlank(final String s){
		return !isStringNotBlank(s);
	}

	/**
	 * Verify if a String is not blank (null, empty or white space)
	 * @param s a String
	 * @return true if the String is not blank
	 * 
	 * @author Tony EMMA
	 */
	public static final boolean isStringNotBlank(final String s){
		if (s == null || "".equals(s)){
			return false;
		}
		
		final byte[] bytes = s.getBytes();
		for (final byte c:bytes){
			if (((char) c) != ' '){
				return true;
			}
		}
		return false;
	}
}
