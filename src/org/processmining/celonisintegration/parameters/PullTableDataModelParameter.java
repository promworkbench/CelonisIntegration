package org.processmining.celonisintegration.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class PullTableDataModelParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String dataPool;
	private String dataModel;
	private String tableName;
	private Boolean isMerged;
	
	public PullTableDataModelParameter() {
		super();
	}

	public PullTableDataModelParameter(PullTableDataModelParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setDataPool(parameters.getDataPool());
		setDataModel(parameters.getDataModel());
		setTableName(parameters.getTableName());
		setIsMerged(parameters.getIsMerged());
	}
	
	public boolean equals(Object object) {
		if (object instanceof PullTableDataModelParameter) {
			PullTableDataModelParameter parameters = (PullTableDataModelParameter) object;
			return super.equals(parameters) &&
					getUrl() == parameters.getUrl() &&
					getToken() == parameters.getToken() &&
					getDataPool().equals(parameters.getDataPool()) &&
					getDataModel().equals(parameters.getDataModel()) &&
					getTableName().equals(parameters.getTableName());
		}
		return false;
	}	

	public Boolean getIsMerged() {
		return isMerged;
	}

	public void setIsMerged(Boolean isMerged) {
		this.isMerged = isMerged;
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String toString() {
		return "(" + getUrl() + ", " + getToken() + ", " + getDataPool() + ", " + getDataModel() + ", " + getTableName() +")";
	}
}
