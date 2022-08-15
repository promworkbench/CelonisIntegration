package org.processmining.celonisintegration.parameters;

import java.io.File;

import org.processmining.basicutils.parameters.impl.PluginParametersImpl;

public class UploadEventLogParameter extends PluginParametersImpl {

	private String url;
	private String token;
	private String casePrefix;
	private String tableName;
	private String dataPool;
	private String dataModel;
	private String workspace;
	private String analysis;
	private String tableNameReplace;
	private String dataPoolReplace;
	private String dataModelReplace;
	private String workspaceReplace;
	private String analysisReplace;
	private String tableNameAdd;
	private String dataPoolAdd;
	private String dataModelAdd;
	private String workspaceAdd;
	private String analysisAdd;
	private TableStatus tableStatus;
	private DataPoolStatus dataPoolStatus;
	private DataModelStatus dataModelStatus;
	private WorkspaceStatus workspaceStatus;
	private AnalysisStatus analysisStatus;
	private String actCol;
	private String caseCol;
	private String timeCol;
	private String actColNew;
	private String caseColNew;
	private String timeColNew;
	private File actCSV;
	public enum TableStatus {
		NEW, REPLACE
	}
	public enum DataPoolStatus {
		NEW, REPLACE, ADD
	}
	public enum DataModelStatus {
		NEW, REPLACE, ADD
	}
	public enum WorkspaceStatus {
		NEW, REPLACE, ADD, FORBIDDEN
	}
	public enum AnalysisStatus {
		NEW, REPLACE, ADD
	}
	

	public UploadEventLogParameter(UploadEventLogParameter parameters) {
		super(parameters);
		setUrl(parameters.getUrl());
		setToken(parameters.getToken());
		setTableName(parameters.getTableName());
		setDataPool(parameters.getDataPool());
		setDataModel(parameters.getDataModel());
		setWorkspace(parameters.getWorkspace());
		setAnalysis(parameters.getAnalysis());
		setTableNameReplace(parameters.getTableNameReplace());
		setDataPoolReplace(parameters.getDataPoolReplace());
		setDataModelReplace(parameters.getDataModelReplace());
		setWorkspaceReplace(parameters.getWorkspaceReplace());
		setAnalysisReplace(parameters.getAnalysisReplace());
		setActColNew(parameters.getActColNew());
		setCaseColNew(parameters.getCaseColNew());
		setTimeColNew(parameters.getTimeColNew());
	}

	public boolean equals(Object object) {
		if (object instanceof UploadEventLogParameter) {
			UploadEventLogParameter parameters = (UploadEventLogParameter) object;
			return super.equals(parameters) && getUrl() == parameters.getUrl() && getToken() == parameters.getToken()
					&& getTableName().equals(parameters.getTableName())
					&& getDataPool().equals(parameters.getDataPool())
					&& getDataModel().equals(parameters.getDataModel())
					&& getWorkspace().equals(parameters.getWorkspace())
					&& getTableNameReplace().equals(parameters.getTableNameReplace())
					&& getDataPoolReplace().equals(parameters.getDataPoolReplace())
					&& getDataModelReplace().equals(parameters.getDataModelReplace())
					&& getWorkspaceReplace().equals(parameters.getWorkspaceReplace())
					&& getAnalysisReplace().equals(parameters.getAnalysisReplace())
					&& getTableName().equals(parameters.getTableName())
					&& getActColNew().equals(parameters.getActColNew())
					&& getCaseColNew().equals(parameters.getCaseColNew())
					&& getTimeColNew().equals(parameters.getTimeColNew())
					&& getAnalysis().equals(parameters.getAnalysis());
		}
		return false;
	}
	
	
	public String getTableNameAdd() {
		return tableNameAdd;
	}

	public void setTableNameAdd(String tableNameAdd) {
		this.tableNameAdd = tableNameAdd;
	}

	public String getDataPoolAdd() {
		return dataPoolAdd;
	}

	public void setDataPoolAdd(String dataPoolAdd) {
		this.dataPoolAdd = dataPoolAdd;
	}

	public String getDataModelAdd() {
		return dataModelAdd;
	}

	public void setDataModelAdd(String dataModelAdd) {
		this.dataModelAdd = dataModelAdd;
	}

	public String getWorkspaceAdd() {
		return workspaceAdd;
	}

	public void setWorkspaceAdd(String workspaceAdd) {
		this.workspaceAdd = workspaceAdd;
	}

	public String getAnalysisAdd() {
		return analysisAdd;
	}

	public void setAnalysisAdd(String analysisAdd) {
		this.analysisAdd = analysisAdd;
	}

	public TableStatus getTableStatus() {
		return tableStatus;
	}

	public void setTableStatus(TableStatus tableStatus) {
		this.tableStatus = tableStatus;
	}

	public DataPoolStatus getDataPoolStatus() {
		return dataPoolStatus;
	}

	public void setDataPoolStatus(DataPoolStatus dataPoolStatus) {
		this.dataPoolStatus = dataPoolStatus;
	}

	public DataModelStatus getDataModelStatus() {
		return dataModelStatus;
	}

	public void setDataModelStatus(DataModelStatus dataModelStatus) {
		this.dataModelStatus = dataModelStatus;
	}

	public WorkspaceStatus getWorkspaceStatus() {
		return workspaceStatus;
	}

	public void setWorkspaceStatus(WorkspaceStatus workspaceStatus) {
		this.workspaceStatus = workspaceStatus;
	}

	public AnalysisStatus getAnalysisStatus() {
		return analysisStatus;
	}

	public void setAnalysisStatus(AnalysisStatus analysisStatus) {
		this.analysisStatus = analysisStatus;
	}

	public String getTableNameReplace() {
		return tableNameReplace;
	}

	public void setTableNameReplace(String tableNameReplace) {
		this.tableNameReplace = tableNameReplace;
	}

	public String getDataPoolReplace() {
		return dataPoolReplace;
	}

	public void setDataPoolReplace(String dataPoolReplace) {
		this.dataPoolReplace = dataPoolReplace;
	}

	public String getDataModelReplace() {
		return dataModelReplace;
	}

	public void setDataModelReplace(String dataModelReplace) {
		this.dataModelReplace = dataModelReplace;
	}

	public String getWorkspaceReplace() {
		return workspaceReplace;
	}

	public void setWorkspaceReplace(String workspaceReplace) {
		this.workspaceReplace = workspaceReplace;
	}

	public String getAnalysisReplace() {
		return analysisReplace;
	}

	public void setAnalysisReplace(String analysisReplace) {
		this.analysisReplace = analysisReplace;
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
