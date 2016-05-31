package com.github.lecogiteur.csvbang.test.bean.writer;

import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;


@CsvFile(asynchronousWriter=true, maxFileNumber=3, maxRecordByFile=17000, fileName="async-%n.csv")
public class AsynchronousCsvWriterBean extends AsynchronousBlockCsvWriterBean{

	@CsvField(name="Date", position=3)
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="dd/MM/yyyy")
	private Calendar date;

	public AsynchronousCsvWriterBean(){

	}

	public AsynchronousCsvWriterBean(String name, String value, Calendar date) {
		super(name, value);
		this.date = date;
	}

	public Calendar getDate() {
		return date;
	}
	
}
