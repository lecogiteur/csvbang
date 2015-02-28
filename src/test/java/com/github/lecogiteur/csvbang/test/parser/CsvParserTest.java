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

import java.nio.charset.Charset;
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
import com.github.lecogiteur.csvbang.test.bean.csvparser.BigDataCsvParser;
import com.github.lecogiteur.csvbang.test.bean.csvparser.MultipleFieldCsvParserBean;
import com.github.lecogiteur.csvbang.test.bean.csvparser.OneFieldCsvParserBean;
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
	
	@Test
	public void noContentTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator(null, 0, 10, conf.charset);
		final Collection<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			Collection<OneFieldCsvParserBean> l = parser.parse(datagram);
			if (l != null){
				list.addAll(l);
			}
		}
		
		Collection<OneFieldCsvParserBean> l = parser.flush();
		if (l != null){
			list.addAll(l);
		}
		
		Assert.assertEquals(0, list.size());
	}
	
	@Test
	public void oneRecordOneFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty", 0, 10, conf.charset);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals("azerty", list.get(0).getField());
	}
	
	@Test
	public void oneRecordOneField2Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azertyuiop", 0, 10, conf.charset);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals("azertyuiop", list.get(0).getField());
	}
	
	@Test
	public void oneRecordOneField3Test() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azertyuiopQSDFG", 0, 10, conf.charset);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(1, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertEquals("azertyuiopQSDFG", list.get(0).getField());
	}
	
	@Test
	public void multipleRecordOneFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(OneFieldCsvParserBean.class);
		final CsvParser<OneFieldCsvParserBean> parser = new CsvParser<OneFieldCsvParserBean>(OneFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty\nnbjhklopoiurcfjhfj\nnbvcxw", 0, 10, conf.charset);
		final List<OneFieldCsvParserBean> list = new ArrayList<OneFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(3, list.size());
		Assert.assertNotNull(list.get(0));
		Assert.assertNotNull(list.get(1));
		Assert.assertNotNull(list.get(2));
		Assert.assertEquals("azerty", list.get(0).getField());
		Assert.assertEquals("nbjhklopoiurcfjhfj", list.get(1).getField());
		Assert.assertEquals("nbvcxw", list.get(2).getField());
	}
	

	
	@Test
	public void oneRecordMultipleFieldTest() throws CsvBangException{
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(MultipleFieldCsvParserBean.class);
		final CsvParser<MultipleFieldCsvParserBean> parser = new CsvParser<MultipleFieldCsvParserBean>(MultipleFieldCsvParserBean.class, conf);
		final Collection<CsvDatagram> datagrams = generator("azerty,123,54.89", 0, 10, conf.charset);
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(1, list.size());
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
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(1, list.size());
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
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(1, list.size());
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
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(3, list.size());
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
		final List<MultipleFieldCsvParserBean> list = new ArrayList<MultipleFieldCsvParserBean>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(3, list.size());
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
		final List<BigDataCsvParser> list = new ArrayList<BigDataCsvParser>();
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(200, list.size());
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
		final List<BigDataCsvParser> list = new ArrayList<BigDataCsvParser>();
		
		Collections.reverse(list);
		
		int index = 2;
		int end = datagrams.size() - 5;
		for (int i=index; i< end; i+=3){
			datagrams.add(datagrams.remove(i));
		}
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		Assert.assertEquals(200, list.size());
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
				"894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n894857,nbjhiyutof,23.098,true\n";
				
		
		
		
		
		final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(BigDataCsvParser.class);
		final CsvParser<BigDataCsvParser> parser = new CsvParser<BigDataCsvParser>(BigDataCsvParser.class, conf);
		final List<CsvDatagram> datagrams = new ArrayList<CsvDatagram>();
		datagrams.addAll(generator(content1, 0, 7, conf.charset));
		datagrams.addAll(generator(content2, 1, 6, conf.charset));
		datagrams.addAll(generator(content3, 2, 5, conf.charset));
		final List<BigDataCsvParser> list = new ArrayList<BigDataCsvParser>();
		
		Collections.reverse(list);
		
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
		
		for (final CsvDatagram datagram:datagrams){
			list.addAll(parser.parse(datagram));
		}
		list.addAll(parser.flush());
		
		
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
}
