package com.github.lecogiteur.csvbang.test.bean.objectgeneration;

public class OGConstructorNoConstructBean {
	
	private Integer i;
	
	private String s;

	/**
	 * Constructor
	 * @since 1.0.0
	 */
	public OGConstructorNoConstructBean() {
		super();
	}

	/**
	 * Constructor
	 * @param i
	 * @param s
	 * @since 1.0.0
	 */
	public OGConstructorNoConstructBean(Integer i, String s) {
		super();
		this.i = i;
		this.s = s;
	}
	
	private OGConstructorNoConstructBean(Integer i) {
		super();
		this.i = i;
	}
	

}
