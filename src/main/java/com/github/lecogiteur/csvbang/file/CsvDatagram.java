/**
 *  com.github.lecogiteur.csvbang.file.CsvDatagram
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
 * A part of CSV file. Used in order to read CSV file.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsvDatagram {
	
	/**
	 * Offset of datagram in CSV file
	 * @since 1.0.0
	 */
	private final long offset;
	
	/**
	 * The datagram belongs to a CSV file. It is its ID.
	 * @since 1.0.0
	 */
	private final int fileHashCode;
	
	/**
	 * Content of datagram
	 * @since 1.0.0
	 */
	private final byte[] content;

	/**
	 * Constructor
	 * @param offset Offset of datagram in CSV file
	 * @param fileHashCode The datagram belongs to a CSV file. It is its ID.
	 * @param content Content of datagram
	 * @since 1.0.0
	 */
	public CsvDatagram(long offset, int fileHashCode, byte[] content) {
		super();
		this.offset = offset;
		this.fileHashCode = fileHashCode;
		this.content = content;
	}

	/**
	 * Get the offset
	 * @return the offset
	 * @since 1.0.0
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Get the fileHashCode
	 * @return the fileHashCode
	 * @since 1.0.0
	 */
	public int getFileHashCode() {
		return fileHashCode;
	}


	/**
	 * Get the content
	 * @return the content
	 * @since 1.0.0
	 */
	public byte[] getContent() {
		return content;
	}
}
