/**
 *  fr.csvbang.writer.AsynchronousBlockingCsvWriter
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.exception.CsvBangException;
import fr.csvbang.util.CsvbangUti;

/**
 * asynchronous and blocking writer
 * @author Tony EMMA
 * @version 0.0.1
 *
 */
public class AsynchronousBlockingCsvWriter<T> extends AbstractWriter<T> {
	
	/**
	 * the buffer
	 * 
	 * @author Tony EMMA
	 */
	private BlockingQueue<CharSequence> buffer;

	/**
	 * service which manage thread.
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
	public AsynchronousBlockingCsvWriter(final File file, final CsvBangConfiguration conf, final ExecutorService serviceExecutor) {
		super(file, conf);
		this.file = file;
		buffer = new ArrayBlockingQueue<CharSequence>(conf.blockingSize);
		this.executor = serviceExecutor;
	}
	
	/**
	 * Constructor
	 * @param file path of CSV file
	 * @since 0.0.1
	 */
	public AsynchronousBlockingCsvWriter(final String file, final CsvBangConfiguration conf, final ExecutorService serviceExecutor) {
		super(file, conf);
		if (file != null){
			buffer = new ArrayBlockingQueue<CharSequence>(conf.blockingSize);
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
		for (final Object line:lines){
			final StringBuilder sLine = writeLine(line);
			if (sLine != null){
				while (!buffer.offer(sLine)){
					emptyQueue();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#close()
	 * @since 0.0.1
	 */
	public void close() throws CsvBangException {
		emptyQueue();
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
	 * Empty the buffer
	 * 
	 * @throws CsvBangException
	 * @since 0.0.1
	 */
	private void emptyQueue() throws CsvBangException{
		if (out == null){
			open();
		}
		
		final Collection<CharSequence> list = new ArrayList<CharSequence>(); 
		buffer.drainTo(list);
		
		if (list.size() > 0){

			// increment counter of task
			atomic.incrementAndGet();

			//Submit the writing to the executor
			executor.submit(new TaskBlockingCallable(list, this));
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
	//TODO Manage exception
	private class TaskBlockingCallable implements Callable<Void>{

		/**
		 * lines to insert
		 * @since 0.0.1
		 */
		private Collection<CharSequence> lines;

		/**
		 * instance of writer
		 * @since 0.0.1
		 */
		private AsynchronousBlockingCsvWriter<?> writer;
		
		/**
		 * Constructor
		 * @param lines lines to insert
		 * @param writer instance of writer
		 * @since 0.0.1
		 */
		public TaskBlockingCallable(Collection<CharSequence> lines, AsynchronousBlockingCsvWriter<?> writer){
			this.writer = writer;
			this.lines = lines;
		}
		
		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Callable#call()
		 * @since 0.0.1
		 */
		public Void call() throws Exception {
			final StringBuilder sLines = new StringBuilder(buffer.size() * defaultLineSize);
			for (final CharSequence s:lines){
				sLines.append(s);
			}
			
			if (sLines.length() > 0){
				byte[] bTab = null;
				try {
					bTab = sLines.toString().getBytes(conf.charset);
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
					throw new CsvBangException(String.format("An error has occured [%s]: %s", file.getAbsolutePath(), sLines), e);
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
