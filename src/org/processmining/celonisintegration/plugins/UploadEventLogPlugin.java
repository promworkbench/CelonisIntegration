package org.processmining.celonisintegration.plugins;

import java.io.File;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.DataIntegration;
import org.processmining.celonisintegration.algorithms.UploadEventLogAlgo;
import org.processmining.celonisintegration.algorithms.XESUtils;
import org.processmining.celonisintegration.dialogs.UploadEventLogAccessDialog;
import org.processmining.celonisintegration.dialogs.UploadEventLogDialog;
import org.processmining.celonisintegration.help.YourHelp;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

public class UploadEventLogPlugin extends UploadEventLogAlgo {

	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get
	 * the parameters.
	 * 
	 * @param context
	 *            The context to run in.
	 * @param log
	 *            The event log.
	 * @return
	 * @throws Exception
	 */
	@Plugin(name = "Upload event log to Celonis", parameterLabels = { "Event log" }, returnLabels = {
			"Event log" }, returnTypes = { XLog.class }, help = YourHelp.TEXT)
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public XLog runUI(UIPluginContext context, XLog log) throws Exception {
		// Get the default parameters.
		UploadEventLogParameter parameters = new UploadEventLogParameter();
		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(10);
		context.getProgress().setIndeterminate(false);

		context.log("Converting to CSV");
		File actCsv = XESUtils.createActCSV(log, "case-", 0);
		System.out.println("done transfering file");
		context.getProgress().inc();
		parameters.setActCSV(actCsv);
		context.log("done getting param12");
		UploadEventLogAccessDialog dialog = new UploadEventLogAccessDialog(context, parameters);
		InteractionResult result = context.showWizard("Celonis access", true, true, dialog);
		if (result == InteractionResult.FINISHED) {
			context.log("getting information about the workspaces, analyses, data pools, data models");
			DataIntegration di = new DataIntegration(parameters.getUrl(), parameters.getToken());
			UploadEventLogDialog dialog1 = new UploadEventLogDialog(context, parameters, log, di);
			InteractionResult result1 = context.showWizard("Celonis access", true, true, dialog1);
			if (result1 == InteractionResult.FINISHED) {
				context.log("done getting param");
				// check empty input			
//				if (parameters.getTableName() == null ) {
//					throw new Exception("Table name can not be empty");
//				}
//				else if (parameters.getTableName().replaceAll("\\s+", "") == "") {
//					throw new Exception("Table name can not be empty");
//				}
//				
//				if (parameters.getDataPool() == null ) {
//					throw new Exception("Data pool name can not be empty");
//				}
//				else if (parameters.getDataPool().replaceAll("\\s+", "") == "") {
//					throw new Exception("Data pool name can not be empty");
//				}
//				
//				if (parameters.getDataModel() == null ) {
//					throw new Exception("Data model name can not be empty");
//				}
//				else if (parameters.getDataModel().replaceAll("\\s+", "") == "") {
//					throw new Exception("Data model name can not be empty");
//				}
//				
//				if (parameters.getWorkspace() == null ) {
//					throw new Exception("Workspace name can not be empty");
//				}
//				else if (parameters.getWorkspace().replaceAll("\\s+", "") == "") {
//					throw new Exception("Workspace name can not be empty");
//				}
//				
//				if (parameters.getAnalysis() == null ) {
//					throw new Exception("Analysis name can not be empty");
//				}
//				else if (parameters.getAnalysis().replaceAll("\\s+", "") == "") {
//					throw new Exception("Analysis name can not be empty");
//				}

				
				// check logic
				// check workspace
				
				runConnections(context, log, parameters);
				context.getProgress().inc();
				return log;
			}
			;
		}

		// Get a dialog for this parameters.

		return null;

	}

	private void runConnections(PluginContext context, XLog log, UploadEventLogParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		context.log("getting in");
		apply(context, log, parameters);

	}

}