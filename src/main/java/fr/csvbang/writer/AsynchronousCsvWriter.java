/**
 *  fr.csvbang.writer.AsynchronousCsvWriter
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
package fr.csvbang.writer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.exception.CsvBangException;
import fr.csvbang.util.CsvbangUti;

/**
 * asynchronous writer
 * @author Tony EMMA
 * @version 0.0.1
 *
 */
public class AsynchronousCsvWriter<T> extends AbstractWriter<T> {
	
	/**
	 * Service which manage thread
	 * @since 0.0.1
	 */
	private ExecutorService executor;
	
	/**
	 * Number of thread
	 * @since 0.0.1
	 */
	private AtomicInteger atomic = new AtomicInteger(0);

	/**
	 * Constructor
	 * @param file CSV file
	 * @since 0.0.1
	 */
	public AsynchronousCsvWriter(final File file, final CsvBangConfiguration conf, final ExecutorService serviceExecutor) {
		super(file, conf);
		this.file = file;
		if (file != null){
			this.executor = serviceExecutor;
		}
	}
	
	/**
	 * Constructor
	 * @param file path of CSV file
	 * @since 0.0.1
	 */
	public AsynchronousCsvWriter(final String file, final CsvBangConfiguration conf, final ExecutorService serviceExecutor) {
		super(file, conf);
		if (file != null){
			this.executor = serviceExecutor;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#write(java.util.Collection)
	 * @since 0.0.1
	 */
	public void write(final Collection<T> lines) throws CsvBangException {
		if (CsvbangUti.isCollectionEmpty(lines)){
			return;
		}

		if (out == null){
			//if not open
			open();
		}
		
		//Génère le block
		final StringBuilder result = new StringBuilder(lines.size() * defaultLineSize);
		for (final Object line:lines){
			final StringBuilder sLine = writeLine(line);
			if (sLine != null){
				result.append(sLine);
			}
		}
		
		// increment counter of task
		atomic.incrementAndGet();
		
		//Submit the writing to the executor
		executor.submit(new TaskCallable(result, this));
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#close()
	 * @since 0.0.1
	 */
	public void close() throws CsvBangException {
		while (atomic.get() != 0){
			//wait end of file write
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new CsvBangException(String.format("Error has occurred on closing file (%). Some data cannot be written in file.", file.getAbsolutePath()), e);
				}				
			}
		}
		try {
			if (out != null){
				out.close();
			}
		} catch (IOException e) {
			throw new CsvBangException("An error has occured when closed file", e);
		}
	}
	
	
	
	/**
	 * Call back method when terminate to write a block of record in file
	 * @since 0.0.1
	 */
	public int callbackEndWriting(){
		return atomic.decrementAndGet();
	}
	
	/**
	 * 
	 * Task which write in file
	 * @author Tony EMMA
	 * @version 0.0.1
	 *
	 */
	//TODO manage Exception
	private class TaskCallable implements Callable<Void>{

		/**
		 * Lines to insert
		 * @since 0.0.1
		 */
		private StringBuilder result;

		/**
		 * instance of writer
		 * @since 0.0.1
		 */
		private AsynchronousCsvWriter<?> writer;
		
		/**
		 * Constructor
		 * @param s Lines to insert
		 * @param writer instance of writer
		 * @since 0.0.1
		 */
		public TaskCallable(StringBuilder s, AsynchronousCsvWriter<?> writer){
			result = s;
			this.writer = writer;
		}
		
		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Callable#call()
		 * @since 0.0.1
		 */
		public Void call() throws Exception {
			if (result.length() > 0){
				byte[] bTab = null;
				try {
					bTab = result.toString().getBytes(conf.charset);
				} catch (UnsupportedEncodingException e) {
					throw new CsvBangException(String.format("The charset for [%s] is not supported: %s", 
							file.getAbsolutePath(), conf.charset), e);
				}
				final ByteBuffer bb = ByteBuffer.allocateDirect(bTab.length);
				bb.put(bTab);
				bb.flip();
				try {
					out.getChannel().write(bb);
				} catch (IOException e) {
					throw new CsvBangException(String.format("An error has occured [%s]: %s", file.getAbsolutePath(), result), e);
				}
			}
			int dec = writer.callbackEndWriting();
			if (dec == 0){
				synchronized (writer) {
					writer.notify();				
				}
			}
			return null;
		}
		
	}

}
