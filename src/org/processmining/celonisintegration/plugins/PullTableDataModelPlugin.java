package org.processmining.celonisintegration.plugins;


import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.celonisintegration.algorithms.ErrorUtils;
import org.processmining.celonisintegration.algorithms.PullTableDataModelAlgo;
import org.processmining.celonisintegration.dialogs.PullTableAccessDialog;
import org.processmining.celonisintegration.dialogs.PullTableDataPoolDialog;
import org.processmining.celonisintegration.help.YourHelp;
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
            name = "Pull Table from Celonis Data Model", 
            parameterLabels = {}, 
            returnLabels = { "CSV File" }, 
            returnTypes = { CSVFile.class },
            help = YourHelp.PULL_TABLE
    
    )
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public CSVFile runUI(UIPluginContext context) throws Exception {
		
		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(3);
		context.getProgress().setIndeterminate(false);
		// Get the default parameters.
		PullTableDataModelParameter parameters = new PullTableDataModelParameter();
	    // Get a dialog for this parameters.
	    PullTableAccessDialog dialog1 = new PullTableAccessDialog(context, parameters);
	    InteractionResult result1 = context.showWizard("Celonis access", true, true, dialog1);	  
	    if (result1 == InteractionResult.FINISHED) {
	    	context.log("Checking validation of login information...");
			ErrorUtils.checkLoginValidation(parameters.getUrl(), parameters.getToken());
			context.log("Check validation of login information done");			
	    	context.log("Getting the list of Data Model Tables...");
		    PullTableDataPoolDialog dialog2 = new PullTableDataPoolDialog(context, parameters);
		    context.getProgress().inc();
		    InteractionResult result2 = context.showWizard("Choose a Table", true, true, dialog2);	   
		    if (result2 == InteractionResult.FINISHED) {
		    	CSVFile csv = runConnections(context, parameters);	 
		    	context.getProgress().inc();
		    	context.getFutureResult(0).setLabel(parameters.getDataPool() + " / " + 
						parameters.getDataModel() + " / " + 
						parameters.getTableName() + ".csv");
			    return csv;
		    }
		    else {
		    	context.getFutureResult(0).cancel(true);
			    return null;
		    }
	    }
	    else {
	    	context.getFutureResult(0).cancel(true);
		    return null;
	    }
	    
	}	
	

	private CSVFile runConnections(PluginContext context, PullTableDataModelParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		CSVFile csv = apply(context, parameters);
		return csv;
		
	}

}
