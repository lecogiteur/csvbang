/**
 *  com.github.lecogiteur.csvbang.writer.CsvWriter
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
package com.github.lecogiteur.csvbang.writer;

import java.nio.channels.Channel;
import java.util.Collection;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;
import com.github.lecogiteur.csvbang.util.Comment;


/**
 * Writer of CSV file
 * @author Tony EMMA
 * @version 0.1.0
 */
public interface CsvWriter<T> extends Channel{
	
	
	/**
	 * <p>Open file in order to write. Initialize file.
	 * Create CSV file and write header.</p>
	 * <p>Not necessary to open writer. To the first, write the writer if it's not opened, will be opened.</p>
	 * @throws CsvBangException if a problem occurred during creation of file
	 * @since 0.0.1
	 */
	public void open() throws CsvBangException;
	
	
	/**
	 * Verify if writer is opened
	 * @return True if already open
	 * @since 0.0.1
	 */
	@Override
	public boolean isOpen();
	
	/**
	 * Write a line in file
	 * @param line a line
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.0.1
	 */
	public void write(final T line) throws CsvBangException;
	
	/**
	 * Write lines in file
	 * @param lines lines
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.0.1
	 */
	public void write(final T[] lines) throws CsvBangException;
	
	/**
	 * Write lines in file
	 * @param lines lines
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.0.1
	 */
	public void write(final Collection<T> lines) throws CsvBangException;
	
	/**
	 * Comment
	 * @param comment a comment
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.1.0
	 */
	public void comment(final Comment comment) throws CsvBangException;
	
	/**
	 * comment a line in file
	 * @param line a line
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.1.0
	 */
	public void comment(final T line) throws CsvBangException;
	
	/**
	 * comment lines in file
	 * @param lines lines
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.1.0
	 */
	public void comment(final T[] lines) throws CsvBangException;
	
	/**
	 * comment lines in file
	 * @param lines lines
	 * @throws CsvBangException if a problem occurred during writing file
	 * @since 0.1.0
	 */
	public void comment(final Collection<T> lines) throws CsvBangException;
	
	/**
	 * Close file and write footer
	 * @throws CsvBangIOException if a problem occurred during closing file
	 * @since 0.0.1
	 */
	@Override
	public void close() throws CsvBangIOException;
	
	/**
	 * Verify if writer is closed
	 * @return True if writer is closed
	 * @since 0.1.0
	 */
	public boolean isClose();
	
	/**
	 * Set a custom header. The method {@link Object#toString()} will be call.
	 * @param header a custom header
	 * @throws CsvBangException if the files are already open. You can't set the header after the CSV file are open.
	 * @since 0.1.0
	 */
	public void setHeader(Object header) throws CsvBangException;
	
	/**
	 * Set a custom footer. The method {@link Object#toString()} will be call.
	 * @param footer a custom footer
	 * @throws CsvBangException if the files are already open. You can't set the footer after the CSV file are open.
	 * @since 0.1.0
	 */
	public void setFooter(Object footer) throws CsvBangException;
	
	
	

}
