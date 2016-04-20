/**
 *  com.github.lecogiteur.csvbang.test.factory.FactoryCsvWriterTest
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
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
package com.github.lecogiteur.csvbang.test.factory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.factory.FactoryCsvbang;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.reader.CsvReader;
import com.github.lecogiteur.csvbang.test.bean.BeanCsv;
import com.github.lecogiteur.csvbang.writer.CsvWriter;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
//TODO crer des reader et des writer qui surcharge les classe pour la gestion de registerThread
@RunWith(BlockJUnit4ClassRunner.class)
public class FactoryCsvbangTest {
	private File file;
	
	public FactoryCsvbangTest() throws URISyntaxException{
		file = new File(getClass().getResource("/csvfilename/file.csv").toURI());
	}
	
	@Test
	public void nullInitFactory() throws CsvBangException{
		FactoryCsvbang factory = new FactoryCsvbang();
		factory.add((Class<?>)null);
		factory.add((Collection<Class<?>>)null);
		factory.addPackage(null);
		CsvWriter<BeanCsv> writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		CsvReader<BeanCsv> reader;
		reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
		
		factory = new FactoryCsvbang((String)null);
		factory.add((Class<?>)null);
		factory.add((Collection<Class<?>>)null);
		factory.addPackage(null);
		writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
		
		factory = new FactoryCsvbang((Collection<Class<?>>)null);
		factory.add((Class<?>)null);
		factory.add((Collection<Class<?>>)null);
		factory.addPackage(null);
		writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
	}
	
	@Test
	public void classFactory() throws CsvBangException{
		FactoryCsvbang factory = new FactoryCsvbang();
		factory.add(BeanCsv.class);
		CsvWriter<BeanCsv> writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		CsvReader<BeanCsv> reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
		
		factory = new FactoryCsvbang();
		factory.add(new HashSet<Class<?>>(Collections.singleton(BeanCsv.class)));
		writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
		
		factory = new FactoryCsvbang(new HashSet<Class<?>>(Collections.singleton(BeanCsv.class)));
		writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
	}
	
	@Test
	public void packageFactory() throws CsvBangException{
		FactoryCsvbang factory = new FactoryCsvbang();
		factory.addPackage("com.github.lecogiteur.csvbang.test.bean");
		CsvWriter<BeanCsv> writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		CsvReader<BeanCsv> reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
		
		factory = new FactoryCsvbang("com.github.lecogiteur.csvbang.test.bean");
		writer = factory.createCsvWriter(BeanCsv.class);
		Assert.assertNotNull(writer);
		reader = factory.createCsvReader(BeanCsv.class, new FileName(file.getAbsolutePath(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file.getParentFile()), new FileName(file.getName(), null));
		Assert.assertNotNull(reader);
		reader = factory.createCsvReader(BeanCsv.class, Collections.singleton(file));
		Assert.assertNotNull(reader);
	}
	
}
