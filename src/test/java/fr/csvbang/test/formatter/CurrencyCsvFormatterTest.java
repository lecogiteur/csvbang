/**
 *  fr.csvbang.test.formatter.CurrencyCsvFormatterTest
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
package fr.csvbang.test.formatter;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fr.csvbang.formatter.CsvFormatter;
import fr.csvbang.formatter.CurrencyCsvFormatter;

/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CurrencyCsvFormatterTest {

	@Test
	public void currencyTest(){
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
	}
	
	@Test
	public void currencyNoLocaleTest(){
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
	}
	
	@Test
	public void currencyWithPatternTest(){
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
		
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.00");
		format.init();
		Assert.assertEquals("$12.00", format.format(12, "default"));
		Assert.assertEquals("$23.00", format.format(23.0f, "default"));
		Assert.assertEquals("$2,000.96", format.format(2000.95874d, "default"));
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.##");
		format.init();
		Assert.assertEquals("$12", format.format(12, "default"));
		Assert.assertEquals("$23.5", format.format(23.5f, "default"));
		Assert.assertEquals("$2,000.96", format.format(2000.95874d, "default"));
		
		format = new CurrencyCsvFormatter();
		format.setPattern("#####.0");
		format.init();
		Assert.assertEquals("$12.0", format.format(12, "default"));
		Assert.assertEquals("$23.5", format.format(23.5f, "default"));
		Assert.assertEquals("$2,001.0", format.format(2000.95874d, "default"));
	}
}
