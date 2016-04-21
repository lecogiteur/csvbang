package com.github.lecogiteur.csvbang.test.bean.writer;

import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvFile;

@CsvFile(registerThread=false)
public class CommentWriterBeanWithoutRegisterThread extends CommentWriterBean {

	/**
	 * Constructor
	 * @since 1.0.0
	 */
	public CommentWriterBeanWithoutRegisterThread() {
		super();
	}

	/**
	 * Constructor
	 * @param id
	 * @param name
	 * @param birthday
	 * @param price
	 * @since 1.0.0
	 */
	public CommentWriterBeanWithoutRegisterThread(Integer id, String name,
			Calendar birthday, Double price) {
		super(id, name, birthday, price);
	}

	
}
