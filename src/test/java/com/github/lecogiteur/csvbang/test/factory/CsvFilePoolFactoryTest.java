/**
 *  com.github.lecogiteur.csvbang.test.factory.CsvFilePoolFactoryTest
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
package com.github.lecogiteur.csvbang.test.factory;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.CsvFilePoolFactory;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.pool.MultiCsvFilePool;
import com.github.lecogiteur.csvbang.pool.OneByOneCsvFilePool;
import com.github.lecogiteur.csvbang.pool.SimpleCsvFilePool;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CsvFilePoolFactoryTest {
	
	@Test
	public void simplePoolTest() throws CsvBangException{
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof SimpleCsvFilePool);
		Assert.assertEquals(1, pool.getAllFiles().size());
		Assert.assertNotNull(pool.getFile(10, 100));
		Assert.assertEquals(1, pool.getAllFiles().size());
		Assert.assertEquals(pool.getFile(10, 100), pool.getFile(54, 2345));
	}
	
	@Test
	public void oneByOnePoolTest() throws CsvBangException{
		CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFileSize = 1000000l;
		conf.isFileByFile = false;
		conf.init();
		CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		
		conf = new CsvBangConfiguration();
		conf.maxRecordByFile = 1000000l;
		conf.isFileByFile = false;
		conf.init();
		pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		
		conf = new CsvBangConfiguration();
		conf.maxFile = 1;
		conf.maxRecordByFile = 1000000l;
		conf.isFileByFile = false;
		conf.init();
		pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		
		conf = new CsvBangConfiguration();
		conf.maxFile = 3;
		conf.maxRecordByFile = 1000000l;
		conf.maxFileSize = 1000000l;
		conf.init();
		pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
	}
	
	@Test
	public void multiplePoolTest() throws CsvBangException{
		CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFile = 2;
		conf.maxRecordByFile = 1000000l;
		conf.maxFileSize = 1000000l;
		conf.isFileByFile = false;
		conf.init();
		CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof MultiCsvFilePool);
		
		Assert.assertEquals(0, pool.getAllFiles().size());
		Assert.assertNotNull(pool.getFile(10, 100000));
		Assert.assertEquals(1, pool.getAllFiles().size());
		
		pool.getFile(10, 100000);
		pool.getFile(54, 100000);
		pool.getFile(54, 600000); 
		pool.getFile(54, 800000); 
		Assert.assertEquals(2, pool.getAllFiles().size());
		
		try{
			pool.getFile(54, 800000);
		}catch(CsvBangException e){
			return;
		}
		Assert.fail("Maximum of two file");
	}
	
	@Test
	public void oneByOnePoolNoLimitTest() throws CsvBangException{
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFileSize = 1000000l;
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		Assert.assertEquals(0, pool.getAllFiles().size());
		Assert.assertNotNull(pool.getFile(10, 100000));
		Assert.assertEquals(1, pool.getAllFiles().size());
		
		CsvFileContext file = pool.getFile(10, 100000);
		Assert.assertEquals(file, pool.getFile(54, 100000));
		Assert.assertEquals(file, pool.getFile(54, 600000)); //total size: 900000
		Assert.assertNotEquals(file, pool.getFile(54, 800000)); //new file
		Assert.assertEquals(2, pool.getAllFiles().size());
	}
	
	@Test
	public void oneByOnePoolWithLimitTest() throws CsvBangException{
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFileSize = 1000000l;
		conf.maxFile = 2;
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		Assert.assertEquals(0, pool.getAllFiles().size());
		Assert.assertNotNull(pool.getFile(10, 100000));
		Assert.assertEquals(1, pool.getAllFiles().size());
		
		CsvFileContext file = pool.getFile(10, 100000);
		Assert.assertEquals(file, pool.getFile(54, 100000));
		Assert.assertEquals(file, pool.getFile(54, 600000)); //total size: 900000
		Assert.assertNotEquals(file, pool.getFile(54, 800000)); //new file
		Assert.assertEquals(2, pool.getAllFiles().size());
		
		try{
			pool.getFile(54, 800000);
		}catch(CsvBangException e){
			return;
		}
		Assert.fail("Maximum of two file");
	}

}
