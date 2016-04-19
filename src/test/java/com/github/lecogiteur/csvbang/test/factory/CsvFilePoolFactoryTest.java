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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.CsvFilePoolFactory;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.file.FileName;
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
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
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
		conf.isWriteFileByFile = false;
		conf.init();
		CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		
		conf = new CsvBangConfiguration();
		conf.maxRecordByFile = 1000000l;
		conf.isWriteFileByFile = false;
		conf.init();
		pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		
		conf = new CsvBangConfiguration();
		conf.maxFile = 1;
		conf.maxRecordByFile = 1000000l;
		conf.isWriteFileByFile = false;
		conf.init();
		pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		
		conf = new CsvBangConfiguration();
		conf.maxFile = 3;
		conf.maxRecordByFile = 1000000l;
		conf.maxFileSize = 1000000l;
		conf.init();
		pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
	}
	
	@Test
	public void multiplePoolTest() throws CsvBangException{
		CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFile = 2;
		conf.maxRecordByFile = 1000000l;
		conf.maxFileSize = 1000000l;
		conf.isWriteFileByFile = false;
		conf.init();
		CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
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
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
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
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
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

	@Test
	public void nullReadPoolTest(){
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		
		final CsvFilePool pool1 = CsvFilePoolFactory.createPoolForReading(conf, null, null);
		Assert.assertNull(pool1);
		
		final CsvFilePool pool2 = CsvFilePoolFactory.createPoolForReading(conf, new ArrayList<File>(), null);
		Assert.assertNull(pool2);
		
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("notExist.csv"));
		final CsvFilePool pool3 = CsvFilePoolFactory.createPoolForReading(conf, files, null);
		Assert.assertNull(pool3);
	}
	
	@Test
	public void simpleReadPoolTest() throws IOException, CsvBangException{
		final File file = File.createTempFile("test", ".csv");
		file.deleteOnExit();
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("notExist.csv"));
		files.add(file);
		
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, files, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof SimpleCsvFilePool);
		Assert.assertEquals(1, pool.getAllFiles().size());
		Assert.assertNull(pool.getFile(0, 0));
		Assert.assertEquals(1, pool.getAllFiles().size());
	}
	
	@Test
	public void simpleReadDirectoryPoolTest() throws IOException, CsvBangException{
		final File file = File.createTempFile("test-simple", ".csv");
		file.deleteOnExit();
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("notExist.csv"));
		files.add(file.getParentFile());
		
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.isReadFileByFile = true;
		
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, files, new FileName("test-simple*.csv", null));
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof SimpleCsvFilePool);
		Assert.assertEquals(1, pool.getAllFiles().size());
		Assert.assertNull(pool.getFile(0, 0));
		Assert.assertEquals(1, pool.getAllFiles().size());
	}
	
	@Test
	public void oneByOneReadPoolTest() throws IOException, CsvBangException{
		final File file1 = File.createTempFile("test-one", ".csv");
		file1.deleteOnExit();
		final File file2 = File.createTempFile("test-one", ".csv");
		file2.deleteOnExit();
		
		FileWriter writer = new FileWriter(file1);
		writer.append("a big csv string").flush();
		writer.close();
		
		FileWriter writer2 = new FileWriter(file2);
		writer2.append("a big csv string2").flush();
		writer2.close();
		
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("notExist.csv"));
		files.add(file1);
		files.add(file2);
		
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.isReadFileByFile = true;
		
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, files, new FileName("test-one*.csv", null));
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		Assert.assertEquals(2, pool.getAllFiles().size());
		Assert.assertNotNull(pool.getFile(0, 0));
		Assert.assertNotNull(pool.getFile(1000, 0));
		Assert.assertNotNull(pool.getFile(1000, 0));
		Assert.assertEquals(2, pool.getAllFiles().size());

		int i=0;
		for (; i<="a big csv stringa big csv string2".length(); i+=3){
			Assert.assertNotNull(pool.getFile(0, 3));
		}
		Assert.assertEquals("a big csv stringa big csv string2".length() + 3, i);
		Assert.assertNull(pool.getFile(0, 3));
	}
	
	@Test
	public void multiReadPoolTest() throws IOException, CsvBangException{
		final File file1 = File.createTempFile("test-multi", ".csv");
		file1.deleteOnExit();
		final File file2 = File.createTempFile("test-multi", ".csv");
		file2.deleteOnExit();
		
		FileWriter writer = new FileWriter(file1);
		writer.append("a big csv string").flush();
		writer.close();
		
		FileWriter writer2 = new FileWriter(file2);
		writer2.append("a big csv string2").flush();
		writer2.close();
		
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File("notExist.csv"));
		files.add(file1);
		files.add(file2);
		
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.isReadFileByFile = false;
		
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, files, new FileName("test-multi*.csv", null));
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof MultiCsvFilePool);
		Assert.assertEquals(2, pool.getAllFiles().size());
		Assert.assertNotNull(pool.getFile(0, 0));
		Assert.assertNotNull(pool.getFile(1000, 0));
		Assert.assertNotNull(pool.getFile(1000, 0));
		Assert.assertEquals(2, pool.getAllFiles().size());
		int i=0;
		for (; i<="a big csv stringa big csv string2".length(); i+=3){
			Assert.assertNotNull(pool.getFile(0, 3));
		}
		Assert.assertEquals("a big csv stringa big csv string2".length() + 3, i);
		Assert.assertNull(pool.getFile(0, 3))conf.isReadFileByFile = true;;
	}
	
	@Test
	public void should_read_static_file() throws IOException, CsvBangException, URISyntaxException{
		URL dir = getClass().getResource("/csvfilename");
		File f = new File(dir.toURI());
		
		
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.isReadFileByFile = true;
		
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, null, 
				new FileName(f.getAbsolutePath()+ "/file%n.csv", null));
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		Assert.assertEquals(3, pool.getAllFiles().size());
	}
}
