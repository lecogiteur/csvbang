/**
 *  com.github.lecogiteur.csvbang.file.FileToOpenCsvFileState
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

import java.util.concurrent.atomic.AtomicBoolean;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;

/**
 * This state delegates the state of file in function of the file action.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileToOpenCsvFileState implements CsvFileState {
	
	/**
	 * The file context
	 * @since 0.1.0
	 */
	private final CsvFileContext context;
	
	/**
	 * Effective state of file
	 * @since 0.1.0
	 */
	private final CsvFileState internState;
	
	/**
	 * Verify if the state of file is initialized in context.
	 * @since 1.0.0
	 */
	private final AtomicBoolean isInit = new AtomicBoolean(false);
	

	/**
	 * Constructor
	 * @param conf The configuration
	 * @param csvFile The CSV file
	 * @param context The file context
	 * @since 1.0.0
	 */
	public FileToOpenCsvFileState(final CsvBangConfiguration conf,
			final CsvFileWrapper csvFile, final CsvFileContext context) {
		super();
		this.context = context;
		
		switch(csvFile.getAction()){
		case READ_ONLY:
			this.internState = new FileToOpenForReadingCsvFileState(csvFile, context);
			break;
		case WRITE_ONLY:
			this.internState = new FileToOpenForWritingCsvFileState(conf, csvFile, context);
			break;
		default:
			//not possible but...
			this.internState = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void open(final Object customHeader) throws CsvBangException,
			CsvBangCloseException {
		if (!isInit.getAndSet(true)){
			context.setCsvFileState(internState);
		}
		context.open();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#write(java.lang.Object, java.lang.String)
	 * @since 1.0.0
	 */
	@Override
	public void write(final Object customHeader, final String content)
			throws CsvBangException, CsvBangCloseException {
		if (!isInit.getAndSet(true)){
			context.setCsvFileState(internState);
		}
		context.write(content);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#read(int)
	 * @since 1.0.0
	 */
	@Override
	public CsvDatagram read(final int nbByteToRead) throws CsvBangException, CsvBangCloseException  {
		if (!isInit.getAndSet(true)){
			context.setCsvFileState(internState);
		}
		return context.read(nbByteToRead);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void close(final Object customFooter) throws CsvBangException,
			CsvBangIOException {
		if (!isInit.getAndSet(true)){
			context.setCsvFileState(internState);
		}
		context.close();
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
