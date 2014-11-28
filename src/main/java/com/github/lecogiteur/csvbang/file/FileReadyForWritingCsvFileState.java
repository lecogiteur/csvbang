/**
 *  com.github.lecogiteur.csvbang.file.FileReadyForWritingCsvFileState
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
/**
 * Describe the state of CSV file when it is ready for writing
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class FileReadyForWritingCsvFileState implements CsvFileState {
	
	/**
	 * The CSV file
	 * @since 0.1.0
	 */
	private CsvFileWrapper csvFile;
	
	/**
	 * The instance of channel. Because the getter of channel is synchronized
	 * @since 0.1.0
	 */
	private FileChannel channel;
	
	/**
	 * CSV configuration
	 * @since 0.1.0
	 */
	private CsvBangConfiguration conf;
	
	/**
	 * The file context
	 * @since 0.1.0
	 */
	private CsvFileContext context;
	
	/**
	 * Isn't file closed
	 * @since 0.1.0
	 */
	private boolean isNotClosed = true;
	
	
	/**
	 * Constructor
	 * @param csvFile the CSVfile
	 * @param conf configuration
	 * @param context the file context
	 * @since 0.1.0
	 */
	public FileReadyForWritingCsvFileState(final CsvFileWrapper csvFile,
			final CsvBangConfiguration conf, final CsvFileContext context) {
		super();
		this.csvFile = csvFile;
		this.conf = conf;
		this.context = context;
		channel = csvFile.getOutPutStream().getChannel();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void open(final Object customHeader) throws CsvBangException {
		//do nothing. the file is already open
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#write(java.lang.Object, java.lang.String)
	 * @since 0.1.0
	 */
	@Override
	public void write(final Object customHeader, final String content) throws CsvBangException {
		if (content.length() > 0){
			byte[] bTab = null;
			bTab = content.getBytes(conf.charset);
			final ByteBuffer bb = ByteBuffer.allocateDirect(bTab.length);
			bb.put(bTab);
			bb.flip();
			try {
				channel.write(bb);
			} catch (IOException e) {
				throw new CsvBangException(String.format("An error has occured [%s]: %s", csvFile.getFile().getAbsolutePath(), content), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void close(final Object customFooter) throws CsvBangException {
		if (!isNotClosed){
			//already closed
			return;
		}
		boolean doIt = false;
		synchronized (this) {
			if (isNotClosed){
				isNotClosed = false;
				doIt = true;
			}
		}
		if (doIt){
			final FileToCloseForWritingCsvFileState state = new FileToCloseForWritingCsvFileState(csvFile, conf);
			state.close(customFooter);
			context.setCsvFileState(state);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#isOpen()
	 * @since 0.1.0
	 */
	@Override
	public boolean isOpen() {
		return true;
	}

}
