/**
 *  com.github.lecogiteur.csvbang.reader.AsynchronousCsvReader
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
package com.github.lecogiteur.csvbang.reader;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.pool.CsvbangExecutorService;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Asynchronous implementation of CSV reader
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class AsynchronousCsvReader<T> extends AbstractCsvReader<T> {
	
	/**
	 * Service which manages threads
	 * @since 0.1.0
	 */
	private final CsvbangExecutorService executor;
	
	/**
	 * List of record
	 * @since 1.0.0
	 */
	private final ConcurrentLinkedQueue<Collection<T>> records = new ConcurrentLinkedQueue<Collection<T>>();
	
	/**
	 * Number of thread which read CSV files
	 * @since 1.0.0
	 */
	private final AtomicInteger numberOfWorker = new AtomicInteger(0);
	
	/**
	 * The max number of thread which can be read files
	 * @since 1.0.0
	 */
	private final int maxNumberOfWorker;
	
	/**
	 * True if readers have read all CSV files
	 * @since 1.0.0
	 */
	private volatile boolean isTerminate = false;

	/**
	 * Constructor
	 * @param conf Csvbang configuration of CSV bean
	 * @param clazz the CSV bean
	 * @param pool the pool of file to read
	 * @param executor the service which manages threads
	 * @throws CsvBangException
	 * @since 1.0.0
	 */
	public AsynchronousCsvReader(final CsvBangConfiguration conf, final Class<T> clazz,
			final CsvFilePool pool, final CsvbangExecutorService executor) throws CsvBangException {
		super(conf, clazz, pool);
		this.executor = executor;
		maxNumberOfWorker = Math.round(executor.getMaxNumberThread() * 7/3);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.AbstractCsvReader#readBlock()
	 * @since 1.0.0
	 */
	@Override
	public Collection<T> readBlock() throws CsvBangException,
			CsvBangCloseException {
		while ((!isTerminate || numberOfWorker.get() > 0) && !isClose()){
			if (!isTerminate && numberOfWorker.get() < maxNumberOfWorker){
				executor.submit(this.hashCode(), new ReadWorker(this));
			}
			final Collection<T> beans = records.poll();
			if (CsvbangUti.isCollectionNotEmpty(beans)){
				return beans;
			}
		}
		executor.awaitGroupTermination(this.hashCode());
		return records.poll();
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.reader.AbstractCsvReader#close()
	 * @since 1.0.0
	 */
	@Override
	public void close() throws IOException {
		try {
			if (executor.awaitGroupTermination(this.hashCode())){
				super.close();
			}
		} catch (CsvBangException e) {
			throw new IOException("We can't close the CSV reader.", e);
		}
	}





	/**
	 * Asynchronous reader. Read part of CSV file in a new thread
	 * @author Tony EMMA
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class ReadWorker implements Callable<Void>{
		
		/**
		 * CSV reader
		 * @since 1.0.0
		 */
		private final CsvReader<T> reader;
		
		/**
		 * Constructor
		 * @param reader the reader
		 * @since 1.0.0
		 */
		public ReadWorker(CsvReader<T> reader) {
			super();
			this.reader = reader;
			numberOfWorker.incrementAndGet();
		}

		/**
		 * {@inheritDoc}
		 * @see java.util.concurrent.Callable#call()
		 * @since 1.0.0
		 */
		@Override
		public Void call() throws Exception {
			Collection<T> beans = null;
			while ((beans = reader.readBlock()) != null){
				if (CsvbangUti.isCollectionNotEmpty(beans)){
					records.add(beans);
				}
			}
			numberOfWorker.decrementAndGet();
			isTerminate = true;
			return null;
		}
	}
	
}
