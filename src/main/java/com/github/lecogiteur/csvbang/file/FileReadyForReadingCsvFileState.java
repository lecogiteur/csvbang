/**
 *  com.github.lecogiteur.csvbang.file.FileReadyForReadingCsvFileState
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;

/**
 * State of file when we can read it
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileReadyForReadingCsvFileState implements CsvFileState {
	
	/**
	 * The CSV file
	 * @since 0.1.0
	 */
	private final CsvFileWrapper csvFile;
	
	/**
	 * The instance of channel. Because the getter of channel is synchronized
	 * @since 0.1.0
	 */
	private final FileChannel channel;
	
	/**
	 * The file context
	 * @since 0.1.0
	 */
	private final CsvFileContext context;
	
	/**
	 * Size of byte array used to read file
	 * @since 1.0.0
	 * @see {@link com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_BYTE_BLOCK_SIZE}
	 */
	private final int sizeBlock;
	
	/**
	 * The current offset in file
	 * @since 1.0.0
	 */
	private final AtomicLong currentOffset = new AtomicLong(0);
	
	/**
	 * Id of file
	 * @since 1.0.0
	 */
	private final int fileHashCode;
	
	/**
	 * Number of byte to read in file
	 * @since 1.0.0
	 */
	private final long totalBytes;
	
	/**
	 * Isn't file closed
	 * @since 0.1.0
	 */
	private volatile boolean isNotClosed = true;
	
	/**
	 * Number of thread writing in file
	 * @since 0.1.0
	 */
	private final AtomicInteger workers = new AtomicInteger(0);
	
	

	/**
	 * Constructor
	 * @param csvFile The CSV file
	 * @param context The file context
	 * @param sizeBlock Size of byte array used to read file
	 * @param fileHashCode Id of file
	 * @param totalBytes Number of byte to read in file
	 * @since 1.0.0
	 */
	public FileReadyForReadingCsvFileState(CsvFileWrapper csvFile, CsvFileContext context, int sizeBlock,
			int fileHashCode, long totalBytes) {
		super();
		this.csvFile = csvFile;
		this.context = context;
		this.sizeBlock = sizeBlock;
		this.fileHashCode = fileHashCode;
		this.totalBytes = totalBytes;
		this.channel = csvFile.getInputStream().getChannel();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void open(Object customHeader) throws CsvBangException,
			CsvBangCloseException {
		// nothing to do the file is already open
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
		if (!isNotClosed){
			throw new CsvBangCloseException(String.format("The file [%s] is being closed. We can't read file.", csvFile.getFile().getAbsolutePath()));
		}
		workers.incrementAndGet();
		//get current offset
		final long offset = currentOffset.getAndAdd(sizeBlock);
		
		if (offset >= totalBytes){
			//We have read all file, we can close it
			try {
				close(null);
			} catch (CsvBangIOException e) {
				throw new CsvBangCloseException(String.format("A problem has occurred when we closed the file [%s].", csvFile.getFile().getAbsolutePath()), e);
			}finally{
				workers.decrementAndGet();
			}
			return null;
		}
		
		//calculate size of allocation with length of file
		int size = sizeBlock;
		if (offset + sizeBlock > totalBytes){
			size = (int) (totalBytes - offset); //in fact, size < sizeBlock, so it's a int
		}
		final ByteBuffer buffer = ByteBuffer.allocate(size);
		
		try {
			channel.read(buffer, offset);
		} catch (CsvBangIOException e) {
			throw new CsvBangCloseException(String.format("The file [%s] is being closed. We can't read file.", csvFile.getFile().getAbsolutePath()));
		} catch (IOException e) {
			throw new CsvBangException(String.format("A problem has occurred when we read the file [%s].", csvFile.getFile().getAbsolutePath()), e);
		}finally{
			workers.decrementAndGet();
		}
		
		return new CsvDatagram(offset, fileHashCode, buffer.array());
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void close(Object customFooter) throws CsvBangException,
			CsvBangIOException {
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
			int i = workers.get();
			while(i > 0){
				//wait that all writings terminate
				i = workers.get();
			}
			try {
				channel.close();
			} catch (IOException e) {
				new CsvBangIOException(String.format("Can't close file [%s].", csvFile.getFile().getAbsolutePath()), e);
			}
			final FileToCloseForReadingCsvFileState state = new FileToCloseForReadingCsvFileState(csvFile);
			state.close(customFooter);
			context.setCsvFileState(state);
		}
		
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#isOpen()
	 * @since 1.0.0
	 */
	@Override
	public boolean isOpen() {
		return isNotClosed;
	}

}
