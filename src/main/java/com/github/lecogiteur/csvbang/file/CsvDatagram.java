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
	private long offset = 0;
	
	/**
	 * The datagram belongs to a CSV file. It is its identifiant.
	 * @since 1.0.0
	 */
	private int fileHashCode = 0;
	
	/**
	 * The number of datagram in CSV file.
	 * @since 1.0.0
	 */
	private int number = 0;
	
	/**
	 * Content of datagram
	 * @since 1.0.0
	 */
	private byte[] content;

	/**
	 * Get the offset
	 * @return the offset
	 * @since 1.0.0
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Set the offset
	 * @param offset the offset to set
	 * @since 1.0.0
	 */
	public void setOffset(long offset) {
		this.offset = offset;
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
	 * Set the fileHashCode
	 * @param fileHashCode the fileHashCode to set
	 * @since 1.0.0
	 */
	public void setFileHashCode(int fileHashCode) {
		this.fileHashCode = fileHashCode;
	}

	/**
	 * Get the number
	 * @return the number
	 * @since 1.0.0
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Set the number
	 * @param number the number to set
	 * @since 1.0.0
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Get the content
	 * @return the content
	 * @since 1.0.0
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Set the content
	 * @param content the content to set
	 * @since 1.0.0
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}
}
