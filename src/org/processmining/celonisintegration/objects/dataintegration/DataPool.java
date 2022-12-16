package org.processmining.celonisintegration.objects.dataintegration;

public class DataPool {
	private String name;
	private String id;

	public DataPool(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public DataPool() {
		this.name = "";
		this.id = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}