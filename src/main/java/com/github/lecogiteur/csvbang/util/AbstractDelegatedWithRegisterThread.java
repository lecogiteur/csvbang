/**
 *  com.github.lecogiteur.csvbang.util.AbstractDelegatedWithRegisterThread
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

import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Abstract class manages all thread which write, read or open Csv Writer/Reader and close the Writer/Reader when all thread are terminated
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractDelegatedWithRegisterThread implements Closeable{
	
	/**
	 * Register thread which open and write or read in order to verify if thread are closed before closing file.
	 * @since 1.0.0
	 */
	private final ConcurrentSkipListSet<Thread> registeredThreads = new ConcurrentSkipListSet<Thread>(new Comparator<Thread>() {
		@Override
		public int compare(Thread o1, Thread o2) {
			return o1.equals(o2)?0:1;
		}
	});

	/**
	 * Get Csv Writer/Reader
	 * @return the Csv Reader/Writer
	 * @since 1.0.0
	 */
	public abstract Closeable getActor();
	
	/**
	 * {@inheritDoc}
	 * @throws IOException
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 1.0.0
	 */
	public void close() throws IOException {
		if (isAllRegisteredThreadAreTeminated()){
			getActor().close();
		}
	}
	
	/**
	 * Register the current thread
	 * @since 1.0.0
	 * @see #registeredThreads
	 */
	public void registerCurrentThread(){
		registeredThreads.add(Thread.currentThread());
	}

	/**
	 * Verify if all thread which are registered, are terminated
	 * @return True if all thread is registered
	 * @since 1.0.0
	 * @see #registeredThreads
	 */
	public boolean isAllRegisteredThreadAreTeminated(){
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
