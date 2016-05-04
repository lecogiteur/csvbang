/**
 *  com.github.lecogiteur.csvbang.test.reader.SimpleReaderTest
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
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
package com.github.lecogiteur.csvbang.test.reader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.FactoryCsvbang;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.reader.CsvReader;
import com.github.lecogiteur.csvbang.test.bean.reader.Read1;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class SimpleReaderTest {
	
	private class ReaderCsv implements Runnable{

		private CsvReader<Read1> reader;
		
		private boolean mustClose = false;
		
		private boolean hasError = false;
		
		private Calendar start;
		
		private Calendar end;
		
		private ConcurrentSkipListSet<Integer> ids;
		
		private int counter = 0;
		
		/**
		 * Constructor
		 * @param reader
		 * @param list
		 * @param mustClose
		 * @throws ParseException 
		 * @since 1.0.0
		 */
		public ReaderCsv(CsvReader<Read1> reader, boolean mustClose, ConcurrentSkipListSet<Integer> ids) throws ParseException {
			super();
			this.reader = reader;
			this.mustClose = mustClose;
			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			start = Calendar.getInstance();
			start.setTime(format.parse("12/03/2016"));
			end = Calendar.getInstance();
			end.add(Calendar.DAY_OF_YEAR, 1999);
			this.ids = ids;
		}

		@Override
		public void run() {
			try {
				while(true){
					final Collection<Read1> read1s = reader.readBlock();
					if (read1s != null){
						for (Read1 r:read1s){
							try{
								Assert.assertNotNull(r);
								Assert.assertNotNull(r.getId());
								Assert.assertTrue(r.getId() > 0 && r.getId() < 2001);
								Assert.assertNotNull(r.getNbChar());
								Assert.assertTrue(r.getNbChar() > 0);
								Assert.assertNotNull(r.getText());
								Assert.assertEquals(r.getNbChar().intValue(), r.getText().length());
								Assert.assertNotNull(r.getDate());
								Assert.assertTrue(start.before(r.getDate()) || start.equals(r.getDate()));
								Assert.assertTrue(end.after(r.getDate()) || start.equals(r.getDate()));
							}catch(Throwable e){
								hasError = true;
								throw new RuntimeException("Erreur with id : " + r.getId(), e);
							}
							ids.remove(r.getId());
							counter++;
						}
						System.out.println(ids.size());
					}else{
						break;
					}
				}
				if (mustClose){
					reader.close();
				}
			} catch (CsvBangCloseException e) {
				e.printStackTrace();
			} catch (CsvBangException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public boolean hasError(){
			return hasError;
		}
		
	}

	@Test
	public void should_read_a_file() throws URISyntaxException, CsvBangException, ParseException, IOException{
		File file = new File(this.getClass().getResource("/csvbang/read/read1.csv").toURI());
		
		final FactoryCsvbang factory = new FactoryCsvbang();
		final CsvReader<Read1> reader = factory.createCsvReader(Read1.class, new FileName(file.getAbsolutePath(), null));
		ConcurrentSkipListSet<Integer> ids = new ConcurrentSkipListSet<Integer>();
		for (int i=1; i<2001; i++){
			ids.add(i);
		}
		
		
		ReaderCsv r1 = new ReaderCsv(reader, false, ids);
		ReaderCsv r2 = new ReaderCsv(reader, false, ids);
		ReaderCsv r3 = new ReaderCsv(reader, false, ids);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		
		t1.start();
		t2.start();
		t3.start();
		while (t1.isAlive() || t2.isAlive() || t3.isAlive());
		
		reader.close();
		
		Assert.assertFalse(r1.hasError);
		Assert.assertFalse(r2.hasError);
		Assert.assertFalse(r3.hasError);
		
		Assert.assertEquals(2000, r1.counter + r2.counter + r3.counter);
		Assert.assertEquals(0, ids.size());
		
	}
	
	@Test
	public void should_read_a_file2() throws URISyntaxException, CsvBangException, ParseException, IOException{
		File file = new File(this.getClass().getResource("/csvbang/read/read2.csv").toURI());
		
		final FactoryCsvbang factory = new FactoryCsvbang();
		final CsvReader<Read1> reader = factory.createCsvReader(Read1.class, new FileName(file.getAbsolutePath(), null));
		ConcurrentSkipListSet<Integer> ids = new ConcurrentSkipListSet<Integer>();
		for (int i=1980; i<2001; i++){
			ids.add(i);
		}
		
		
		ReaderCsv r1 = new ReaderCsv(reader, false, ids);
		ReaderCsv r2 = new ReaderCsv(reader, false, ids);
		ReaderCsv r3 = new ReaderCsv(reader, false, ids);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		
		t1.start();
		t2.start();
		t3.start();
		while (t1.isAlive() || t2.isAlive() || t3.isAlive());
		
		reader.close();
		
		Assert.assertFalse(r1.hasError);
		Assert.assertFalse(r2.hasError);
		Assert.assertFalse(r3.hasError);
		
		Assert.assertEquals(23, r1.counter + r2.counter + r3.counter);
		Assert.assertEquals(0, ids.size());
		
	}
}
