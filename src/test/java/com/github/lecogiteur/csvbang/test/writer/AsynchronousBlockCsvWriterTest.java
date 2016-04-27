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
import com.github.lecogiteur.csvbang.test.bean.writer.AsynchronousBlockCsvWriterBean;
import com.github.lecogiteur.csvbang.util.AbstractDelegatedWithRegisterThread;
import com.github.lecogiteur.csvbang.writer.AsynchronousBlockCsvWriter;
import com.github.lecogiteur.csvbang.writer.CsvWriter;
import com.github.lecogiteur.csvbang.writer.DelegatedWriterWithRegisterThreadCsvWriter;

@RunWith(BlockJUnit4ClassRunner.class)
public class AsynchronousBlockCsvWriterTest {
	
	private class Writer<T> implements Runnable{
		private final CsvWriter<T> w;
		private final Integer millis;
		private final Integer nbSamples;
		private final T[] samples;
		private long nbWriting = 0;
		private boolean fail = false;

		public Writer(CsvWriter<T> w, Integer millis, Integer nbSamples, T[] samples) {
			super();
			this.w = w;
			this.millis = millis;
			this.nbSamples = nbSamples;
			this.samples = samples;
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
					w.write(samples);
					nbWriting += samples.length;
				} catch (CsvBangException e) {
					e.printStackTrace();
					fail = true;
				} catch (CsvBangCloseException e) {
					e.printStackTrace();
					fail = true;
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
	public void simpleWrite() throws CsvBangException, IOException{
		FactoryCsvbang factory = new FactoryCsvbang("com.github.lecogiteur.csvbang.test.bean.writer");
		File folder = testFolder.newFolder();
		System.out.println("Folder: " + folder.getAbsolutePath());
		
		CsvWriter<AsynchronousBlockCsvWriterBean> writer = factory.createCsvWriter(AsynchronousBlockCsvWriterBean.class, folder);
		Assert.assertNotNull(writer);
		Assert.assertTrue(writer instanceof DelegatedWriterWithRegisterThreadCsvWriter);
		Assert.assertTrue(((AbstractDelegatedWithRegisterThread)writer).getActor() instanceof AsynchronousBlockCsvWriter);
		
		Writer<AsynchronousBlockCsvWriterBean> w1 = new Writer<AsynchronousBlockCsvWriterBean>(writer, 20, 10000, 
				new AsynchronousBlockCsvWriterBean[]{new AsynchronousBlockCsvWriterBean("name1W1", "value1W1"), 
				new AsynchronousBlockCsvWriterBean("name2W1", "value2W1")});
		
		Writer<AsynchronousBlockCsvWriterBean> w2 = new Writer<AsynchronousBlockCsvWriterBean>(writer, 10, 5000, 
				new AsynchronousBlockCsvWriterBean[]{new AsynchronousBlockCsvWriterBean("name1W2", "value1W2"), 
				new AsynchronousBlockCsvWriterBean("name2W2", "value2W2"), new AsynchronousBlockCsvWriterBean("name3W2", "value3W2")});
		
		Writer<AsynchronousBlockCsvWriterBean> w3 = new Writer<AsynchronousBlockCsvWriterBean>(writer, 0, 15000, 
				new AsynchronousBlockCsvWriterBean[]{new AsynchronousBlockCsvWriterBean("name1W3", "value1W3")});
		
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
			Assert.assertEquals("Name,Value", br.readLine());
			
			String line = br.readLine();
			while (line != null){
				++nbLines;
				++nb;
				int value = 1;
				if (count.containsKey(line)){
					value += count.get(line);
				}
				count.put(line, value);
				line = br.readLine();
			}
			
			Assert.assertTrue(18000 >=  nb);
			br.close();
			file.close();
		}
		
		Assert.assertEquals(50000, nbLines);
		Assert.assertEquals(6, count.size());
		Assert.assertEquals(5000, count.get("name1W2,value1W2").intValue());
		Assert.assertEquals(5000, count.get("name2W2,value2W2").intValue());
		Assert.assertEquals(5000, count.get("name3W2,value3W2").intValue());
		Assert.assertEquals(10000, count.get("name1W1,value1W1").intValue());
		Assert.assertEquals(10000, count.get("name2W1,value2W1").intValue());
		Assert.assertEquals(15000, count.get("name1W3,value1W3").intValue());
		
		Assert.assertTrue(names.contains("async-1.csv"));
		Assert.assertTrue(names.contains("async-2.csv"));
		Assert.assertTrue(names.contains("async-3.csv"));
	}
}
