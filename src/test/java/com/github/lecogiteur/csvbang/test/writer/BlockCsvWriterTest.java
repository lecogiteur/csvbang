/**
 *  com.github.lecogiteur.csvbang.test.writer.BlockCsvWriterTest
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
package com.github.lecogiteur.csvbang.test.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.FactoryCsvbang;
import com.github.lecogiteur.csvbang.test.bean.writer.BlockCsvWriterBean;
import com.github.lecogiteur.csvbang.writer.BlockCsvWriter;
import com.github.lecogiteur.csvbang.writer.CsvWriter;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BlockCsvWriterTest {

	private class Writer<T> implements Runnable{
		private final CsvWriter<T> w;
		private final Integer millis;
		private final Integer nbSamples;
		private final T[] samples;
		private long nbWriting = 0;
		private boolean close = false;
		private boolean fail = false;

		public Writer(CsvWriter<T> w, Integer millis, Integer nbSamples, T[] samples, boolean close) {
			super();
			this.w = w;
			this.millis = millis;
			this.nbSamples = nbSamples;
			this.samples = samples;
			this.close = close;
		}


		@Override
		public void run() {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(int i=0; i<nbSamples; i++){
				try {
					if (!close || !w.isClose()){
						w.write(samples);
						nbWriting += samples.length;
					}else{
						break;
					}
				} catch (CsvBangException e) {
					e.printStackTrace();
					fail=true;
				} catch (CsvBangCloseException e) {
					e.printStackTrace();
				}
			}
			
			if (close){
				try {
					w.close();
				} catch (IOException e) {
					fail=!(e instanceof CsvBangCloseException);
					e.printStackTrace();
				}
			}
		}

		public long getNbWriting() {
			return nbWriting;
		}

		public boolean isFail() {
			return fail;
		}
	}

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
	@Test
	public void simpleWriteTest() throws CsvBangException, IOException{
		FactoryCsvbang factory = new FactoryCsvbang("com.github.lecogiteur.csvbang.test.bean.writer");
		testFolder.create();
		File folder = testFolder.newFolder();
		System.out.println("Folder: " + folder.getAbsolutePath());
		
		CsvWriter<BlockCsvWriterBean> writer = factory.createCsvWriter(BlockCsvWriterBean.class, folder);
		Assert.assertNotNull(writer);
		Assert.assertTrue(writer instanceof BlockCsvWriter);
		
		Writer<BlockCsvWriterBean> w1 = new Writer<BlockCsvWriterBean>(writer, 20, 10000, 
				new BlockCsvWriterBean[]{new BlockCsvWriterBean("name1W1-toto"), 
				new BlockCsvWriterBean("name2W1-tu")}, false);
		
		Writer<BlockCsvWriterBean> w2 = new Writer<BlockCsvWriterBean>(writer, 10, 5000, 
				new BlockCsvWriterBean[]{new BlockCsvWriterBean("name1W2;csa"), 
				new BlockCsvWriterBean("name2W2;tututi"), new BlockCsvWriterBean("name3W2;oioi")}, false);
		
		Writer<BlockCsvWriterBean> w3 = new Writer<BlockCsvWriterBean>(writer, 0, 15000, 
				new BlockCsvWriterBean[]{new BlockCsvWriterBean("name1W3/2")}, false);
		
		Thread t1 = new Thread(w1);
		Thread t2 = new Thread(w2);
		Thread t3 = new Thread(w3);
		
		t1.start();
		t2.start();
		t3.start();
		
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){}
		
		writer.close();
		
		Assert.assertFalse(w1.isFail());
		Assert.assertFalse(w2.isFail());
		Assert.assertFalse(w3.isFail());
		
		Assert.assertEquals(50000, w1.getNbWriting() +  w2.getNbWriting() + w3.getNbWriting() );
		File[] files = folder.listFiles();
		Assert.assertNotNull(files);
		Assert.assertEquals(3, files.length);
		
		//file name
		ArrayList<String> names = new ArrayList<String>();
		Map<String, Integer> count = new HashMap<String, Integer>();
		long nbLines = 0;
		for (File f:files){
			names.add(f.getName());
			FileReader file = new FileReader(f);
			BufferedReader br = new BufferedReader(file);
			int nb = 0;
			//header
			Assert.assertEquals("test header", br.readLine());
			Assert.assertEquals("name;value", br.readLine());
			
			String line = br.readLine();
			while (line != null){
				if (line.equals("test footer")){
					break;
				}
				++nbLines;
				++nb;
				int value = 1;
				if (count.containsKey(line)){
					value += count.get(line);
				}
				count.put(line, value);
				line = br.readLine();
			}
			Assert.assertEquals("test footer", line);
			line = br.readLine();
			Assert.assertEquals("retest footer", line);
			line = br.readLine();
			Assert.assertNull(line);
			Assert.assertTrue(18000 >=  nb);
			br.close();
			file.close();
		}
		
		Assert.assertEquals(50000, nbLines);
		Assert.assertEquals(6, count.size());
		Assert.assertEquals(5000, count.get("\"name1W2;csa\";\"11\"").intValue());
		Assert.assertEquals(5000, count.get("\"name2W2;tututi\";\"14\"").intValue());
		Assert.assertEquals(5000, count.get("\"name3W2;oioi\";\"12\"").intValue());
		Assert.assertEquals(10000, count.get("\"name1W1-toto\";\"12\"").intValue());
		Assert.assertEquals(10000, count.get("\"name2W1-tu\";\"10\"").intValue());
		Assert.assertEquals(15000, count.get("\"name1W3/2\";\"9\"").intValue());
		
		Assert.assertTrue(names.contains("block-1.csv"));
		Assert.assertTrue(names.contains("block-2.csv"));
		Assert.assertTrue(names.contains("block-3.csv"));
	}
	
	@Test
	public void closeByThreadWriteTest() throws CsvBangException, IOException{
		FactoryCsvbang factory = new FactoryCsvbang("com.github.lecogiteur.csvbang.test.bean.writer");
		testFolder.create();
		File folder = testFolder.newFolder();
		System.out.println("Folder: " + folder.getAbsolutePath());
		
		CsvWriter<BlockCsvWriterBean> writer = factory.createCsvWriter(BlockCsvWriterBean.class, folder);
		Assert.assertNotNull(writer);
		Assert.assertTrue(writer instanceof BlockCsvWriter);
		
		Writer<BlockCsvWriterBean> w1 = new Writer<BlockCsvWriterBean>(writer, 20, 10000, 
				new BlockCsvWriterBean[]{new BlockCsvWriterBean("name1W1-toto"), 
				new BlockCsvWriterBean("name2W1-tu")}, true);
		
		Writer<BlockCsvWriterBean> w2 = new Writer<BlockCsvWriterBean>(writer, 10, 5000, 
				new BlockCsvWriterBean[]{new BlockCsvWriterBean("name1W2;csa"), 
				new BlockCsvWriterBean("name2W2;tututi"), new BlockCsvWriterBean("name3W2;oioi")}, true);
		
		Writer<BlockCsvWriterBean> w3 = new Writer<BlockCsvWriterBean>(writer, 0, 15000, 
				new BlockCsvWriterBean[]{new BlockCsvWriterBean("name1W3/2")}, true);
		
		Thread t1 = new Thread(w1);
		Thread t2 = new Thread(w2);
		Thread t3 = new Thread(w3);
		
		t1.start();
		t2.start();
		t3.start();
		
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){}
		
		Assert.assertFalse(w1.isFail());
		Assert.assertFalse(w2.isFail());
		Assert.assertFalse(w3.isFail());
		
		File[] files = folder.listFiles();
		Assert.assertNotNull(files);
		Assert.assertEquals(3, files.length);
		
		//file name
		ArrayList<String> names = new ArrayList<String>();
		Map<String, Integer> count = new HashMap<String, Integer>();
		long nbLines = 0;
		for (File f:files){
			names.add(f.getName());
			FileReader file = new FileReader(f);
			BufferedReader br = new BufferedReader(file);
			int nb = 0;
			//header
			Assert.assertEquals("test header", br.readLine());
			Assert.assertEquals("name;value", br.readLine());
			
			String line = br.readLine();
			while (line != null){
				if (line.equals("test footer")){
					break;
				}
				++nbLines;
				++nb;
				int value = 1;
				if (count.containsKey(line)){
					value += count.get(line);
				}
				count.put(line, value);
				line = br.readLine();
			}
			Assert.assertEquals("test footer", line);
			line = br.readLine();
			Assert.assertEquals("retest footer", line);
			line = br.readLine();
			Assert.assertNull(line);
			Assert.assertTrue(18000 >=  nb);
			br.close();
			file.close();
		}
		
		Assert.assertEquals(w1.getNbWriting() +  w2.getNbWriting() + w3.getNbWriting(), nbLines);
		Assert.assertEquals(6, count.size());
		Assert.assertTrue(5000 >= count.get("\"name1W2;csa\";\"11\"").intValue());
		Assert.assertTrue(5000 >= count.get("\"name2W2;tututi\";\"14\"").intValue());
		Assert.assertTrue(5000 >= count.get("\"name3W2;oioi\";\"12\"").intValue());
		Assert.assertTrue(10000 >= count.get("\"name1W1-toto\";\"12\"").intValue());
		Assert.assertTrue(10000 >= count.get("\"name2W1-tu\";\"10\"").intValue());
		Assert.assertTrue(15000 >= count.get("\"name1W3/2\";\"9\"").intValue());
		
		Assert.assertTrue(names.contains("block-1.csv"));
		Assert.assertTrue(names.contains("block-2.csv"));
		Assert.assertTrue(names.contains("block-3.csv"));
	}
}
