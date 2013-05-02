/**
 *  fr.csvbang.formatter.Default
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

import java.util.Locale;

/**
 * @author Tony EMMA
 *
 */
public class Default implements CsvFormatter {

	public void init() {
		
	}

	public void setPattern(String pattern) {
		
	}

	public void setLocal(Locale locale) {
		
	}

	public String format(Object o, final String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		
		if (o instanceof String){
			if ("".equals(o)){
				return defaultIfNull;
			}
			
			return (String) o;
		}
		
		return o.toString();
	}

}
