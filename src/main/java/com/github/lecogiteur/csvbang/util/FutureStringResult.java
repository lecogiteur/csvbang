/**
 *  com.github.lecogiteur.csvbang.util.FutureStringResult
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
package com.github.lecogiteur.csvbang.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A future result of String type. Used in order to retrieve comment, header or footer. 
 * In order to read CSV file we use multiple thread. So we don't know when we can retrieve this elements.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FutureStringResult implements Future<String>{
	
	/**
	 * Is cancelled
	 * @since 1.0.0
	 */
	private volatile boolean isCancelled = false;
	
	/**
	 * True if we must interrupt the running process
	 * @since 1.0.0
	 */
	private volatile boolean isMustStop = false;
	
	/**
	 * Have you retrieve the result
	 * @since 1.0.0
	 */
	private volatile boolean isDone = false;
	
	/**
	 * The result to retrieve. Can be a comment, header, footer or another elements.
	 * @since 1.0.0
	 */
	private volatile String result;
	
	/**
	 * When an exception has occurred when we try to retrieve the result
	 * @since 1.0.0
	 */
	private Exception exception;

	/**
	 * {@inheritDoc}
	 * @see java.util.concurrent.Future#cancel(boolean)
	 * @since 1.0.0
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		isCancelled = !isDone && exception == null;
		isMustStop = mayInterruptIfRunning;
		isDone = true;
		return isCancelled;
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.concurrent.Future#isCancelled()
	 * @since 1.0.0
	 */
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.concurrent.Future#isDone()
	 * @since 1.0.0
	 */
	@Override
	public boolean isDone() {
		return isDone || exception != null || isCancelled;
	}
	
	/**
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @since 1.0.0
	 */
	private String internalGet() throws ExecutionException, InterruptedException{
		if (exception != null){
			throw new ExecutionException(exception);
		}else if (isMustStop){
			throw new InterruptedException("We can't retrieve the result. The process has been interrupted because it was cancelled.");
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.concurrent.Future#get()
	 * @since 1.0.0
	 */
	@Override
	public String get() throws InterruptedException, ExecutionException {
		if (isCancelled){
			throw new InterruptedException("We can't retrieve the result. The process has been interrupted because it was cancelled.");
		}
		while (!isDone){
			final String r = internalGet();
			if (r != null){
				return r;
			}
		}
		return internalGet();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 * @since 1.0.0
	 */
	@Override
	public String get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		final long end = System.nanoTime() + unit.toNanos(timeout);
		if (isCancelled){
			throw new InterruptedException("We can't retrieve the result. The process has been interrupted because it was cancelled.");
		}
		while(System.nanoTime() < end){
			final String r = internalGet();
			if (r != null){
				return r;
			}
		}
		throw new TimeoutException(String.format("Timeout. We can retrieve result in %s %s", timeout, unit));
	}
	
	/**
	 * Set the result
	 * @param result the result
	 * @throws InterruptedException if user cancel the process
	 * @since 1.0.0
	 */
	public void setResult(final String result) throws InterruptedException{
		if (isMustStop){
			throw new InterruptedException("We can't set the result. The process has been interrupted because it was cancelled.");
		}
		if (!isCancelled){
			this.result = result;
			isDone = true;
		}
	}
	
	/**
	 * Set the execution exception
	 * @param exception an exception
	 * @throws InterruptedException if process is interrupted
	 * @since 1.0.0
	 */
	public void setException(final Exception exception) throws InterruptedException{
		if (isMustStop){
			throw new InterruptedException("We can't set the result. The process has been interrupted because it was cancelled.");
		}
		if (!isCancelled){
			this.exception = exception;
			isDone = true;
		}
	}

}
