/**
 *  com.github.lecogiteur.csvbang.test.pool.CsvbangThreadPoolExecutorTest
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
package com.github.lecogiteur.csvbang.test.pool;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.pool.CsvbangExecutorService;
import com.github.lecogiteur.csvbang.pool.CsvbangThreadPoolExecutor;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CsvbangThreadPoolExecutorTest {

	static class TaskTest implements Callable<Void>{

		private volatile int isAlive = 0;
		
		private long millis = 0;
		
		public TaskTest(long millis) {
			super();
			this.millis = millis;
		}

		@Override
		public Void call() throws Exception {
			isAlive = 1;
			Thread.sleep(millis);
			isAlive = -1;
			return null;
		}

		public int getIsAlive() {
			return isAlive;
		}
		
	}
	
	@Test
	public void simpleServiceTest() throws CsvBangException, InterruptedException{
		TaskTest task1 = new TaskTest(5000);
		TaskTest task2 = new TaskTest(20000);
		TaskTest task3 = new TaskTest(5000);
		TaskTest task4 = new TaskTest(20000);
		TaskTest task5 = new TaskTest(5000);
		
		Assert.assertEquals(0, task1.getIsAlive());
		Assert.assertEquals(0, task2.getIsAlive());
		Assert.assertEquals(0, task3.getIsAlive());
		Assert.assertEquals(0, task4.getIsAlive());
		Assert.assertEquals(0, task5.getIsAlive());
		
		CsvbangExecutorService service = new CsvbangThreadPoolExecutor(2);
		service.submit(1, task1);
		service.submit(1, task3);
		service.submit(1, task5);
		service.submit(2, task2);
		service.submit(2, task4);
		
		Assert.assertTrue(0 <= task1.getIsAlive());
		Assert.assertTrue(0 <= task2.getIsAlive());
		Assert.assertTrue(0 <= task3.getIsAlive());
		Assert.assertTrue(0 <= task4.getIsAlive());
		Assert.assertTrue(0 <= task5.getIsAlive());
		
		Assert.assertTrue(service.awaitGroupTermination(1));
		Assert.assertFalse(service.isTerminated());
		
		
		Assert.assertEquals(-1, task1.getIsAlive());
		Assert.assertEquals(1, task2.getIsAlive());
		Assert.assertEquals(-1, task3.getIsAlive());
		Assert.assertEquals(1, task4.getIsAlive());
		Assert.assertEquals(-1, task5.getIsAlive());
		
		
		Assert.assertTrue(service.awaitGroupTermination(2));
		
		Assert.assertEquals(-1, task1.getIsAlive());
		Assert.assertEquals(-1, task2.getIsAlive());
		Assert.assertEquals(-1, task3.getIsAlive());
		Assert.assertEquals(-1, task4.getIsAlive());
		Assert.assertEquals(-1, task5.getIsAlive());
		
		//Assert.assertTrue(service.isTerminated());
		
		while(!service.isTerminated()){
			System.out.println("Test");
			Thread.sleep(500);
		}
		
	}
}
