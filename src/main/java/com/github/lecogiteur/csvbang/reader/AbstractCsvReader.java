/**
 *  com.github.lecogiteur.csvbang.reader.AbstractCsvReader
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
package com.github.lecogiteur.csvbang.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.parser.CsvParser;
import com.github.lecogiteur.csvbang.parser.CsvParsingResult;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.FutureStringResult;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractCsvReader<T> implements CsvReader<T> {
	
	/**
	 * Csv pool file which deliver CSV file to read
	 * @since 1.0.0
	 */
	final private CsvFilePool pool;
	
	/**
	 * The CSV parser
	 * @since 1.0.0
	 */
	final private CsvParser<T> parser;
	
	/**
	 * True if the Csv reader is opened
	 * @since 1.0.0
	 */
	private volatile boolean isOpen = false;
	
	/**
	 * True if the Csv reader is closed
	 * @since 1.0.0
	 */
	private volatile boolean isClose = false;
	
	/**
	 * Csv headers
	 * @since 1.0.0
	 */
	private final FutureManager headers = new FutureManager();
	
	/**
	 * Csv footers
	 * @since 1.0.0
	 */
	private final FutureManager footers = new FutureManager();
	
	/**
	 * Csv comments
	 * @since 1.0.0
	 */
	private final FutureManager comments = new FutureManager();
	
	/**
	 * Constructor
	 * @param conf the Csvbang configuration for the clazz
	 * @param clazz the CSV bean class
	 * @param pool the pool of file to read
	 * @throws CsvBangException if a problem has occurred when we initialize the CSV parser
	 * @since 1.0.0
	 */
	public AbstractCsvReader(final CsvBangConfiguration conf, final Class<T> clazz, final CsvFilePool pool) throws CsvBangException {
		super();
		this.pool = pool;
		this.parser = new CsvParser<T>(clazz, conf);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#open()
	 * @since 1.0.0
	 */
	@Override
	public void open() throws CsvBangException, CsvBangCloseException {
		if (isOpen){
			return;
		}

		if (isClose){
			//the reader is closed !
			throw new CsvBangCloseException("The reader is closed; we can't open it");
		}
		
		final Collection<CsvFileContext> files = pool.getAllFiles();
		if (CsvbangUti.isCollectionNotEmpty(files)){
			for (final CsvFileContext file:files){
				//thread-safe operation
				file.open();
				if (isOpen){
					return;
				}
			}
		}
		isOpen = true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#isOpen()
	 * @since 1.0.0
	 */
	@Override
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#readBlock()
	 * @since 1.0.0
	 */
	@Override
	public Collection<T> readBlock() throws CsvBangException,
			CsvBangCloseException {
		
		if (!isOpen){
			//open reader
			open();
		}
		
		if (isClose){
			//the reader is closed !
			throw new CsvBangCloseException("The reader is closed; we can't read");
		}
		
		while (true){
			//get a file in pool
			final CsvFileContext file = pool.getFile(-1, IConstantsCsvBang.DEFAULT_BYTE_BLOCK_SIZE);
			
			
			if (file == null){
				//no file
				//flush the parser
				final Collection<CsvParsingResult<T>> results = parser.flush();

				if (CsvbangUti.isCollectionNotEmpty(results)){
					final Collection<T> beans = new ArrayList<T>();
					for (final CsvParsingResult<T> result:results){
						final Collection<T> bean = processResultParser(result);
						if (CsvbangUti.isCollectionNotEmpty(bean)){
							beans.addAll(bean);
						}
					}
					return beans;
				}
				
				//no result to return
				return null;
			}

			//parse a part of file
			final CsvParsingResult<T> result = parser.parse(file.read(IConstantsCsvBang.DEFAULT_BYTE_BLOCK_SIZE));

			final Collection<T> beans = processResultParser(result);
			if (beans != null){
				return beans;
				//else continue
			}
		}
	}
	
	/**
	 * Process the the result from CSV parser
	 * @param result the result from the CSV parser
	 * @return the result from parser
	 * @throws InterruptedException if the thread is interrupted
	 * @since 1.0.0
	 */
	private Collection<T> processResultParser(final CsvParsingResult<T> result) throws CsvBangException{
		if (result != null){
			if (CsvbangUti.isStringNotBlank(result.getHeader())){
				//header
				try{
					headers.setResult(result.getHeader());
				} catch (InterruptedException e) {
					throw new CsvBangException(String.format("We can't add the header[%s]. The thread is interrupted.", result.getHeader()), e);
				}
			}
			if (CsvbangUti.isStringNotBlank(result.getFooter())){
				//footer
				try {
					footers.setResult(result.getFooter());
				} catch (InterruptedException e) {
					throw new CsvBangException(String.format("We can't add the footer[%s]. The thread is interrupted.", result.getFooter()), e);
				}
			}
			if (CsvbangUti.isCollectionNotEmpty(result.getComments())){
				//footer
					for (final String comment:result.getComments()){
						try {
							comments.setResult(comment);
						} catch (InterruptedException e) {
							throw new CsvBangException(String.format("We can't add the comment[%s]. The thread is interrupted.", comment), e);
						}
					}
			}
			if (CsvbangUti.isCollectionNotEmpty(result.getCsvBeans())){
				//return result
				return result.getCsvBeans();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#close()
	 * @since 1.0.0
	 */
	@Override
	public void close() throws IOException {
		if (isClose){
			//already close, nothing to do
			return;
		}
		isOpen = false;
		final Collection<CsvFileContext> files = pool.getAllFiles();
		if (CsvbangUti.isCollectionNotEmpty(files)){
			for (final CsvFileContext file:files){
				//thread-safe operation
				file.close();
				if (isClose){
					return;
				}
			}
		}
		try {
			headers.close();
		} catch (InterruptedException e) {
			throw new CsvBangIOException("Can't flush headers", e);
		}
		try{
			footers.close();
		} catch (InterruptedException e) {
			throw new CsvBangIOException("Can't flush footers", e);
		}
		try{
			comments.close();
		} catch (InterruptedException e) {
			throw new CsvBangIOException("Can't flush comments", e);
		}
		isClose = true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#isClose()
	 * @since 1.0.0
	 */
	@Override
	public boolean isClose() {
		return isClose;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#getHeader()
	 * @since 1.0.0
	 */
	@Override
	public Future<String> getHeader() throws CsvBangException {
		return headers.poll();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#getFooter()
	 * @since 1.0.0
	 */
	@Override
	public Future<String> getFooter() throws CsvBangException {
		return footers.poll();
	}
	

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#getComment()
	 * @since 1.0.0
	 */
	@Override
	public Future<String> getComment() throws CsvBangException {
		return comments.poll();
	}


	/**
	 * Manage future for header, footer or comments. A header, footer or comment is a result.
	 * @author Tony EMMA
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class FutureManager{
		
		/**
		 * Waiting result
		 * @since 1.0.0
		 */
		private final ConcurrentLinkedQueue<FutureStringResult> waiting = new ConcurrentLinkedQueue<FutureStringResult>();
		
		/**
		 * List of result which are done.
		 * @since 1.0.0
		 */
		private final ConcurrentLinkedQueue<FutureStringResult> futures = new ConcurrentLinkedQueue<FutureStringResult>();
		
		/**
		 * True if no more result authorized
		 * @since 1.0.0
		 */
		private volatile boolean isClosedFuture = false;
		
		/**
		 * Get a result
		 * @return a result (a header, footer, ...)
		 * @since 1.0.0
		 */
		public FutureStringResult poll(){
			if (isClosedFuture){
				return futures.poll();
			}else{
				final FutureStringResult future = futures.poll();
				if (future != null){
					return future;
				}
			}
			final FutureStringResult future = new FutureStringResult();
			waiting.offer(future);
			if (!isClosedFuture){
				//double - not synchronize
				return future;
			}
			return null;
		}
		
		/**
		 * Set a result
		 * @param result a result
		 * @throws InterruptedException if thread is interrupted
		 * @since 1.0.0
		 */
		public void setResult(final String result) throws InterruptedException{
			FutureStringResult future = waiting.poll();
			if (future == null){
				future = new FutureStringResult();
				futures.offer(future);
			}
			future.setResult(result);
		}
		
		/**
		 * Terminate the creation of new result
		 * @throws InterruptedException if thread is interrupted
		 * @since 1.0.0
		 */
		public void close() throws InterruptedException{
			isClosedFuture = true;
			while (!waiting.isEmpty()){
				waiting.poll().setResult(null);
			}
		}
	}
	
}
