package org.processmining.celonisintegration.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.celonisintegration.algorithms.DataIntegration;
import org.processmining.celonisintegration.parameters.PullTableDataModelParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

import com.opencsv.exceptions.CsvValidationException;

public class PullTableDataModelDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public PullTableDataModelDialog(UIPluginContext context, final PullTableDataModelParameter parameters) throws CsvValidationException, IOException {
		DataIntegration di = new DataIntegration(parameters.getUrl(), parameters.getToken());
		String dpId = di.getDataPoolIdByName(parameters.getDataPool());
		List<String> dataModels = di.getDataModels(dpId);
		
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
		for(String dataModel: dataModels) {
			String dmId = di.getDataModelIdByName(dataModel, dpId);
			List<String> tables = di.getTables(dpId, dmId);
			for (String table: tables) {
				String name = dataModel + "/" + table;
				JRadioButton x = new JRadioButton(name);
				g.add(x);
				options.add(x);		
			}
		}	
		JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));        
        for (JRadioButton x: options) {
        	myPanel.add(x);
        }        
        
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                        "Choose your table",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Data pool");
        String dataModel = "";
        String tableName = "";
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		String text = x.getText();
        		String[] split = text.split("/");
        		dataModel = split[0];
        		tableName = split[1];
        		break;
        	}
        }
        parameters.setDataModel(dataModel);
        parameters.setTableName(tableName);
        
	}
}























