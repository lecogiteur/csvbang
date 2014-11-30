/**
 *  com.github.lecogiteur.csvbang.file.CsvFileState
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
package com.github.lecogiteur.csvbang.file;


import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;

/**
 * Represents the state of a file
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public interface CsvFileState{
	
	/**
	 * Open a file
	 * @param customHeader the custom header
	 * @throws CsvBangException if a problem has occurred during the opening
	 * @since 0.1.0
	 */
	public void open(final Object customHeader) throws CsvBangException;
	
	/**
	 * Write content into the file
	 * @param customHeader the custom header
	 * @param content the content to write
	 * @throws CsvBangException if a problem has occurred during the writing
	 * @since 0.1.0
	 */
	public void write(final Object customHeader, final String content) throws CsvBangException;
	
	/**
	 * Close a file
	 * @param customFooter the custom footer
	 * @throws CsvBangException if a problem has occurred during the closing
	 * @throws CsvBangIOException if a problem has occurred during the closing
	 * @since 0.1.0
	 */
	public void close(final Object customFooter) throws CsvBangException, CsvBangIOException;
	
	/**
	 * Verify if the file is already opened
	 * @return True if the file is opened
	 * @since 0.1.0
	 */
	public boolean isOpen();

}
