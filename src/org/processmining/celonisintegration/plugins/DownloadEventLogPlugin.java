package org.processmining.celonisintegration.plugins;

import org.processmining.celonisintegration.algorithms.DownloadEventLogAlgo;
import org.processmining.celonisintegration.dialogs.DownloadEventLogDialog;
import org.processmining.celonisintegration.parameters.DownloadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.log.csv.CSVFile;


public class DownloadEventLogPlugin extends DownloadEventLogAlgo {

	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the parameters.
	 * 
	 * @param context The context to run in.
	 * @param log The event log.
	 * @return 
	 * @throws Exception 
	 */
	@Plugin(
            name = "Download the base Event Log from Celonis Analysis", 
            parameterLabels = {}, 
            returnLabels = { "CSV File" }, 
            returnTypes = { CSVFile.class }
    
    )
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public CSVFile runUI(UIPluginContext context) throws Exception {
		// Get the default parameters.
	    DownloadEventLogParameter parameters = new DownloadEventLogParameter();
	    // Get a dialog for this parameters.
	    DownloadEventLogDialog dialog1 = new DownloadEventLogDialog(context, parameters);
	    CSVFile csv = runConnections(context, parameters);	    
	    return csv;
	}	
	

	private CSVFile runConnections(PluginContext context, DownloadEventLogParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		CSVFile csv = apply(context, parameters);
		return csv;
		
	}

}
