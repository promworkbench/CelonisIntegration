package org.processmining.celonisintegration.algorithms;

import java.io.FileInputStream;
import java.io.InputStream;

import org.processmining.celonisintegration.parameters.PullBpmnParameter;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.plugins.BpmnSelectDiagramPlugin;

public class PullBpmnAlgo {
	/**
	 * The method that implements your algorithm.
	 * 
	 * Note that this method only uses the boolean which is stored in the parameters.
	 * Nevertheless, it could have used the integer and/or the String as well.
	 * 
	 * @param context The context where to run in.
	 * @param input1 The first input.
	 * @param input2 The second input.
	 * @param parameters The parameters to use.
	 * @return The output.
	 * @throws Exception 
	 */
	public BPMNDiagram apply(PluginContext context, PullBpmnParameter parameters) throws Exception {
		/**
		 * Put your algorithm here, which computes an output form the inputs provided the parameters.
		 */
		long time = -System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] Start");
		parameters.displayMessage("[YourAlgorithm] Parameters = " + parameters.toString());
	    
		String url = parameters.getUrl();
		String token = parameters.getToken();
		ProcessRepository proRepo = new ProcessRepository(url, token);
		String fileLocation = proRepo.getBpmnFileLocation(parameters.getCateName(), parameters.getPmName());
		InputStream input = new FileInputStream(fileLocation);
    	long fileSizeInBytes = 1000;
    	Bpmn bpmn = BpmnUtils.importFromStream(context, input, parameters.getPmName(), fileSizeInBytes);
    	
    	BpmnSelectDiagramPlugin diagram = new BpmnSelectDiagramPlugin();
		
		time += System.currentTimeMillis();
		parameters.displayMessage("[YourAlgorithm] End (took " + time/1000.0 + "  seconds).");
		
		return diagram.selectDefault(context, bpmn);
	}
}
