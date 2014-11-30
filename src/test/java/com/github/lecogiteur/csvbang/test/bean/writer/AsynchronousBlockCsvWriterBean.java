package com.github.lecogiteur.csvbang.test.bean.writer;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;
import com.github.lecogiteur.csvbang.annotation.CsvType;

@CsvType
@CsvHeader(header=true)
@CsvFile(asynchronousWriter=true, maxFileNumber=3, maxRecordByFile=18000, blocksize=1000, fileName="async-%n.csv")
public class AsynchronousBlockCsvWriterBean {
	
	/**
	 * @param name
	 * @param value
	 * @since 0.1.0
	 */
	public AsynchronousBlockCsvWriterBean(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@CsvField(position=1, name="Name")
	private String name;
	
	@CsvField(position=2, name="Value")
	private String value;

	/**
	 * Get the name
	 * @return the name
	 * @since 0.1.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name
	 * @param name the name to set
	 * @since 0.1.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the value
	 * @return the value
	 * @since 0.1.0
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value
	 * @param value the value to set
	 * @since 0.1.0
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
