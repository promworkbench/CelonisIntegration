package org.processmining.celonisintegration.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.CSVUtils;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

public class UploadEventLogCaseColumnDialog {
	public UploadEventLogCaseColumnDialog(UIPluginContext context, final UploadEventLogParameter parameters, XLog log) {
		List<String> cols = CSVUtils.getColumns(log, parameters.getCasePrefix());
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
        for(String col: cols) {
        	JRadioButton x = new JRadioButton(col);
			g.add(x);
			options.add(x);
        }
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));        
        for (JRadioButton x: options) {
        	myPanel.add(x);
        }        
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                "Choose the case ID column",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Case ID column");
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		parameters.setCaseCol(x.getText());
        		break;
        	}
        }  
	}
}
