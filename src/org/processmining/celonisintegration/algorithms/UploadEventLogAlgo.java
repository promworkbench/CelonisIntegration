package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.AnalysisStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataModelStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataPoolStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.TableStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.WorkspaceStatus;
import org.processmining.framework.plugin.PluginContext;

public class UploadEventLogAlgo {

	/**
	 * 
	 * 
	 * @param context
	 * @param log
	 * @param parameters
	 * @throws Exception
	 */
	public void apply(PluginContext context, XLog log, UploadEventLogParameter parameters) throws Exception {
		/**
		 * Put your algorithm here, which computes an output form the inputs
		 * provided the parameters.
		 */
		long time = -System.currentTimeMillis();
		String url = parameters.getUrl();
		String token = parameters.getToken();
		DataIntegration celonis = new DataIntegration(url, token);

		String casePrefix = "case-";
		String actCol = parameters.getActCol();
		String caseCol = parameters.getCaseCol();
		String timeCol = parameters.getTimeCol();
		String actColNew = parameters.getActColNew();
		String caseColNew = parameters.getCaseColNew();
		String timeColNew = parameters.getTimeColNew();

		String dp = parameters.getDataPool();
		String dm = parameters.getDataModel();
		String tableName = parameters.getTableName();
		String ws = parameters.getWorkspace();
		String ana = parameters.getAnalysis();

		if (parameters.getWorkspaceStatus() == WorkspaceStatus.REPLACE 
				|| parameters.getWorkspaceStatus() == WorkspaceStatus.ADD) {
			ws = parameters.getWorkspaceReplace();
		}
		
		if (parameters.getAnalysisStatus() == AnalysisStatus.REPLACE 
				||parameters.getAnalysisStatus() == AnalysisStatus.ADD) {
			ana = parameters.getAnalysisReplace();
		}

		String dataPoolId = "";
		String dataModelId = "";
		String workspaceId = "";
		String anaId = "";
		context.log("Processing the request...");
		if (parameters.getDataPoolStatus() == DataPoolStatus.NEW) {
			context.log("Creating data pool " + dp);
			dataPoolId = celonis.createDataPool(dp);
			context.log("Creating data model " + dm);
			dataModelId = celonis.createDataModel(dm, dataPoolId);
			context.log("Creating workspace " + ws);
			workspaceId = celonis.createWorkspace(dataModelId, ws);
			context.log("Creating analysis " + ana);
			anaId = celonis.createAnalysis(workspaceId, ana);
		}
		
		else if (parameters.getDataPoolStatus() == DataPoolStatus.REPLACE) {
			dp = parameters.getDataPoolReplace();
			String dpId = celonis.getDataPoolId(dp);
			context.log("Deleting old data pool " + dp);
			celonis.deleteDataPool(dpId);
			context.log("Creating data pool " + dp);
			dataPoolId = celonis.createDataPool(dp);
			context.log("Creating data model " + dm);
			dataModelId = celonis.createDataModel(dm, dataPoolId);
			context.log("Creating workspace " + ws);
			workspaceId = celonis.createWorkspace(dataModelId, ws);
			context.log("Creating analysis " + ana);
			anaId = celonis.createAnalysis(workspaceId, ana);
		}
		
		else {
			dp = parameters.getDataPoolReplace();
			dataPoolId = celonis.getDataPoolId(dp);
			if (parameters.getDataModelStatus() == DataModelStatus.NEW) {	
				context.log("Creating data model " + dm);
				dataModelId = celonis.createDataModel(dm, dataPoolId);
				context.log("Creating workspace " + ws);
				workspaceId = celonis.createWorkspace(dataModelId, ws);
				context.log("Creating analysis " + ana);
				anaId = celonis.createAnalysis(workspaceId, ana);
			}
			else if (parameters.getDataModelStatus() == DataModelStatus.REPLACE) {
				dm = parameters.getDataModelReplace();
				String dmId = celonis.getDataModelId(dp, dm);
				context.log("Deleting old data model " + dm);
				celonis.deleteDataModel(dataPoolId, dmId);
				context.log("Creating data model " + dm);
				dataModelId = celonis.createDataModel(dm, dataPoolId);
				context.log("Creating workspace " + ws);
				workspaceId = celonis.createWorkspace(dataModelId, ws);
				context.log("Creating analysis " + ana);
				anaId = celonis.createAnalysis(workspaceId, ana);
				
			}
			else {
				dm = parameters.getDataModelReplace();
				dataModelId = celonis.getDataModelId(dp, dm);
				if (parameters.getTableStatus() == TableStatus.REPLACE) {
					tableName = parameters.getTableNameReplace();
					String tableId = celonis.getDataModeTablelId(dp, dm, tableName);
					context.log("Deleting old table " + tableName);
					celonis.deleteDataModelTable(dataPoolId, dataModelId, tableId);
				}
				if (parameters.getWorkspaceStatus() == WorkspaceStatus.NEW) {
					context.log("Creating workspace " + ws);
					workspaceId = celonis.createWorkspace(dataModelId, ws);
					context.log("Creating analysis " + ana);
					anaId = celonis.createAnalysis(workspaceId, ana);
				}
				else if (parameters.getWorkspaceStatus() == WorkspaceStatus.REPLACE) {
					ws = parameters.getWorkspaceReplace();
					String wsId = celonis.getWorkspaceId(ws);
					context.log("Deleting old workspace " + ws);
					celonis.deleteWorkspace(wsId);
					context.log("Creating workspace " + ws);
					workspaceId = celonis.createWorkspace(dataModelId, ws);
					context.log("Creating analysis " + ana);
					anaId = celonis.createAnalysis(workspaceId, ana);
				}
				else {
					ws = parameters.getWorkspaceReplace();
					workspaceId = celonis.getWorkspaceId(ws);
					if (parameters.getAnalysisStatus() == AnalysisStatus.NEW) {
						context.log("Creating analysis " + ana);
						anaId = celonis.createAnalysis(workspaceId, ana);
					}
					else {
						ana = parameters.getAnalysisReplace();
						anaId = celonis.getAnalysisId(ws, ana);
					}
				}
			}
		}
		
		HashMap<String, String> mapping = new HashMap<String, String>();
		if (dp.length() == 0) {
			dp = tableName + "_DATAPOOL";
		}
		if (dm.length() == 0) {
			dm = tableName + "_DATAMODEL";
		}
		if (ws.length() == 0) {
			ws = tableName + "_WORKSPACE";
		}
		if (ana.length() == 0) {
			ana = tableName + "_ANALYSIS";
		}
		if (!(actColNew.length() == 0)) {
			mapping.put(actCol, actColNew);
			actCol = actColNew;
		}
		String caseColOld = caseCol;
		if (!(caseColNew.length() == 0)) {
			mapping.put(caseCol, caseColNew);
			caseCol = caseColNew;
		}
		String timeColOld = timeCol;
		if (!(timeColNew.length() == 0)) {
			mapping.put(timeCol, timeColNew);
			timeCol = timeColNew;
		}
		
		File actCSV = parameters.getActCSV();
		context.log("Creating case table...");
		File caseCSV = XESUtils.createCaseCSV(log, casePrefix);
		context.getProgress().inc();

		if (!mapping.isEmpty()) {
			actCSV = XESUtils.changeColumnName(actCSV, mapping);
			caseCSV = XESUtils.changeColumnName(caseCSV, mapping);
		}
		context.log("Process the request done");
		
		context.log("Pushing activity table of " + tableName + " to Celonis...");
		celonis.uploadCSV(context, dataPoolId, actCSV.getPath(), tableName + "_ACTIVITIES", timeCol, 100000);
		context.log("Pushing activity table of " + tableName + " to Celonis done");
		context.getProgress().inc();
		context.log("Pushing case table of " + tableName + " to Celonis...");
		celonis.uploadCSV(context, dataPoolId, caseCSV.getPath(), tableName + "_CASE", timeCol, 100000);
		context.log("Pushing case table of " + tableName + " to Celonis done");
		context.getProgress().inc();
		context.log("Uploading activity table of " + tableName + " to data pool " + dp + "...");
		celonis.addTableFromPool(tableName + "_ACTIVITIES", dataPoolId, dataModelId);
		context.log("Uploading activity table of " + tableName + " to data pool " + dp + " done");
		context.getProgress().inc();
		context.log("Uploading case table of " + tableName + " to data pool " + dp + "...");
		celonis.addTableFromPool(tableName + "_CASE", dataPoolId, dataModelId);
		context.log("Uploading case table of " + tableName + " to data pool " + dp + " done");
		context.getProgress().inc();
		context.log("Configuring foreign keys...");
		celonis.addForeignKeys(tableName + "_ACTIVITIES", caseCol, tableName + "_CASE", caseCol, dataModelId,
				dataPoolId);
		celonis.addProcessConfiguration(dataModelId, dataPoolId, tableName + "_ACTIVITIES", tableName + "_CASE",
				caseCol, actCol, timeCol);
		context.log("Configuring foreign keys done");
		context.getProgress().inc();
		context.log("Reloading data model...");
		celonis.reloadDataModel(dataModelId, dataPoolId);
		context.log("Reloading data model done");
		context.getProgress().inc();
		actCSV.delete();
		caseCSV.delete();

		time += System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] End (took " + time / 1000.0 + "  seconds).");
	}
	
}
