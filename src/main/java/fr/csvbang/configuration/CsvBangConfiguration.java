/**
 *  fr.csvbang.configuration.CsvKuaiConfiguration
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
package fr.csvbang.configuration;

import java.util.Collection;

/**
 * General configuration
 * @author Tony EMMA
 *
 */
public class CsvBangConfiguration {
	
	public String delimiter;
	
	public String endLine;
	
	public String startLine;
	
	public String charset;
	
	public Collection<CsvFieldConfiguration> fields;
	
	public int blockingSize = 50;
	
	public String header;

	public boolean isDisplayHeader = false;
	
	public Character quote;
	
	public char escapeQuoteCharacter = '\\';
}
