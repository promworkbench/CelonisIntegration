package org.processmining.celonisintegration.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.celonisintegration.algorithms.ProcessAnalytics;
import org.processmining.celonisintegration.parameters.PullOlapTableParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

public class PullOlapTableAnaDialog {
	public PullOlapTableAnaDialog(UIPluginContext context, final PullOlapTableParameter parameters) {
		ProcessAnalytics pa = new ProcessAnalytics(parameters.getUrl(), parameters.getToken());
		String wsName = parameters.getWorkspaceName();
		String wsId = pa.getWorkspaceByName(wsName);
		List<String> analyses = pa.getAnalyses(wsId);
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
		for (String ana: analyses) {						
			JRadioButton x = new JRadioButton(ana);
			g.add(x);
			options.add(x);
			
		}		
		String text = parameters.getWorkspaceName() + "/";
		JLabel label = new JLabel(text);
		JPanel myPanel = new JPanel();
		
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));     
        myPanel.add(label);
        
        for (JRadioButton x: options) {
        	myPanel.add(x);
        }        
        
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                        "Choose your analysis",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Analysis");
        
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		parameters.setAnalysisName(x.getText());
        		break;
        	}
        }  
        
        
	}
}
