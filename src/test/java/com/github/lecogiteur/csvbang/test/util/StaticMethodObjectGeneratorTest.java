package com.github.lecogiteur.csvbang.test.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGConstructorInterfaceImpl;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSMChildBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSMInterfaceBean;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSMInterfaceResult;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSMNoMethod;
import com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSMSimpleBean;
import com.github.lecogiteur.csvbang.util.StaticMethodObjectGenerator;


/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class StaticMethodObjectGeneratorTest {

	@Test
	public void simpleTest() throws CsvBangException{
		StaticMethodObjectGenerator<OGSMSimpleBean> generator = StaticMethodObjectGenerator.newInstance(OGSMSimpleBean.class, null);

		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		OGSMSimpleBean bean = generator.generate(new Integer(10));
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(10), bean.getIn());
		
		bean = generator.generate("765");
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(765), bean.getIn());
		
		try{
			bean = generator.generate(Long.valueOf(304));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Boolean.TRUE);
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
	@Test
	public void simpleWithMethodNameTest() throws CsvBangException{
		StaticMethodObjectGenerator<OGSMSimpleBean> generator = StaticMethodObjectGenerator.newInstance(OGSMSimpleBean.class, "valueOf");

		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		OGSMSimpleBean bean = generator.generate("765");
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(765), bean.getIn());
		
		try{
			bean = generator.generate(new Integer(10));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Long.valueOf(304));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
		
		try{
			bean = generator.generate(Boolean.TRUE);
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
	@Test
	public void noMethodTest() throws CsvBangException{
		StaticMethodObjectGenerator<OGSMNoMethod> generator = StaticMethodObjectGenerator.newInstance(OGSMNoMethod.class, null);
		Assert.assertNull(generator);
	}
	
	@Test
	public void interfaceTest() throws CsvBangException{
		OGConstructorInterfaceImpl ogc = new OGConstructorInterfaceImpl(987);
		
		StaticMethodObjectGenerator<OGSMInterfaceBean> generator = StaticMethodObjectGenerator.newInstance(OGSMInterfaceBean.class, null);
		
		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		OGSMInterfaceBean bean = generator.generate(ogc);
		Assert.assertNotNull(bean);
		Assert.assertEquals(new Integer(987), bean.getInt());
		
		StaticMethodObjectGenerator<OGSMInterfaceResult> generator2 = StaticMethodObjectGenerator.newInstance(OGSMInterfaceResult.class, null);
		Assert.assertNull(generator2);
	}
	
	@Test
	public void childTest() throws CsvBangException{
		StaticMethodObjectGenerator<OGSMChildBean> generator = StaticMethodObjectGenerator.newInstance(OGSMChildBean.class, null);

		Assert.assertNotNull(generator);
		Assert.assertNull(generator.generate(null));
		
		try{
			OGSMSimpleBean bean = generator.generate(new Integer(10));
			Assert.fail();
		}catch(CsvBangException e){
			
		}
	}
	
}
