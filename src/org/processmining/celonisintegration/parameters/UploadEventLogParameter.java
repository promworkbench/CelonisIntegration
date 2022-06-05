package org.processmining.celonisintegration.parameters;

import java.io.File;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class UploadEventLogParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String tableName;
	private String casePrefix;
	private String dataPool;
	private String dataModel;
	private String workspace;
	private String analysis;
	private String actCol;
	private String caseCol;
	private String timeCol;
	private String actColNew;
	private String caseColNew;
	private String timeColNew;
	private File actCSV;
	public UploadEventLogParameter(UploadEventLogParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setTableName(parameters.getTableName());
		setDataPool(parameters.getDataPool());
		setDataModel(parameters.getDataModel());
		setWorkspace(parameters.getWorkspace());
		setAnalysis(parameters.getAnalysis());
		setActColNew(parameters.getActColNew());
		setCaseColNew(parameters.getCaseColNew());
		setTimeColNew(parameters.getTimeColNew());
	}
	
	public boolean equals(Object object) {
		if (object instanceof UploadEventLogParameter) {
			UploadEventLogParameter parameters = (UploadEventLogParameter) object;
			return super.equals(parameters) &&
					getUrl() == parameters.getUrl() &&
					getToken() == parameters.getToken() &&
					getTableName().equals(parameters.getTableName()) &&
					getDataPool().equals(parameters.getDataPool()) &&
					getDataModel().equals(parameters.getDataModel()) &&
					getWorkspace().equals(parameters.getWorkspace()) &&
					getTableName().equals(parameters.getTableName()) &&
					getActColNew().equals(parameters.getActColNew()) &&
					getCaseColNew().equals(parameters.getCaseColNew()) &&
					getTimeColNew().equals(parameters.getTimeColNew()) &&
					getAnalysis().equals(parameters.getAnalysis());
		}
		return false;
	}
	
	public String getActColNew() {
		return actColNew;
	}

	public void setActColNew(String actColNew) {
		this.actColNew = actColNew;
	}

	public String getCaseColNew() {
		return caseColNew;
	}

	public void setCaseColNew(String caseColNew) {
		this.caseColNew = caseColNew;
	}

	public String getTimeColNew() {
		return timeColNew;
	}

	public void setTimeColNew(String timeColNew) {
		this.timeColNew = timeColNew;
	}

	public String getDataPool() {
		return dataPool;
	}

	public void setDataPool(String dataPool) {
		this.dataPool = dataPool;
	}

	public String getDataModel() {
		return dataModel;
	}

	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}	
	
	public String getActCol() {
		return actCol;
	}

	public void setActCol(String actCol) {
		this.actCol = actCol;
	}

	public String getCaseCol() {
		return caseCol;
	}

	public void setCaseCol(String caseCol) {
		this.caseCol = caseCol;
	}

	public String getTimeCol() {
		return timeCol;
	}

	public void setTimeCol(String timeCol) {
		this.timeCol = timeCol;
	}

	public String getCasePrefix() {
		return casePrefix;
	}

	public void setCasePrefix(String casePrefix) {
		this.casePrefix = casePrefix;
	}

	public UploadEventLogParameter() {
		super();
	}

	public File getActCSV() {
		return actCSV;
	}

	public void setActCSV(File actCSV) {
		this.actCSV = actCSV;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String toString() {
		return "(" + getUrl() + ", " + getToken() + ", " + getTableName() + ")";
	}
}
