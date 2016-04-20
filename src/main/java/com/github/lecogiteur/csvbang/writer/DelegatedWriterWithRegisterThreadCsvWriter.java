/**
 *  com.github.lecogiteur.csvbang.writer.DelegatedWriterWithRegisterThreadCsvWriter
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
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
package com.github.lecogiteur.csvbang.writer;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.Comment;

/**
 * delegate to another writer operations. It manage all thread which write or open Csv Writer and close the Writer when all thread are terminated
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class DelegatedWriterWithRegisterThreadCsvWriter<T> implements CsvWriter<T> {
	
	/**
	 * Csv Writer to delegate
	 * @since 1.0.0
	 */
	private final CsvWriter<T> writer;
	
	/**
	 * Register thread which open and write in order to verify if thread are closed before closing file.
	 * @since 1.0.0
	 */
	protected final ConcurrentSkipListSet<Thread> registeredThreads = new ConcurrentSkipListSet<Thread>(new Comparator<Thread>() {
		@Override
		public int compare(Thread o1, Thread o2) {
			return o1.equals(o2)?0:1;
		}
	});
	
	

	/**
	 * Constructor
	 * @param writer Csv Writer to delegate
	 * @since 1.0.0
	 */
	public DelegatedWriterWithRegisterThreadCsvWriter(final CsvWriter<T> writer) {
		super();
		this.writer = writer;
	}

	/**
	 * Get the writer
	 * @return the writer
	 * @since 1.0.0
	 */
	public CsvWriter<T> getWriter() {
		return writer;
	}

	/**
	 * {@inheritDoc}
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#open()
	 * @since 1.0.0
	 */
	public void open() throws CsvBangException, CsvBangCloseException {
		registerCurrentThread();
		writer.open();
	}



	/**
	 * {@inheritDoc}
	 * @return
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#isOpen()
	 * @since 1.0.0
	 */
	public boolean isOpen() {
		return writer.isOpen();
	}



	/**
	 * {@inheritDoc}
	 * @param line
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.lang.Object)
	 * @since 1.0.0
	 */
	public void write(T line) throws CsvBangException, CsvBangCloseException {
		registerCurrentThread();
		writer.write(line);
	}



	/**
	 * {@inheritDoc}
	 * @param lines
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.lang.Object[])
	 * @since 1.0.0
	 */
	public void write(T[] lines) throws CsvBangException, CsvBangCloseException {
		registerCurrentThread();
		writer.write(lines);
	}



	/**
	 * {@inheritDoc}
	 * @param lines
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.util.Collection)
	 * @since 1.0.0
	 */
	public void write(Collection<T> lines) throws CsvBangException,
			CsvBangCloseException {
		registerCurrentThread();
		writer.write(lines);
	}



	/**
	 * {@inheritDoc}
	 * @param comment
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(com.github.lecogiteur.csvbang.util.Comment)
	 * @since 1.0.0
	 */
	public void comment(Comment comment) throws CsvBangException,
			CsvBangCloseException {
		registerCurrentThread();
		writer.comment(comment);
	}



	/**
	 * {@inheritDoc}
	 * @param line
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(java.lang.Object)
	 * @since 1.0.0
	 */
	public void comment(T line) throws CsvBangException, CsvBangCloseException {
		registerCurrentThread();
		writer.comment(line);
	}



	/**
	 * {@inheritDoc}
	 * @param lines
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(java.lang.Object[])
	 * @since 1.0.0
	 */
	public void comment(T[] lines) throws CsvBangException,
			CsvBangCloseException {
		registerCurrentThread();
		writer.comment(lines);
	}



	/**
	 * {@inheritDoc}
	 * @param lines
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(java.util.Collection)
	 * @since 1.0.0
	 */
	public void comment(Collection<T> lines) throws CsvBangException,
			CsvBangCloseException {
		registerCurrentThread();
		writer.comment(lines);
	}



	/**
	 * {@inheritDoc}
	 * @throws IOException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 1.0.0
	 */
	public void close() throws IOException {
		if (isAllRegisteredThreadAreTeminated()){
			writer.close();
		}
	}



	/**
	 * {@inheritDoc}
	 * @return
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#isClose()
	 * @since 1.0.0
	 */
	public boolean isClose() {
		return writer.isClose();
	}



	/**
	 * {@inheritDoc}
	 * @param header
	 * @throws CsvBangException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#setHeader(java.lang.Object)
	 * @since 1.0.0
	 */
	public void setHeader(Object header) throws CsvBangException {
		writer.setHeader(header);
	}



	/**
	 * {@inheritDoc}
	 * @param footer
	 * @throws CsvBangException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#setFooter(java.lang.Object)
	 * @since 1.0.0
	 */
	public void setFooter(Object footer) throws CsvBangException {
		writer.setFooter(footer);
	}

	/**
	 * Register the current thread
	 * @since 1.0.0
	 * @see #registeredThreads
	 */
	private void registerCurrentThread(){
		registeredThreads.add(Thread.currentThread());
	}

	/**
	 * Verify if all thread which are registered, are terminated
	 * @return True if all thread is registered
	 * @since 1.0.0
	 * @see #registeredThreads
	 */
	private boolean isAllRegisteredThreadAreTeminated(){
		if (registeredThreads != null){
			for (final Thread t:registeredThreads){
				if (t.isAlive() && !Thread.currentThread().equals(t)){
					return false;
				}
			}
		}
		return true;
	}
}
