package org.processmining.celonisintegration.objects.processanalytics;


public class Analysis {
	private String name;
	private String id;
	private Workspace workspace;

	public Analysis(String name, String id, Workspace workspace) {
		this.name = name;
		this.id = id;
		this.workspace = workspace;
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

	public Workspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

}