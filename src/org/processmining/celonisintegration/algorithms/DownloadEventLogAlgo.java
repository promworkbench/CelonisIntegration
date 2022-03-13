package org.processmining.celonisintegration.algorithms;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.processmining.celonisintegration.parameters.DownloadEventLogParameter;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.csv.CSVFile;


public class DownloadEventLogAlgo {
	

	/**
	 * 
	 * 
	 * @param context
	 * @param log
	 * @param parameters
	 * @throws Exception
	 */
	public CSVFile apply(PluginContext context, DownloadEventLogParameter parameters) throws Exception {
		/**
		 * Put your algorithm here, which computes an output form the inputs provided the parameters.
		 */
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] Start");
		parameters.displayMessage("[YourAlgorithm] First input = " + parameters.toString());		
		
		String url = parameters.getUrl();
		String token = parameters.getToken();
	    String anaName = parameters.getAnalysisName();
	    
	    PqlAnalysis ana = new PqlAnalysis(url, token);
	    String fileLocation = ana.downloadCsv(anaName);
	    Path filePath = Paths.get(fileLocation);
	    String fileName = filePath.getFileName().toString();	
		
		CSVFile csv1 = CSVUtils.importFromStream(filePath, fileName, 10000);		
		
		time += System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] End (took " + time/1000.0 + "  seconds).");
		return csv1;
	}
}
