package com.intersys.gds.test;

public class Value {
	private String name;
	private long actualValue;
	public Value(int i) {
		setName("value" + i);
		setActualValue(i + System.currentTimeMillis());
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param actualValue the actualValue to set
	 */
	public void setActualValue(long actualValue) {
		this.actualValue = actualValue;
	}
	/**
	 * @return the actualValue
	 */
	public long getActualValue() {
		return actualValue;
	}

}
