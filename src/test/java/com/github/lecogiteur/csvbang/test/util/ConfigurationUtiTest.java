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

import java.lang.reflect.Member;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.formatter.DateCsvFormatter;
import com.github.lecogiteur.csvbang.formatter.Default;
import com.github.lecogiteur.csvbang.test.bean.ChildSimpleConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.FinalConfigurationBean;
import com.github.lecogiteur.csvbang.test.bean.SimpleConfigurationBean;
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
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_BLOCKING_SIZE, conf.blockingSize);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER, conf.escapeQuoteCharacter);
		Assert.assertNull(conf.quote);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_CHARSET_NAME, conf.charset);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_DELIMITER, conf.delimiter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_END_RECORD, conf.endRecord);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FILE_NAME, conf.filename);
		Assert.assertNull(conf.header);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_APPEND_FILE, conf.isAppendToFile);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE, conf.isAsynchronousWrite);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_HEADER, conf.isDisplayHeader);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_START_RECORD, conf.startRecord);

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
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_BLOCKING_SIZE, conf.blockingSize);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER, conf.escapeQuoteCharacter);
		Assert.assertNull(conf.quote);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_CHARSET_NAME, conf.charset);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_DELIMITER, conf.delimiter);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_END_RECORD, conf.endRecord);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_FILE_NAME, conf.filename);
		Assert.assertNull(conf.header);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_APPEND_FILE, conf.isAppendToFile);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE, conf.isAsynchronousWrite);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_HEADER, conf.isDisplayHeader);
		Assert.assertEquals(IConstantsCsvBang.DEFAULT_START_RECORD, conf.startRecord);

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
		Assert.assertEquals(20, conf.blockingSize);
		Assert.assertEquals('\'', conf.escapeQuoteCharacter);
		Assert.assertEquals(new Character('\''), conf.quote);
		Assert.assertEquals("ISO-8859-1", conf.charset);
		Assert.assertEquals("||", conf.delimiter);
		Assert.assertEquals("\nEND\n", conf.endRecord);
		Assert.assertEquals("test.csv", conf.filename);
		Assert.assertEquals(true, conf.isDisplayHeader);
		Assert.assertEquals("*The Name||old||custom||date||TheYear\nEND\n", conf.header);
		Assert.assertEquals(true, conf.isAppendToFile);
		Assert.assertEquals(true, conf.isAsynchronousWrite);
		Assert.assertEquals("*", conf.startRecord);

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

}
