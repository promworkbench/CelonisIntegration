package org.processmining.celonisintegration.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.celonisintegration.algorithms.PullOlapTableAlgo;
import org.processmining.celonisintegration.dialogs.PullOlapTableAccessDialog;
import org.processmining.celonisintegration.dialogs.PullOlapTableWsDialog;
import org.processmining.celonisintegration.parameters.PullOlapTableParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.log.csv.CSVFile;

public class PullOlapTablePlugin extends PullOlapTableAlgo {

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
	@Plugin(name = "Pull OLAP Table from Celonis Analysis", parameterLabels = {}, returnLabels = {
			"CSV File" }, returnTypes = { CSVFile.class }

	)
	@UITopiaVariant(affiliation = "RWTH Aachen", author = "Hieu Le", email = "hieu.le@rwth-aachen.de")
	public CSVFile runUI(UIPluginContext context) throws Exception {
		// Get the default parameters.

		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(4);
		context.getProgress().setIndeterminate(false);
		
		PullOlapTableParameter parameters = new PullOlapTableParameter();
		// Get a dialog for this parameters.
		PullOlapTableAccessDialog dialog1 = new PullOlapTableAccessDialog(context, parameters);
		InteractionResult result1 = context.showWizard("Celonis access", true, true, dialog1);
		if (result1 == InteractionResult.FINISHED) {
			String r = "";
		}
		context.log("Getting the list of OLAP Tables");
		
		PullOlapTableWsDialog dialog2 = new PullOlapTableWsDialog(context, parameters);
		context.getProgress().inc();
		InteractionResult result2 = context.showWizard("Pull OLAP Table from Celonis", true, true, dialog2);
		if (result2 == InteractionResult.FINISHED) {
			context.log("Extracting PQL query of the table");
			CSVFile csv = runConnections(context, parameters);
			context.getProgress().inc();
			context.getFutureResult(0).setLabel(parameters.getWorkspaceName() + " / " + 
												parameters.getAnalysisName() + " / " + 
												parameters.getSheetName() + " / " +
												parameters.getTableName() + ".csv");
			return csv;
		}

		return null;
	}

	private CSVFile runConnections(PluginContext context, PullOlapTableParameter parameters) throws Exception {
		// No connection found. Apply the algorithm to compute a fresh output result.
		CSVFile csv = apply(context, parameters);
		return csv;

	}

}
