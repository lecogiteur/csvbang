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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.CsvFilePoolFactory;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.test.bean.writer.CommentWriterBean;
import com.github.lecogiteur.csvbang.test.bean.writer.NoEndRecordWithCommentWriterBean;
import com.github.lecogiteur.csvbang.test.bean.writer.NoEndRecordWriterBean;
import com.github.lecogiteur.csvbang.test.bean.writer.SimpleWriterBean;
import com.github.lecogiteur.csvbang.util.Comment;
import com.github.lecogiteur.csvbang.util.ConfigurationUti;


@RunWith(BlockJUnit4ClassRunner.class)
public class WriterTest {

	
	private SimpleWriterTest<SimpleWriterBean> getSimpleWriter() throws CsvBangException{
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(SimpleWriterBean.class);
		CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (String)null, null, null);
		return new SimpleWriterTest<SimpleWriterBean>(pool, conf);
	}
	
	@Test
	public void simpleTest() throws CsvBangException, CsvBangCloseException{
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
	public void simple2Test() throws CsvBangException, CsvBangCloseException{
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
	
	@Test
	public void simple3Test() throws CsvBangException, CsvBangCloseException{
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
		result += "\n#super name,public Name: super name," + format.format(c2.getTime()) + ",1287.45";
		
		SimpleWriterBean bean3 = new SimpleWriterBean();
		bean3.setId(125874);
		bean3.setPrice(1287.45);
		bean3.setName("super name");
		result += "\n125874,super name,public Name: super name,no date,1287.45\n";
		
		writer.write(bean);
		writer.comment(bean2);
		writer.write(bean3);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
	
	@Test
	public void simple4Test() throws CsvBangException, CsvBangCloseException{
		SimpleWriterTest<SimpleWriterBean> writer = getSimpleWriter();
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		
		SimpleWriterBean bean = new SimpleWriterBean();
		Calendar c = Calendar.getInstance();
		bean.setBirthday(c);
		bean.setId(125874);
		bean.setName("the name");
		String result = "125874,the name,public Name: the name," + format.format(c.getTime()) +",";
		
		Comment comment1 = new Comment("my comment");
		result += "\n#my comment";
		
		SimpleWriterBean bean2 = new SimpleWriterBean();
		Calendar c2 = Calendar.getInstance();
		bean2.setBirthday(c2);
		bean2.setPrice(1287.45);
		bean2.setName("super name");
		result += "\nsuper name,public Name: super name," + format.format(c2.getTime()) + ",1287.45";
		
		Comment comment2 = new Comment("\nmy comment line 1\r\nmy line 2\n my line 3 \n");
		result += "\n#\n#my comment line 1\r\n#my line 2\n# my line 3 ";
		
		SimpleWriterBean bean3 = new SimpleWriterBean();
		bean3.setId(125874);
		bean3.setPrice(1287.45);
		bean3.setName("super name");
		result += "\n125874,super name,public Name: super name,no date,1287.45\n";
		
		writer.write(bean);
		writer.comment(comment1);
		writer.write(bean2);
		writer.comment(comment2);
		writer.write(bean3);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
	
	@Test
	public void simpleWithCommentTest() throws CsvBangException, CsvBangCloseException{

		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(CommentWriterBean.class);
		CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (String)null, null, null);
		SimpleWriterTest<CommentWriterBean> writer = new SimpleWriterTest<CommentWriterBean>(pool, conf);
		
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		
		String getMyComment = "#my comment\r\n#toto\n";
		String getName = "#the name\n";
		String before = "";
		for (AnnotatedElement e: conf.commentsBefore){
			if (!"getName".equals(((Member)e).getName())){
				before += getName;
			}else{
				before += getMyComment;
			}
		}
		
		CommentWriterBean bean = new CommentWriterBean();
		Calendar c = Calendar.getInstance();
		bean.setBirthday(c);
		bean.setId(125874);
		bean.setName("the name");
		String result = before + "125874,the name,public Name: the name," + format.format(c.getTime()) +",\n#145.15\n";
		
		CommentWriterBean bean2 = new CommentWriterBean();
		Calendar c2 = Calendar.getInstance();
		bean2.setBirthday(c2);
		bean2.setPrice(1287.45);
		bean2.setName("the name");
		result += before + "the name,public Name: the name," + format.format(c2.getTime()) + ",1287.45\n#145.15\n";
		
		CommentWriterBean bean3 = new CommentWriterBean();
		bean3.setId(125874);
		bean3.setPrice(1287.45);
		bean3.setName("the name");
		result += before + "125874,the name,public Name: the name,no date,1287.45\n#145.15\n";
		
		writer.write(bean);
		writer.write(bean2);
		writer.write(bean3);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
	
	//@Test
	public void noEndRecordTest() throws CsvBangException, CsvBangCloseException{

		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(NoEndRecordWriterBean.class);
		CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (String)null, null, null);
		SimpleWriterTest<NoEndRecordWriterBean> writer = new SimpleWriterTest<NoEndRecordWriterBean>(pool, conf);
		
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		
		NoEndRecordWriterBean bean = new NoEndRecordWriterBean();
		Calendar c = Calendar.getInstance();
		bean.setBirthday(c);
		bean.setId(125874);
		bean.setName("the name");
		String result = "125874,the name,public Name: the name," + format.format(c.getTime()) +",";
		
		NoEndRecordWriterBean bean2 = new NoEndRecordWriterBean();
		Calendar c2 = Calendar.getInstance();
		bean2.setBirthday(c2);
		bean2.setPrice(1287.45);
		bean2.setName("super name");
		result += "\n#super name,public Name: super name," + format.format(c2.getTime()) + ",1287.45";
		
		NoEndRecordWriterBean bean3 = new NoEndRecordWriterBean();
		bean3.setId(125874);
		bean3.setPrice(1287.45);
		bean3.setName("super name");
		result += "\n125874,super name,public Name: super name,no date,1287.45";
		
		writer.write(bean);
		writer.comment(bean2);
		writer.write(bean3);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
	
	@Test
	public void noEndRecordWithCommentTest() throws CsvBangException, CsvBangCloseException{

		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(NoEndRecordWithCommentWriterBean.class);
		CsvFilePool pool = CsvFilePoolFactory.createPool(conf, (String)null, null, null);
		SimpleWriterTest<NoEndRecordWithCommentWriterBean> writer = new SimpleWriterTest<NoEndRecordWithCommentWriterBean>(pool, conf);
		
		SimpleDateFormat format = new SimpleDateFormat(SimpleWriterBean.DATE_PATTERN);
		
		NoEndRecordWithCommentWriterBean bean = new NoEndRecordWithCommentWriterBean();
		Calendar c = Calendar.getInstance();
		bean.setBirthday(c);
		bean.setId(125874);
		bean.setName("the name");
		String result = "125874,the name,public Name: the name," + format.format(c.getTime()) +",\n#a comment";
		
		NoEndRecordWithCommentWriterBean bean2 = new NoEndRecordWithCommentWriterBean();
		Calendar c2 = Calendar.getInstance();
		bean2.setBirthday(c2);
		bean2.setPrice(1287.45);
		bean2.setName("super name");
		result += "\n#super name,public Name: super name," + format.format(c2.getTime()) + ",1287.45\n#a comment";
		
		NoEndRecordWithCommentWriterBean bean3 = new NoEndRecordWithCommentWriterBean();
		bean3.setId(125874);
		bean3.setPrice(1287.45);
		bean3.setName("super name");
		result += "\n125874,super name,public Name: super name,no date,1287.45\n#a comment\n";
		
		writer.write(bean);
		writer.comment(bean2);
		writer.write(bean3);
		
		Assert.assertEquals(result, writer.getResult());
	
	}
	
	
}
