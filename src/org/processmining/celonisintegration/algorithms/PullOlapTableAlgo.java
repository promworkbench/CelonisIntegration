package org.processmining.celonisintegration.algorithms;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.processmining.celonisintegration.parameters.PullOlapTableParameter;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.csv.CSVFile;


public class PullOlapTableAlgo {
	

	/**
	 * 
	 * 
	 * @param context
	 * @param log
	 * @param parameters
	 * @throws Exception
	 */
	public CSVFile apply(PluginContext context, PullOlapTableParameter parameters) throws Exception {
		/**
		 * Put your algorithm here, which computes an output form the inputs provided the parameters.
		 */
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[Pull OLAP Table from Celonis] Start");
		parameters.displayMessage("[Pull OLAP Table from Celonis] First input = " + parameters.toString());		
		
		String url = parameters.getUrl();
		String token = parameters.getToken();
	    String anaName = parameters.getAnalysisName();
	    String sheetName = parameters.getSheetName();
	    String tableName = parameters.getTableName();
	    
	    ProcessAnalytics ana = new ProcessAnalytics(url, token);
	    String fileLocation = ana.getCsv(anaName, sheetName, tableName);
	    Path filePath = Paths.get(fileLocation);
	    String fileName = filePath.getFileName().toString();	
		
		CSVFile csv1 = CSVUtils.importFromStream(filePath, fileName, 10000);		
		
		time += System.currentTimeMillis();
		parameters.displayMessage("Pull OLAP Table from Celonis] End (took " + time/1000.0 + "  seconds).");
		return csv1;
	}
}
