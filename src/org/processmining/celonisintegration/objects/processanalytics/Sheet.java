package org.processmining.celonisintegration.objects.processanalytics;


public class Sheet {
	private String name;
	private String id;
	private Analysis analysis;
	private String translatedName;

	public Sheet(String name, String translatedName, String id, Analysis analysis) {
		this.name = name;
		this.id = id;
		this.analysis = analysis;
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

	public Analysis getAnalysis() {
		return analysis;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}
}