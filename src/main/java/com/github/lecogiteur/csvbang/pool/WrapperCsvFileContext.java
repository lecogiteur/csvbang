/**
 *  com.github.lecogiteur.csvbang.pool.WappredCsvFileContext
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

import com.github.lecogiteur.csvbang.file.CsvFileContext;

/**
 * Wrapper of file used in implementation of somepool
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class WrapperCsvFileContext {
	
	/**
	 * The file context
	 * @since 0.1.0
	 */
	CsvFileContext file;
	
	/**
	 * Number of records in file
	 * @since 0.1.0
	 */
	long nbRecord = 0;
	
	/**
	 * Size of file
	 * @since 0.1.0
	 */
	long nbByte = 0;
}
