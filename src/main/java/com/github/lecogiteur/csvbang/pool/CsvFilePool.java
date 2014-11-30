/**
 *  com.github.lecogiteur.csvbang.file.CsvFilePool
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
package com.github.lecogiteur.csvbang.pool;

import java.util.Collection;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;

/**
 * This Interface permit to implements different pool of file.
 * The pool of file can manage many files on size, number of record or another criteria.
 * The pool deliver a file to a CSV reader or writer. A pool manage only one type of CSV file. A pool is created for each type.
 * 
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
public interface CsvFilePool {

	
	/**
	 * Get all file managed by pool
	 * @return all files
	 * @since 0.1.0
	 */
	public Collection<CsvFileContext> getAllFiles();
	
	/**
	 * Get A file in pool
	 * @param nbRecord the number of record which will be added to the file
	 * @param nbByte the number of byte which will be appended to the file
	 * @return a file
	 * @throws CsvBangException if no file to return
	 * @since 0.1.0
	 */
	public CsvFileContext getFile(int nbRecord, int nbByte) throws CsvBangException;
	
	/**
	 * Set a custom header. 
	 * @param customHeader set a custom header for the file
	 * @throws CsvBangException if a problem occurred during the setting.
	 * @since 0.1.0
	 */
	public void setCustomHeader(final Object customHeader) throws CsvBangException;
	
	/**
	 * Set a custom footer.
	 * @param customFooter set a custom footer for the file
	 * @throws CsvBangException if a problem occurred during the setting.
	 * @since 0.1.0
	 */
	public void setCustomFooter(final Object customFooter) throws CsvBangException;
}
