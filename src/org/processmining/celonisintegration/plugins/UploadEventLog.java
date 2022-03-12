package org.processmining.celonisintegration.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.UploadEventLogAlgo;
import org.processmining.celonisintegration.dialogs.UploadEventLogDialog;
import org.processmining.celonisintegration.help.YourHelp;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;


public class UploadEventLog extends UploadEventLogAlgo {

	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the parameters.
	 * 
	 * @param context The context to run in.
	 * @param log The event log.
	 * @return 
	 * @throws Exception 
	 */
	@Plugin(name = "Upload event log to Celonis", parameterLabels = { "Event log" }, 
			returnLabels = { "Event log" }, returnTypes = {XLog.class}, help = YourHelp.TEXT)
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public XLog runUI(UIPluginContext context, XLog log) throws Exception {
		// Get the default parameters.
	    UploadEventLogParameter parameters = new UploadEventLogParameter();
	    // Get a dialog for this parameters.
	    UploadEventLogDialog dialog = new UploadEventLogDialog(context, parameters);
	    runConnections(context, log, parameters);	    
	    return log;
	}	
	

	private void runConnections(PluginContext context, XLog log,  UploadEventLogParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		apply(context, log, parameters);
		
	}

}
