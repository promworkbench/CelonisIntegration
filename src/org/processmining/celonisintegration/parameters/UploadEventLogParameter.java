package org.processmining.celonisintegration.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class UploadEventLogParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String tableName;
	
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
