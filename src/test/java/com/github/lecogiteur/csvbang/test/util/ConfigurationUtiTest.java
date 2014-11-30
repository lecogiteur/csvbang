/**
 *  com.github.lecogiteur.csvbang.test.util.ConfigurationUtiTest
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
package com.github.lecogiteur.csvbang.test.util;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.formatter.DateCsvFormatter;
import com.github.lecogiteur.csvbang.formatter.Default;
import com.github.lecogiteur.csvbang.test.bean.configuration.Child2SimpleConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.Child3SimpleConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.ChildSimpleConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.Final2ConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.Final3ConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.FinalConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.Simple3ConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.configuration.SimpleConfigurationBean;
import com.github.lecogiteur.csvbang.util.ConfigurationUti;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ConfigurationUtiTest {
	
	@Test
	public void simpleConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(SimpleConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_BLOCKING_SIZE, conf.blockSize);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER, conf.escapeQuoteCharacter);
		Assert.assertNull(conf.quote);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_CHARSET, conf.charset);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_DELIMITER, conf.delimiter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_END_LINE.toString(), conf.endRecord);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_END_LINE, conf.defaultEndLineCharacter);
		Assert.assertEquals("out-1.csv", conf.fileName.getNewFileName(false));
		Assert.assertNull(conf.header);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_APPEND_FILE, conf.isAppendToFile);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE, conf.isAsynchronousWrite);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_HEADER, conf.isDisplayHeader);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_START_RECORD, conf.startRecord);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_COMMENT_CHARACTER, conf.commentCharacter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_NO_END_RECORD, conf.noEndRecordOnLastRecord);
		
		
		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment", "getName"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment"});
		Assert.assertEquals(2, conf.commentsBefore.size());
		Assert.assertEquals(1, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}
		
		

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(4, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(1, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("No Date", e.nullReplaceString);
		Assert.assertEquals(3, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof DateCsvFormatter);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("customMethod", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	@Test
	public void childSimpleConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(ChildSimpleConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_BLOCKING_SIZE, conf.blockSize);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER, conf.escapeQuoteCharacter);
		Assert.assertNull(conf.quote);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_CHARSET, conf.charset);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_DELIMITER, conf.delimiter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_END_RECORD, conf.endRecord);
		Assert.assertEquals("out-1.csv", conf.fileName.getNewFileName(false));
		Assert.assertNull(conf.header);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_APPEND_FILE, conf.isAppendToFile);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE, conf.isAsynchronousWrite);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_HEADER, conf.isDisplayHeader);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_START_RECORD, conf.startRecord);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_COMMENT_CHARACTER, conf.commentCharacter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_NO_END_RECORD, conf.noEndRecordOnLastRecord);
		
		
		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment", "getName"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment"});
		Assert.assertEquals(2, conf.commentsBefore.size());
		Assert.assertEquals(1, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(5, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(true, e.isDeleteFieldIfNull);
		Assert.assertEquals("No Date", e.nullReplaceString);
		Assert.assertEquals(3, e.position);
		Assert.assertNull(e.format);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("The Name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(5, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("old", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(8, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("isOld", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("customMethod", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("0", e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(4);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	@Test
	public void finalConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(FinalConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(20, conf.blockSize);
		Assert.assertEquals('\'', conf.escapeQuoteCharacter);
		Assert.assertEquals(new Character('\''), conf.quote);
		Assert.assertEquals(Charset.forName("ISO-8859-1"), conf.charset);
		Assert.assertEquals("||", conf.delimiter);
		Assert.assertEquals("\nEND\n", conf.endRecord);
		Assert.assertEquals("test.csv", conf.fileName.getNewFileName(false));
		Assert.assertEquals(true, conf.isDisplayHeader);
		Assert.assertEquals("*The Name||old||custom||date||TheYear\nEND\n", conf.header);
		Assert.assertEquals(true, conf.isAppendToFile);
		Assert.assertEquals(true, conf.isAsynchronousWrite);
		Assert.assertEquals("*", conf.startRecord);
		Assert.assertEquals('%', conf.commentCharacter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_NO_END_RECORD, conf.noEndRecordOnLastRecord);
		
		
		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment", "getName"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment"});
		Assert.assertEquals(2, conf.commentsBefore.size());
		Assert.assertEquals(1, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(5, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("The Name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(5, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("old", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(8, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("isOld", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("custom", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("0", e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(true, e.isDeleteFieldIfNull);
		Assert.assertEquals("No Date", e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(4);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	
	@Test
	public void child2SimpleConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(Child2SimpleConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(20, conf.blockSize);
		Assert.assertEquals('\'', conf.escapeQuoteCharacter);
		Assert.assertEquals(new Character('\''), conf.quote);
		Assert.assertEquals(Charset.forName("ISO-8859-1"), conf.charset);
		Assert.assertEquals("||", conf.delimiter);
		Assert.assertEquals("\nEND\n", conf.endRecord);
		Assert.assertEquals("test.csv", conf.fileName.getNewFileName(false));
		Assert.assertEquals(true, conf.isDisplayHeader);
		Assert.assertEquals("*date||The Name||old||customMethod||TheYear\nEND\n", conf.header);
		Assert.assertEquals(true, conf.isAppendToFile);
		Assert.assertEquals(true, conf.isAsynchronousWrite);
		Assert.assertEquals("*", conf.startRecord);
		Assert.assertEquals('%', conf.commentCharacter);
		Assert.assertEquals(true, conf.noEndRecordOnLastRecord);
		
		

		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment", "getName"});
		Assert.assertEquals(1, conf.commentsBefore.size());
		Assert.assertEquals(2, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}
		
		

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(5, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(true, e.isDeleteFieldIfNull);
		Assert.assertEquals("No Date", e.nullReplaceString);
		Assert.assertEquals(3, e.position);
		Assert.assertNull(e.format);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("The Name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(5, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("old", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(8, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("isOld", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("customMethod", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("0", e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(4);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	@Test
	public void final2ConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(Final2ConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(20, conf.blockSize);
		Assert.assertEquals('\'', conf.escapeQuoteCharacter);
		Assert.assertEquals(new Character('\''), conf.quote);
		Assert.assertEquals(Charset.forName("ISO-8859-1"), conf.charset);
		Assert.assertEquals("||", conf.delimiter);
		Assert.assertEquals("\nEND\n", conf.endRecord);
		Assert.assertEquals("test2.csv", conf.fileName.getNewFileName(false));
		Assert.assertEquals(true, conf.isDisplayHeader);
		Assert.assertEquals("**The Name||old||custom||date||TheYear\nEND\n", conf.header);
		Assert.assertEquals(true, conf.isAppendToFile);
		Assert.assertEquals(true, conf.isAsynchronousWrite);
		Assert.assertEquals("**", conf.startRecord);
		Assert.assertEquals('%', conf.commentCharacter);
		Assert.assertEquals(true, conf.noEndRecordOnLastRecord);
		
		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment", "getName"});
		Assert.assertEquals(1, conf.commentsBefore.size());
		Assert.assertEquals(2, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(5, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("The Name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(5, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("old", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(8, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("isOld", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("custom", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("0", e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(true, e.isDeleteFieldIfNull);
		Assert.assertEquals("No Date", e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(4);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	@Test
	public void simple3ConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(Simple3ConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_BLOCKING_SIZE, conf.blockSize);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER, conf.escapeQuoteCharacter);
		Assert.assertNull(conf.quote);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_CHARSET, conf.charset);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_DELIMITER, conf.delimiter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_END_RECORD, conf.endRecord);
		Assert.assertEquals("out-1.csv", conf.fileName.getNewFileName(false));
		Assert.assertEquals("a custom header\n", conf.header);
		Assert.assertEquals("the custom footer", conf.footer);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_APPEND_FILE, conf.isAppendToFile);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE, conf.isAsynchronousWrite);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_HEADER, conf.isDisplayHeader);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_START_RECORD, conf.startRecord);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_COMMENT_CHARACTER, conf.commentCharacter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_NO_END_RECORD, conf.noEndRecordOnLastRecord);
		
		
		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment", "getName"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment"});
		Assert.assertEquals(2, conf.commentsBefore.size());
		Assert.assertEquals(1, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}
		
		

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(4, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(1, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("No Date", e.nullReplaceString);
		Assert.assertEquals(3, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof DateCsvFormatter);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("customMethod", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	@Test
	public void child3SimpleConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(Child3SimpleConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(20, conf.blockSize);
		Assert.assertEquals('\'', conf.escapeQuoteCharacter);
		Assert.assertEquals(new Character('\''), conf.quote);
		Assert.assertEquals(Charset.forName("ISO-8859-1"), conf.charset);
		Assert.assertEquals("||", conf.delimiter);
		Assert.assertEquals("\nEND\n", conf.endRecord);
		Assert.assertEquals("test.csv", conf.fileName.getNewFileName(false));
		Assert.assertEquals(true, conf.isDisplayHeader);
		Assert.assertEquals("a custom header\n*date||The Name||old||customMethod||TheYear\nEND\n", conf.header);
		Assert.assertEquals("the custom footer", conf.footer);
		Assert.assertEquals(true, conf.isAppendToFile);
		Assert.assertEquals(true, conf.isAsynchronousWrite);
		Assert.assertEquals("*", conf.startRecord);
		Assert.assertEquals('%', conf.commentCharacter);
		Assert.assertEquals(true, conf.noEndRecordOnLastRecord);
		
		

		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment", "getName"});
		Assert.assertEquals(1, conf.commentsBefore.size());
		Assert.assertEquals(2, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}
		
		

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(5, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(true, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(3, e.position);
		Assert.assertNull(e.format);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("The Name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(5, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("old", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(8, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("isOld", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("customMethod", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("0", e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(4);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
	
	@Test
	public void final3ConfigurationTest() throws CsvBangException {
		CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(Final3ConfigurationBean.class);
		
		Assert.assertNotNull(conf);
		Assert.assertEquals(20, conf.blockSize);
		Assert.assertEquals('\'', conf.escapeQuoteCharacter);
		Assert.assertEquals(new Character('\''), conf.quote);
		Assert.assertEquals(Charset.forName("ISO-8859-1"), conf.charset);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_DELIMITER, conf.delimiter);
		Assert.assertEquals("\nEND\n", conf.endRecord);
		Assert.assertEquals("test2.csv", conf.fileName.getNewFileName(false));
		Assert.assertEquals(false, conf.isDisplayHeader);
		Assert.assertNull(conf.header);
		Assert.assertNull(conf.footer);
		Assert.assertEquals(true, conf.isAppendToFile);
		Assert.assertEquals(true, conf.isAsynchronousWrite);
		Assert.assertEquals("**", conf.startRecord);
		Assert.assertEquals('%', conf.commentCharacter);
		Assert.assertEquals(false, conf.noEndRecordOnLastRecord);
		
		Assert.assertNotNull(conf.commentsBefore);
		Assert.assertNotNull(conf.commentsAfter);
		List<String> commentBefore = Arrays.asList(new String[]{"getDoubleComment"});
		List<String> commentAfter = Arrays.asList(new String[]{"getMyComment", "getName"});
		Assert.assertEquals(1, conf.commentsBefore.size());
		Assert.assertEquals(2, conf.commentsAfter.size());
		for(AnnotatedElement e:conf.commentsBefore){
			Assert.assertTrue(commentBefore.contains(((Member)e).getName()));
		}
		for(AnnotatedElement e:conf.commentsAfter){
			Assert.assertTrue(commentAfter.contains(((Member)e).getName()));
		}

		Assert.assertNotNull(conf.fields);
		
		Assert.assertEquals(5, conf.fields.size());
		
		CsvFieldConfiguration e = conf.fields.get(0);
		Assert.assertEquals("The Name", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(5, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getName", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(1);
		Assert.assertEquals("old", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(8, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("isOld", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(2);
		Assert.assertEquals("custom", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals("0", e.nullReplaceString);
		Assert.assertEquals(10, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("customMethod", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(3);
		Assert.assertEquals("date", e.name);
		Assert.assertEquals(true, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("getDate", ((Member)e.memberBean).getName());
		
		e = conf.fields.get(4);
		Assert.assertEquals("TheYear", e.name);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL, e.isDeleteFieldIfNull);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE, e.nullReplaceString);
		Assert.assertEquals(-2, e.position);
		Assert.assertNotNull(e.format);
		Assert.assertTrue(e.format instanceof Default);
		Assert.assertEquals("year", ((Member)e.memberBean).getName());
		
	}
}
