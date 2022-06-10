package org.processmining.celonisintegration.algorithms;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.processmining.celonisintegration.parameters.PullTableDataModelParameter;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.csv.CSVFile;


public class PullTableDataModelAlgo {
	

	/**
	 * 
	 * 
	 * @param context
	 * @param log
	 * @param parameters
	 * @throws Exception
	 */
	public CSVFile apply(PluginContext context, PullTableDataModelParameter parameters) throws Exception {
		/**
		 * Put your algorithm here, which computes an output form the inputs provided the parameters.
		 */
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] Start");
		parameters.displayMessage("[YourAlgorithm] First input = " + parameters.toString());		
		
		String url = parameters.getUrl();
		String token = parameters.getToken();
	    String dataPool = parameters.getDataPool();
	    String dataModel = parameters.getDataModel();
	    System.out.println(dataModel);
	    String tableName = parameters.getTableName();
	    Boolean isMerged = parameters.getIsMerged();
	    
	    DataIntegration di = new DataIntegration(url, token);
	    String dpId = di.getDataPoolId(dataPool);
	    String dmId = di.getDataModelId(dataPool, dataModel);
	    String fileLocation = di.getCsv(dpId, dmId, tableName);
	    
	    if (isMerged) {
	    	String caseTableName = di.getCaseTableName(dpId, dmId, tableName);
	    	String caseCsvLocation = di.getCsv(dpId, dmId, caseTableName);
	    	String[] colName = di.getForeignKey(dpId, dmId, tableName, caseTableName);
	    	String mergedCsv = di.mergeCsv(fileLocation, caseCsvLocation, colName[0], colName[1]);
	    	fileLocation = mergedCsv;
	    	
	    }
	    
	   
	    
	    
	    Path filePath = Paths.get(fileLocation);
	    String fileName = filePath.getFileName().toString();	
		
		CSVFile csv1 = XESUtils.importFromStream(filePath, fileName, 10000);		
		
		time += System.currentTimeMillis();
		parameters.displayMessage("Pull Table from Data Model End (took " + time/1000.0 + "  seconds).");
		return csv1;
	}
}
