package org.processmining.celonisintegration.objects.studio;

public class Analysis {
	private String key;
	private String name;
	private String id;
	private String spaceId;
	private String packageId;
	public Analysis(String key, String name, String id, String spaceId, String packageId) {
		this.key = key;
		this.name = name;
		this.id = id;
		this.spaceId = spaceId;
		this.packageId = packageId;
	}
	
	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
	public String getId() {
		return id;
	}
	public String getSpaceId() {
		return spaceId;
	}
	public String getPackageId() {
		return packageId;
	}
	public String toString() {
		return this.name;
	}
	
}
