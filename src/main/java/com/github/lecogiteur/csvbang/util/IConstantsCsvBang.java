/**
 *  com.github.lecogiteur.csvbang.util.IConstantsCsvBang
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
package com.github.lecogiteur.csvbang.util;

import java.nio.charset.Charset;

/**
 * Constants
 * @author Tony EMMA
 * @version 1.0.0
 *
 */
public interface IConstantsCsvBang {

	
	/**
	 * Delimiter between fields. By default {@value}.
	 * @since 0.0.1
	 */
	public static final String DEFAULT_DELIMITER = ",";
	
	/**
	 * Charset of file. By default {@value}
	 * @since 0.0.1
	 */
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	
	/**
	 * Charset of file. By default {@value}
	 * @since 0.0.1
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * String to put at the start of record. By default nothing
	 * @since 0.0.1
	 */
	public static final String DEFAULT_START_RECORD = "";
	
	/**
	 * The default end line characters. By default {@value}.
	 * @since 0.1.0
	 */
	public static final EndLineType DEFAULT_END_LINE = EndLineType.LINE_FEED;
	
	/**
	 * String to put at the end of record. By default the character {@value}
	 * @since 0.0.1
	 */
	public static final String DEFAULT_END_RECORD = "\n";
	
	/**
	 * Size of buffer in number of record. Negative value means no buffer.
	 * @since 0.0.1
	 */
	public static final int DEFAULT_BLOCKING_SIZE = -1;
	
	/**
	 * True if you want to write file asynchronously. 
	 * You can define the number of thread dedicate to write file in {@link com.github.lecogiteur.csvbang.factory.FactoryCsvWriter}. 
	 * By default the number of processor divide by 3.
	 * If you create several files in the same time, the thread will be share for each files.
	 * @since 0.0.1
	 */
	public static final boolean DEFAULT_ASYNCHRONOUS_WRITE = false;
	
	/**
	 * Display the header on the first line. By default {@value}.
	 * The header is generated with the name of field. If no name is defined for a field, 
	 * we take the property name or method name
	 * @since 0.0.1
	 */
	public static final boolean DEFAULT_HEADER = false;
	
	/**
	 * Default custom header. By default {@value}.
	 * @since 0.1.0
	 * 
	 */
	public static final String DEFAULT_CUSTOM_HEADER = "null";
	
	/**
	 * Default custom footer. By default {@value}.
	 * @since 0.1.0
	 * 
	 */
	public static final String DEFAULT_CUSTOM_FOOTER = "null";
	
	/**
	 * Put or not the end record characters on the last record. By default {@value}.
	 * @since 0.1.0
	 */
	public static final boolean DEFAULT_NO_END_RECORD = false;
	
	/**
	 * Character in order to quote value. By default, no quote defined.
	 * @since 0.0.1
	 */
	public static final String DEFAULT_QUOTE_CHARACTER = "";
	
	/**
	 * Character in order to escape the quote character. By default {@value}
	 * @since 0.0.1
	 */
	public static final char DEFAULT_QUOTE_ESCAPE_CHARACTER = '\\';
	
	/**
	 * Default file name for a CSV bean. By default {@value}
	 * @since 0.0.1
	 */
	public static final String DEFAULT_FILE_NAME = "out-%n.csv";
	
	/**
	 * Default value in order to append csv data to a file or must create a file. By default create a new file.
	 * @since 0.0.1
	 */
	public static final boolean DEFAULT_APPEND_FILE = false;
	
	/**
	 * <P>
	 * Position of field in file. The first position of the first field is 1. 
	 * It is not required that position numbers follow. The default position is -1.
	 * </p>
	 * <p>
	 * If no position is required you can set a negative position. If you want to override a {@link CsvField} annotation in sub class, you
	 * must set a negative value other than -1
	 * </p>
	 * @since 0.0.1
	 */
	public static final int DEFAULT_FIELD_POSITION = -1;
	
	/**
	 * Name of field in CSV file. If no name is defined, the name of member (field or method) is selected.
	 * By default no name is defined.
	 * @since 0.0.1
	 */
	public static final String DEFAULT_FIELD_NAME = "";
	
	/**
	 * Default value if the value is null. By default, it's an empty string
	 * @since 0.0.1
	 */
	public static final String DEFAULT_FIELD_NULL_VALUE = "";
	
	/**
	 * Delete field if the value is null. Delete field only for the current record. By default {@value}.
	 * @since 0.0.1
	 */
	public static final boolean DEFAULT_FIELD_DELETE_IF_NULL = false;
	
	/**
	 * The default character {@value} in order to comment a record or data.
	 * @since 0.1.0
	 */
	public static final char DEFAULT_COMMENT_CHARACTER = '#';
	
	/**
	 * The default max number of record in a file. By default {@value}.
	 * If the max number of record is negative, no max number of record is defined.
	 * @since 0.1.0
	 */
	public static final int DEFAULT_FILE_MAX_RECORD = -1;
	
	/**
	 * The default max size of file. By default {@value}.
	 * If the max size is negative, no max size is defined.
	 * @since 0.1.0
	 */
	public static final int DEFAULT_FILE_MAX_SIZE = -1;
	
	/**
	 * The default max number of file. By default {@value}.
	 * If the max is negative, no max is defined.
	 * @since 0.1.0
	 */
	public static final int DEFAULT_MAX_NUMBER_FILE = -1;
	

	/**
	 * Default date pattern to use in file name. By default {@value}.
	 * @since 0.1.0
	 */
	public static final String DEFAULT_FILE_NAME_DATE_PATTERN = "yyyyMMdd";
	
	/**
	 * If CsvBang must process file one by one or multiple file. This option is active only if a max number of files in pool is defined.
	 * By default, it's value is {@value}.
	 * @since 0.1.0
	 * @see com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration#maxFile
	 */
	public static final boolean DEFAULT_PROCESS_FILE_BY_FILE = true;
	
	/**
	 * If CsvBang must reading CSV of subdirectory of base directory. The base directory is defined in CsvFactoryReader
	 * By default, it's value is {@value}.
	 * @since 1.0.0
	 * @see com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration#isReadingSubFolder
	 */
	public static final boolean DEFAULT_READING_SUB_FOLDER = false;
	
	/**
	 * Size of byte array used in order to read (8ko). 
	 * @since 1.0.0
	 */
	public static final int DEFAULT_BYTE_BLOCK_SIZE = 8192;
	
	/**
	 * The name of default custom setter for CSV field. The default value is {@value}.
	 * @since 1.0.0
	 */
	public static final String DEFAULT_FIELD_SETTER = "";
	
	/**
	 * <p>The factory is used in order to set CSV bean. When CsvBang read a CSV file, it generate field with String type. 
	 * But, each field can define its type in CSV bean.</p> 
	 * <p>By default, in order to generate a new instance of field type, CsvBang searches by priority:
	 * <ul>
	 * 	<li>A custom factory</li>
	 * 	<li>Static method in class of CSV field type named "valueOf"</li>
	 * 	<li>Static method in class of CSV field type named "newInstance"</li>
	 * 	<li>constructor in class of CSV field type</li>
	 * 	<li>Static methods in class of CSV field type with custom method name</li>
	 * </ul>
	 * </p>
	 * <p>
	 * CsvBang stops to a step when it finds some static methods or constructor with one parameter. 
	 * If the type of CSV is String and there is no factory defined, CsvBang sets directly the field in CSV bean.
	 * </p>
	 * </BR>
	 * <p>
	 * "factory" permit you to define a custom factory in order to generate a new instance of type of CSV field with a String parameter. 
	 * The factory must contains some static methods or non-static methods with String parameter. In case of non-static method you must define a default constructor for your factory. 
	 * </p>
	 * @since 1.0.0
	 */
	public static final Class<?> DEFAULT_FIELD_FACTORY = Void.class;
	
	/**
	 * The name of method to use in factory. If the name is not defined, CsvBang searches automatically all methods with one parameter.
	 * @return the name method. By default, no method is defined
	 * @since 1.0.0
	 * @see {@link com.github.lecogiteur.csvbang.annotation.CsvField#factory()}
	 */
	public static final String DEFAULT_FIELD_FACTORY_METHOD_NAME = "";
}
