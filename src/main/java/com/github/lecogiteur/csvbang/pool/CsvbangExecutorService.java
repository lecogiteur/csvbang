/**
 *  com.github.lecogiteur.csvbang.pool.CsvbangExecutorService
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public interface CsvbangExecutorService extends ExecutorService {
	
	public void submit(final Integer groupId,  final Callable<Void> task);
	
	public boolean awaitGroupTermination(final Integer groupId) throws CsvBangException;

}
