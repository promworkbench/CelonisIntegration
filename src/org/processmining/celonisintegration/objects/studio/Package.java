package org.processmining.celonisintegration.objects.studio;

public class Package {
	private String key;
	private String name;
	private String id;
	private String spaceId;
	public Package(String key, String name, String id, String spaceId) {
		this.key = key;
		this.name = name;
		this.id = id;
		this.spaceId = spaceId;
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
	
	public String toString() {
		return this.name;
	}
	
}
