package org.processmining.celonisintegration.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.celonisintegration.algorithms.ProcessAnalytics;
import org.processmining.celonisintegration.parameters.PullOlapTableParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

public class PullOlapTableWsDialog {
	public PullOlapTableWsDialog(UIPluginContext context, final PullOlapTableParameter parameters) {
		ProcessAnalytics pa = new ProcessAnalytics(parameters.getUrl(), parameters.getToken());
		List<String> workspaces = pa.getWorkspaces();
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
		for (String ws: workspaces) {						
			JRadioButton x = new JRadioButton(ws);
			g.add(x);
			options.add(x);
			
		}		
		JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));        
        for (JRadioButton x: options) {
        	myPanel.add(x);
        }        
        
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                        "Choose your workspace",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Workspace");
        
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		parameters.setWorkspaceName(x.getText());
        		break;
        	}
        }  
        
        
	}
}
