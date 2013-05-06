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
	
	/**
	 * Delimiter between fields. By default {@value ,}
	 */
	public String delimiter;
	
	/**
	 * String to put at the end of record. By default the character {@value \n}
	 */
	public String endRecord;
	
	/**
	 * String to put at the start of record. By default nothing
	 */
	public String startRecord;
	
	/**
	 * Charset of file. By default {@value UTF-8}
	 */
	public String charset;
	
	/**
	 * list of fields
	 */
	public Collection<CsvFieldConfiguration> fields;
	
	/**
	 * Size of buffer in number of record. Negative value means no buffer.
	 * By default -1
	 */
	public int blockingSize = -1;
	
	/**
	 * 
	 * True if you want to write file asynchronously. 
	 * You can define the number of thread dedicate to write file in {@link fr.csvbang.factory.FactoryCsvWriter}. 
	 * By default the number of processor divide by 3.
	 * If you create several files in the same time, the thread will be share for each files. c
	 */
	public boolean isAsynchronousWrite = false;
	
	/**
	 * the header of CSV file generated.
	 * The header is generated with the name of field. If no name is defined for a field, 
	 * we take the property name or method name
	 * 
	 */
	public String header;

	/**
	 * True if we must display the name of field on first line of file.
	 */
	public boolean isDisplayHeader = false;
	
	/**
	 * Character in order to quote value of field. By default, no quote defined.
	 */
	public Character quote;
	
	/**
	 * Character in order to escape quote. By default {@value \}
	 */
	public char escapeQuoteCharacter = '\\';
}
