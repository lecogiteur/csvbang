package com.github.lecogiteur.csvbang.test.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.github.lecogiteur.csvbang.test.bean.writer.CommentWriterBean;
import com.github.lecogiteur.csvbang.test.bean.writer.SimpleWriterBean;
import com.github.lecogiteur.csvbang.writer.CsvWriter;
import com.github.lecogiteur.csvbang.writer.SimpleCsvWriter;

@RunWith(BlockJUnit4ClassRunner.class)
public class SimpleCsvWriterTest {

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
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		String cf = format.format(c.getTime());
		FactoryCsvbang factory = new FactoryCsvbang("com.github.lecogiteur.csvbang.test.bean.writer");
		File folder = testFolder.newFolder();
		System.out.println("Folder: " + folder.getAbsolutePath());
		
		CsvWriter<CommentWriterBean> writer = factory.createCsvWriter(CommentWriterBean.class, folder);
		Assert.assertNotNull(writer);
		Assert.assertTrue(writer instanceof SimpleCsvWriter);
		
		Writer<CommentWriterBean> w1 = new Writer<CommentWriterBean>(writer, 20, 10000, 
				new CommentWriterBean[]{new CommentWriterBean(1, "name1W1", c, 18.3333), 
				new CommentWriterBean(2, "name2W1", null, 23.54)}, false);
		
		Writer<CommentWriterBean> w2 = new Writer<CommentWriterBean>(writer, 10, 5000, 
				new CommentWriterBean[]{new CommentWriterBean(3, "name1W2", c, 34434.34345356), 
				new CommentWriterBean(4, "name2W2", c, 64.4534), new CommentWriterBean(5, "name3W2", c, 128.0)}, false);
		
		Writer<CommentWriterBean> w3 = new Writer<CommentWriterBean>(writer, 0, 15000, 
				new CommentWriterBean[]{new CommentWriterBean(6, "name1W3", c, 1348.3333)}, false);
		
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
		Assert.assertEquals(1, files.length);
		
		//file name
		ArrayList<String> names = new ArrayList<String>();
		Map<String, Integer> count = new HashMap<String, Integer>();
		long nbLines = 0;
		long nbCommentTotal = 0;
		for (File f:files){
			names.add(f.getName());
			FileReader file = new FileReader(f);
			BufferedReader br = new BufferedReader(file);
			
			String line = br.readLine();
			int nbCommentBeforeLine = 0;
			while (line != null){
				if (line.startsWith("#")){
					nbCommentTotal++;
					nbCommentBeforeLine++;
				}else{
					Assert.assertEquals(3, nbCommentBeforeLine);
					++nbLines;
					nbCommentBeforeLine = -1;
				}
				int value = 1;
				if (count.containsKey(line)){
					value += count.get(line);
				}
				count.put(line, value);
				line = br.readLine();
			}
			
			br.close();
			file.close();
		}
		
		Assert.assertEquals(50000, nbLines);
		Assert.assertEquals(200000, nbCommentTotal);
		Assert.assertEquals(15, count.size());
		Assert.assertEquals(5000, count.get("3,name1W2,public Name: name1W2," + cf + ",34434.34345356").intValue());
		Assert.assertEquals(5000, count.get("4,name2W2,public Name: name2W2," + cf + ",64.4534").intValue());
		Assert.assertEquals(5000, count.get("5,name3W2,public Name: name3W2," + cf + ",128.0").intValue());
		Assert.assertEquals(10000, count.get("1,name1W1,public Name: name1W1," + cf + ",18.3333").intValue());
		Assert.assertEquals(10000, count.get("2,name2W1,public Name: name2W1,no date,23.54").intValue());
		Assert.assertEquals(15000, count.get("6,name1W3,public Name: name1W3," + cf + ",1348.3333").intValue());
		Assert.assertEquals(50000, count.get("#my comment").intValue());
		Assert.assertEquals(50000, count.get("#toto").intValue());
		Assert.assertEquals(5000, count.get("#name1W2").intValue());
		Assert.assertEquals(5000, count.get("#name2W2").intValue());
		Assert.assertEquals(5000, count.get("#name3W2").intValue());
		Assert.assertEquals(10000, count.get("#name1W1").intValue());
		Assert.assertEquals(10000, count.get("#name2W1").intValue());
		Assert.assertEquals(15000, count.get("#name1W3").intValue());
		Assert.assertEquals(50000, count.get("#145.15").intValue());
		
		Assert.assertTrue(names.contains("out-1.csv"));
	}
	
	@Test
	public void closeByThreadWriteTest() throws CsvBangException, IOException{
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		String cf = format.format(c.getTime());
		FactoryCsvbang factory = new FactoryCsvbang("com.github.lecogiteur.csvbang.test.bean.writer");
		File folder = testFolder.newFolder();
		System.out.println("Folder: " + folder.getAbsolutePath());
		
		CsvWriter<CommentWriterBean> writer = factory.createCsvWriter(CommentWriterBean.class, folder);
		Assert.assertNotNull(writer);
		Assert.assertTrue(writer instanceof SimpleCsvWriter);
		
		Writer<CommentWriterBean> w1 = new Writer<CommentWriterBean>(writer, 20, 10000, 
				new CommentWriterBean[]{new CommentWriterBean(1, "name1W1", c, 18.3333), 
				new CommentWriterBean(2, "name2W1", null, 23.54)}, true);
		
		Writer<CommentWriterBean> w2 = new Writer<CommentWriterBean>(writer, 10, 5000, 
				new CommentWriterBean[]{new CommentWriterBean(3, "name1W2", c, 34434.34345356), 
				new CommentWriterBean(4, "name2W2", c, 64.4534), new CommentWriterBean(5, "name3W2", c, 128.0)}, true);
		
		Writer<CommentWriterBean> w3 = new Writer<CommentWriterBean>(writer, 0, 15000, 
				new CommentWriterBean[]{new CommentWriterBean(6, "name1W3", c, 1348.3333)}, true);
		
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
		Assert.assertEquals(1, files.length);
		
		//file name
		ArrayList<String> names = new ArrayList<String>();
		Map<String, Integer> count = new HashMap<String, Integer>();
		long nbLines = 0;
		long nbCommentTotal = 0;
		for (File f:files){
			names.add(f.getName());
			FileReader file = new FileReader(f);
			BufferedReader br = new BufferedReader(file);
			
			String line = br.readLine();
			int nbCommentBeforeLine = 0;
			while (line != null){
				if (line.startsWith("#")){
					nbCommentTotal++;
					nbCommentBeforeLine++;
				}else{
					Assert.assertEquals(3, nbCommentBeforeLine);
					++nbLines;
					nbCommentBeforeLine = -1;
				}
				int value = 1;
				if (count.containsKey(line)){
					value += count.get(line);
				}
				count.put(line, value);
				line = br.readLine();
			}
			
			br.close();
			file.close();
		}
		
		Assert.assertEquals(w1.getNbWriting() +  w2.getNbWriting() + w3.getNbWriting(), nbLines);
		Assert.assertEquals(4*nbLines, nbCommentTotal);
		Assert.assertEquals(15, count.size());
		Assert.assertTrue(5000 >= count.get("3,name1W2,public Name: name1W2," + cf + ",34434.34345356").intValue());
		Assert.assertTrue(5000 >= count.get("4,name2W2,public Name: name2W2," + cf + ",64.4534").intValue());
		Assert.assertTrue(5000 >= count.get("5,name3W2,public Name: name3W2," + cf + ",128.0").intValue());
		Assert.assertTrue(10000 >= count.get("1,name1W1,public Name: name1W1," + cf + ",18.3333").intValue());
		Assert.assertTrue(10000 >= count.get("2,name2W1,public Name: name2W1,no date,23.54").intValue());
		Assert.assertTrue(15000 >= count.get("6,name1W3,public Name: name1W3," + cf + ",1348.3333").intValue());
		Assert.assertEquals(nbLines, count.get("#my comment").intValue());
		Assert.assertEquals(nbLines, count.get("#toto").intValue());
		Assert.assertEquals(count.get("3,name1W2,public Name: name1W2," + cf + ",34434.34345356").intValue(), count.get("#name1W2").intValue());
		Assert.assertEquals(count.get("4,name2W2,public Name: name2W2," + cf + ",64.4534").intValue(), count.get("#name2W2").intValue());
		Assert.assertEquals(count.get("5,name3W2,public Name: name3W2," + cf + ",128.0").intValue(), count.get("#name3W2").intValue());
		Assert.assertEquals(count.get("1,name1W1,public Name: name1W1," + cf + ",18.3333").intValue(), count.get("#name1W1").intValue());
		Assert.assertEquals(count.get("2,name2W1,public Name: name2W1,no date,23.54").intValue(), count.get("#name2W1").intValue());
		Assert.assertEquals(count.get("6,name1W3,public Name: name1W3," + cf + ",1348.3333").intValue(), count.get("#name1W3").intValue());
		Assert.assertEquals(nbLines, count.get("#145.15").intValue());
		
		Assert.assertTrue(names.contains("out-1.csv"));
	}
		
}
