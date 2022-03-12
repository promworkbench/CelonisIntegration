package org.processmining.celonisintegration.algorithms;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.framework.plugin.PluginContext;

public class UploadEventLogAlgo {
	

	/**
	 * The method that implements your algorithm.
	 * 
	 * Note that this method only uses the boolean which is stored in the parameters.
	 * Nevertheless, it could have used the integer and/or the String as well.
	 * 
	 * @param context The context where to run in.
	 * @param input1 The first input.
	 * @param input2 The second input.
	 * @param parameters The parameters to use.
	 * @return The output.
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
		
		File actCSV = File.createTempFile("act", ".csv");
    	File caseCSV = File.createTempFile("case", ".csv");
    	CSVUtils.createActCSV(log, actCSV);
    	CSVUtils.createCaseCSV(log, caseCSV);
    	
    	
    	DataIntegration celonis = new DataIntegration(url, token);    	
        String dataPoolId = celonis.createDataPool(tableName + "_DATAPOOL");
    	celonis.uploadCSV(dataPoolId, actCSV.getPath(), tableName + "_ACTIVITIES", EventLogUtils.TIMESTAMPKEY, 100000);    
    	celonis.uploadCSV(dataPoolId, caseCSV.getPath(), tableName + "_CASE", EventLogUtils.TIMESTAMPKEY, 100000);
    	String dataModelId = celonis.createDataModel(tableName + "_DATAMODEL", dataPoolId);
    	celonis.addTableFromPool(tableName + "_ACTIVITIES", dataPoolId, dataModelId);
    	celonis.addTableFromPool(tableName + "_CASE", dataPoolId, dataModelId);
    	celonis.addForeignKeys(tableName + "_ACTIVITIES", EventLogUtils.CASEIDKEY, tableName + "_CASE", 
    							EventLogUtils.CASEIDKEY, dataModelId, dataPoolId);
    	celonis.addProcessConfiguration(dataModelId, dataPoolId, tableName+"_ACTIVITIES", tableName+"_CASE", 
    								EventLogUtils.CASEIDKEY, EventLogUtils.ACTKEY, EventLogUtils.TIMESTAMPKEY);
    	celonis.reloadDataModel(dataModelId, dataPoolId);
    	String workspaceId = celonis.createWorkspace(dataModelId, tableName+"_WORKSPACE");
    	String anaId = celonis.createAnalysis(workspaceId, tableName+"_ANALYSIS");
    	
    	actCSV.delete();
    	caseCSV.delete();
		
		time += System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] End (took " + time/1000.0 + "  seconds).");
	}
}
