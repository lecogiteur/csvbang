/**
 *  com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration
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
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.util.Comment;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.EndLineType;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * General configuration for one CSV file.
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
	public Charset charset = IConstantsCsvBang.DEFAULT_CHARSET;
	
	/**
	 * list of fields
	 * @since 0.0.1
	 */
	public List<CsvFieldConfiguration> fields;
	
	/**
	 * <p>Size of buffer in number of record. Negative value means no buffer.</p>
	 * <p>The buffer is used in order to write by block in file. The record is generated in memory into buffer. When the buffer is full, we flush the buffer in file.</p>
	 * By default -1.
	 * @since 0.0.1
	 */
	public int blockSize = IConstantsCsvBang.DEFAULT_BLOCKING_SIZE;
	
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
	 * we take the property name or method name.
	 * If a custom header is defined, its value is append to variable header
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
	 * the footer of CSV file generated.
	 * @since 0.0.1
	 * 
	 */
	public String footer;
	
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
	 * @since 0.1.0
	 * @see Comment
	 */
	public char commentCharacter = IConstantsCsvBang.DEFAULT_COMMENT_CHARACTER;
	
	/**
	 * Define Pattern with the comment character
	 * @since 0.1.0
	 * @see Comment
	 * 
	 */
	public Pattern patternCommentCharacter = null;
	
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
	 * Put or not the end record characters on the last record. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_NO_END_RECORD}.
	 * 
	 * @since 0.0.1
	 */
	public boolean noEndRecordOnLastRecord = IConstantsCsvBang.DEFAULT_NO_END_RECORD;
	
	/**
	 * Max records by file. If the max number of record is negative, no max number of record is defined. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_MAX_RECORD}.
	 * @since 0.1.0
	 */
	public long maxRecordByFile = IConstantsCsvBang.DEFAULT_FILE_MAX_RECORD;
	
	/**
	 * The default max size of file. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_MAX_SIZE}.
	 * If the max size is negative, no max size is defined. The size is defined in byte. 
	 * @since 0.1.0
	 */
	public long maxFileSize = IConstantsCsvBang.DEFAULT_FILE_MAX_SIZE;
	
	/**
	 * The default max number of file. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_MAX_NUMBER_FILE}.
	 * If the max is negative, no max is defined.
	 * @since 0.1.0
	 */
	public long maxFile = IConstantsCsvBang.DEFAULT_MAX_NUMBER_FILE;
	
	/**
	 * <p>You can define a static file name. </p>
	 * <p>It is not required. You could define dynamically the filename in Factory.</p>
	 * @since 0.0.1
	 */
	public FileName fileName;
	
	/**
	 * If CsvBang must process file one by one or multiple file. This option is active only if a max number of files in pool is defined.
	 * By default, it's value is {@value}.
	 * @since 0.1.0
	 * @see com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration#maxFile
	 */
	public boolean isFileByFile = true;
	
	/**
	 * The pattern of date to use in file name. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_NAME_DATE_PATTERN}
	 * @since 0.1.0
	 */
	public String fileDatePattern = IConstantsCsvBang.DEFAULT_FILE_NAME_DATE_PATTERN;
	
	/**
	 * The default end line to use in file for comment, header, footer, end record, ...
	 * By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_NAME_DATE_PATTERN}
	 * @since 0.1.0
	 */
	public EndLineType defaultEndLineCharacter = IConstantsCsvBang.DEFAULT_END_LINE;
	
	/**
	 * Initialize the configuration
	 * @throws CsvBangException if a problem occurred when we create the the file name
	 * @since 0.1.0
	 */
	public void init() throws CsvBangException{
			
		if (endRecord.endsWith(defaultEndLineCharacter.toString())){
			//A comment always start on a new line
			startComment = defaultEndLineCharacter.toString();
		}
		
		patternCommentCharacter = Pattern.compile("^" + Pattern.quote(commentCharacter + "") + ".*$");
		
		//generate header
		generateHeader();
		
		//generate file name
		if (fileName == null){
			fileName = new FileName(IConstantsCsvBang.DEFAULT_FILE_NAME, fileDatePattern);
		}
		
		if (IConstantsCsvBang.DEFAULT_CUSTOM_FOOTER.equals(footer) || CsvbangUti.isStringBlank(footer)){
			footer = null;
		}
	}
	
	/**
	 * Generate header of file if necessary
	 * @param conf a general configuration
	 * @since 0.1.0
	 */
	private void generateHeader(){
		if (IConstantsCsvBang.DEFAULT_CUSTOM_HEADER.equals(header)){
			header = null;
		}
		
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
			
			if (CsvbangUti.isStringBlank(header)){
				//if there is not custom header
				header = h.toString();
			}else{
				header += h.toString();
			}			
		}
		
		if (CsvbangUti.isStringBlank(header)){
			header = null;
		}
	}
}
