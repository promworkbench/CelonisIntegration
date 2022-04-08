package org.processmining.celonisintegration.plugins;


import org.processmining.celonisintegration.algorithms.PullTableDataModelAlgo;
import org.processmining.celonisintegration.dialogs.PullTableAccessDialog;
import org.processmining.celonisintegration.dialogs.PullTableDataModelDialog;
import org.processmining.celonisintegration.dialogs.PullTableDataPoolDialog;
import org.processmining.celonisintegration.parameters.PullTableDataModelParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.log.csv.CSVFile;


public class PullTableDataModelPlugin extends PullTableDataModelAlgo {

	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the parameters.
	 * 
	 * @param context The context to run in.
	 * @param log The event log.
	 * @return 
	 * @throws Exception 
	 */
	@Plugin(
            name = "Pull Table from Data Model", 
            parameterLabels = {}, 
            returnLabels = { "CSV File" }, 
            returnTypes = { CSVFile.class }
    
    )
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public CSVFile runUI(UIPluginContext context) throws Exception {
		// Get the default parameters.
		PullTableDataModelParameter parameters = new PullTableDataModelParameter();
	    // Get a dialog for this parameters.
	    PullTableAccessDialog dialog1 = new PullTableAccessDialog(context, parameters);
	    PullTableDataPoolDialog dialog2 = new PullTableDataPoolDialog(context, parameters);
	    PullTableDataModelDialog dialog3 = new PullTableDataModelDialog(context, parameters);
	    CSVFile csv = runConnections(context, parameters);	    
	    return csv;
	}	
	

	private CSVFile runConnections(PluginContext context, PullTableDataModelParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		CSVFile csv = apply(context, parameters);
		return csv;
		
	}

}
