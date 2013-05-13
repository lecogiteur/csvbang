/**
 *  fr.csvbang.exception.CsvKuaiException
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
package fr.csvbang.exception;

/**
 * Exception from Csv file.
 * @author Tony EMMA
 * @version 0.0.1
 */
public class CsvBangException extends Exception {

	/**
	 * Serial version UID
	 * @since 0.0.1
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @since 0.0.1
	 */
	public CsvBangException() {
		super();
	}

	/**
	 * Constructor
	 * @param message message of exception
	 * @param cause cause of exception
	 * @since 0.0.1
	 */
	public CsvBangException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message message of exception
	 * @since 0.0.1
	 */
	public CsvBangException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param cause cause of exception
	 * @since 0.0.1
	 */
	public CsvBangException(Throwable cause) {
		super(cause);
	}
	
	
	

}
