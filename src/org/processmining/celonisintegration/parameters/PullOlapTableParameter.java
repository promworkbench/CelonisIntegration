package org.processmining.celonisintegration.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class PullOlapTableParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String workspaceName;
	private String analysisName;
	private String sheetName;
	private String tableName;
	
	

	public PullOlapTableParameter(PullOlapTableParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setAnalysisName(parameters.getAnalysisName());
		setWorkspaceName(parameters.getWorkspaceName());
		setTableName(parameters.getTableName());
		setSheetName(parameters.getSheetName());
	}
	
	public boolean equals(Object object) {
		if (object instanceof PullOlapTableParameter) {
			PullOlapTableParameter parameters = (PullOlapTableParameter) object;
			return super.equals(parameters) &&
					getUrl() == parameters.getUrl() &&
					getToken() == parameters.getToken() &&
					getAnalysisName().equals(parameters.getAnalysisName()) &&
					getWorkspaceName().equals(parameters.getWorkspaceName()) &&
					getSheetName().equals(parameters.getSheetName()) &&
					getTableName().equals(parameters.getTableName());
			
		}
		return false;
	}
	
	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String getWorkspaceName() {
		return workspaceName;
	}

	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public PullOlapTableParameter() {
		super();
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

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public String toString() {
		return "(" + getUrl() + ", " + getToken() + ", " + getWorkspaceName() + getAnalysisName() + getSheetName() + getTableName() + ")";
	}
}
