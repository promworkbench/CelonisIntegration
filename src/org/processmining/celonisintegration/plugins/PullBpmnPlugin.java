package org.processmining.celonisintegration.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.celonisintegration.algorithms.ErrorUtils;
import org.processmining.celonisintegration.algorithms.PullBpmnAlgo;
import org.processmining.celonisintegration.dialogs.PullBpmnAccessDialog;
import org.processmining.celonisintegration.dialogs.PullBpmnDialog;
import org.processmining.celonisintegration.help.YourHelp;
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
            name = "Pull BPMN from Celonis Process Repository", 
            parameterLabels = {}, 
            returnLabels = { "BPMN diagram" }, 
            returnTypes = { BPMNDiagram.class },
            help = YourHelp.PULL_BPMN
    
    )
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public BPMNDiagram runUI(UIPluginContext context) throws Exception {
		// Get the default parameters.
		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(3);
		context.getProgress().setIndeterminate(false);
		
	    PullBpmnParameter parameters = new PullBpmnParameter();
	    // Get a dialog for this parameters.
	    PullBpmnAccessDialog dialog1 = new PullBpmnAccessDialog(context, parameters);
	    InteractionResult result1 = context.showWizard("Celonis access", true, true, dialog1);
	    if (result1 == InteractionResult.FINISHED) {
	    	ErrorUtils.checkLoginValidation(parameters.getUrl(), parameters.getToken());
	    	context.log("Getting the list of BPMN models...");
		    PullBpmnDialog dialog2 = new PullBpmnDialog(context, parameters);
		    context.getProgress().inc();
		    InteractionResult result2 = context.showWizard("Choose a Model", true, true, dialog2);
		    if (result2 == InteractionResult.FINISHED) {
		    	BPMNDiagram bpmn = runConnections(context, parameters);	   
		    	context.getProgress().inc();
			    return bpmn;
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
	

	private BPMNDiagram runConnections(PluginContext context, PullBpmnParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		BPMNDiagram bpmn = apply(context, parameters);
		return bpmn;
		
	}

}
