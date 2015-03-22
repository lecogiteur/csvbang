/**
 *  com.github.lecogiteur.csvbang.test.formatter.DefaultTest
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
package com.github.lecogiteur.csvbang.test.formatter;


import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.formatter.CsvFormatter;
import com.github.lecogiteur.csvbang.formatter.Default;


/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class DefaultTest {
	
	@Test
	public void defaultTest(){
		CsvFormatter format = new Default();
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("default", format.format("", "default"));
		Assert.assertEquals(" ", format.format(" ", "default"));
		Assert.assertEquals("18", format.format(18, "default"));
	}
	
	@Test
	public void defaultParseTest(){
		CsvFormatter format = new Default();
		format.init();
		Assert.assertEquals(null, format.parse(null, String.class));
		Assert.assertEquals("", format.parse("", Integer.class));
		Assert.assertEquals(" ", format.parse(" ", Calendar.class));
		Assert.assertEquals("18", format.parse("18", Timestamp.class));
	}

}
