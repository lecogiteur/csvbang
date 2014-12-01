/**
 *  com.github.lecogiteur.csvbang.exception.CsvBangCloseException
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
package com.github.lecogiteur.csvbang.exception;

import java.nio.channels.ClosedChannelException;

/**
 * Exception throws when some threads are writing and we want close.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 0.1.0
 */
public class CsvBangCloseException extends ClosedChannelException {

	/**
	 * serialVersionUID
	 * @since 0.1.0
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param nbThread number of thread which are writing
	 * @since 0.1.0
	 */
	public CsvBangCloseException(int nbThread) {
		initCause(new CsvBangIOException(String.format("Error has occurred on closing file. We can't close writer. Some threads (%s thread(s)) are writing.", nbThread)));
	}
	
	/**
	 * Constructor
	 * @param message message of exception
	 * @since 1.0.0
	 */
	public CsvBangCloseException(String message){
		initCause(new CsvBangIOException(message));
	}

	
}
