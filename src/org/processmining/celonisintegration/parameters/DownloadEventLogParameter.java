package org.processmining.celonisintegration.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class DownloadEventLogParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String analysisName;
	
	public DownloadEventLogParameter() {
		super();
	}

	public DownloadEventLogParameter(DownloadEventLogParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setAnalysisName(parameters.getAnalysisName());
	}
	
	public boolean equals(Object object) {
		if (object instanceof DownloadEventLogParameter) {
			DownloadEventLogParameter parameters = (DownloadEventLogParameter) object;
			return super.equals(parameters) &&
					getUrl() == parameters.getUrl() &&
					getToken() == parameters.getToken() &&
					getAnalysisName().equals(parameters.getAnalysisName());
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

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public String toString() {
		return "(" + getUrl() + ", " + getToken() + ", " + getAnalysisName() + ")";
	}
}
