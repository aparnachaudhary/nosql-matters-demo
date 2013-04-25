package net.arunoday.demo.model;

import java.io.Serializable;

/**
 * Filter criteria for search operations.
 * 
 * @author Aparna
 * 
 */
public class FilterCriteria implements Serializable {

	private static final long serialVersionUID = -840537782753960299L;

	private String name;
	private String value;

	public FilterCriteria(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "FilterCriteria [name=" + name + ", value=" + value + "]";
	}

}
