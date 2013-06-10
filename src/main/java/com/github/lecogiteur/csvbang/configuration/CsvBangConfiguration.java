/**
 *  com.github.lecogiteur.csvbang.configuration.CsvKuaiConfiguration
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
package com.github.lecogiteur.csvbang.configuration;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * General configuration
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
public class CsvBangConfiguration {
	
	/**
	 * Delimiter between fields. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_DELIMITER}
	 * @since 0.0.1
	 */
	public String delimiter = IConstantsCsvBang.DEFAULT_DELIMITER;
	
	/**
	 * String to put at the end of record. By default the character {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_END_RECORD}
	 * @since 0.0.1
	 */
	public String endRecord = IConstantsCsvBang.DEFAULT_END_RECORD;
	
	/**
	 * String to put at the start of record. By default nothing
	 * @since 0.0.1
	 */
	public String startRecord = IConstantsCsvBang.DEFAULT_START_RECORD;
	
	/**
	 * Charset of file. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_CHARSET_NAME}
	 * @since 0.0.1
	 */
	public String charset = IConstantsCsvBang.DEFAULT_CHARSET_NAME;
	
	/**
	 * list of fields
	 * @since 0.0.1
	 */
	public List<CsvFieldConfiguration> fields;
	
	/**
	 * Size of buffer in number of record. Negative value means no buffer.
	 * By default -1.
	 * @since 0.0.1
	 */
	public int blockingSize = IConstantsCsvBang.DEFAULT_BLOCKING_SIZE;
	
	/**
	 * 
	 * True if you want to write file asynchronously. 
	 * You can define the number of thread dedicate to write file in {@link com.github.lecogiteur.csvbang.factory.FactoryCsvWriter}. 
	 * By default the number of processor divide by 3.
	 * If you create several files in the same time, the threads will be share for each files.
	 * 
	 * @since 0.0.1
	 */
	public boolean isAsynchronousWrite = IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE;
	
	/**
	 * the header of CSV file generated.
	 * The header is generated with the name of field. If no name is defined for a field, 
	 * we take the property name or method name
	 * @since 0.0.1
	 * 
	 */
	public String header;

	/**
	 * True if we must display the name of field on first line of file.
	 * @since 0.0.1
	 */
	public boolean isDisplayHeader = IConstantsCsvBang.DEFAULT_HEADER;
	
	/**
	 * Character in order to quote value of field. By default, no quote defined.
	 * @since 0.0.1
	 */
	public Character quote;
	
	/**
	 * Character in order to escape quote. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_QUOTE_ESCAPE_CHARACTER}
	 * @since 0.0.1
	 */
	public char escapeQuoteCharacter = IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER;
	
	/**
	 * <p>You can define a static file name. </p>
	 * <p>It is not required. You could define dynamically the filename in Factory.</p>
	 * @since 0.0.1
	 */
	public String filename = IConstantsCsvBang.DEFAULT_FILE_NAME;
	
	/**
	 * <p>True if you want to append csv data to a file (if it exist). If the file doesn't exist, it create a new file.</p>
	 * <p>False, if you already want to create a new file. By default, it's false.</p>
	 * 
	 * @since 0.0.1
	 */
	public boolean isAppendToFile = IConstantsCsvBang.DEFAULT_APPEND_FILE;
	
	/**
	 * Define the character in order to comment a line or data. Each comments will be write on a new line. 
	 * If a field value has carriage return, a comment character will be added after the carriage return.
	 * By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_COMMENT_CHARACTER}
	 * @return comment character
	 * @since 0.1.0
	 * @see Comment
	 */
	public char commentCharacter = IConstantsCsvBang.DEFAULT_COMMENT_CHARACTER;
	
	/**
	 * List of members (fields or methods) which are comment before record
	 * @since 0.1.0
	 */
	public Collection<AnnotatedElement> commentsBefore;
	
	/**
	 * List of members (fields or methods) which are comment after record
	 * @since 0.1.0
	 */
	public Collection<AnnotatedElement> commentsAfter;
	
	/**
	 * Start string for comment
	 * @since 0.1.0
	 */
	public String startComment = "";
	
	/**
	 * Initialize the configuration
	 * @since 0.1.0
	 */
	public void init(){
			
		if (endRecord.charAt(endRecord.length() - 1) != '\n'){
			startComment = "\n";
		}
		
		//generate header
		generateHeader();
	}
	
	/**
	 * Generate header of file if necessary
	 * @param conf a general configuration
	 * @since 0.1.0
	 */
	private void generateHeader(){
		if (isDisplayHeader){
			final StringBuilder h = new StringBuilder(1000).append(startRecord);
			for (final CsvFieldConfiguration field : fields){
				
				String n = field.name;
				if (!(n != null && n.length() > 0)){
					n = ((Member)field.memberBean).getName();
				}
				h.append(n).append(delimiter);
			}
			h.delete(h.length() - delimiter.length(), h.length());
			h.append(endRecord);
			header = h.toString();
		}
	}
}
