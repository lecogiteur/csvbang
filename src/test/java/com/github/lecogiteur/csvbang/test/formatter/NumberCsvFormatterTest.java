/**
 *  com.github.lecogiteur.csvbang.test.formatter.NumberCsvFormatterTest
 * 
 *  This program is called MyPlanner and help you to plan tasks and projects.
 * 
 *  Copyright (C) 2011  Tony EMMA
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.lecogiteur.csvbang.test.formatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.formatter.CsvFormatter;
import com.github.lecogiteur.csvbang.formatter.NumberCsvFormatter;


/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class NumberCsvFormatterTest {

	@Test
	public void numberTest(){
		CsvFormatter format = new NumberCsvFormatter();
		format.setPattern("#,###.##");
		format.setLocal(Locale.FRENCH);
		format.init();
		
		Assert.assertEquals("default", format.format(null, "default"));
		
		try{
			Assert.assertEquals("default", format.format("string", "default"));
			Assert.fail();
		}catch(Exception e){
			//good
		}
		
		Assert.assertEquals("12", format.format(12, "default"));
		Assert.assertEquals("26,4", format.format(26.4f, "default"));
		Assert.assertEquals("262 556,43", format.format(262556.42569d, "default"));
	}
	


	@Test
	public void numberParseTest(){
		CsvFormatter format = new NumberCsvFormatter();
		format.setLocal(Locale.FRENCH);
		format.setPattern("#,###.##");
		format.init();
		
		Assert.assertNull(format.parse(null, Integer.class));
		Assert.assertNull(format.parse(null, Double.class));
		Assert.assertNull(format.parse(null, Float.class));
		Assert.assertNull(format.parse(null, AtomicLong.class));
		Assert.assertNull(format.parse(null, BigDecimal.class));
		Assert.assertNull(format.parse(null, Byte.class));
		Assert.assertNull(format.parse(null, AtomicInteger.class));
		Assert.assertNull(format.parse(null, BigInteger.class));
		
		try{
			Assert.assertEquals(null, format.parse("string", Integer.class));
			Assert.fail();
		}catch(Exception e){
			//good
		}
		
		Assert.assertEquals(new Integer(12), format.parse("12", Integer.class));
		Assert.assertEquals(new Double(26.4d), format.parse("26,4", Double.class));
		Assert.assertEquals(new BigDecimal("262556.43"), format.parse("262 556,43", BigDecimal.class));
		Assert.assertEquals(2556, ((AtomicInteger)format.parse("2 556,43", AtomicInteger.class)).get());
	}
}
