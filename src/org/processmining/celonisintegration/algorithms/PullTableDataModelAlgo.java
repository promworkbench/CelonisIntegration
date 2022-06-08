package org.processmining.celonisintegration.algorithms;

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
	    String tableName = parameters.getTableName();
	    
	    DataIntegration di = new DataIntegration(url, token);
//	    String dpId = di.getDataPoolIdByName(dataPool);
//	    String dmId = di.getDataModelIdByName(dataModel, dpId);
//	    
//	    String fileLocation = di.getCsv(dpId, dmId, tableName);
//	    Path filePath = Paths.get(fileLocation);
//	    String fileName = filePath.getFileName().toString();	
//		
//		CSVFile csv1 = XESUtils.importFromStream(filePath, fileName, 10000);		
		
		time += System.currentTimeMillis();
		parameters.displayMessage("Pull Table from Data Model End (took " + time/1000.0 + "  seconds).");
		return null;
	}
}
