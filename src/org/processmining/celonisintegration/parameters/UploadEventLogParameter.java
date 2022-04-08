package org.processmining.celonisintegration.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class UploadEventLogParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String tableName;
	private String casePrefix;
	private String actCol;
	private String caseCol;
	private String timeCol;
	
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

	public UploadEventLogParameter(UploadEventLogParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setTableName(parameters.getTableName());
	}
	
	public boolean equals(Object object) {
		if (object instanceof UploadEventLogParameter) {
			UploadEventLogParameter parameters = (UploadEventLogParameter) object;
			return super.equals(parameters) &&
					getUrl() == parameters.getUrl() &&
					getToken() == parameters.getToken() &&
					getTableName().equals(parameters.getTableName());
		}
		return false;
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
