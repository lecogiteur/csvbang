/**
 *  com.github.lecogiteur.csvbang.test.util.FactoryObjectGeneratorTest
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
package com.github.lecogiteur.csvbang.test.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorInterfaceImpl;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactoryChildBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactoryInterface;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactoryInterfaceBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactoryNoDefaultConstNoStaticMethod;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactoryNoDefaultConstructor;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactorySimpleBean;
import com.github.lecogiteur.csvbang.util.FactoryObjectGenerator;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FactoryObjectGeneratorTest {
	
	@Test
	public void simpleTest() throws CsvBangException{
		final FactoryObjectGenerator<Integer> generator = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactorySimpleBean.class, null);
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		Integer bean = generator.generate("543");
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(543), bean);
		
		bean = generator.generate(new Byte("65"));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(65*100), bean);
		
		try{
			bean = generator.generate(new Long(7654));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Boolean.FALSE);
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
	@Test
	public void simpleMethodTest() throws CsvBangException{
		final FactoryObjectGenerator<Integer> generator = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactorySimpleBean.class, "valueIntOf");
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		Integer bean = generator.generate(new Byte("54"));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(54*100), bean);
		
		try{
			bean = generator.generate("7654");
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(new Long(7654));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Boolean.FALSE);
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
	@Test
	public void noMethodTest(){
		final FactoryObjectGenerator<Integer> generator2 = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactorySimpleBean.class, "valueIntOfNotExist");
		Assert.assertNull(generator2);
	}
	
	@Test
	public void noDefaultConstructorTest() throws CsvBangException{
		final FactoryObjectGenerator<Integer> generator = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactoryNoDefaultConstructor.class, null);
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		Integer bean = generator.generate("543");
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(543), bean);
		
		try{
			bean = generator.generate(new Integer(23543));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
	}
	
	@Test
	public void noDefaultConstructorNoMethodTest() throws CsvBangException{
		
		final FactoryObjectGenerator<Integer> generator2 = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactoryNoDefaultConstNoStaticMethod.class, null);
		Assert.assertNull(generator2);
		
	}
	
	@Test
	public void interfaceTest() throws CsvBangException{
		OGConstructorInterfaceImpl ogc = new OGConstructorInterfaceImpl(987);
		

		final FactoryObjectGenerator<Integer> generator = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactoryInterfaceBean.class, null);
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		Integer bean = generator.generate(ogc);
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(987), bean);
		
		final FactoryObjectGenerator<Integer> generator2 = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactoryInterface.class, null);
		Assert.assertNull(generator2);
	}
	
	@Test
	public void simpleChildTest() throws CsvBangException{
		final FactoryObjectGenerator<Integer> generator = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactoryChildBean.class, null);

		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		Integer bean = generator.generate("543");
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(543), bean);
		
		bean = generator.generate(new Byte("33"));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(33*100), bean);
		
		try{
			bean = generator.generate(new Long(7654));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Boolean.FALSE);
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
	@Test
	public void childSimpleMethodTest() throws CsvBangException{
		final FactoryObjectGenerator<Integer> generator = FactoryObjectGenerator.newInstance(Integer.class, 
				OGFactoryChildBean.class, "valueIntOf");
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		Integer bean = generator.generate(new Byte("20"));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(20*100), bean);
		
		try{
			bean = generator.generate("7654");
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(new Long(7654));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Boolean.FALSE);
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
}
