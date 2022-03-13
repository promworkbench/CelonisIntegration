package org.processmining.celonisintegration.plugins;

import org.processmining.celonisintegration.algorithms.PullBpmnAlgo;
import org.processmining.celonisintegration.dialogs.PullBpmnAccessDialog;
import org.processmining.celonisintegration.dialogs.PullBpmnDialog;
import org.processmining.celonisintegration.parameters.PullBpmnParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;


public class PullBpmnPlugin extends PullBpmnAlgo {

	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the parameters.
	 * 
	 * @param context The context to run in.
	 * @param log The event log.
	 * @return 
	 * @throws Exception 
	 */
	@Plugin(
            name = "Pull BPMN from Celonis", 
            parameterLabels = {}, 
            returnLabels = { "BPMN diagram" }, 
            returnTypes = { BPMNDiagram.class }
    
    )
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public BPMNDiagram runUI(UIPluginContext context) throws Exception {
		// Get the default parameters.
	    PullBpmnParameter parameters = new PullBpmnParameter();
	    // Get a dialog for this parameters.
	    PullBpmnAccessDialog dialog1 = new PullBpmnAccessDialog(context, parameters);
	    PullBpmnDialog dialog2 = new PullBpmnDialog(context, parameters);
	    BPMNDiagram bpmn = runConnections(context, parameters);	    
	    return bpmn;
	}	
	

	private BPMNDiagram runConnections(PluginContext context, PullBpmnParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		BPMNDiagram bpmn = apply(context, parameters);
		return bpmn;
		
	}

}
