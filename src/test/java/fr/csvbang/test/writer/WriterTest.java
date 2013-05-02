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
import fr.csvbang.test.uti.WriterThread;
import fr.csvbang.writer.CsvWriter;


@RunWith(BlockJUnit4ClassRunner.class)
public class WriterTest {

	@Test
	public void testSimpleWriter() throws ClassNotFoundException, IOException, IntrospectionException, CsvBangException, IllegalAccessException, InstantiationException{
		FactoryCsvWriter factory = new FactoryCsvWriter("fr.csvbang.test.bean.*");
		final CsvWriter<BeanChildCsv> writer = factory.createCsvWriter(BeanChildCsv.class, "/tmp/tony.csv");
		
		
		List<BeanChildCsv> list1 = new ArrayList<BeanChildCsv>(10000);
		for (int i=0; i<10000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("toto");
			b.setValue("titi");
			b.setDudu("myDudu");
			b.setDate("myDate");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date()}));
			list1.add(b);
		}
		Runnable r1 = new WriterThread<BeanChildCsv>(writer, list1);
		
		List<BeanChildCsv> list2 = new ArrayList<BeanChildCsv>(10000);
		for (int i=0; i<10000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("jiji");
			b.setValue("juju");
			b.setDudu("mumu");
			b.setDate("gugu");
			b.setMadate(new Date());
			b.setYes("Non");
			list2.add(b);
		}
		Runnable r2 = new WriterThread<BeanChildCsv>(writer, list2);
		
		List<BeanChildCsv> list3 = new ArrayList<BeanChildCsv>(10000);
		for (int i=0; i<10000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("sasa");
			b.setValue("fafa");
			b.setDudu("zaza");
			b.setDate("rara");
			b.setMadate(new Date());
			b.setYes("Oui");
			b.setDateYear(Arrays.asList(new Date[]{new Date(), new Date(), new Date()}));
			list3.add(b);
		}
		Runnable r3 = new WriterThread<BeanChildCsv>(writer, list3);
		
		
		
		//List<BeanChildCsv> list = new ArrayList<BeanChildCsv>(10000);
		
		/*for (int i=0; i<10000; i++){
			BeanChildCsv b = new BeanChildCsv();
			b.setName("toto");
			b.setValue("titi");
			b.setDudu("myDudu");
			b.setDate("myDate");
			list.add(b);
		}*/
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		
		long start = System.currentTimeMillis();
		//writer.write(list);
		t1.start();
		t2.start();
		t3.start();
		while (t1.isAlive() || t2.isAlive() || t3.isAlive()){};
		/*writer.write(list1);
		writer.write(list2);
		writer.write(list3);*/
		writer.close();
		System.out.println(System.currentTimeMillis() - start + " ms" );
	}
}
