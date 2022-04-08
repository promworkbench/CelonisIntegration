package org.processmining.celonisintegration.algorithms;

import java.io.File;

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
	    
		String url = parameters.getUrl();
		String token = parameters.getToken();
		String tableName = parameters.getTableName();
		String casePrefix = parameters.getCasePrefix();
		String actCol = parameters.getActCol();
		String caseCol = parameters.getCaseCol();
		String timeCol = parameters.getTimeCol();
		
		
		File actCSV = File.createTempFile("act", ".csv");
    	File caseCSV = File.createTempFile("case", ".csv");
    	CSVUtils.createActCSV(log, actCSV, casePrefix, timeCol);
    	CSVUtils.createCaseCSV(log, caseCSV, caseCol, casePrefix);
    	
    	DataIntegration celonis = new DataIntegration(url, token);    	
        String dataPoolId = celonis.createDataPool(tableName + "_DATAPOOL");
        
    	celonis.uploadCSV(dataPoolId, actCSV.getPath(), tableName + "_ACTIVITIES", timeCol, 100000);    
    	celonis.uploadCSV(dataPoolId, caseCSV.getPath(), tableName + "_CASE", timeCol, 100000);
    	String dataModelId = celonis.createDataModel(tableName + "_DATAMODEL", dataPoolId);
    	celonis.addTableFromPool(tableName + "_ACTIVITIES", dataPoolId, dataModelId);
    	celonis.addTableFromPool(tableName + "_CASE", dataPoolId, dataModelId);
    	celonis.addForeignKeys(tableName + "_ACTIVITIES", caseCol, tableName + "_CASE", 
    			caseCol, dataModelId, dataPoolId);
    	celonis.addProcessConfiguration(dataModelId, dataPoolId, tableName+"_ACTIVITIES", tableName+"_CASE", 
    			caseCol, actCol, timeCol);
    	celonis.reloadDataModel(dataModelId, dataPoolId);
    	String workspaceId = celonis.createWorkspace(dataModelId, tableName+"_WORKSPACE");
    	String anaId = celonis.createAnalysis(workspaceId, tableName+"_ANALYSIS");
    	
    	actCSV.delete();
    	caseCSV.delete();
		
		time += System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] End (took " + time/1000.0 + "  seconds).");
	}
}
