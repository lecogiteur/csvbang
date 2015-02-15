/**
 *  com.github.lecogiteur.csvbang.file.FileToCloseForReadingCsvFileState
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

import java.io.FileInputStream;
import java.io.IOException;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;

/**
 * State of CSV file when we close it.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileToCloseForReadingCsvFileState implements CsvFileState {
	
	/**
	 * The CSV file
	 * @since 0.1.0
	 */
	private final CsvFileWrapper csvFile;
	
	/**
	 * True if file is closed
	 * @since 1.0.0
	 */
	private volatile boolean isClosed = false;
	
	

	/**
	 * Constructor
	 * @param csvFile The CSV file
	 * @since 1.0.0
	 */
	public FileToCloseForReadingCsvFileState(CsvFileWrapper csvFile) {
		super();
		this.csvFile = csvFile;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void open(Object customHeader) throws CsvBangException,
			CsvBangCloseException {
		throw new CsvBangCloseException(String.format("The file %s is closed. We can't open it.", csvFile.getFile().getAbsolutePath()));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#write(java.lang.Object, java.lang.String)
	 * @since 1.0.0
	 */
	@Override
	public void write(Object customHeader, String content)
			throws CsvBangException, CsvBangCloseException {
		throw new CsvBangException("You cannot write into file with a reader !");
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#read()
	 * @since 1.0.0
	 */
	@Override
	public CsvDatagram read() throws CsvBangException, CsvBangCloseException {
		throw new CsvBangCloseException(String.format("The file [%s] is being closed. We can't read file.", csvFile.getFile().getAbsolutePath()));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void close(Object customFooter) throws CsvBangException,
			CsvBangIOException {
		if (isClosed){
			//no warranties, but eliminates some thread
			return;
		}
		isClosed = true;
		
		final FileInputStream in = csvFile.getInputStream();
		try {
			in.close();
		} catch (IOException e) {
			throw new CsvBangIOException(String.format("A problem has occurred when we closed this file [%s].", 
					csvFile.getFile().getAbsolutePath()), e);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#isOpen()
	 * @since 1.0.0
	 */
	@Override
	public boolean isOpen() {
		return false;
	}

}
