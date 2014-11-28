/**
 *  com.github.lecogiteur.csvbang.pool.CsvbangThreadPoolExecutor
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
package com.github.lecogiteur.csvbang.pool;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.ReflectionUti;

/**
 * Implementation of Csvbang task executor
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class CsvbangThreadPoolExecutor extends ThreadPoolExecutor implements CsvbangExecutorService{
	
	/**
	 * The logger
	 * @since 0.1.0
	 */
	private static final Logger LOGGER = Logger.getLogger(ReflectionUti.class.getName());
	
	/**
	 * List of tasks by group
	 * @since 0.1.0
	 */
	private ConcurrentHashMap<Integer, Collection<Future<Void>>> groupOfTasks = new ConcurrentHashMap<Integer, Collection<Future<Void>>>();

	/**
	 * Constructor
	 * @param nThreads number of thread in pool
	 * @since 0.1.0
	 */
	public CsvbangThreadPoolExecutor(int nThreads) {
		super(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvbangExecutorService#submit(java.lang.Integer, java.util.concurrent.Callable)
	 * @since 0.1.0
	 */
	@Override
	public void submit(final Integer groupId,  final Callable<Void> task){
		//submit the task for execution
		final Future<Void> future = super.submit(task);
		Collection<Future<Void>> c = groupOfTasks.putIfAbsent(groupId, new ConcurrentLinkedQueue<Future<Void>>());
		if (c == null){
			c = groupOfTasks.get(groupId);
		}
		c.add(future);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvbangExecutorService#awaitGroupTermination(java.lang.Integer)
	 * @since 0.1.0
	 */
	@Override
	public boolean awaitGroupTermination(final Integer groupId) throws CsvBangException{
		final Collection<Future<Void>> c = groupOfTasks.remove(groupId);
		if (CsvbangUti.isCollectionEmpty(c)){
			return true;
		}
		for (final Future<Void> f:c){
			try{
				f.get();
			}catch(InterruptedException e){
				LOGGER.log(Level.WARNING, String.format("A task of group [%s] is interrupted in Csvbang.", groupId), e);
			}catch(CancellationException e){
				LOGGER.log(Level.WARNING, String.format("A task of group [%s] is cancelled in Csvbang.", groupId), e);
			}catch(ExecutionException e){
				throw new CsvBangException(String.format("A task of group [%s] is in error in Csvbang.", groupId), e);
			}
		}
		return true;
	}
	

}
