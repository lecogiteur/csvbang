/**
 *  com.github.lecogiteur.csvbang.file.CsvFileWrapper
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

import java.io.File;
import java.io.FileOutputStream;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * A CSV file
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
public class CsvFileWrapper { 
	
	/**
	 * File Writer
	 * @since 0.1.0
	 */
	private FileOutputStream out;
	
	/**
	 * Csv File
	 * @since 0.1.0
	 */
	private File file;
	
	/**
	 * Name of file
	 * @since 0.1.0
	 */
	private final String fileName;
	
	
	/**
	 * Constructor
	 * @param file the file
	 * @since 0.1.0
	 */
	public CsvFileWrapper(final String fileName) {
		super();
		this.fileName = fileName;
		this.file = new File(fileName);
	}
	
	/**
	 * Set the out put stream of file
	 * @param out the out put stream
	 * @throws CsvBangException if the file is null or if we can't modify it
	 * @since 0.1.0
	 */
	public void setOutputStream(final FileOutputStream out) throws CsvBangException{
		this.out = out;
	}


	/**
	 * get the ouput stream
	 * @param conf Configuration of CSV bean
	 * @return the ouput stream
	 * @exception CsvBangException if the file is null or if we can't modify it
	 * @since 0.1.0
	 */
	public FileOutputStream getOutPutStream(){	
		return out;
	}


	/**
	 * get the csv file
	 * @return the csv file
	 * @since 0.1.0
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Get the fileName
	 * @return the fileName
	 * @since 0.1.0
	 */
	public String getFileName() {
		return fileName;
	}
}
