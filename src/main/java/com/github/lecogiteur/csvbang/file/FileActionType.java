/**
 *  com.github.lecogiteur.csvbang.file.FileActionType
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

/**
 * Enumeratin of action on file
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public enum FileActionType {

	/**
	 * Read only CSV files
	 * @since 1.0.0
	 */
	READ_ONLY,
	
	/**
	 * Write only CSV files
	 * @since 1.0.0
	 */
	WRITE_ONLY;
}