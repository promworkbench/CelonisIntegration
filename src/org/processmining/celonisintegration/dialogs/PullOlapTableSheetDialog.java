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

public class PullOlapTableSheetDialog {
	public PullOlapTableSheetDialog(UIPluginContext context, final PullOlapTableParameter parameters) throws Exception {
		ProcessAnalytics pa = new ProcessAnalytics(parameters.getUrl(), parameters.getToken());
		String wsName = parameters.getWorkspaceName();
		String wsId = pa.getWorkspaceByName(wsName);
		String anaName = parameters.getAnalysisName();
		String anaId = pa.getAnalysisByName(anaName);
		List<String> sheets = pa.getSheets(anaId);
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
		for (String sheet: sheets) {	
			List<String> tables = pa.getOlapTables(sheet, anaId);
			for (String table: tables) {
				String name = sheet + "/" + table;
				JRadioButton x = new JRadioButton(name);
				g.add(x);
				options.add(x);
			}			
		}		
		String text = parameters.getWorkspaceName() + "/" + parameters.getAnalysisName() + "/";
		JLabel label = new JLabel(text);
		JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));     
        myPanel.add(label);
        for (JRadioButton x: options) {
        	myPanel.add(x);
        }        
        
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                        "Choose your sheet and OLAP table",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Sheet and OLAP table");
        
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		String text1 = x.getText();
        		String[] split = text1.split("/");
        		parameters.setSheetName(split[0]);
        		parameters.setTableName(split[1]);
        		break;
        	}
        }  
        
        
	}
}
