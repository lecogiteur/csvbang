/**
 *  com.github.lecogiteur.csvbang.test.util.ReflectionUti
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

import java.beans.IntrospectionException;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServicesSupport;
import java.beans.beancontext.BeanContextSupport;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.test.bean.reflection.BeanReflectionUtiTest;
import com.github.lecogiteur.csvbang.test.bean.reflection.BeanSetValue;
import com.github.lecogiteur.csvbang.test.bean.reflection.ChildBeanReflectionUtiTest;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.ObjectGenerator;
import com.github.lecogiteur.csvbang.util.ReflectionUti;


/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ReflectionUtiTest {
	
	//@Test
	//Not work with maven. Must explore
	public void scanPackageClassTest() throws CsvBangException{
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
		
		Collection<Class<?>> clazz3s = ReflectionUti.scanPackageClass("com.github.lecogiteur.csvbang");
		Assert.assertNotNull("com.github.lecogiteur.csvbang.util package has classes", clazz3s);
		Assert.assertTrue("ReflectionUti.class is in com.github.lecogiteur.csvbang package", clazz3s.contains(ReflectionUti.class));
		Assert.assertTrue("ReflectionUtiTest.class is in com.github.lecogiteur.csvbang package", clazz3s.contains(ReflectionUtiTest.class));
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
	public void getSetterMethodTest() throws IntrospectionException, CsvBangException, SecurityException, NoSuchFieldException{
		Assert.assertNull(ReflectionUti.getSetterMethod(null, BeanReflectionUtiTest.class));
		
		//public field
		final Field publicfield = BeanReflectionUtiTest.class.getDeclaredField("publicField");
		AnnotatedElement element = ReflectionUti.getSetterMethod(publicfield, BeanReflectionUtiTest.class);
		Assert.assertNotNull(element);
		Assert.assertEquals(publicfield, element);
		
		//simple setter
		final Field privateField = BeanReflectionUtiTest.class.getDeclaredField("privateField");
		element = ReflectionUti.getSetterMethod(privateField, BeanReflectionUtiTest.class);
		Assert.assertNotNull(element);
		Assert.assertNotEquals(publicfield, element);
		Assert.assertTrue(element instanceof Method);
		Assert.assertEquals("setPrivateField", ((Method)element).getName());
		
		//no setter defined
		final Field protectedField = BeanReflectionUtiTest.class.getDeclaredField("protectedField");
		element = ReflectionUti.getSetterMethod(protectedField, BeanReflectionUtiTest.class);
		Assert.assertNull(element);
		
		//final field
		final Field finalField = BeanReflectionUtiTest.class.getDeclaredField("finalField");
		try{
			element = ReflectionUti.getSetterMethod(finalField, BeanReflectionUtiTest.class);
			Assert.fail("The field is final so an exception must be thrown");
		}catch(CsvBangException e){
			//good !!
		}
		
		//child class
		//public field
		final Field subpublicfield = BeanReflectionUtiTest.class.getDeclaredField("publicField");
		element = ReflectionUti.getSetterMethod(subpublicfield, ChildBeanReflectionUtiTest.class);
		Assert.assertNotNull(element);
		Assert.assertEquals(subpublicfield, element);

		//simple setter
		final Field subprivateField = BeanReflectionUtiTest.class.getDeclaredField("privateField");
		element = ReflectionUti.getSetterMethod(subprivateField, ChildBeanReflectionUtiTest.class);
		Assert.assertNotNull(element);
		Assert.assertNotEquals(subprivateField, element);
		Assert.assertTrue(element instanceof Method);
		Assert.assertEquals("setPrivateField", ((Method)element).getName());
		
		
		final Field subprotField = BeanReflectionUtiTest.class.getDeclaredField("protectedField");
		element = ReflectionUti.getSetterMethod(subprotField, ChildBeanReflectionUtiTest.class);
		Assert.assertNotNull(element);
		Assert.assertNotEquals(subprotField, element);
		Assert.assertTrue(element instanceof Method);
		Assert.assertEquals("setProtectedField", ((Method)element).getName());
		
		
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
	public void getCsvFileAnnotationTest() throws SecurityException, NoSuchFieldException{
		Annotation[] annotations = BeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNull(ReflectionUti.getCsvFileAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFileAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getDeclaredAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFileAnnotation(annotations));
	}
	
	@Test
	public void getCsvHeaderAnnotationTest() throws SecurityException, NoSuchFieldException{
		Annotation[] annotations = BeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNull(ReflectionUti.getCsvHeaderAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvHeaderAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getDeclaredAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvHeaderAnnotation(annotations));
	}
	
	@Test
	public void getCsvFooterAnnotationTest() throws SecurityException, NoSuchFieldException{
		Annotation[] annotations = BeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNull(ReflectionUti.getCsvFooterAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFooterAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getDeclaredAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvFooterAnnotation(annotations));
	}
	
	@Test
	public void getCsvCommentAnnotationTest() throws SecurityException, NoSuchFieldException{
		Annotation[] annotations = BeanReflectionUtiTest.class.getField("comment").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvCommentAnnotation(annotations));
		
		annotations = BeanReflectionUtiTest.class.getField("publicField").getAnnotations();
		Assert.assertNull(ReflectionUti.getCsvCommentAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getField("comment").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvCommentAnnotation(annotations));
		
		annotations = ChildBeanReflectionUtiTest.class.getField("comment").getAnnotations();
		Assert.assertNotNull(ReflectionUti.getCsvCommentAnnotation(annotations));
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
	
	@Test
	public void getSetterTypeTest() throws SecurityException, NoSuchFieldException, CsvBangException{
		//for field
		final Field publicfield = BeanReflectionUtiTest.class.getDeclaredField("publicField");
		final AnnotatedElement fieldsetter = ReflectionUti.getSetterMethod(publicfield, BeanReflectionUtiTest.class);
		
		Assert.assertNotNull(fieldsetter);
		Assert.assertTrue(fieldsetter instanceof Field);
		Assert.assertEquals("publicField", ((Field)fieldsetter).getName());
		
		final Class<?> fieldtype = ReflectionUti.getSetterType(fieldsetter);
		Assert.assertNotNull(fieldtype);
		Assert.assertEquals(String.class, fieldtype);
		
		
		//for method
		final Field privatefield = BeanReflectionUtiTest.class.getDeclaredField("privateField");
		final AnnotatedElement methodsetter = ReflectionUti.getSetterMethod(privatefield, BeanReflectionUtiTest.class);
		
		Assert.assertNotNull(methodsetter);
		Assert.assertTrue(methodsetter instanceof Method);
		Assert.assertEquals("setPrivateField", ((Method)methodsetter).getName());
		
		final Class<?> methodtype = ReflectionUti.getSetterType(methodsetter);
		Assert.assertNotNull(methodtype);
		Assert.assertEquals(String.class, methodtype);
	}
	
	@Test
	public void setValueTest() throws CsvBangException, SecurityException, NoSuchFieldException{
		final Field fieldvalue1 = BeanSetValue.class.getDeclaredField("value1");
		final Field fieldvalue2 = BeanSetValue.class.getDeclaredField("value2");
		final Field fieldvalue3 = BeanSetValue.class.getDeclaredField("value3");
		
		final AnnotatedElement setter1 = ReflectionUti.getSetterMethod(fieldvalue1, BeanSetValue.class);
		final AnnotatedElement setter2 = ReflectionUti.getSetterMethod(fieldvalue2, BeanSetValue.class);
		final AnnotatedElement setter3 = ReflectionUti.getSetterMethod(fieldvalue3, BeanSetValue.class);
		
		final Class<?> type1 = ReflectionUti.getSetterType(setter1);
		final Class<?> type2 = ReflectionUti.getSetterType(setter2);
		final Class<?> type3 = ReflectionUti.getSetterType(setter3);
		
		final ObjectGenerator<?> generator1 = ReflectionUti.createTypeGenerator(type1, null, null);
		final ObjectGenerator<?> generator2 = ReflectionUti.createTypeGenerator(type2, null, null);
		final ObjectGenerator<?> generator3 = ReflectionUti.createTypeGenerator(type3, null, null);
		
		BeanSetValue bean = new BeanSetValue();
		ReflectionUti.setValue(setter1, generator1, bean, null);
		Assert.assertNull(bean.getValue1());
		ReflectionUti.setValue(setter1, generator1, bean, "345");
		Assert.assertEquals(new Integer(345), bean.getValue1());
		
		ReflectionUti.setValue(setter2, generator2, bean, null);
		Assert.assertNull(bean.getValue2());
		ReflectionUti.setValue(setter2, generator2, bean, "345");
		Assert.assertEquals(new Long(345), bean.getValue2());
		
		ReflectionUti.setValue(setter3, generator3, bean, null);
		Assert.assertNull(bean.getValue3());
		ReflectionUti.setValue(setter3, generator3, bean, "345");
		Assert.assertEquals("345", bean.getValue3());
	}
	
	@Test
	public void isCollectionTest(){
		Assert.assertFalse(ReflectionUti.isCollection(null));
		Assert.assertTrue(ReflectionUti.isCollection(Collection.class));
		Assert.assertTrue(ReflectionUti.isCollection(List.class));
		Assert.assertTrue(ReflectionUti.isCollection(ArrayList.class));
		Assert.assertTrue(ReflectionUti.isCollection(HashSet.class));
		Assert.assertFalse(ReflectionUti.isCollection(Integer.class));
	}
	
	@SuppressWarnings({"unused","rawtypes"})
	private final class ParameterizedSetterTypeBean{
		public Integer field1;
		public List<Calendar> field2;
		public Set<?> field3;
		public Collection field4;
		public Collection<? extends Integer> field5;
		public void setCol1(List<Double> d){
			
		}
		public void setCol2(Double d){
			
		}
		public void setCol3(Set<?> d){
			
		}
		public void setCol4(Collection d){
			
		}
		public void setCol5(Collection<? extends Double> d){
			
		}
		
	}
	
	@Test
	public void getParameterizedSetterTypeTest() throws SecurityException, NoSuchFieldException, CsvBangException{
		final Field field1 = ParameterizedSetterTypeBean.class.getField("field1");
		final Field field2 = ParameterizedSetterTypeBean.class.getField("field2");
		final Field field3 = ParameterizedSetterTypeBean.class.getField("field3");
		final Field field4 = ParameterizedSetterTypeBean.class.getField("field4");
		final Field field5 = ParameterizedSetterTypeBean.class.getField("field5");
		final Method[] methods = ParameterizedSetterTypeBean.class.getMethods();
		Method setCol1 = null;
		Method setCol2 = null;
		Method setCol3 = null;
		Method setCol4 = null;
		Method setCol5 = null;
		for (final Method method:methods){
			if ("setCol1".equals(method.getName())){
				setCol1 = method;
			}else if ("setCol2".equals(method.getName())){
				setCol2 = method;
			}else if ("setCol3".equals(method.getName())){
				setCol3 = method;
			}else if ("setCol4".equals(method.getName())){
				setCol4 = method;
			}else if ("setCol5".equals(method.getName())){
				setCol5 = method;
			}
		}
		
		
		Assert.assertNull(ReflectionUti.getParameterizedSetterType(null));
		try{
			ReflectionUti.getParameterizedSetterType(field1);
			Assert.fail();
		}catch(ClassCastException e){}
		try{
			ReflectionUti.getParameterizedSetterType(setCol2);
			Assert.fail();
		}catch(ClassCastException e){}
		
		Assert.assertEquals(Calendar.class, ReflectionUti.getParameterizedSetterType(field2));
		Assert.assertEquals(Double.class, ReflectionUti.getParameterizedSetterType(setCol1));
		Assert.assertEquals(Object.class, ReflectionUti.getParameterizedSetterType(setCol3));
		Assert.assertEquals(Object.class, ReflectionUti.getParameterizedSetterType(setCol4));
		Assert.assertEquals(Object.class, ReflectionUti.getParameterizedSetterType(field3));
		Assert.assertEquals(Object.class, ReflectionUti.getParameterizedSetterType(field4));
		Assert.assertEquals(Integer.class, ReflectionUti.getParameterizedSetterType(field5));
		Assert.assertEquals(Double.class, ReflectionUti.getParameterizedSetterType(setCol5));
		
	}
	
	@Test
	public void newInstanceCollectionOrArrayTest() throws CsvBangException{
		int[] array = new int[0];
		Assert.assertNull(ReflectionUti.newInstanceCollectionOrArray(null));
		Assert.assertEquals(BeanContextSupport.class, ReflectionUti.newInstanceCollectionOrArray(BeanContext.class).getClass());
		Assert.assertEquals(BeanContextServicesSupport.class, ReflectionUti.newInstanceCollectionOrArray(BeanContextServices.class).getClass());
		Assert.assertEquals(LinkedBlockingDeque.class, ReflectionUti.newInstanceCollectionOrArray(BlockingDeque.class).getClass());
		Assert.assertEquals(LinkedBlockingQueue.class, ReflectionUti.newInstanceCollectionOrArray(BlockingQueue.class).getClass());
		Assert.assertEquals(ArrayDeque.class, ReflectionUti.newInstanceCollectionOrArray(Deque.class).getClass());
		Assert.assertEquals(ArrayList.class, ReflectionUti.newInstanceCollectionOrArray(List.class).getClass());
		Assert.assertEquals(TreeSet.class, ReflectionUti.newInstanceCollectionOrArray(NavigableSet.class).getClass());
		Assert.assertEquals(ArrayDeque.class, ReflectionUti.newInstanceCollectionOrArray(Queue.class).getClass());
		Assert.assertEquals(HashSet.class, ReflectionUti.newInstanceCollectionOrArray(Set.class).getClass());
		Assert.assertEquals(TreeSet.class, ReflectionUti.newInstanceCollectionOrArray(SortedSet.class).getClass());
		Assert.assertEquals(ArrayList.class, ReflectionUti.newInstanceCollectionOrArray(Collection.class).getClass());
		Assert.assertEquals(ArrayList.class, ReflectionUti.newInstanceCollectionOrArray(ArrayList.class).getClass());
		Assert.assertEquals(ArrayList.class, ReflectionUti.newInstanceCollectionOrArray(array.getClass()).getClass());
		Assert.assertNull(ReflectionUti.newInstanceCollectionOrArray(String.class));
	}
}
