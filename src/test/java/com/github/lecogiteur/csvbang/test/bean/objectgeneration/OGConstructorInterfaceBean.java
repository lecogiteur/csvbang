package com.github.lecogiteur.csvbang.test.bean.objectgeneration;

public class OGConstructorInterfaceBean {
	
	private Integer i;

	/**
	 * Constructor
	 * @param i
	 * @since 1.0.0
	 */
	public OGConstructorInterfaceBean(OGConstructorInterface in) {
		super();
		this.i = in.getI();
	}
	
	public OGConstructorInterfaceBean(OGConstructorSimpleBean in) {
		super();
		this.i = in.getI();
	}

	/**
	 * Constructor
	 * @param i
	 * @since 1.0.0
	 */
	public OGConstructorInterfaceBean(Integer i) {
		super();
		this.i = i + 100;
	}

	/**
	 * Get the i
	 * @return the i
	 * @since 1.0.0
	 */
	public Integer getI() {
		return i;
	}

	/**
	 * Set the i
	 * @param i the i to set
	 * @since 1.0.0
	 */
	public void setI(Integer i) {
		this.i = i;
	}
	
	
	

}
