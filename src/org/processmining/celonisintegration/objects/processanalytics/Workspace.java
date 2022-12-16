package org.processmining.celonisintegration.objects.processanalytics;

public class Workspace {
	private String name;
	private String id;
	private String dmId;

	public Workspace(String name, String id, String dmId) {
		this.name = name;
		this.id = id;
		this.dmId = dmId;
	}

	public Workspace() {
		// TODO Auto-generated constructor stub
		this.name = "";
		this.id = "";
		this.dmId = "";
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

	public String getDmId() {
		return dmId;
	}

	public void setDmId(String dmId) {
		this.dmId = dmId;
	}

}
