/**
 *  com.github.lecogiteur.csvbang.annotation.CsvFile
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
package com.github.lecogiteur.csvbang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;

import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;

/**
 * Defines options about writing, reading and file
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvFile {
	
	/**
	 * Size of buffer in number of record. Negative value means no buffer. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_BLOCKING_SIZE}
	 * @return the size in number of record
	 * @since 0.1.0
	 */
	int blocksize() default IConstantsCsvBang.DEFAULT_BLOCKING_SIZE;
	
	/**
	 * True if you want to write file asynchronously. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_ASYNCHRONOUS_WRITE}.
	 * <p>You can define the number of thread dedicate to write file in {@link com.github.lecogiteur.csvbang.factory.FactoryCsvWriter}. 
	 * By default the number of thread is the number of processor divide by 3.
	 * If you create several files in the same time, the thread will be share for each files.</p>
	 * @return True if you want to write asynchronously 
	 * @since 0.1.0
	 */
	boolean asynchronousWriter() default IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE;
	
	/**
	 * <p>You can define a static file name. </p>
	 * <p>It is not required. You could define dynamically the filename in Factory.</p>
	 * <p>It is possible to mix configuration. For example you can define dynamically 
	 * the directory with the factory and a static file name with this annotation.</p>
	 * <p>If a file name is defined by factory, it overrides the static file name created by the annotation</p>
	 * <p>By default the static file name is {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_NAME}.</p>
	 * <p>
	 * You can define a pattern for the static file:
	 * <ul>
	 * 	<li>%n : the number file</li>
	 * 	<li>%d : date format</li>
	 * </ul>
	 * Example: <i>myCsvFile-%n.csv</i>
	 * </p>
	 * </p>
	 * @return the file name
	 * @see com.github.lecogiteur.csvbang.factory.FactoryCsvWriter
	 * @see datePattern of CsvFile annotation
	 * @since 0.1.0
	 */
	String fileName() default IConstantsCsvBang.DEFAULT_FILE_NAME;
	
	/**
	 * Pattern of date to use for file name when you add "%d" in the file name. This pattern is based on {@link SimpleDateFormat} pattern.
	 * By Default, it's {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_NAME_DATE_PATTERN}.
	 * @return the pattern of date
	 * @since 0.1.0
	 */
	String datePattern() default IConstantsCsvBang.DEFAULT_FILE_NAME_DATE_PATTERN;
	
	/**
	 * <p>True if you want to append csv data to a file (if it exist). If the file doesn't exist, it create a new file.</p>
	 * <p>False, if you already want to create a new file. By default, it's {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_APPEND_FILE}.</p>
	 * @return True if you want to append csv data to a file.
	 * @since 0.0.1
	 */
	boolean append() default IConstantsCsvBang.DEFAULT_APPEND_FILE;
	
	/**
	 * <p>Max records by file. If the max number of record is negative, no max number of record is defined. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_MAX_RECORD}.</p>
	 * <p>There is no warranty that all full files in pool have the maximum records defined. But we sure that no file exceed the maximum of records.</p>
	 * @return the maximum of records in a file.
	 * @since 0.1.0
	 */
	int maxRecordByFile() default IConstantsCsvBang.DEFAULT_FILE_MAX_RECORD;
	
	/**
	 * <p>The max size of file. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FILE_MAX_SIZE}.</p>
	 * <p>If the max size is negative, no max size is defined. The size is defined in byte. </p>
	 * <p>There is no warranty that all full files in pool have the maximum size defined. But we sure that no file exceed the maximum size.</p>
	 * @return the number of bytes about a CSV file
	 * @since 0.1.0
	 */
	int maxFileSize() default IConstantsCsvBang.DEFAULT_FILE_MAX_SIZE;
	
	/**
	 * <p>The max number of file in pool file.</p>
	 * <p>CsvBang can manage a pool of file and can write or read simultaneous files.</p>
	 * <p>By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_MAX_NUMBER_FILE}.</p>
	 * <p>If the max is negative, no max is defined.</p>
	 * @return the maximum of file which can be written.
	 * @since 0.1.0
	 */
	int maxFileNumber() default IConstantsCsvBang.DEFAULT_MAX_NUMBER_FILE;
	
	/**
	 * <p>When you set a maximum of file in pool, the maximum of records in file or the maximum size of file, you can define if you process file by file or if you process files simultaneous.</p>
	 * <p>This option is deactivated if the maximum of files in pool is lesser than 1. You must have at least two files in pool in order to use this option.</p>
	 * <p>By default, the value is {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_PROCESS_FILE_BY_FILE}</p>
	 * @return True if you want to process file by file
	 * @see com.github.lecogiteur.csvbang.annotation.CsvFile#maxFileNumber()
	 * @see com.github.lecogiteur.csvbang.annotation.CsvFile#maxFileSize()
	 * @see com.github.lecogiteur.csvbang.annotation.CsvFile#maxRecordByFile()
	 * @since 0.1.0
	 */
	boolean fileByFile() default IConstantsCsvBang.DEFAULT_PROCESS_FILE_BY_FILE;
	
	/**
	 * True if you want that reader search all CSV files in sub-directories. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_PROCESS_FILE_BY_FILE}
	 * @return True if you want that reader search all CSV files in sub-directories.
	 * @since 1.0.0
	 */
	boolean readSubFolders() default IConstantsCsvBang.DEFAULT_READING_SUB_FOLDER;

}
