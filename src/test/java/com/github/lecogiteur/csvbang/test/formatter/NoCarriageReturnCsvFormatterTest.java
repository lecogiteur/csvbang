/**
 *  com.github.lecogiteur.csvbang.test.formatter.NoCarriageReturnCsvFormatterTest
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.formatter.NoCarriageReturnCsvFormatter;

/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class NoCarriageReturnCsvFormatterTest {

	@Test
	public void noCarriageReturnTest(){
		NoCarriageReturnCsvFormatter formatter = new NoCarriageReturnCsvFormatter();
		formatter.init();
		
		Assert.assertEquals("default string", formatter.format(null, "default string"));
		Assert.assertEquals("normal string", formatter.format("normal string", "default string"));
		Assert.assertEquals("a  string", formatter.format("a \nstring", "default string"));
		Assert.assertEquals("a  string", formatter.format("a \r\nstring", "default string"));
		Assert.assertEquals("a   string", formatter.format("a \r\n\nstring", "default string"));
	}

	@Test
	public void noCarriageReturnWithPatternTest(){
		NoCarriageReturnCsvFormatter formatter = new NoCarriageReturnCsvFormatter();
		formatter.setPattern("<BR/>");
		formatter.init();
		
		Assert.assertEquals("default string", formatter.format(null, "default string"));
		Assert.assertEquals("normal string", formatter.format("normal string", "default string"));
		Assert.assertEquals("a <BR/>string", formatter.format("a \nstring", "default string"));
		Assert.assertEquals("a <BR/>string", formatter.format("a \r\nstring", "default string"));
		Assert.assertEquals("a <BR/><BR/>string", formatter.format("a \r\n\nstring", "default string"));
	}

	@Test
	public void noCarriageReturnWithNoPatternTest(){
		NoCarriageReturnCsvFormatter formatter = new NoCarriageReturnCsvFormatter();
		formatter.setPattern(null);
		formatter.init();
		
		Assert.assertEquals("default string", formatter.format(null, "default string"));
		Assert.assertEquals("normal string", formatter.format("normal string", "default string"));
		Assert.assertEquals("a  string", formatter.format("a \nstring", "default string"));
		Assert.assertEquals("a  string", formatter.format("a \r\nstring", "default string"));
		Assert.assertEquals("a   string", formatter.format("a \r\n\nstring", "default string"));
	}

	@Test
	public void noCarriageReturnWithPatternEmptyTest(){
		NoCarriageReturnCsvFormatter formatter = new NoCarriageReturnCsvFormatter();
		formatter.setPattern("");
		formatter.init();
		
		Assert.assertEquals("default string", formatter.format(null, "default string"));
		Assert.assertEquals("normal string", formatter.format("normal string", "default string"));
		Assert.assertEquals("a string", formatter.format("a \nstring", "default string"));
		Assert.assertEquals("a string", formatter.format("a \r\nstring", "default string"));
		Assert.assertEquals("a string", formatter.format("a \r\n\nstring", "default string"));
	}
	
}
