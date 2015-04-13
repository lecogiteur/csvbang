/**
 *  com.github.lecogiteur.csvbang.test.parser.CsvParserTest
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
package com.github.lecogiteur.csvbang.test.parser;

import java.math.BigDecimal;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvDatagram;
import com.github.lecogiteur.csvbang.parser.CsvParser;
import com.github.lecogiteur.csvbang.parser.CsvParsingResult;
import com.github.lecogiteur.csvbang.test.bean.csvparser.BeforeCommentCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.BigDataCsvParser;
import com.github.lecogiteur.csvbang.test.bean.csvparser.CustomFooterCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.EmptyHeaderCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.FooterHeaderCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.FormattedCsvParseBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.HeaderCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.MultipleDeletedFieldCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.MultipleFieldCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.NoFooterCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.NoFooterWithNoEndCharCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.NoHeaderCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.OneDeletedFieldCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.OneDeletedFieldWithFooterCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.OneDeletedFieldWithHeaderCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.OneFieldCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.QuoteCsvParserBean;
import com.github.lecogiteur.csvbang.util.ConfigurationUti;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CsvParserTest {

	/**
	 * Simulator of file reader
	 * @param fileContent
	 * @param fileID
	 * @param blockSize
	 * @param charset
	 * @return
	 * @since 1.0.0
	 */
	@Ignore
	private Collection<CsvDatagram> generator(final String fileContent, final int fileID, final int blockSize, final Charset charset){
		if (CsvbangUti.isStringBlank(fileContent)){
			return Collections.singleton(new CsvDatagram(0, fileID, new byte[]{}, true));
		}
		
		final byte[] content = fileContent.getBytes(charset);
		byte[] block = new byte[blockSize];
		int index = 0;
		int offset = 0;
		final Collection<CsvDatagram> list = new ArrayList<CsvDatagram>();
		
		for (final byte b:content){
			if (index >= blockSize){
				list.add(new CsvDatagram(offset, fileID, block, offset == content.length -1));
				index = 0;
				block = new byte[blockSize];
				offset += blockSize;
			}
			block[index++] = b;
		}
		
		if (index <= blockSize){
			byte[] tmp = new byte[index];
			System.arraycopy(block, 0, tmp, 0, index);
			list.add(new CsvDatagram(offset, fileID, tmp, true));
		}
		return list;
	}
	
	@Ignore
	private <T> CsvParsingResult<T> parse(Collection<CsvDatagram> datagrams, CsvParser<T> parser ) 
			throws CsvBangException{
		CsvParsingResult<T> result = new CsvParsingResult<T>();
		for (final CsvDatagram datagram:datagrams){
			CsvParsingResult<T> l = parser.parse(datagram);
			if (l != null){
				result.getCsvBeans().addAll(l.getCsvBeans());
				result.getComments().addAll(l.getComments());
				if (l.getHeader() != null){
					result.setHeader(l.getHeader());
				}
				if (l.getFooter() != null){
					result.setFooter(l.getFooter());
				}
			}
		}
		
		CsvParsingResult<T> l = parser.flush();
		if (l != null){
			result.getCsvBeans().addAll(l.getCsvBeans());
			result.getComments().addAll(l.getComments());
			if (l.getHeader() != null){
				result.setHeader(l.getHeader());
			}
			if (l.getFooter() != null){
				result.setFooter(l.getFooter());
			}
		}
		return result;
	}
	
	@Test
	public void noContentTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(null, 0, 10, conf.charset);
		final CsvParsingResult<OneFieldCsvParserBean> result = parse(datagrams, parser);
		final Collection<OneFieldCsvParserBean> list = result.getCsvBeans();
		
		Assert.assertEquals(0, list.size());
		Assert.assertEquals(0, result.getComments().size());
	}
	
	@Test
	public void oneRecordOneFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty", 0, 10, conf.charset);
		final CsvParsingResult<OneFieldCsvParserBean> result = parse(datagrams, parser);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertEquals("azerty", list.get(0).getField());
	}
	
	@Test
	public void oneRecordOneField2Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azertyuiop", 0, 10, conf.charset);
		final CsvParsingResult<OneFieldCsvParserBean> result = parse(datagrams, parser);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertEquals("azertyuiop", list.get(0).getField());
	}
	
	@Test
	public void oneRecordOneField3Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azertyuiopQSDFG", 0, 10, conf.charset);
		final CsvParsingResult<OneFieldCsvParserBean> result = parse(datagrams, parser);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertEquals("azertyuiopQSDFG", list.get(0).getField());
	}
	
	@Test
	public void multipleRecordOneFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty\nnbjhklopoiurcfjhfj\nnbvcxw", 0, 10, conf.charset);
		final CsvParsingResult<OneFieldCsvParserBean> result = parse(datagrams, parser);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertNotNull(list.get(0));
		Assert.assertNotNull(list.get(1));
		Assert.assertNotNull(list.get(2));
		for (OneFieldCsvParserBean bean:list){
			Assert.assertTrue("azerty".equals(bean.getField())
					|| "nbjhklopoiurcfjhfj".equals(bean.getField())
					|| "nbvcxw".equals(bean.getField())
					);
		}
	}
	

	
	@Test
	public void oneRecordMultipleFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty,123,54.89", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals("azerty", list.get(0).getField1());
		Assert.assertEquals(new Integer(123), list.get(0).getField2());
		Assert.assertEquals(new Double(54.89), list.get(0).getField3());
	}
	
	@Test
	public void oneRecordMultipleField2Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("a,1,6.0", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals("a", list.get(0).getField1());
		Assert.assertEquals(new Integer(1), list.get(0).getField2());
		Assert.assertEquals(new Double(6.0), list.get(0).getField3());
	}
	
	@Test
	public void oneRecordMultipleField3Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty,1,1", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals("azerty", list.get(0).getField1());
		Assert.assertEquals(new Integer(1), list.get(0).getField2());
		Assert.assertEquals(new Double(1), list.get(0).getField3());
	}
	
	@Test
	public void multipleRecordMultipleFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty,354,56.097\nnbj,34567,12345678\nFQDSFQDSFQFQDFQDSF,234,76.98\n", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertNotNull(list.get(0));
		Assert.assertNotNull(list.get(1));
		Assert.assertNotNull(list.get(2));
		Assert.assertEquals("azerty", list.get(0).getField1());
		Assert.assertEquals(new Integer(354), list.get(0).getField2());
		Assert.assertEquals(new Double(56.097), list.get(0).getField3());
		Assert.assertEquals("nbj", list.get(1).getField1());
		Assert.assertEquals(new Integer(34567), list.get(1).getField2());
		Assert.assertEquals(new Double(12345678), list.get(1).getField3());
		Assert.assertEquals("FQDSFQDSFQFQDFQDSF", list.get(2).getField1());
		Assert.assertEquals(new Integer(234), list.get(2).getField2());
		Assert.assertEquals(new Double(76.98), list.get(2).getField3());
	}
	
	@Test
	public void multipleRecordMultipleFieldWithEmptyFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(",354,56.097\nnbj,,12345678\nFQDSFQDSFQFQDFQDSF,234,\n", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertNotNull(list.get(0));
		Assert.assertNotNull(list.get(1));
		Assert.assertNotNull(list.get(2));
		Assert.assertNull(list.get(0).getField1());
		Assert.assertEquals(new Integer(354), list.get(0).getField2());
		Assert.assertEquals(new Double(56.097), list.get(0).getField3());
		Assert.assertEquals("nbj", list.get(1).getField1());
		Assert.assertNull(list.get(1).getField2());
		Assert.assertEquals(new Double(12345678), list.get(1).getField3());
		Assert.assertEquals("FQDSFQDSFQFQDFQDSF", list.get(2).getField1());
		Assert.assertEquals(new Integer(234), list.get(2).getField2());
		Assert.assertNull(list.get(2).getField3());
	}
	
	@Test
	public void zbigDataTest() throws CsvBangException{
		final String content = "12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" ;
				
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(BigDataCsvParser.class);
		final CsvParser<BigDataCsvParser> parser = new CsvParser<BigDataCsvParser>(BigDataCsvParser.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 10, conf.charset);
		final CsvParsingResult<BigDataCsvParser> result = parse(datagrams, parser);
		final List<BigDataCsvParser> list = new ArrayList<BigDataCsvParser>(result.getCsvBeans());
		
		Assert.assertEquals(200, list.size());
		Assert.assertEquals(0, result.getComments().size());
		for (BigDataCsvParser o:list){
			Assert.assertNotNull(o);
			Assert.assertEquals(new Integer(12), o.getField1());
			Assert.assertEquals("azerty", o.getField2());
			Assert.assertEquals(new Double(65.78), o.getField3());
			Assert.assertTrue(o.getField4());
		}
	}
	
	@Test
	public void zbigDataWithReverseSortDatagramTest() throws CsvBangException{
		final String content = "12,azerty,65.78,\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azzzzzzzzzzzerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" ;
				
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(BigDataCsvParser.class);
		final CsvParser<BigDataCsvParser> parser = new CsvParser<BigDataCsvParser>(BigDataCsvParser.class, conf);
		final List<CsvDatagram> datagrams = new ArrayList<CsvDatagram>(generator(content, 0, 5, conf.charset));
		
		Collections.reverse(datagrams);
		
		int index = 2;
		int end = datagrams.size() - 5;
		for (int i=index; i< end; i+=3){
			datagrams.add(datagrams.remove(i));
		}

		final CsvParsingResult<BigDataCsvParser> result = parse(datagrams, parser);
		final List<BigDataCsvParser> list = new ArrayList<BigDataCsvParser>(result.getCsvBeans());
		
		Assert.assertEquals(200, list.size());
		Assert.assertEquals(0, result.getComments().size());
		boolean isFirstString = true;
		boolean isFirstBool = true;
		for (BigDataCsvParser o:list){
			Assert.assertNotNull(o);
			Assert.assertEquals(new Integer(12), o.getField1());
			if (isFirstString && "azzzzzzzzzzzerty".equals(o.getField2())){
				isFirstString = false;
			}else{
				Assert.assertEquals("azerty", o.getField2());
			}
			Assert.assertEquals(new Double(65.78), o.getField3());
			if (isFirstBool && o.getField4() == null){
				isFirstBool = false;
			}else{
				Assert.assertTrue(o.getField4());				
			}
		}
	}
	
	@Test
	public void zbigDataWithMultipleFileDatagramTest() throws CsvBangException{
		//200
		final String content1 = "12,azerty,65.78,\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azzzzzzzzzzzerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" +
				"12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n12,azerty,65.78,true\n" ;
				
		
		//80
		final String content2 = "33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n" +
				"33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n33,poiu,34.0,false\n";
				
				
		//110
		final String content3 = "894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n" +
				"894857,nbjhiyutof,23.098,true\n\n\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n";
				
		
		
		
		
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(BigDataCsvParser.class);
		final CsvParser<BigDataCsvParser> parser = new CsvParser<BigDataCsvParser>(BigDataCsvParser.class, conf);
		final List<CsvDatagram> datagrams = new ArrayList<CsvDatagram>();
		datagrams.addAll(generator(content1, 0, 7, conf.charset));
		datagrams.addAll(generator(content2, 1, 6, conf.charset));
		datagrams.addAll(generator(content3, 2, 5, conf.charset));
		
		Collections.reverse(datagrams);
		
		int index = 2;
		int end = datagrams.size() - 5;
		for (int i=index; i< end; i+=3){
			datagrams.add(datagrams.remove(i));
		}
		for (int j=0; j<100; j++){
			int i1 = (int)(datagrams.size() * Math.random());
			int i2 = datagrams.size() - 2 -(int)(i1 * Math.random());
			end = Math.max(i1, i2);
			index = Math.min(i1, i2);
			for (int i=index; i< end; i+=7){
				datagrams.add(datagrams.remove(i));
			}
		}

		final CsvParsingResult<BigDataCsvParser> result = parse(datagrams, parser);
		final List<BigDataCsvParser> list = new ArrayList<BigDataCsvParser>(result.getCsvBeans());
		
		
		Map<String, Integer> count = new HashMap<String, Integer>(); 
		count.put("894857", 0);
		count.put("nbjhiyutof", 0);
		count.put("23.098", 0);
		count.put("true", 0);
		count.put("33", 0);
		count.put("poiu", 0);
		count.put("34.0", 0);
		count.put("false", 0);
		count.put("12", 0);
		count.put("azerty", 0);
		count.put("65.78", 0);
		count.put("azzzzzzzzzzzerty", 0);
		count.put("null", 0);
		
		Assert.assertEquals(390, list.size());
		Assert.assertEquals(0, result.getComments().size());
		for (BigDataCsvParser o:list){
			Assert.assertNotNull(o);
			if (o.getField1() == null){
				count.put("null", count.get("null") + 1);
			}else{
				Assert.assertTrue(count.containsKey(o.getField1().toString()));
				count.put(o.getField1().toString(), count.get(o.getField1().toString()) + 1);
			}
			if (o.getField2() == null){
				count.put("null", count.get("null") + 1);
			}else{
				Assert.assertTrue(count.containsKey(o.getField2().toString()));
				count.put(o.getField2().toString(), count.get(o.getField2().toString()) + 1);
			}
			if (o.getField3() == null){
				count.put("null", count.get("null") + 1);
			}else{
				Assert.assertTrue(count.containsKey(o.getField3().toString()));
				count.put(o.getField3().toString(), count.get(o.getField3().toString()) + 1);
			}
			if (o.getField4() == null){
				count.put("null", count.get("null") + 1);
			}else{
				Assert.assertTrue(count.containsKey(o.getField4().toString()));
				count.put(o.getField4().toString(), count.get(o.getField4().toString()) + 1);
			}
		}
		
		Assert.assertEquals(new Integer(80), count.get("false"));
		Assert.assertEquals(new Integer(110), count.get("894857"));
		Assert.assertEquals(new Integer(110), count.get("nbjhiyutof"));
		Assert.assertEquals(new Integer(109), count.get("23.098"));
		Assert.assertEquals(new Integer(309), count.get("true"));
		Assert.assertEquals(new Integer(80), count.get("33"));
		Assert.assertEquals(new Integer(80), count.get("poiu"));
		Assert.assertEquals(new Integer(80), count.get("34.0"));
		Assert.assertEquals(new Integer(200), count.get("12"));
		Assert.assertEquals(new Integer(199), count.get("azerty"));
		Assert.assertEquals(new Integer(200), count.get("65.78"));
		Assert.assertEquals(new Integer(1), count.get("azzzzzzzzzzzerty"));
		Assert.assertEquals(new Integer(2), count.get("null"));
	}
	
	@Test
	public void singleCommentTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("#a comment", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<String> comments = new ArrayList<String>(result.getComments());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertEquals(0, result.getCsvBeans().size());
		Assert.assertEquals(1, comments.size());
		Assert.assertEquals("a comment", comments.get(0));
	}
	
	@Test
	public void multipleCommentTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("#a comment\n#another comment", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<String> comments = new ArrayList<String>(result.getComments());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertEquals(0, result.getCsvBeans().size());
		Assert.assertEquals(2, comments.size());
		for (String c:comments){
			Assert.assertTrue("a comment".equals(c) || "another comment".equals(c));
		}
	}
	
	@Test
	public void commentsAndFieldsTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(BeforeCommentCsvParserBean.class);
		final CsvParser<BeforeCommentCsvParserBean> parser = new CsvParser<BeforeCommentCsvParserBean>(BeforeCommentCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("#18\nmy Field\n\nfield 2\n\n#coconuts\n\nfield3\n#commentTest\n#456\nfield4\n#56", 0, 10, conf.charset);
		final CsvParsingResult<BeforeCommentCsvParserBean> result = parse(datagrams, parser);
		final List<String> comments = new ArrayList<String>(result.getComments());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(comments);
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(5, comments.size());
		
		for(String comment:comments){
			Assert.assertTrue("coconuts".equals(comment) 
					|| "commentTest".equals(comment)
					|| "18".equals(comment)
					|| "456".equals(comment)
					|| "56".equals(comment));
		}
		
		for (BeforeCommentCsvParserBean bean:result.getCsvBeans()){
			Assert.assertTrue(
					("my Field".equals(bean.field1) && bean.myComment == null)
					|| ("field 2".equals(bean.field1) && bean.myComment == null)
					|| ("field3".equals(bean.field1) && bean.myComment == null)
					|| ("field4".equals(bean.field1) && bean.myComment == null)
					);
		}
	}

	
	@Test
	public void simpleQuoteFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("#a comment, two comments #i say: \"comment\"\n\"test\",\"18\",\"32.6\"", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> beans = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertEquals(1, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		for (String c:result.getComments()){
			Assert.assertTrue("a comment, two comments #i say: \"comment\"".equals(c));
		}
		Assert.assertEquals("test", beans.get(0).getField1());
		Assert.assertEquals(new Integer(18), beans.get(0).getField2());
		Assert.assertEquals(new Double(32.6), beans.get(0).getField3());
	}
	
	//TODO test unitaire avec un charactère de start crecord, idem supprimer le caractère de fin de ligne du end record 
	//TODO idem mettre un quote et désactiver le caractère de quote . Normalement le caractère doit apparraitre dans le contenu du champs
	
	//TODO tester un csv pourri
	
	@Test
	public void simpleQuoteField2Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("\"test\",\"18\",\"32.6\"", 0, 10, conf.charset);
		final CsvParsingResult<MultipleFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleFieldCsvParserBean> beans = new ArrayList<MultipleFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertEquals(1, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		Assert.assertEquals("test", beans.get(0).getField1());
		Assert.assertEquals(new Integer(18), beans.get(0).getField2());
		Assert.assertEquals(new Double(32.6), beans.get(0).getField3());
	}

	
	@Test
	public void multipleQuoteFieldTest() throws CsvBangException{
		final String content = "\"test||gogo\"||234||retyu\n"
				+ "fringe||\"6543\"||youpala\n\n"
				+ "fringe2||\"65434\"||\"toto tutu\"\n\n"
				+ "\"nouille#fakecomment\"||||\"toto say: \n \\\"I'm toto\\\"...\\\"\"";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(QuoteCsvParserBean.class);
		final CsvParser<QuoteCsvParserBean> parser = new CsvParser<QuoteCsvParserBean>(QuoteCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 10, conf.charset);
		final CsvParsingResult<QuoteCsvParserBean> result = parse(datagrams, parser);
		final List<QuoteCsvParserBean> beans = new ArrayList<QuoteCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		
		for (QuoteCsvParserBean bean:beans){
			Assert.assertTrue(
					("test||gogo".equals(bean.field1) && new Integer(234).equals(bean.getField2()) && "retyu".equals(bean.getField()) && new Integer(5).equals(bean.getField3())) ||
					("fringe".equals(bean.field1) && new Integer(6543).equals(bean.getField2()) && "youpala".equals(bean.getField()) && new Integer(7).equals(bean.getField3())) ||
					("fringe2".equals(bean.field1) && new Integer(65434).equals(bean.getField2()) && "toto tutu".equals(bean.getField()) && new Integer(9).equals(bean.getField3())) ||
					("nouille#fakecomment".equals(bean.field1) && null == bean.getField2() && "toto say: \n \"I'm toto\"...\"".equals(bean.getField()) && new Integer(26).equals(bean.getField3()))
					);
		}
	}
	

	@Test
	public void customHeaderTest() throws CsvBangException{
		final String content = "a new custom header year !!!\na custom header\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd&"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd&"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd&"
				+ "\n#a comment & a comment\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(NoHeaderCsvParserBean.class);
		final CsvParser<NoHeaderCsvParserBean> parser = new CsvParser<NoHeaderCsvParserBean>(NoHeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<NoHeaderCsvParserBean> result = parse(datagrams, parser);
		final List<NoHeaderCsvParserBean> beans = new ArrayList<NoHeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		Assert.assertEquals("a comment & a comment", result.getComments().iterator().next());
		Assert.assertEquals("a new custom header year !!!\na custom header", result.getHeader());
		
		for (NoHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	
	@Test
	public void customHeaderNoHeaderTest() throws CsvBangException{
		final String content = "a new custom header year !!!\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd&"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd&"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd&"
				+ "\n#a comment & a comment\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(EmptyHeaderCsvParserBean.class);
		final CsvParser<EmptyHeaderCsvParserBean> parser = new CsvParser<EmptyHeaderCsvParserBean>(EmptyHeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<EmptyHeaderCsvParserBean> result = parse(datagrams, parser);
		final List<EmptyHeaderCsvParserBean> beans = new ArrayList<EmptyHeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		Assert.assertEquals("a comment & a comment", result.getComments().iterator().next());
		Assert.assertEquals("a new custom header year !!!", result.getHeader());
		
		for (NoHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}

	@Test
	public void customHeader2Test() throws CsvBangException{
		final String content = "a new custom header year !!!\nnew custom header\nmyField1,myField2,field3,the field\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "\n#a comment & a comment\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(HeaderCsvParserBean.class);
		final CsvParser<HeaderCsvParserBean> parser = new CsvParser<HeaderCsvParserBean>(HeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<HeaderCsvParserBean> result = parse(datagrams, parser);
		final List<HeaderCsvParserBean> beans = new ArrayList<HeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		Assert.assertEquals("a comment & a comment", result.getComments().iterator().next());
		Assert.assertEquals("a new custom header year !!!\nnew custom header\nmyField1,myField2,field3,the field\n", result.getHeader());
		
		for (NoHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					"*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}

	@Test
	public void customHeaderWithCommentTest() throws CsvBangException{
		final String content = "a new custom header year !!!\n#a comment\nnew custom header\nmyField1,myField2,field3,the field\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "\n#a comment & a comment\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(HeaderCsvParserBean.class);
		final CsvParser<HeaderCsvParserBean> parser = new CsvParser<HeaderCsvParserBean>(HeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<HeaderCsvParserBean> result = parse(datagrams, parser);
		final List<HeaderCsvParserBean> beans = new ArrayList<HeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		Assert.assertEquals("a comment & a comment", result.getComments().iterator().next());
		Assert.assertEquals("a new custom header year !!!\n#a comment\nnew custom header\nmyField1,myField2,field3,the field\n", result.getHeader());
		
		for (NoHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					"*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	
	@Test
	public void customHeaderNoDataTest() throws CsvBangException{
		final String content = "new custom header\nmyField1,myField2,field3,the field\n";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(HeaderCsvParserBean.class);
		final CsvParser<HeaderCsvParserBean> parser = new CsvParser<HeaderCsvParserBean>(HeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<HeaderCsvParserBean> result = parse(datagrams, parser);
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertEquals(0, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		
		Assert.assertEquals("new custom header\nmyField1,myField2,field3,the field\n", result.getHeader());
	}
	


	@Test
	public void customHeaderStartCommentTest() throws CsvBangException{
		final String content = "#a comment\na new custom header year !!!\nnew custom header\nmyField1,myField2,field3,the field\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "\n#a comment & a comment\n"
				+ "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(HeaderCsvParserBean.class);
		final CsvParser<HeaderCsvParserBean> parser = new CsvParser<HeaderCsvParserBean>(HeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<HeaderCsvParserBean> result = parse(datagrams, parser);
		final List<HeaderCsvParserBean> beans = new ArrayList<HeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		for (String c:result.getComments()){
			Assert.assertTrue("a comment & a comment".equals(c));
		}
		Assert.assertEquals("#a comment\na new custom header year !!!\nnew custom header\nmyField1,myField2,field3,the field\n", result.getHeader());
		
		for (NoHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					"*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	


	@Test
	public void customFooterTest() throws CsvBangException{
		final String content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "\n#a comment & a comment\n"
				+ "a custom footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(NoFooterCsvParserBean.class);
		final CsvParser<NoFooterCsvParserBean> parser = new CsvParser<NoFooterCsvParserBean>(NoFooterCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<NoFooterCsvParserBean> result = parse(datagrams, parser);
		final List<NoFooterCsvParserBean> beans = new ArrayList<NoFooterCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		for (String c:result.getComments()){
			Assert.assertTrue("a comment & a comment".equals(c));
		}
		Assert.assertEquals("a custom footer", result.getFooter());
		
		for (NoFooterCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	

	@Test
	public void customFooterWithEndCharTest() throws CsvBangException{
		final String content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "\n#a comment & a comment\n"
				+ "a custom footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(NoFooterWithNoEndCharCsvParserBean.class);
		final CsvParser<NoFooterWithNoEndCharCsvParserBean> parser = new CsvParser<NoFooterWithNoEndCharCsvParserBean>(NoFooterWithNoEndCharCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<NoFooterWithNoEndCharCsvParserBean> result = parse(datagrams, parser);
		final List<NoFooterWithNoEndCharCsvParserBean> beans = new ArrayList<NoFooterWithNoEndCharCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		for (String c:result.getComments()){
			Assert.assertTrue("a comment & a comment".equals(c));
		}
		Assert.assertEquals("a custom footer", result.getFooter());
		
		for (NoFooterWithNoEndCharCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	

	@Test
	public void customFooterWithEndCharNoCommentTest() throws CsvBangException{
		final String content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd||"
				+ "a custom footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(NoFooterWithNoEndCharCsvParserBean.class);
		final CsvParser<NoFooterWithNoEndCharCsvParserBean> parser = new CsvParser<NoFooterWithNoEndCharCsvParserBean>(NoFooterWithNoEndCharCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<NoFooterWithNoEndCharCsvParserBean> result = parse(datagrams, parser);
		final List<NoFooterWithNoEndCharCsvParserBean> beans = new ArrayList<NoFooterWithNoEndCharCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
	
		Assert.assertEquals("a custom footer", result.getFooter());
		
		for (NoFooterWithNoEndCharCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	
	@Test
	public void footerTest() throws CsvBangException{
		final String content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "\n#a comment & a comment\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd"
				+ "a footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(CustomFooterCsvParserBean.class);
		final CsvParser<CustomFooterCsvParserBean> parser = new CsvParser<CustomFooterCsvParserBean>(CustomFooterCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<CustomFooterCsvParserBean> result = parse(datagrams, parser);
		final List<CustomFooterCsvParserBean> beans = new ArrayList<CustomFooterCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		for (String c:result.getComments()){
			Assert.assertTrue("a comment & a comment".equals(c));
		}
		Assert.assertEquals("a footer", result.getFooter());
		
		for (NoFooterCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5())
							);
		}
	}
	

	/**
	 * Impossible to define limits of footer
	 * @throws CsvBangException
	 * @since 1.0.0
	 */
	@Test
	public void footerWithCustomFooterTest() throws CsvBangException{
		final String content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd\n"
				+ "\n#a comment & a comment\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd"
				+ "a custom footera footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(CustomFooterCsvParserBean.class);
		final CsvParser<CustomFooterCsvParserBean> parser = new CsvParser<CustomFooterCsvParserBean>(CustomFooterCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<CustomFooterCsvParserBean> result = parse(datagrams, parser);
		final List<CustomFooterCsvParserBean> beans = new ArrayList<CustomFooterCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(1, result.getComments().size());
		
		for (String c:result.getComments()){
			Assert.assertTrue("a comment & a comment".equals(c));
		}
		Assert.assertEquals("a footer", result.getFooter());
		
		for (CustomFooterCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					("dddddddddddddddddddddddddddddddddddd".equals(bean.getField5()) ||
							"dddddddddddddddddddddddddddddddddddda custom footer".equals(bean.getField5()))
							);
		}
	}
	

	@Test
	public void footerWithHeaderNoDataTest() throws CsvBangException{
		final String content = "myField1,myField2,field3,the field,field6\na footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(FooterHeaderCsvParserBean.class);
		final CsvParser<FooterHeaderCsvParserBean> parser = new CsvParser<FooterHeaderCsvParserBean>(FooterHeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<FooterHeaderCsvParserBean> result = parse(datagrams, parser);
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(0, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		
		Assert.assertEquals("a footer", result.getFooter());
		Assert.assertEquals("myField1,myField2,field3,the field,field6\n", result.getHeader());
	}
	

	@Test
	public void footerWithHeaderDataTest() throws CsvBangException{
		final String content = "myField1,myField2,field3,the field,field6\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd,38\n"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa,bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb,ccccccccccccccccccccccccccccccccccc,dddddddddddddddddddddddddddddddddddd,38"
				+ "a custom footera footer";
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(FooterHeaderCsvParserBean.class);
		final CsvParser<FooterHeaderCsvParserBean> parser = new CsvParser<FooterHeaderCsvParserBean>(FooterHeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<FooterHeaderCsvParserBean> result = parse(datagrams, parser);
		final List<FooterHeaderCsvParserBean> beans = new ArrayList<FooterHeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNotNull(result.getHeader());
		Assert.assertNotNull(result.getFooter());
		Assert.assertEquals(2, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		
		Assert.assertEquals("a custom footera footer", result.getFooter());
		Assert.assertEquals("myField1,myField2,field3,the field,field6\n", result.getHeader());
		

		
		for (FooterHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".equals(bean.field1) &&
					"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".equals(bean.field2) &&
					"ccccccccccccccccccccccccccccccccccc".equals(bean.getField3()) &&
					"dddddddddddddddddddddddddddddddddddd".equals(bean.getField5()) &&
					new Integer(38).equals(bean.getField6())
				);
		}
	}
	

	

	@Test
	public void formattedBeanTest() throws CsvBangException, CharacterCodingException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(FormattedCsvParseBean.class);
		final String content = "1,12-03-2013,\"123,43 €\",\"1,10\",name1\n"
				+ "2,13-03-2013,\"123,43 €\",\"12,10\",name2\n"
				+ "3,14-03-2013,\"12,45 €\",\"13,10\",name3\n"
				+ "4,15-03-2013,\"345,00 €\",\"14,10\",name4\n"
				+ "5,16-03-2013,\"12,45 €\",\"15,10\",name5\n"
				+ "6,17-03-2013,\"123,43 €\",\"16,10\",name6\n"
				+ "7,18-03-2013,\"12,45 €\",\"17,10\",name7\n"
				+ "8,19-03-2013,\"345,00 €\",\"18,10\",name8\n"
				+ "9,20-03-2013,\"12,45 €\",\"19,10\",name9\n"
				+ "10,21-03-2013,\"123,43 €\",\"21,10\",name10\n"
				+ "11,22-03-2013,\"12,45 €\",\"31,10\",name11\n"
				+ "12,23-03-2013,\"345,00 €\",\"41,10\",name12\n"
				+ "13,24-03-2013,\"123,43 €\",\"51,10\",name13\n"
				+ "14,25-03-2013,\"345,00 €\",\"61,10\",name14\n"
				+ "15,26-03-2013,\"123,43 €\",\"71,10\",name15\n";
		final CsvParser<FormattedCsvParseBean> parser = new CsvParser<FormattedCsvParseBean>(FormattedCsvParseBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<FormattedCsvParseBean> result = parse(datagrams, parser);
		final List<FormattedCsvParseBean> beans = new ArrayList<FormattedCsvParseBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNull(result.getFooter());
		Assert.assertEquals(15, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		
		for (FormattedCsvParseBean bean:beans){
			Assert.assertTrue(
					(new Integer(1).equals(bean.getNumber()) && "12-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("123.43").equals(bean.getCost()) && new Double(2.10d).equals(bean.getAnotherNumber()) && "name1".equals(bean.getName()) )
					|| (new Integer(2).equals(bean.getNumber()) && "13-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("123.43").equals(bean.getCost()) && new Double(13.10d).equals(bean.getAnotherNumber()) && "name2".equals(bean.getName()))
					|| (new Integer(3).equals(bean.getNumber()) && "14-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("12.45").equals(bean.getCost()) && new Double(14.10d).equals(bean.getAnotherNumber()) && "name3".equals(bean.getName()))
					|| (new Integer(4).equals(bean.getNumber()) && "15-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("345.00").equals(bean.getCost()) && new Double(15.10d).equals(bean.getAnotherNumber()) && "name4".equals(bean.getName()))
					|| (new Integer(5).equals(bean.getNumber()) && "16-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("12.45").equals(bean.getCost()) && new Double(16.10d).equals(bean.getAnotherNumber()) && "name5".equals(bean.getName()))
					|| (new Integer(6).equals(bean.getNumber()) && "17-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("123.43").equals(bean.getCost()) && new Double(17.10d).equals(bean.getAnotherNumber()) && "name6".equals(bean.getName()))
					|| (new Integer(7).equals(bean.getNumber()) && "18-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("12.45").equals(bean.getCost()) && new Double(18.10d).equals(bean.getAnotherNumber()) && "name7".equals(bean.getName()))
					|| (new Integer(8).equals(bean.getNumber()) && "19-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("345.00").equals(bean.getCost()) && new Double(19.10d).equals(bean.getAnotherNumber()) && "name8".equals(bean.getName()))
					|| (new Integer(9).equals(bean.getNumber()) && "20-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("12.45").equals(bean.getCost()) && new Double(20.10d).equals(bean.getAnotherNumber()) && "name9".equals(bean.getName()))
					|| (new Integer(10).equals(bean.getNumber()) && "21-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("123.43").equals(bean.getCost()) && new Double(22.10d).equals(bean.getAnotherNumber()) && "name10".equals(bean.getName()))
					|| (new Integer(11).equals(bean.getNumber()) && "22-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("12.45").equals(bean.getCost()) && new Double(32.10d).equals(bean.getAnotherNumber()) && "name11".equals(bean.getName()))
					|| (new Integer(12).equals(bean.getNumber()) && "23-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("345.00").equals(bean.getCost()) && new Double(42.10d).equals(bean.getAnotherNumber()) && "name12".equals(bean.getName()))
					|| (new Integer(13).equals(bean.getNumber()) && "24-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("123.43").equals(bean.getCost()) && new Double(52.10d).equals(bean.getAnotherNumber()) && "name13".equals(bean.getName()))
					|| (new Integer(14).equals(bean.getNumber()) && "25-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("345.00").equals(bean.getCost()) && new Double(62.10d).equals(bean.getAnotherNumber()) && "name14".equals(bean.getName()))
					|| (new Integer(15).equals(bean.getNumber()) && "26-03-2013".equals(format.format(bean.getDate().getTime())) && new BigDecimal("123.43").equals(bean.getCost()) && new Double(72.10d).equals(bean.getAnotherNumber()) && "name15".equals(bean.getName()))
					
				);
		}
	}
	
	@Test
	public void oneSimpleDeletedFieldBeanTest() throws CsvBangException, CharacterCodingException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneDeletedFieldCsvParserBean.class);
		final String content = "field1,field2,field3\n"
				+ "unit1,unit3\n"
				+ "field1,field2,field3\n";
		final CsvParser<OneDeletedFieldCsvParserBean> parser = new CsvParser<OneDeletedFieldCsvParserBean>(OneDeletedFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<OneDeletedFieldCsvParserBean> result = parse(datagrams, parser);
		final List<OneDeletedFieldCsvParserBean> beans = new ArrayList<OneDeletedFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNull(result.getFooter());
		Assert.assertEquals(3, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		

		
		for (OneDeletedFieldCsvParserBean bean:beans){
			Assert.assertTrue(
					("*field1*".equals(bean.getField1()) && "field2".equals(bean.getField2()) && "field3".equals(bean.getField3()))
					|| ("*unit1*".equals(bean.getField1()) && bean.getField2() == null && "unit3".equals(bean.getField3()))
				);
		}
	}
	
	@Test
	public void simpleDeletedFieldBeanTest() throws CsvBangException, CharacterCodingException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneDeletedFieldCsvParserBean.class);
		final String content = "field1,field2,field3\n"
				+ "unit1,unit3\n"
				+ "field1,field2,field3\n"
				+ "test1,test3";
		final CsvParser<OneDeletedFieldCsvParserBean> parser = new CsvParser<OneDeletedFieldCsvParserBean>(OneDeletedFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<OneDeletedFieldCsvParserBean> result = parse(datagrams, parser);
		final List<OneDeletedFieldCsvParserBean> beans = new ArrayList<OneDeletedFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertNull(result.getHeader());
		Assert.assertNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		

		
		for (OneDeletedFieldCsvParserBean bean:beans){
			Assert.assertTrue(
					("*field1*".equals(bean.getField1()) && "field2".equals(bean.getField2()) && "field3".equals(bean.getField3()))
					|| ("*unit1*".equals(bean.getField1()) && bean.getField2() == null && "unit3".equals(bean.getField3()))
					|| ("*test1*".equals(bean.getField1()) && bean.getField2() == null && "test3".equals(bean.getField3()))
				);
		}
	}
	

	@Test
	public void deletedFieldWithHeaderBeanTest() throws CsvBangException, CharacterCodingException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneDeletedFieldWithHeaderCsvParserBean.class);
		final String content = "a custom header\nfield1,field2,field3\n"
				+ "unit2,unit3\n"
				+ "field1,field2,field3\n"
				+ "test2,test3";
		final CsvParser<OneDeletedFieldWithHeaderCsvParserBean> parser = new CsvParser<OneDeletedFieldWithHeaderCsvParserBean>(OneDeletedFieldWithHeaderCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<OneDeletedFieldWithHeaderCsvParserBean> result = parse(datagrams, parser);
		final List<OneDeletedFieldWithHeaderCsvParserBean> beans = new ArrayList<OneDeletedFieldWithHeaderCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertEquals("a custom header", result.getHeader());
		Assert.assertNull(result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		

		
		for (OneDeletedFieldWithHeaderCsvParserBean bean:beans){
			Assert.assertTrue(
					("field1".equals(bean.getField1()) && "field2".equals(bean.getField2()) && "field3".equals(bean.getField3()))
					|| ("unit2".equals(bean.getField2()) && bean.getField1() == null && "unit3".equals(bean.getField3()))
					|| ("test2".equals(bean.getField2()) && bean.getField1() == null && "test3".equals(bean.getField3()))
				);
		}
	}
	

	@Test
	public void deletedFieldWithFooterBeanTest() throws CsvBangException, CharacterCodingException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneDeletedFieldWithFooterCsvParserBean.class);
		final String content = "a custom header\nfield1,field2,field3\n"
				+ "unit1,unit2\n"
				+ "field1,field2,field3\n"
				+ "test1,test2\na custom footer";
		final CsvParser<OneDeletedFieldWithFooterCsvParserBean> parser = new CsvParser<OneDeletedFieldWithFooterCsvParserBean>(OneDeletedFieldWithFooterCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<OneDeletedFieldWithFooterCsvParserBean> result = parse(datagrams, parser);
		final List<OneDeletedFieldWithFooterCsvParserBean> beans = new ArrayList<OneDeletedFieldWithFooterCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertEquals("a custom header", result.getHeader());
		Assert.assertEquals("a custom footer", result.getFooter());
		Assert.assertEquals(4, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		

		
		for (OneDeletedFieldWithFooterCsvParserBean bean:beans){
			Assert.assertTrue(
					("field1".equals(bean.getField1()) && "field2".equals(bean.getField2()) && "field3".equals(bean.getField3()))
					|| ("unit1".equals(bean.getField1()) && bean.getField3() == null && "unit2".equals(bean.getField2()))
					|| ("test1".equals(bean.getField1()) && bean.getField3() == null && "test2".equals(bean.getField2()))
				);
		}
	}
	
	@Test
	public void multipleDeletedFieldBeanTest() throws CsvBangException, CharacterCodingException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleDeletedFieldCsvParserBean.class);
		final String content = "a custom header\nfield1,10,field3\n"
				+ "5\n"
				+ "field1,10,field3\n"
				+ "30,test3\n"
				+ "toto1,67\na custom footer";
		final CsvParser<MultipleDeletedFieldCsvParserBean> parser = new CsvParser<MultipleDeletedFieldCsvParserBean>(MultipleDeletedFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(content, 0, 5, conf.charset);
		final CsvParsingResult<MultipleDeletedFieldCsvParserBean> result = parse(datagrams, parser);
		final List<MultipleDeletedFieldCsvParserBean> beans = new ArrayList<MultipleDeletedFieldCsvParserBean>(result.getCsvBeans());
		
		Assert.assertNotNull(result.getCsvBeans());
		Assert.assertNotNull(result.getComments());
		Assert.assertEquals("a custom header", result.getHeader());
		Assert.assertEquals("a custom footer", result.getFooter());
		Assert.assertEquals(5, result.getCsvBeans().size());
		Assert.assertEquals(0, result.getComments().size());
		

		
		for (MultipleDeletedFieldCsvParserBean bean:beans){
			Assert.assertTrue(
					("field1".equals(bean.getField1()) && new Integer(10).equals(bean.getField2()) && "field3".equals(bean.getField3()))
					|| (new Integer(5).equals(bean.getField2()) && bean.getField3() == null && bean.getField1() == null)
					|| (new Integer(30).equals(bean.getField2()) && bean.getField1() == null && "test3".equals(bean.getField3()))
					|| (new Integer(67).equals(bean.getField2()) && bean.getField3() == null && "toto1".equals(bean.getField1()))
				);
		}
	}
	
}
