package com.github.lecogiteur.csvbang.test.bean.objectgeneration;

public class OGConstructorInterfaceImpl implements OGConstructorInterface{
	
	private Integer i;

	/**
	 * Constructor
	 * @param i
	 * @since 1.0.0
	 */
	public OGConstructorInterfaceImpl(Integer i) {
		super();
		this.i = i;
	}



	@Override
	public Integer getI() {
		return i;
	}

}
