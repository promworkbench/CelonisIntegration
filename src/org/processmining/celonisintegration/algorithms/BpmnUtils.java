package org.processmining.celonisintegration.algorithms;

import java.io.InputStream;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.bpmn.Bpmn;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class BpmnUtils {
	public static Bpmn importFromStream(PluginContext context, InputStream input, String filename,
			long fileSizeInBytes) throws Exception {
		Bpmn bpmn = importBpmnFromStream(context, input, filename, fileSizeInBytes);
		if (bpmn == null) {
			/*
			 * No BPMN found in file. Fail.
		`	 */
			return null;
		}
		/*
		 * XPDL file has been imported. Now we need to convert the contents to a
		 * BPMN diagram.
		 */
//		BpmnDiagrams diagrams = new BpmnDiagrams();
//		diagrams.setBpmn(bpmn);
//		diagrams.setName(filename);
//		diagrams.addAll(bpmn.getDiagrams());
		context.getFutureResult(0).setLabel(filename);
		return bpmn;
	}

	public static Bpmn importBpmnFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Bpmn bpmn = new Bpmn();

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(bpmn.tag)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			bpmn.importElement(xpp, bpmn);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			bpmn.log(bpmn.tag, xpp.getLineNumber(), "Expected " + bpmn.tag + ", got " + xpp.getName());
		}
		if (bpmn.hasErrors()) {
			context.getProvidedObjectManager().createProvidedObject("Log of BPMN import", bpmn.getLog(), XLog.class,
					context);
			return null;
		}
		return bpmn;
	}


}
