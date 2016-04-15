/**
 *  com.github.lecogiteur.csvbang.test.pool.MultiCsvFilePoolTest
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

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CsvFilePoolTest {
	
	private class Getter implements Runnable{

		private final Integer millis;
		private final Integer nbRecord;
		private final Integer nbByte;
		private final Long nbRound;
		private final CsvFilePool pool;
		private final boolean forRead;
		private final Map<Integer, Integer> countRecord = new HashMap<Integer, Integer>();
		private final Map<Integer, Integer> countByte = new HashMap<Integer, Integer>();
		
		public Getter(Integer millis, CsvFilePool pool) {
			super();
			this.millis = millis;
			this.pool = pool;
			this.nbRecord = 10;
			this.nbByte = 100;
			forRead = false;
			this.nbRound = 10000l;
		}
		
		public Getter(Integer millis, CsvFilePool pool, Integer nbRecord, Integer nbByte, boolean forRead, long nbRound) {
			super();
			this.millis = millis;
			this.pool = pool;
			this.nbRecord = nbRecord;
			this.nbByte = nbByte;
			this.forRead = forRead;
			this.nbRound = nbRound;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for (long i=0; i<nbRound; i++){
				try {
					CsvFileContext file = pool.getFile(nbRecord, nbByte);
					if (forRead && file == null){
						return;
					}
					Assert.assertNotNull(file);
					int key = file.hashCode();
					int total = nbRecord;
					if (countRecord.containsKey(key)){
						total += countRecord.get(key);
					}
					countRecord.put(key, total);
					
					total = nbByte;
					if (countByte.containsKey(key)){
						total += countByte.get(key);
					}
					countByte.put(key, total);
				} catch (CsvBangException e) {
					Assert.fail("" + countRecord.size());
				}
			}
		}

		public Map<Integer, Integer> getCountRecord() {
			return countRecord;
		}
		
		public Map<Integer, Integer> getCountByte() {
			return countByte;
		}
		
		
	}
	
	private void threadSafeReadingTest(CsvBangConfiguration conf, CsvFilePool pool, String name, boolean isNotSimple, long nbFile, long totalsize){
		System.out.println(String.format("Test name: %s || NB file: %s || nbRound: %s", name, nbFile, (totalsize / 43)));
		Getter g1 = new Getter(20, pool, -1, 43, true, (totalsize / 43));
		Getter g2 = new Getter(10, pool, -1, 43, true, (totalsize / 43));
		Getter g3 = new Getter(0, pool, -1, 43, true, (totalsize / 43));
		Thread t1 = new Thread(g1);
		Thread t2 = new Thread(g2);
		Thread t3 = new Thread(g3);
		
		t1.start();
		t2.start();
		t3.start();
		
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){};
		
		Map<Integer, Integer> c1 = g1.getCountByte();
		Map<Integer, Integer> c2 = g2.getCountByte();
		Map<Integer, Integer> c3 = g3.getCountByte();
		
		Assert.assertEquals(nbFile, pool.getAllFiles().size());
		int nbBytes = 0;
		for (CsvFileContext c:pool.getAllFiles()){
			int nbByte = 0;
			if (c1.containsKey(c.hashCode())){
				nbByte += c1.get(c.hashCode());
				System.out.println(c.hashCode() + " --> t1 : " + c1.get(c.hashCode()));
			}
			if (c2.containsKey(c.hashCode())){
				nbByte += c2.get(c.hashCode());
				System.out.println(c.hashCode() + " --> t2 : " + c2.get(c.hashCode()));
			}
			if (c3.containsKey(c.hashCode())){
				nbByte += c3.get(c.hashCode());
				System.out.println(c.hashCode() + " --> t3 : " + c3.get(c.hashCode()));
			}
			nbBytes += nbByte;
		}
		
		long total = nbBytes;
		Assert.assertTrue(String.format("Total byte read: %s || Total bytes in all file: %s", total, totalsize), total >= totalsize);
		Assert.assertTrue(String.format("Total byte read: %s || Total bytes in all file: %s", total, totalsize), total - totalsize < 43 * 3);
	}
	
	private void threadSafeWritingTest(CsvBangConfiguration conf, CsvFilePool pool, String name, boolean isNotSimple, long nbFile){
		System.out.println("Test name: " + name);
		Getter g1 = new Getter(20, pool);
		Getter g2 = new Getter(10, pool);
		Getter g3 = new Getter(0, pool);
		Thread t1 = new Thread(g1);
		Thread t2 = new Thread(g2);
		Thread t3 = new Thread(g3);
		
		t1.start();
		t2.start();
		t3.start();
		
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){};
		
		Map<Integer, Integer> c1 = g1.getCountRecord();
		Map<Integer, Integer> c2 = g2.getCountRecord();
		Map<Integer, Integer> c3 = g3.getCountRecord();
		
		Assert.assertEquals(nbFile, pool.getAllFiles().size());
		List<Integer> nbRecords = new ArrayList<Integer>();
		for (CsvFileContext c:pool.getAllFiles()){
			int nbRecord = 0;
			if (c1.containsKey(c.hashCode())){
				nbRecord += c1.get(c.hashCode());
				System.out.println(c.hashCode() + " --> t1 : " + c1.get(c.hashCode()));
			}
			if (c2.containsKey(c.hashCode())){
				nbRecord += c2.get(c.hashCode());
				System.out.println(c.hashCode() + " --> t2 : " + c2.get(c.hashCode()));
			}
			if (c3.containsKey(c.hashCode())){
				nbRecord += c3.get(c.hashCode());
				System.out.println(c.hashCode() + " --> t3 : " + c3.get(c.hashCode()));
			}
			nbRecords.add(nbRecord);
		}
		

		if(isNotSimple){
			for (int nbRecord:nbRecords){
				Assert.assertEquals(conf.maxRecordByFile, nbRecord);
			}

			try{
				pool.getFile(54, 800000);
			}catch(CsvBangException e){
				return;
			}
			Assert.fail("Maximum of file");
		}else{
			for (int nbRecord:nbRecords){
				Assert.assertEquals(300000, nbRecord);
			}
		}
	}

	@Test
	public void threadsafeMultiFileTest() throws CsvBangException{
		CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFile = 3;
		conf.maxRecordByFile = 100000l;
		conf.maxFileSize = 100000000l;
		conf.isWriteFileByFile = false;
		conf.init();
		CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof MultiCsvFilePool);
		Assert.assertEquals(0, pool.getAllFiles().size());
		
		threadSafeWritingTest(conf, pool, "MultiCsvFilePool", true, conf.maxFile);
	}
	
	@Test
	public void threadsafeOneByOneTest() throws CsvBangException{
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxRecordByFile = 100000l;
		conf.maxFileSize = 100000000l;
		conf.maxFile = 3;
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		Assert.assertEquals(0, pool.getAllFiles().size());
		
		
		threadSafeWritingTest(conf, pool, "OneByOneCsvFilePool", true, conf.maxFile);
	}
	
	@Test
	public void threadsafeSimpleTest() throws CsvBangException{
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForWriting(conf, (File)null, null, null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof SimpleCsvFilePool);
		Assert.assertEquals(1, pool.getAllFiles().size());
		
		
		threadSafeWritingTest(conf, pool, "SimpleCsvFilePool", false, 1);
	}
	
	@Test
	public void threadsafeMultiFileForReadingTest() throws CsvBangException, URISyntaxException{
		File baseDir = new File(CsvFilePoolFactory.class.getResource("/csvfilepool").toURI());
		CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxFile = 3;
		conf.maxRecordByFile = 100000l;
		conf.maxFileSize = 100000000l;
		conf.isReadFileByFile = false;
		conf.fileName = new FileName("*.csv", null);
		conf.init();
		CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, Collections.singleton(baseDir), null);
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof MultiCsvFilePool);
		Assert.assertEquals(4, pool.getAllFiles().size());
		
		Collection<File> files = CsvbangUti.getAllFiles(baseDir, conf.fileName.generateFilter());
		Assert.assertEquals(4, files.size());
		long totalSize = 0;
		for (File file:files){
			totalSize += file.length();
		}
		
		threadSafeReadingTest(conf, pool, "MultiCsvFilePool", true, files.size(), totalSize);
	}
	
	@Test
	public void threadsafeReadingOneByOneTest() throws CsvBangException, URISyntaxException{
		File baseDir = new File(CsvFilePoolFactory.class.getResource("/csvfilepool").toURI());
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.maxRecordByFile = 100000l;
		conf.maxFileSize = 100000000l;
		conf.maxFile = 3;
		conf.fileName = new FileName("*.csv", null);
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, 
				Collections.singleton(baseDir), new FileName("*/csvfilepool/*file*.csv", null));
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof OneByOneCsvFilePool);
		Assert.assertEquals(3, pool.getAllFiles().size());
		
		Collection<File> files = CsvbangUti.getAllFiles(baseDir, new FileName("*/csvfilepool/*file*.csv", null).generateFilter());
		Assert.assertEquals(3, files.size());
		long totalSize = 0;
		for (File file:files){
			totalSize += file.length();
		}
		
		threadSafeReadingTest(conf, pool, "OneByOneCsvFilePool", true, files.size(), totalSize);
	}
	
	@Test
	public void threadsafeReadingSimpleTest() throws CsvBangException, URISyntaxException{
		File baseDir = new File(CsvFilePoolFactory.class.getResource("/csvfilepool/folder1").toURI());
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		conf.init();
		final CsvFilePool pool = CsvFilePoolFactory.createPoolForReading(conf, 
				Collections.singleton(baseDir), new FileName("*file*.csv", null));
		Assert.assertNotNull(pool);
		Assert.assertTrue(pool instanceof SimpleCsvFilePool);
		Assert.assertEquals(1, pool.getAllFiles().size());
		
		Collection<File> files = CsvbangUti.getAllFiles(baseDir, new FileName("*file*.csv", null).generateFilter());
		Assert.assertEquals(1, files.size());
		long totalSize = 0;
		for (File file:files){
			totalSize += file.length();
		}
		
		threadSafeReadingTest(conf, pool, "SimpleCsvFilePool", true, files.size(), totalSize);
	}
}
