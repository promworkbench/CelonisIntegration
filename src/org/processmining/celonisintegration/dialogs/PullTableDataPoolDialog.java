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

public class PullTableDataPoolDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public PullTableDataPoolDialog(UIPluginContext context, final PullTableDataModelParameter parameters) throws CsvValidationException, IOException {
		DataIntegration di = new DataIntegration(parameters.getUrl(), parameters.getToken());
		List<String> dataPools = di.getDataPools();
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
		for (String dataPool: dataPools) {	
			JRadioButton x = new JRadioButton(dataPool);
			g.add(x);
			options.add(x);				
		}		
		JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));        
        for (JRadioButton x: options) {
        	myPanel.add(x);
        }        
        
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                        "Choose your data pool",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Data pool");
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		parameters.setDataPool(x.getText());
        		break;
        	}
        }
        
	}
}























