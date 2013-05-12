package fr.csvbang.test.writer;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import fr.csvbang.exception.CsvBangException;
import fr.csvbang.factory.FactoryCsvWriter;
import fr.csvbang.test.bean.BeanChildCsv;
import fr.csvbang.test.util.WriterThread;
import fr.csvbang.writer.CsvWriter;


//@RunWith(BlockJUnit4ClassRunner.class)
public class WriterTest {

	//@Test
	public void testSimpleWriter() throws ClassNotFoundException, IOException, IntrospectionException, CsvBangException, IllegalAccessException, InstantiationException{
		FactoryCsvWriter factory = new FactoryCsvWriter("fr.csvbang.test.bean.*");
		final CsvWriter<BeanChildCsv> writer = factory.createCsvWriter(BeanChildCsv.class, "/tmp/tony.csv");
		
		
		List<BeanChildCsv> list1 = new ArrayList<BeanChildCsv>(100000);
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("toto");
			b.setValue("titi");
			b.setDudu("myDududududududududududududududududududududududu");
			b.setDate("myDate");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date()}));
			list1.add(b);
		}
		Runnable r1 = new WriterThread<BeanChildCsv>(writer, list1);
		
		List<BeanChildCsv> list2 = new ArrayList<BeanChildCsv>(100000);
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("jijijuju");
			b.setValue("jujujujujujujujujujjuujujujujujujuuujujuujujuju");
			b.setDudu("mumuumumumumumumumumumumumumumumumumumumu");
			b.setDate("gugu");
			b.setMadate(new Date());
			b.setYes("Non");
			list2.add(b);
		}
		Runnable r2 = new WriterThread<BeanChildCsv>(writer, list2);
		
		List<BeanChildCsv> list3 = new ArrayList<BeanChildCsv>(100000);
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("sasa");
			b.setValue("fafafafafafafafafafafafafafafafafaffafafafafa");
			b.setDudu("zaza");
			b.setDate("rara");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date(), new Date()}));
			list3.add(b);
		}
		Runnable r3 = new WriterThread<BeanChildCsv>(writer, list3);
		
		
		
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		
		long start = System.currentTimeMillis();
		t1.start();
		t2.start();
		t3.start();
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){};
		writer.close();
		System.out.println(System.currentTimeMillis() - start + " ms" );
	}
	
	//@Test
	public void testSimpleWriter2() throws ClassNotFoundException, IOException, IntrospectionException, CsvBangException, IllegalAccessException, InstantiationException{
		FactoryCsvWriter factory = new FactoryCsvWriter("fr.csvbang.test.bean.*");
		final CsvWriter<BeanChildCsv> writer = factory.createCsvWriter(BeanChildCsv.class, "/tmp/tony2.csv");
		
		List<BeanChildCsv> list = new ArrayList<BeanChildCsv>(10000);
		
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("sasa");
			b.setValue("fafafafafafafafafafafafafafafafafaffafafafafa");
			b.setDudu("zaza");
			b.setDate("rara");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date(), new Date()}));
			list.add(b);
		}
		
		long start = System.currentTimeMillis();
		
		for (int j=0; j<45; j++){
			int i=0;
			while (i<list.size()){
				final List<BeanChildCsv> l = list.subList(i, Math.min(i + 1000, list.size()));
				writer.write(l);
				i +=1000;
			}
		}
		
		
		
		writer.close();
		System.out.println(System.currentTimeMillis() - start + " ms" );
	}
	
	//@Test
	public void testSimpleWriter3() throws ClassNotFoundException, IOException, IntrospectionException, CsvBangException, IllegalAccessException, InstantiationException{
		FactoryCsvWriter factory = new FactoryCsvWriter("fr.csvbang.test.bean.*");
		final CsvWriter<BeanChildCsv> writer = factory.createCsvWriter(BeanChildCsv.class, "/tmp/tony3.csv");
		
		
		List<BeanChildCsv> list1 = new ArrayList<BeanChildCsv>(100000);
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("sasa");
			b.setValue("gtgtfafafafafafafafafafafafafafafaffafafafafa");
			b.setDudu("zaza");
			b.setDate("rara");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date(), new Date()}));
			list1.add(b);
		}
		Runnable r1 = new WriterThread<BeanChildCsv>(writer, list1);
		
		List<BeanChildCsv> list2 = new ArrayList<BeanChildCsv>(100000);
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("sasa");
			b.setValue("sesefafafafafafafafafafafafafafafaffafafafafa");
			b.setDudu("zaza");
			b.setDate("rara");
			b.setMadate(new Date());
			b.setYes("Non");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date(), new Date()}));
			list2.add(b);
		}
		Runnable r2 = new WriterThread<BeanChildCsv>(writer, list2);
		
		List<BeanChildCsv> list3 = new ArrayList<BeanChildCsv>(100000);
		for (int i=0; i<100000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("sasa");
			b.setValue("fafafafafafafafafafafafafafafafafaffafafafafa");
			b.setDudu("zaza");
			b.setDate("rara");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date(), new Date()}));
			list3.add(b);
		}
		Runnable r3 = new WriterThread<BeanChildCsv>(writer, list3);
		
		
		
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		
		long start = System.currentTimeMillis();
		t1.start();
		t2.start();
		t3.start();
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){};
		writer.close();
		System.out.println(System.currentTimeMillis() - start + " ms" );
	}
}
