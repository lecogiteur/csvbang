/**
 *  com.github.lecogiteur.csvbang.reader.CsvReader
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
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
package com.github.lecogiteur.csvbang.reader;

import java.io.IOException;
import java.nio.channels.Channel;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Read a CSV file. The method (asynchronous or not) uses depends of the configuration of your CSV bean.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CsvReader<T> extends Channel{
	
	/**
	 * <p>Open file in order to read. Initialize file and reading.</p>
	 * <p>Not necessary to open the reader. To the first read, the reader if it's not opened, will be opened.</p>
	 * @throws CsvBangException if a problem occurred during reading of file
	 * @throws CsvBangCloseException if the reader is closed
	 * @since 1.0.0
	 */
	public void open() throws CsvBangException, CsvBangCloseException;
	
	/**
	 * Verify if reader is opened
	 * @return True if already open
	 * @since 1.0.0
	 */
	@Override
	public boolean isOpen();
	
	/**
	 * Read a line
	 * @return a line
	 * @throws CsvBangException if a problem occurred during reading file
	 * @throws CsvBangCloseException if the reader is closed
	 * @since 1.0.0
	 */
	public T read() throws CsvBangException, CsvBangCloseException;
	
	/**
	 * {@inheritDoc}
	 * @see java.nio.channels.Channel#close()
	 * @since 1.0.0
	 */
	@Override
	public void close() throws IOException;
	
	/**
	 * Verify if reader is closed
	 * @return True if reader is closed
	 * @since 1.0.0
	 */
	public boolean isClose();
	
	/**
	 * Get header of file
	 * @return the header
	 * @throws CsvBangException if problem occurred during we retrieve header
	 * @throws CsvBangCloseException if reader is closed
	 * @since 1.0.0
	 */
	public String getHeader() throws CsvBangException, CsvBangCloseException;
	
	/**
	 * Get footer of file
	 * @return the footer
	 * @throws CsvBangException if problem occurred during we retrieve header
	 * @throws CsvBangCloseException if reader is closed
	 * @since 1.0.0
	 */
	public String getFooter() throws CsvBangException, CsvBangCloseException;

}
