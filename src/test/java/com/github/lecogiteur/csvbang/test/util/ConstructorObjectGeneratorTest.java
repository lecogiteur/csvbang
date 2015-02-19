/**
 *  com.github.lecogiteur.csvbang.test.util.ObjectGeneratorTest
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
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorChildBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorInterface;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorInterfaceBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorInterfaceImpl;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorNoConstructBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorSimpleBean;
import com.github.lecogiteur.csvbang.util.ConstructorObjectGenerator;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ConstructorObjectGeneratorTest {

	@Test
	public void simpleTest() throws CsvBangException{
		final ConstructorObjectGenerator<OGConstructorSimpleBean> generator = ConstructorObjectGenerator.newInstance(OGConstructorSimpleBean.class);
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		OGConstructorSimpleBean bean = generator.generate(new Integer(10));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(10), bean.getI());
		Assert.assertNull(bean.getS());
		
		bean = generator.generate("304");
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(304), bean.getI());
		Assert.assertNull(bean.getS());
		

		try{
			bean = generator.generate(Long.valueOf(304));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
	@Test
	public void noConstructor() throws CsvBangException{
		final ConstructorObjectGenerator<OGConstructorNoConstructBean> generator = ConstructorObjectGenerator.newInstance(OGConstructorNoConstructBean.class);
		
		Assert.assertNull(generator);
	}
	
	@Test
	public void interfaceTest() throws CsvBangException{
		final ConstructorObjectGenerator<OGConstructorInterfaceBean> generator = ConstructorObjectGenerator.newInstance(OGConstructorInterfaceBean.class);
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		OGConstructorInterfaceImpl i = new OGConstructorInterfaceImpl(23);
		OGConstructorInterfaceBean bean = generator.generate(i);
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(23), bean.getI());
		
		bean = generator.generate(new Integer(43));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(143), bean.getI());
		
		final ConstructorObjectGenerator<OGConstructorInterface> generator2 = ConstructorObjectGenerator.newInstance(OGConstructorInterface.class);
		Assert.assertNull(generator2);
		
	}
	
	@Test
	public void subClassTest() throws CsvBangException{
		final ConstructorObjectGenerator<OGConstructorChildBean> generator1 = ConstructorObjectGenerator.newInstance(OGConstructorChildBean.class);
		
		Assert.assertNotNull(generator1);
		Assert.assertNull(generator1.generate(null));
		
		try{
			OGConstructorChildBean bean = generator1.generate(Integer.valueOf(304));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		OGConstructorChildBean child = generator1.generate("543");
		Assert.assertNotNull(child);
		Assert.assertEquals(new Integer(543), child.getI());
		Assert.assertNull(child.getS());
		
		final ConstructorObjectGenerator<OGConstructorInterfaceBean> generator2 = ConstructorObjectGenerator.newInstance(OGConstructorInterfaceBean.class);
		OGConstructorInterfaceBean bean = generator2.generate(child);
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(543), bean.getI());
	}
	
}
