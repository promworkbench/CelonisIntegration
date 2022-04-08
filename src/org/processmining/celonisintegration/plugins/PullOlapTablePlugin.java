package org.processmining.celonisintegration.plugins;

import org.processmining.celonisintegration.algorithms.PullOlapTableAlgo;
import org.processmining.celonisintegration.dialogs.PullOlapTableAccessDialog;
import org.processmining.celonisintegration.dialogs.PullOlapTableAnaDialog;
import org.processmining.celonisintegration.dialogs.PullOlapTableSheetDialog;
import org.processmining.celonisintegration.dialogs.PullOlapTableWsDialog;
import org.processmining.celonisintegration.parameters.PullOlapTableParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.log.csv.CSVFile;


public class PullOlapTablePlugin extends PullOlapTableAlgo {

	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the parameters.
	 * 
	 * @param context The context to run in.
	 * @param log The event log.
	 * @return 
	 * @throws Exception 
	 */
	@Plugin(
            name = "Pull OLAP Table from Celonis Analysis", 
            parameterLabels = {}, 
            returnLabels = { "CSV File" }, 
            returnTypes = { CSVFile.class }
    
    )
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public CSVFile runUI(UIPluginContext context) throws Exception {
		// Get the default parameters.
	    PullOlapTableParameter parameters = new PullOlapTableParameter();
	    // Get a dialog for this parameters.
	    PullOlapTableAccessDialog dialog1 = new PullOlapTableAccessDialog(context, parameters);
	    PullOlapTableWsDialog dialog2 = new PullOlapTableWsDialog(context, parameters);
	    PullOlapTableAnaDialog dialog3 = new PullOlapTableAnaDialog(context, parameters);
	    PullOlapTableSheetDialog dialog4 = new PullOlapTableSheetDialog(context, parameters);
	    CSVFile csv = runConnections(context, parameters);	    
	    return csv;
	}	
	

	private CSVFile runConnections(PluginContext context, PullOlapTableParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		CSVFile csv = apply(context, parameters);
		return csv;
		
	}

}
