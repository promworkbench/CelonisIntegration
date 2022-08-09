package org.processmining.celonisintegration.plugins;

import java.io.File;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.DataIntegration;
import org.processmining.celonisintegration.algorithms.ErrorUtils;
import org.processmining.celonisintegration.algorithms.UploadEventLogAlgo;
import org.processmining.celonisintegration.algorithms.XESUtils;
import org.processmining.celonisintegration.dialogs.UploadEventLogAccessDialog;
import org.processmining.celonisintegration.dialogs.UploadEventLogDialog;
import org.processmining.celonisintegration.help.YourHelp;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.AnalysisStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataModelStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataPoolStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.TableStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.WorkspaceStatus;
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
			"Event log" }, returnTypes = { XLog.class }, help = YourHelp.UPLOAD)
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public XLog runUI(UIPluginContext context, XLog log) throws Exception {
		// Get the default parameters.
		UploadEventLogParameter parameters = new UploadEventLogParameter();
		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(10);
		context.getProgress().setIndeterminate(false);
		UploadEventLogAccessDialog dialog = new UploadEventLogAccessDialog(context, parameters);
		InteractionResult result = context.showWizard("Celonis access", true, true, dialog);
		if (result == InteractionResult.FINISHED) {
			context.log("Checking validation of login information...");
			ErrorUtils.checkLoginValidation(parameters.getUrl(), parameters.getToken());
			context.log("Check validation of login information done");			
			context.log("Converting the event log to CSV...");
			File actCsv = XESUtils.createActCSV(log, "case-", 0);
			context.log("Convert the event log to CSV done");
			context.getProgress().inc();
			parameters.setActCSV(actCsv);			
			context.log("Getting information about the workspaces, analyses, data pools, data models, tables...");
			DataIntegration di = new DataIntegration(parameters.getUrl(), parameters.getToken());
			context.log("Get information about the workspaces, analyses, data pools, data models, tables done");		
			
			UploadEventLogDialog dialog1 = new UploadEventLogDialog(context, parameters, log, di);
			InteractionResult result1 = context.showWizard("Upload event log to Celonis", true, true, dialog1);
			if (result1 == InteractionResult.FINISHED) {	
				context.log("Checking validation of parameters...");
				String dp = parameters.getDataPool();
				if (parameters.getDataPoolStatus() != DataPoolStatus.NEW) {
					dp = "";
				}
				String dm = parameters.getDataModel();
				if (parameters.getDataModelStatus() != DataModelStatus.NEW) {
					dm = "";
				}
				String tableName = parameters.getTableName();
				if (parameters.getTableStatus() != TableStatus.NEW) {
					tableName = "";
				}
				String ws = parameters.getWorkspace();
				if (parameters.getWorkspaceStatus() != WorkspaceStatus.NEW) {
					ws = "";
				}
				String ana = parameters.getAnalysis();		
				if (parameters.getAnalysisStatus() != AnalysisStatus.NEW) {
					ana = "";
				}
				ErrorUtils.checkDuplicateParameterUpload(di, parameters);
				ErrorUtils.checkUniqueColumn(parameters.getCaseCol(), parameters.getActCol());
				context.log("Check validation of parameters done");
				runConnections(context, log, parameters);				
				context.getProgress().inc();
				return log;
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

	private void runConnections(PluginContext context, XLog log, UploadEventLogParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		apply(context, log, parameters);

	}

}
