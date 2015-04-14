package com.github.lecogiteur.csvbang.test.bean.csvparser;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import com.github.lecogiteur.csvbang.annotation.CsvType;

@CsvType
public class MultipleCollectionCsvParserBean {

	@CsvField(position=1)
	public int field1;
	
	@CsvField(position=2)
	public Double[] field2;
	
	@CsvField(position=3)
	public Double field3;
	
	@CsvField(position=4)
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="dd-MM-yyyy")
	public Set<Calendar> field4;
}
