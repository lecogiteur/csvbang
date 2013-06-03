/**
 *  com.github.lecogiteur.csvbang.test.writer.WriterTest
 * 
 * 
 *  Copyright (C) 2013  Tony EMMA
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.test.bean.writer.SimpleWriterBean;
import com.github.lecogiteur.csvbang.util.ConfigurationUti;


@RunWith(BlockJUnit4ClassRunner.class)
public class WriterTest {

	
	private SimpleWriterTest<SimpleWriterBean> getSimpleWriter() throws CsvBangException{
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(SimpleWriterBean.class);
		return new SimpleWriterTest<SimpleWriterBean>(conf);
	}
	
	@Test
	public void simpleTest() throws CsvBangException{
		SimpleWriterTest<SimpleWriterBean> writer = getSimpleWriter();
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		
		SimpleWriterBean bean = new SimpleWriterBean();
		Calendar c = Calendar.getInstance();
		bean.setBirthday(c);
		bean.setId(125874);
		bean.setPrice(28.35);
		bean.setName("the name");
		String result = "125874,the name,public Name: the name," + format.format(c.getTime()) + ",28.35";
		
		SimpleWriterBean bean2 = new SimpleWriterBean();
		Calendar c2 = Calendar.getInstance();
		bean2.setBirthday(c2);
		bean2.setId(51445100);
		bean2.setPrice(1287.45);
		bean2.setName("super name");
		result += "\n51445100,super name,public Name: super name," + format.format(c2.getTime()) + ",1287.45\n";
		
		writer.write(bean);
		writer.write(bean2);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
	
	@Test
	public void simple2Test() throws CsvBangException{
		SimpleWriterTest<SimpleWriterBean> writer = getSimpleWriter();
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		
		SimpleWriterBean bean = new SimpleWriterBean();
		Calendar c = Calendar.getInstance();
		bean.setBirthday(c);
		bean.setId(125874);
		bean.setName("the name");
		String result = "125874,the name,public Name: the name," + format.format(c.getTime()) +",";
		
		SimpleWriterBean bean2 = new SimpleWriterBean();
		Calendar c2 = Calendar.getInstance();
		bean2.setBirthday(c2);
		bean2.setPrice(1287.45);
		bean2.setName("super name");
		result += "\nsuper name,public Name: super name," + format.format(c2.getTime()) + ",1287.45";
		
		SimpleWriterBean bean3 = new SimpleWriterBean();
		bean3.setId(125874);
		bean3.setPrice(1287.45);
		bean3.setName("super name");
		result += "\n125874,super name,public Name: super name,no date,1287.45\n";
		
		writer.write(bean);
		writer.write(bean2);
		writer.write(bean3);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
}
