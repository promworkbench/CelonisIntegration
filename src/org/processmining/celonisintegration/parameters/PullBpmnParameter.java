package org.processmining.celonisintegration.parameters;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class PullBpmnParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String cateName;
	private String pmName;
	
	public PullBpmnParameter() {
		super();
	}

	public PullBpmnParameter(PullBpmnParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setCateName(parameters.getCateName());
		setPmName(parameters.getPmName());
	}
	
	public boolean equals(Object object) {
		if (object instanceof PullBpmnParameter) {
			PullBpmnParameter parameters = (PullBpmnParameter) object;
			return super.equals(parameters) &&
					getUrl() == parameters.getUrl() &&
					getToken() == parameters.getToken() &&
					getCateName() == parameters.getCateName() &&
					getPmName().equals(parameters.getPmName());
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

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public String getPmName() {
		return pmName;
	}

	public void setPmName(String pmName) {
		this.pmName = pmName;
	}

	public String toString() {
		return "(" + getUrl() + ", " + getToken() + ", " + getCateName() + ", " + getPmName() + ")";
	}
}
