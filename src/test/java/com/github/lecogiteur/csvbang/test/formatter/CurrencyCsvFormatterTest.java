/**
 *  com.github.lecogiteur.csvbang.test.formatter.CurrencyCsvFormatterTest
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
package com.github.lecogiteur.csvbang.test.formatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.formatter.CsvFormatter;
import com.github.lecogiteur.csvbang.formatter.CurrencyCsvFormatter;


/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CurrencyCsvFormatterTest {

	@Test
	public void currencyTest() throws CsvBangException{
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setLocal(Locale.FRANCE);
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		
		try{
			format.format("string", "default");
			Assert.fail();
		}catch(Exception e){
			//nothing
		}
		
		Assert.assertEquals("12,00 €", format.format(12, "default"));
		Assert.assertEquals("23,00 €", format.format(23.0f, "default"));
		Assert.assertEquals("2 000,96 €", format.format(2000.95874d, "default"));
		Assert.assertEquals("345 678 896,35 €", format.format(new BigDecimal("345678896.34567890765223334"), "default"));
		Assert.assertEquals("123,00 €", format.format(new AtomicLong(123), "default"));
		Assert.assertEquals("123,00 €", format.format(new AtomicInteger(123), "default"));
		Assert.assertEquals("1,00 €", format.format(new Short((short)1), "default"));
		Assert.assertEquals("1,00 €", format.format(new Byte((byte)1), "default"));
		Assert.assertEquals("123,00 €", format.format(new BigInteger("123"), "default"));
	}
	
	@Test
	public void currencyNoLocaleTest() throws CsvBangException{
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setPattern(null);
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		
		try{
			format.format("string", "default");
			Assert.fail();
		}catch(Exception e){
			//nothing
		}
		
		Assert.assertEquals("$12.00", format.format(12, "default"));
		Assert.assertEquals("$23.00", format.format(23.0f, "default"));
		Assert.assertEquals("$2,000.96", format.format(2000.95874d, "default"));
		Assert.assertEquals("$345,678,896.35", format.format(new BigDecimal("345678896.34567890765223334"), "default"));
	}
	
	@Test
	public void currencyWithPatternTest() throws CsvBangException{
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setPattern(".00");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		
		try{
			format.format("string", "default");
			Assert.fail();
		}catch(Exception e){
			//nothing
		}
		
		Assert.assertEquals("$12.00", format.format(12, "default"));
		Assert.assertEquals("$23.00", format.format(23.0f, "default"));
		Assert.assertEquals("$2,000.96", format.format(2000.95874d, "default"));
		Assert.assertEquals("$345,678,896.35", format.format(new BigDecimal("345678896.34567890765223334"), "default"));
		
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.00");
		format.init();
		Assert.assertEquals("$12.00", format.format(12, "default"));
		Assert.assertEquals("$23.00", format.format(23.0f, "default"));
		Assert.assertEquals("$2,000.96", format.format(2000.95874d, "default"));
		Assert.assertEquals("$78,896.35", format.format(new BigDecimal("345678896.34567890765223334"), "default"));
		
		format = new CurrencyCsvFormatter();
		format.setPattern("######.##");
		format.init();
		Assert.assertEquals("$12", format.format(12, "default"));
		Assert.assertEquals("$23.5", format.format(23.5f, "default"));
		Assert.assertEquals("$2,000.96", format.format(2000.95874d, "default"));
		Assert.assertEquals("$678,896.35", format.format(new BigDecimal("345678896.34567890765223334"), "default"));
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#######.0");
		format.init();
		Assert.assertEquals("$12.0", format.format(12, "default"));
		Assert.assertEquals("$23.5", format.format(23.5f, "default"));
		Assert.assertEquals("$2,001.0", format.format(2000.95874d, "default"));
		Assert.assertEquals("$5,678,896.4", format.format(new BigDecimal("345678896.35567890765223334"), "default"));
	}

	@Test
	public void currencyParseTest() throws CsvBangException{
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setLocal(Locale.FRANCE);
		format.init();
		Assert.assertNull(format.parse(null, Double.class));
		Assert.assertNull(format.parse(null, Float.class));
		Assert.assertNull(format.parse(null, BigDecimal.class));
		Assert.assertNull(format.parse(null, String.class));
		Assert.assertNull(format.parse(null, Integer.class));
		Assert.assertNull(format.parse(null, Long.class));
		Assert.assertNull(format.parse(null, BigInteger.class));
		Assert.assertNull(format.parse(null, AtomicInteger.class));
		Assert.assertNull(format.parse(null, AtomicLong.class));
		Assert.assertNull(format.parse(null, Calendar.class));
		try{
			format.parse("dcxcc<wxc<wc", Calendar.class);
			Assert.fail();
		}catch(Exception e){
			//nothing
		}
		
		Assert.assertEquals(new Integer(12), format.parse("12,00 €", Integer.class));
		Assert.assertEquals(new Float(23), format.parse("23,00 €", Float.class));
		Assert.assertEquals(new Double(2000.96d), format.parse("2 000,96 €", Double.class));
		Assert.assertEquals(new BigDecimal("345678896.35"), format.parse("345 678 896,34567890765223334 €", BigDecimal.class));
		Assert.assertEquals(AtomicLong.class, format.parse("123,00 €", AtomicLong.class).getClass());
		Assert.assertEquals(new AtomicLong(123).longValue(), ((AtomicLong)format.parse("123,00 €", AtomicLong.class)).longValue());
		
		Assert.assertEquals(AtomicInteger.class, format.parse("123,00 €", AtomicInteger.class).getClass());
		Assert.assertEquals(new AtomicInteger(123).intValue(), ((AtomicInteger)format.parse("123,00 €", AtomicInteger.class)).intValue());
		Assert.assertEquals(new Short((short)1), format.parse("1,00 €", Short.class));
		Assert.assertEquals(new Byte((byte)1), format.parse("1,00 €", Byte.class));
		Assert.assertEquals(new BigInteger("123"), format.parse("123,00 €", BigInteger.class));
	}
	
	@Test
	public void currencyParseNoFractionLimitTest() throws CsvBangException{
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setLocal(Locale.FRANCE);
		format.setPattern(".");//no limit
		format.init();
		Assert.assertNull(format.parse(null, Double.class));
		Assert.assertNull(format.parse(null, Float.class));
		Assert.assertNull(format.parse(null, BigDecimal.class));
		Assert.assertNull(format.parse(null, String.class));
		Assert.assertNull(format.parse(null, Integer.class));
		Assert.assertNull(format.parse(null, Long.class));
		Assert.assertNull(format.parse(null, BigInteger.class));
		Assert.assertNull(format.parse(null, AtomicInteger.class));
		Assert.assertNull(format.parse(null, AtomicLong.class));
		Assert.assertNull(format.parse(null, Calendar.class));
		try{
			format.parse("dcxcc<wxc<wc", Calendar.class);
			Assert.fail();
		}catch(Exception e){
			//nothing
		}
		
		Assert.assertEquals(new Integer(12), format.parse("12,00 €", Integer.class));
		Assert.assertEquals(new Float(23), format.parse("23,00 €", Float.class));
		Assert.assertEquals(new Double(2000.96d), format.parse("2 000,96 €", Double.class));
		Assert.assertEquals(new BigDecimal("345678896.34567890765223334"), format.parse("345 678 896,34567890765223334 €", BigDecimal.class));
		Assert.assertEquals("345 678 896,34567890765223334 €", format.format(new BigDecimal("345678896.34567890765223334"), ""));
		Assert.assertEquals(AtomicLong.class, format.parse("123,00 €", AtomicLong.class).getClass());
		Assert.assertEquals(new AtomicLong(123).longValue(), ((AtomicLong)format.parse("123,00 €", AtomicLong.class)).longValue());
		
		Assert.assertEquals(AtomicInteger.class, format.parse("123,00 €", AtomicInteger.class).getClass());
		Assert.assertEquals(new AtomicInteger(123).intValue(), ((AtomicInteger)format.parse("123,00 €", AtomicInteger.class)).intValue());
		Assert.assertEquals(new Short((short)1), format.parse("1,00 €", Short.class));
		Assert.assertEquals(new Byte((byte)1), format.parse("1,00 €", Byte.class));
		Assert.assertEquals(new BigInteger("123"), format.parse("123,00 €", BigInteger.class));
	}
	
	@Test
	public void currencyParseNoLocaleTest(){
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setPattern(null);
		format.init();
		Assert.assertNull(format.parse(null, Double.class));
		Assert.assertNull(format.parse(null, Float.class));
		Assert.assertNull(format.parse(null, BigDecimal.class));
		Assert.assertNull(format.parse(null, String.class));
		Assert.assertNull(format.parse(null, Integer.class));
		Assert.assertNull(format.parse(null, Long.class));
		Assert.assertNull(format.parse(null, BigInteger.class));
		Assert.assertNull(format.parse(null, AtomicInteger.class));
		Assert.assertNull(format.parse(null, AtomicLong.class));
		Assert.assertNull(format.parse(null, Calendar.class));
		
		try{
			format.parse("dcxcc<wxc<wc", Calendar.class);
			Assert.fail();
		}catch(Exception e){
			//nothing
		}

		
		Assert.assertEquals(new Integer(12), format.parse("$12.00", Integer.class));
		Assert.assertEquals(new Float(23), format.parse("$23.00", Float.class));
		Assert.assertEquals(new Double(2000.96d), format.parse("$2,000.96", Double.class));
		Assert.assertEquals(new BigDecimal("345678896.35"), format.parse("$345,678,896.34567890765223334", BigDecimal.class));
		Assert.assertEquals(AtomicLong.class, format.parse("$123.00", AtomicLong.class).getClass());
		Assert.assertEquals(new AtomicLong(123).longValue(), ((AtomicLong)format.parse("$123.00", AtomicLong.class)).longValue());
		
		Assert.assertEquals(AtomicInteger.class, format.parse("$123.00", AtomicInteger.class).getClass());
		Assert.assertEquals(new AtomicInteger(123).intValue(), ((AtomicInteger)format.parse("$123.00", AtomicInteger.class)).intValue());
		
		Assert.assertEquals(new Short((short)1), format.parse("$1.00", Short.class));
		Assert.assertEquals(new Byte((byte)1), format.parse("$1.00", Byte.class));
		Assert.assertEquals(new BigInteger("123"), format.parse("$123.00", BigInteger.class));
		
	}
	
	@Test
	public void currencyParseWithPatternTest(){
		CsvFormatter format = new CurrencyCsvFormatter();
		format.setPattern(".00");
		format.init();
		Assert.assertNull(format.parse(null, Double.class));
		Assert.assertNull(format.parse(null, Float.class));
		Assert.assertNull(format.parse(null, BigDecimal.class));
		Assert.assertNull(format.parse(null, String.class));
		Assert.assertNull(format.parse(null, Integer.class));
		Assert.assertNull(format.parse(null, Long.class));
		Assert.assertNull(format.parse(null, BigInteger.class));
		Assert.assertNull(format.parse(null, AtomicInteger.class));
		Assert.assertNull(format.parse(null, AtomicLong.class));
		Assert.assertNull(format.parse(null, Calendar.class));
		
		try{
			format.parse("dcxcc<wxc<wc", Calendar.class);
			Assert.fail();
		}catch(Exception e){
			//nothing
		}
		
		Assert.assertEquals(new Integer(12), format.parse("$12.00", Integer.class));
		Assert.assertEquals(new Float(23), format.parse("$23.00", Float.class));
		Assert.assertEquals(new Double(-2000.96d), format.parse("($2,000.96)", Double.class));
		Assert.assertEquals(new BigDecimal("345678896.35"), format.parse("$345,678,896.34567890765223334", BigDecimal.class));
		
		Assert.assertEquals(AtomicLong.class, format.parse("$123.00", AtomicLong.class).getClass());
		Assert.assertEquals(new AtomicLong(123).longValue(), ((AtomicLong)format.parse("$123.00", AtomicLong.class)).longValue());
		
		Assert.assertEquals(AtomicInteger.class, format.parse("$123.00", AtomicInteger.class).getClass());
		Assert.assertEquals(new AtomicInteger(123).intValue(), ((AtomicInteger)format.parse("$123.00", AtomicInteger.class)).intValue());
		
		Assert.assertEquals(new Short((short)1), format.parse("$1.00", Short.class));
		Assert.assertEquals(new Byte((byte)1), format.parse("$1.00", Byte.class));
		Assert.assertEquals(new BigInteger("123"), format.parse("$123.00", BigInteger.class));
		
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.00");
		format.init();
		Assert.assertEquals(new BigInteger("12"), format.parse("$12.00", BigInteger.class));
		Assert.assertEquals(new Double(23), format.parse("$23.00", Double.class));
		Assert.assertEquals(new BigDecimal("2000.96"), format.parse("$2,000.95845", BigDecimal.class));
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.##");
		format.init();
		Assert.assertEquals(AtomicInteger.class, format.parse("$12", AtomicInteger.class).getClass());
		Assert.assertEquals(new AtomicInteger(12).intValue(), ((AtomicInteger)format.parse("$12", AtomicInteger.class)).intValue());
		Assert.assertEquals(new Double(23.5d), format.parse("$23.5", Double.class));
		Assert.assertEquals(new BigDecimal("2000.96"), format.parse("$2,000.95845", BigDecimal.class));
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.0");
		format.init();
		Assert.assertEquals("$12.0", format.format(12, "default"));
		Assert.assertEquals("$23.5", format.format(23.5f, "default"));
		Assert.assertEquals("$2,001.0", format.format(2000.95874d, "default"));
		Assert.assertEquals(new Byte((byte)12), format.parse("$12.0", Byte.class));
		Assert.assertEquals(new Integer(24), format.parse("$23.6", Integer.class));
		Assert.assertEquals(new BigDecimal("2001.0"), format.parse("$2,000.95845", BigDecimal.class));
	}
}
