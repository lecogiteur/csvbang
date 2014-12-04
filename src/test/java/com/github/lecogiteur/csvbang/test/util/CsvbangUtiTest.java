/**
 *  com.github.lecogiteur.csvbang.test.util.CsvbangUtiTest
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CsvbangUtiTest {

	@Test
	public void isStringBlankTest(){
		Assert.assertTrue("Null is blank", CsvbangUti.isStringBlank(null));
		Assert.assertTrue("Empty String is blank", CsvbangUti.isStringBlank(""));
		Assert.assertTrue("White String is blank", CsvbangUti.isStringBlank("   "));
		Assert.assertFalse("String is not blank", CsvbangUti.isStringBlank("fghdfg"));
		Assert.assertFalse("String is not blank", CsvbangUti.isStringBlank("   g  "));
		Assert.assertFalse("String is not blank", CsvbangUti.isStringBlank("g  "));
	}
	
	@Test
	public void isStringNotBlankTest(){
		Assert.assertFalse("Null is blank", CsvbangUti.isStringNotBlank(null));
		Assert.assertFalse("Empty String is blank", CsvbangUti.isStringNotBlank(""));
		Assert.assertFalse("White String is blank", CsvbangUti.isStringNotBlank("   "));
		Assert.assertTrue("String is not blank", CsvbangUti.isStringNotBlank("fghdfg"));
		Assert.assertTrue("String is not blank", CsvbangUti.isStringNotBlank("   g  "));
		Assert.assertTrue("String is not blank", CsvbangUti.isStringNotBlank("g  "));
	}
	
	@Test
	public void isCollectionEmptyTest(){
		Collection<String> c = new ArrayList<String>(1);
		Assert.assertTrue("null collection is empty", CsvbangUti.isCollectionEmpty(null));
		Assert.assertTrue("empty collection is empty", CsvbangUti.isCollectionEmpty(c));
		
		c.add("string");
		Assert.assertFalse("no empty collection is not empty", CsvbangUti.isCollectionEmpty(c));
	}
	
	@Test
	public void isCollectionNotEmptyTest(){
		Collection<String> c = new ArrayList<String>(1);
		Assert.assertFalse("null collection is empty", CsvbangUti.isCollectionNotEmpty(null));
		Assert.assertFalse("empty collection is empty", CsvbangUti.isCollectionNotEmpty(c));
		
		c.add("string");
		Assert.assertTrue("no empty collection is not empty", CsvbangUti.isCollectionNotEmpty(c));
	}
	
	@Test
	public void getAllFiles(){
		//null
		Assert.assertNull(CsvbangUti.getAllFiles(null, null));
		
		//not exist
		Assert.assertNull(CsvbangUti.getAllFiles(new File("toto"), null));
		
		//file
		System.out.println(new File("").getAbsolutePath());
		Assert.assertNotNull(CsvbangUti.getAllFiles(new File("csvbanguti/folder4/file4.csv"), null));
		Assert.assertEquals(1, CsvbangUti.getAllFiles(new File("csvbanguti/folder4/file4.csv"), null).size());
		Assert.assertEquals("file4.csv", CsvbangUti.getAllFiles(new File("csvbanguti/folder4/file4.csv"), null).iterator().next().getName());
		
		//directory
		final Collection<File> files = CsvbangUti.getAllFiles(new File("csvbanguti"), null);
		Assert.assertNotNull(files);
		Assert.assertEquals(4, files.size());
		ArrayList<String> list = new ArrayList<String>();
		for (int i=1; i<=4; i++){
			list.add("file" + i + ".csv");
		}
		for (final File f:files){
			Assert.assertTrue(list.contains(f.getName()));
		}
		
		final Collection<File> fileswithFilter = CsvbangUti.getAllFiles(new File("csvbanguti"), new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				Pattern p = Pattern.compile("file[1-9]\\.csv");
				return p.matcher(name).matches();
			}
		});
		Assert.assertNotNull(fileswithFilter);
		Assert.assertEquals(4, fileswithFilter.size());
		for (final File f:files){
			Assert.assertTrue(list.contains(f.getName()));
		}
	}
}
