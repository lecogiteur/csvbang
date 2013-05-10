/**
 *  fr.csvbang.test.util.ReflectionUti
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
package fr.csvbang.test.util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fr.csvbang.exception.CsvBangException;
import fr.csvbang.test.bean.BeanReflectionUtiTest;
import fr.csvbang.test.bean.ChildBeanReflectionUtiTest;
import fr.csvbang.util.CsvbangUti;
import fr.csvbang.util.ReflectionUti;

/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ReflectionUtiTest {
	
	@Test
	public void scanPackageClassTest() throws IOException{
		Assert.assertTrue("No package defined", CsvbangUti.isCollectionEmpty(ReflectionUti.scanPackageClass(null)));
		
		Assert.assertTrue("Empty package defined", CsvbangUti.isCollectionEmpty(ReflectionUti.scanPackageClass("")));
		
		Assert.assertTrue("Package not exist",  CsvbangUti.isCollectionEmpty(ReflectionUti.scanPackageClass("sdfkldf:dfsqsdf!qsdfqù%")));
		
		Collection<Class<?>> clazz1s = ReflectionUti.scanPackageClass("java.lang.*");
		Assert.assertNotNull("java.lang package has classes", clazz1s);
		Assert.assertTrue("String.class is in java.lang package", clazz1s.contains(String.class));
		Assert.assertFalse("Arrays.class is not in java.lang package", clazz1s.contains(Arrays.class));
		
		Collection<Class<?>> clazz2s = ReflectionUti.scanPackageClass("java.lang");
		Assert.assertNotNull("java.lang package has classes", clazz2s);
		Assert.assertEquals("scan of java.lang package must have the same size", clazz1s.size(), clazz2s.size());
		for (Class<?> c:clazz1s){
			Assert.assertTrue("clazz2s must contain the same classes", clazz2s.contains(c));
		}
		
		Collection<Class<?>> clazz3s = ReflectionUti.scanPackageClass("fr.csvbang");
		Assert.assertNotNull("fr.csvbang.util package has classes", clazz3s);
		Assert.assertTrue("ReflectionUti.class is in fr.csvbang package", clazz3s.contains(ReflectionUti.class));
		Assert.assertTrue("ReflectionUtiTest.class is in fr.csvbang package", clazz3s.contains(ReflectionUtiTest.class));
		Assert.assertFalse("Arrays.class is not in java.lang package", clazz3s.contains(Arrays.class));		
	}
	
	
	@Test
	public void getValueTest() throws CsvBangException{
		BeanReflectionUtiTest bean = new BeanReflectionUtiTest();
		ChildBeanReflectionUtiTest child = new ChildBeanReflectionUtiTest();
		
		Field[] beanFields = BeanReflectionUtiTest.class.getDeclaredFields();
		Map<String, Field> beanFieldsMap = new HashMap<String, Field>();
		for (Field f:beanFields){
			beanFieldsMap.put(f.getName(), f);
		}
		
		
		Method[] beanMethods = BeanReflectionUtiTest.class.getDeclaredMethods();
		Map<String, Method> beanMethodsMap = new HashMap<String, Method>();
		for (Method f:beanMethods){
			beanMethodsMap.put(f.getName(), f);
		}
		
		Field[] childFields = ChildBeanReflectionUtiTest.class.getDeclaredFields();
		Map<String, Field> childFieldssMap = new HashMap<String, Field>();
		for (Field f:childFields){
			childFieldssMap.put(f.getName(), f);
		}
		
		Method[] childMethods = ChildBeanReflectionUtiTest.class.getDeclaredMethods();
		Map<String, Method> childMethodsMap = new HashMap<String, Method>();
		for (Method f:childMethods){
			childMethodsMap.put(f.getName(), f);
		}
		
		Assert.assertEquals("public field", "publicField", ReflectionUti.getValue(beanFieldsMap.get("publicField"), bean));
		Assert.assertEquals("int field", 20, ReflectionUti.getValue(beanFieldsMap.get("intField"), bean));
		Assert.assertEquals("double field", new Double(30.5), ReflectionUti.getValue(beanFieldsMap.get("doubleField"), bean));
		
		try {
			ReflectionUti.getValue(beanFieldsMap.get("privateField"), bean);
			Assert.fail("private field");
		} catch (CsvBangException e) {
		}
		
		try {
			ReflectionUti.getValue(beanFieldsMap.get("protectedField"), bean);
			Assert.fail("protected field");
		} catch (CsvBangException e) {
		}
		
		Assert.assertEquals("get private field", "privateField", ReflectionUti.getValue(beanMethodsMap.get("getPrivateField"), bean));
		Assert.assertEquals("get protected field", "protectedField", ReflectionUti.getValue(beanMethodsMap.get("getProtectedField"), bean));
		Assert.assertEquals("get fake public field", "publicMethod", ReflectionUti.getValue(beanMethodsMap.get("getPublicField"), bean));
		Assert.assertEquals("simple method", "simpleMethod", ReflectionUti.getValue(beanMethodsMap.get("simpleMethod"), bean));
		
		try {
			ReflectionUti.getValue(beanMethodsMap.get("protectedMethod"), bean);
			Assert.fail("protected method");
		} catch (CsvBangException e) {
		}

		try {
			ReflectionUti.getValue(beanMethodsMap.get("privateMethod"), bean);
			Assert.fail("private method");
		} catch (CsvBangException e) {
		}
		
		Assert.assertEquals("get int method", -20, ReflectionUti.getValue(beanMethodsMap.get("intMethod"), bean));
		Assert.assertEquals("double method", new Double(-30.5), ReflectionUti.getValue(beanMethodsMap.get("doubleMethod"), bean));
		
		
		Assert.assertEquals("simple method of sub class", "subclass", ReflectionUti.getValue(childMethodsMap.get("getSubclass"), child));
		Assert.assertEquals("override method of sub class", "protectedFieldsubclass", ReflectionUti.getValue(childMethodsMap.get("getProtectedField"), child));
		
	}
	

	@Test
	public void getGetterMethodTest() throws IntrospectionException, CsvBangException{
		Assert.assertNull("If null field, result is null", ReflectionUti.getGetterMethod(BeanReflectionUtiTest.class, null));
		
		Method method = ReflectionUti.getGetterMethod(BeanReflectionUtiTest.class, "privateField");
		Assert.assertNotNull("simple getter must be not null", method);
		Assert.assertEquals("simple getter", "getPrivateField", method.getName());
		
		method = null;
		method = ReflectionUti.getGetterMethod(BeanReflectionUtiTest.class, "otherPrivateField");
		Assert.assertNull(method);
		
		method = null;
		method = ReflectionUti.getGetterMethod(ChildBeanReflectionUtiTest.class, "othrProtectedField");
		Assert.assertNull(method);

		method = null;
		method = ReflectionUti.getGetterMethod(ChildBeanReflectionUtiTest.class, "protectedField");
		ChildBeanReflectionUtiTest child = new ChildBeanReflectionUtiTest();
		Assert.assertNotNull(method);
		Assert.assertEquals("getProtectedField", method.getName());
		Assert.assertEquals("protectedFieldsubclass", ReflectionUti.getValue(method, child));
	}
	
	@Test
	public void getCsvTypeAnnotationTest(){
		Annotation[] annotations = BeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvTypeAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvTypeAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getDeclaredAnnotations();
		Assert.assertNull(ReflectionUti.getCsvTypeAnnotation(annotations));
		
	}
	
	@Test
	public void getCsvFieldAnnotationTest() throws SecurityException, NoSuchFieldException{
		Annotation[] annotations = BeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFieldAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFieldAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFieldAnnotation(annotations));
	}
	
	@Test
	public void getCsvFormatAnnotationTest() throws SecurityException, NoSuchFieldException{
		Annotation[] annotations = BeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFormatAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFormatAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFormatAnnotation(annotations));
	}
	
	@Test
	public void getMembersTest(){
		List<AnnotatedElement> members = ReflectionUti.getMembers(ChildBeanReflectionUtiTest.class);
		List<String> memberList = new ArrayList<String>();
		for (Method m:ChildBeanReflectionUtiTest.class.getDeclaredMethods()){
			memberList.add(m.getName());
		}
		for (Field m:ChildBeanReflectionUtiTest.class.getDeclaredFields()){
			memberList.add(m.getName());
		}
		Assert.assertNotNull(members);
		Assert.assertEquals(memberList.size(), members.size());
		for (AnnotatedElement e:members){
			Assert.assertTrue(memberList.contains(((Member)e).getName()));
		}
		
		Field f = BeanReflectionUtiTest.class.getDeclaredFields()[0];
		for (final AnnotatedElement e:members){
			if (f.getName().equals(((Member)e).getName())){
				Assert.fail("Must not contain fields of parent");
			}
		}
	}
}
