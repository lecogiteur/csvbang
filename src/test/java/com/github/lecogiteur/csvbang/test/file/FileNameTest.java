/**
 *  com.github.lecogiteur.csvbang.test.file.FileNameTest
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
package com.github.lecogiteur.csvbang.test.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.FileName;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FileNameTest {

	
	@SuppressWarnings("unused")
	@Test
	public void simpleFileNameTest() throws CsvBangException{
		boolean error = false;
		try {
			FileName name = new FileName(null, null);
		} catch (CsvBangException e) {
			error = true;
		}
		
		if (!error){
			Assert.fail();
		}
		error = false;
		
		try {
			FileName name = new FileName("", null);
		} catch (CsvBangException e) {
			error = true;
		}
		
		if (!error){
			Assert.fail();
		}
		error = false;
		
		try {
			FileName name = new FileName("  ", null);
		} catch (CsvBangException e) {
			error = true;
		}
		
		if (!error){
			Assert.fail();
		}
		error = false;
		
		FileName name = new FileName("test.csv", null);
		Assert.assertEquals("test.csv", name.getNewFileName(false));
		Assert.assertEquals("test.csv", name.getNewFileName(false));//double vérification
		
		FileName nameCloned = name.clone();
		Assert.assertEquals(name, nameCloned);
		
		FileName nameAck = new FileName("test.csv", null);
		Assert.assertEquals(name, nameAck);
		
		
		Assert.assertEquals("test.csv", nameAck.getNewFileName(true));
		Assert.assertEquals("test.csv", nameAck.getNewFileName(true));//double vérification
		nameAck.ackNewFileName();
		Assert.assertEquals("test.csv", nameAck.getNewFileName(true));
	}
	
	@Test
	public void baseDirTest() throws CsvBangException{
		FileName name = new FileName("test.csv", null);
		name.setBaseDirectory("/tmp");
		Assert.assertEquals("/tmp" + File.separator + "test.csv", name.getNewFileName(false));
		name.setBaseDirectory("/tempo" + File.separator);
		Assert.assertEquals("/tempo" + File.separator + "test.csv", name.getNewFileName(false));
		File dir = new File("/tutu");
		name.setBaseDirectory(dir);
		Assert.assertEquals("/tutu" + File.separator + "test.csv", name.getNewFileName(false));
		name.setBaseDirectory("");
		Assert.assertEquals("test.csv", name.getNewFileName(false));
		name.setBaseDirectory((String)null);
		Assert.assertEquals("test.csv", name.getNewFileName(false));
	}
	
	@Test
	public void filenameWithBaseDirAndNumber() throws CsvBangException{
		FileName name = new FileName("test-%n.csv", null);
		name.setBaseDirectory("/tmp");
		Assert.assertEquals("/tmp" + File.separator + "test-1.csv", name.getNewFileName(false));
		name.setBaseDirectory("/tempo/");
		Assert.assertEquals("/tempo" + File.separator + "test-2.csv", name.getNewFileName(false));		
	}
	
	@Test
	public void numberFileTest() throws CsvBangException{
		FileName name = new FileName("testn-%n.csv", null);
		Assert.assertEquals("testn-1.csv", name.getNewFileName(false));
		Assert.assertEquals("testn-2.csv", name.getNewFileName(false));
		
		//with ack
		Assert.assertEquals("testn-3.csv", name.getNewFileName(true));
		Assert.assertEquals("testn-3.csv", name.getNewFileName(true));//verification
		name.ackNewFileName();
		Assert.assertEquals("testn-4.csv", name.getNewFileName(false));		
	}
	
	@SuppressWarnings("unused")
	@Test
	public void dateFileTest() throws CsvBangException{
		boolean error = false;
		try {
			FileName name = new FileName("te%std-%d.csv", null);
		} catch (CsvBangException e) {
			error = true;
		}
		
		if (!error){
			Assert.fail();
		}
		error = false;
		
		Calendar d = Calendar.getInstance();
		int year = d.get(Calendar.YEAR);
		FileName name = new FileName("te%std-%d.csv", "yyyy");
		Assert.assertEquals("te%std-" + year + ".csv", name.getNewFileName(false));
		Assert.assertEquals("te%std-" + year + ".csv", name.getNewFileName(false));
		
		//with ack
		Assert.assertEquals("te%std-" + year + ".csv", name.getNewFileName(true));
		Assert.assertEquals("te%std-" + year + ".csv", name.getNewFileName(true)); //verification
		name.ackNewFileName();
		Assert.assertEquals("te%std-" + year + ".csv", name.getNewFileName(false));
	}
	
	@Test
	public void multiThreadFileNameTest() throws CsvBangException{
		final FileName name = new FileName("te%std-%d.%n.csv", "yyyy");
		final ConcurrentLinkedQueue<String> queue =new ConcurrentLinkedQueue<String>();
		
		
		Thread t1 = new Thread(new RunFileName(queue, 40, name, 1));
		Thread t2 = new Thread(new RunFileName(queue, 20, name, 2));
		Thread t3 = new Thread(new RunFileName(queue, 0, name, 3));
		
		t1.start();
		t2.start();
		t3.start();
		
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){};
		
		Assert.assertEquals(900, queue.size());
		
		List<Integer> l = new ArrayList<Integer>(900);
		for (int i=1;i<901;i++){
			l.add(i);
		}
		
		for(String s:queue){
			String[] a = s.split("\\.");
			l.remove(Integer.valueOf(a[1]));
		}
		
		Assert.assertEquals(0, l.size());
	}
	
	@Test
	public void filenameFilterTest() throws CsvBangException{
		//simple
		FileName file = new FileName("file1.csv", null);
		FilenameFilter filter = file.generateFilter();
		Assert.assertFalse(filter.accept(new File("/tmp/"), ""));
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file2.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "file1.csv"));

		//number
		file = new FileName("file%n.csv", null);
		filter = file.generateFilter();
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file3a3frk.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "file2.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "file1.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "file13.csv"));
		
		//Date
		file = new FileName("file_%d.csv", "dd-MM-yyyy");
		filter = file.generateFilter();
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file3a3frk.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "file_22-12-2014.csv"));
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file_22-20-2014.csv"));
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file_22_12_2014.csv"));
		
		//joker
		file = new FileName("*le1.csv", "dd-MM-yyyy");
		filter = file.generateFilter();
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file1vcsv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "file1.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "tititile1.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/"), "le1.csv"));
		
		//directory
		file = new FileName("tux/*/mydir_%d/file-%n.csv", "yyyyMMdd");
		filter = file.generateFilter();
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file-1.csv"));
		Assert.assertFalse(filter.accept(new File("/tmp/tux"), "file-1.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tux/mtest/mydir_20141221/"), "file-123.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tux/mydir_20141221"), "file-9.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tux/test1/test2_test3/mydir_20141221"), "file-67.csv"));
		
		//directory 2
		file = new FileName("tux*/mydir_%d/file-%n.csv", "yyyyMMdd");
		filter = file.generateFilter();
		Assert.assertFalse(filter.accept(new File("/tmp/"), "file-1.csv"));
		Assert.assertFalse(filter.accept(new File("/tmp/tux"), "file-1.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tux/test/mydir_20141221/"), "file-123.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tux/mydir_20141221"), "file-9.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tuxy/mydir_20141221"), "file-9.csv"));
		Assert.assertTrue(filter.accept(new File("/tmp/tux/test1/test2_test3/mydir_20141221"), "file-67.csv"));
	}
	
	private class RunFileName implements Runnable{
		private final ConcurrentLinkedQueue<String> queue;
		private final Integer millis;
		private final FileName name;
		private final int threadname;
	
		public RunFileName(ConcurrentLinkedQueue<String> queue, Integer millis,
				FileName name, int threadname) {
			super();
			this.queue = queue;
			this.millis = millis;
			this.name = name;
			this.threadname = threadname;
		}


		@Override
		public void run() {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(int i=0; i<300; i++){
				String n=name.getNewFileName(false);
				//System.out.println("Thread " + threadname + " nom: " + n);
				queue.add(n);
			}
		}
		
		public int getThreadname() {
			return threadname;
		}
		
		
		
	}
}
