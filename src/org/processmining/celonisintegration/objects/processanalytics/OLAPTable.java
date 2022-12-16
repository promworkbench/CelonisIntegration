package org.processmining.celonisintegration.objects.processanalytics;


public class OLAPTable {
	private String name;
	private String translatedName;
	private String id;
	private Sheet sheet;

	public OLAPTable(String name, String translatedName, String id, Sheet sheet) {
		this.name = name;
		this.id = id;
		this.sheet = sheet;
		this.translatedName = translatedName;
	}

	public String getTranslatedName() {
		return translatedName;
	}

	public void setTranslatedName(String translatedName) {
		this.translatedName = translatedName;
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

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
}