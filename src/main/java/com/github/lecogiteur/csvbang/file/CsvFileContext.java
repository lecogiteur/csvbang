/**
 *  com.github.lecogiteur.csvbang.file.CsvFileContext
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

import java.nio.channels.Channel;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;

/**
 * Csv file context. Contains the current state of file (open, processing, close). 
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class CsvFileContext implements Channel{
	
	/**
	 * The current state of file
	 * @since 0.1.0
	 */
	private volatile CsvFileState fileState;
	
	/**
	 * Custom header
	 * @since 0.1.0
	 */
	private final Object customHeader;
	
	/**
	 * Custom footer
	 * @since 0.1.0
	 */
	private final Object customFooter;
	
	
	/**
	 * Constructor
	 * @param configuration configuration of CSV file
	 * @param file the physical CSV file
	 * @param customHeader the custom header
	 * @param customFooter the custom footer
	 * @since 0.1.0
	 */
	public CsvFileContext(final CsvBangConfiguration configuration, final CsvFileWrapper file, final Object customHeader, final Object customFooter){
		fileState = new FileToOpenForWritingCsvFileState(configuration, file, this);
		this.customHeader = customHeader;
		this.customFooter = customFooter;
	}
	
	/**
	 * Open the CSV file.
	 * @throws CsvBangException if problem occurred during opening
	 * @throws CsvBangCloseException if the file is closed
	 * @since 0.1.0
	 */
	public void open() throws CsvBangException, CsvBangCloseException{
		fileState.open(customHeader);
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.nio.channels.Channel#isOpen()
	 * @since 0.1.0
	 */
	@Override
	public boolean isOpen(){
		return fileState.isOpen();
	}
	
	/**
	 * Write some content in csv file
	 * @param content content to write
	 * @throws CsvBangException if problem occurred during writing
	 * @throws CsvBangCloseException if the file is closed
	 * @since 0.1.0
	 */
	public void write(final String content) throws CsvBangException, CsvBangCloseException{
		fileState.write(customHeader, content);
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.nio.channels.Channel#close()
	 * @since 0.1.0
	 */
	@Override
	public void close() throws CsvBangIOException{
		try{
			fileState.close(customFooter);
		}catch(Exception e){
			throw new CsvBangIOException(e);
		}
	}

	/**
	 * Set the state of file
	 * @param state the state of file
	 * @since 0.1.0
	 */
	void setCsvFileState(final CsvFileState state){
		fileState = state;
	}
}
