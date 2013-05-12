/**
 *  fr.csvbang.test.formatter.NumberCsvFormatterTest
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
package fr.csvbang.test.formatter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fr.csvbang.formatter.CsvFormatter;
import fr.csvbang.formatter.NumberCsvFormatter;

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
}
