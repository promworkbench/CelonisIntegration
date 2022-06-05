package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
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
		 * Put your algorithm here, which computes an output form the inputs provided the parameters.
		 */
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] Start");
		parameters.displayMessage("[YourAlgorithm] First input = " + log.toString());		
		parameters.displayMessage("[YourAlgorithm] Parameters = " + parameters.toString());
		context.log("aa1");
		String url = parameters.getUrl();
		String token = parameters.getToken();
		String tableName = parameters.getTableName();
		String casePrefix = "case-";
		String actCol = parameters.getActCol();
		context.log("aa2");
		String caseCol = parameters.getCaseCol();
		context.log("aa3");
		String timeCol = parameters.getTimeCol();
		context.log("aa4");
		String actColNew = parameters.getActColNew();
		context.log("aa5");
		String caseColNew = parameters.getCaseColNew();
		context.log("aa6");
		String timeColNew = parameters.getTimeColNew();
		context.log("aa7");
		String dp = parameters.getDataPool();
		context.log(dp);
		String dm = parameters.getDataModel();
		context.log(dm);
		String ws = parameters.getWorkspace();
		context.log(ws);
		String ana = parameters.getAnalysis();
		context.log(ana);
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
		
		context.log(parameters.getActCSV().getAbsolutePath());
		File actCSV = parameters.getActCSV();
		context.log("Creating case table");
    	File caseCSV = XESUtils.createCaseCSV(log, casePrefix); 
    	context.getProgress().inc();
    	
    	
    	if (!mapping.isEmpty()) {
    		actCSV = XESUtils.changeColumnName(actCSV, mapping);
    		caseCSV = XESUtils.changeColumnName(caseCSV, mapping);
    	}
    	
    	DataIntegration celonis = new DataIntegration(url, token);    	
        String dataPoolId = celonis.createDataPool(dp);
        
        context.log("Uploading activity table");
    	celonis.uploadCSV(dataPoolId, actCSV.getPath(), tableName + "_ACTIVITIES", timeCol, 100000);    
    	context.getProgress().inc();
    	context.log("Uploading case table");
    	celonis.uploadCSV(dataPoolId, caseCSV.getPath(), tableName + "_CASE", timeCol, 100000);
    	context.getProgress().inc();
    	context.log("Creating data model");
    	String dataModelId = celonis.createDataModel(dm, dataPoolId);
    	context.getProgress().inc();
    	context.log("Adding activity table to data pool");
    	celonis.addTableFromPool(tableName + "_ACTIVITIES", dataPoolId, dataModelId);
    	context.getProgress().inc();
    	context.log("Adding case table to data pool");
    	celonis.addTableFromPool(tableName + "_CASE", dataPoolId, dataModelId);
    	context.getProgress().inc();
    	context.log("Configuring foreign keys");
    	celonis.addForeignKeys(tableName + "_ACTIVITIES", caseCol, tableName + "_CASE", 
    			caseCol, dataModelId, dataPoolId);
    	celonis.addProcessConfiguration(dataModelId, dataPoolId, tableName+"_ACTIVITIES", tableName+"_CASE", 
    			caseCol, actCol, timeCol);
    	context.getProgress().inc();
    	context.log("Reloading data model");
    	celonis.reloadDataModel(dataModelId, dataPoolId);
    	context.getProgress().inc();
    	context.log("Creating workspace");
    	String workspaceId = celonis.createWorkspace(dataModelId, ws);
    	context.log("Creating analysis");
    	String anaId = celonis.createAnalysis(workspaceId, ana);
    	
    	actCSV.delete();
    	caseCSV.delete();
		
		time += System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] End (took " + time/1000.0 + "  seconds).");
	}
}
