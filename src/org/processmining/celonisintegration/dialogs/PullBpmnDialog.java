package org.processmining.celonisintegration.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.celonisintegration.algorithms.ProcessRepository;
import org.processmining.celonisintegration.parameters.PullBpmnParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

import com.opencsv.exceptions.CsvValidationException;

public class PullBpmnDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public PullBpmnDialog(UIPluginContext context, final PullBpmnParameter parameters) throws CsvValidationException, IOException {
		ProcessRepository repo = new ProcessRepository(parameters.getUrl(), parameters.getToken());
		HashMap<String, List<String>> repoInfo = repo.getProcessRepoInfo();
		ButtonGroup g = new ButtonGroup();
		List<JRadioButton> options = new ArrayList<JRadioButton>();
		for (String cate: repoInfo.keySet()) {			
			for (String pm: repoInfo.get(cate)) {
				String name = cate + "/" + pm;
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
                        "Choose your category and process model name",JOptionPane.OK_CANCEL_OPTION);

        context.getFutureResult(0).setLabel("Category and process model name");
        String cateName = "";
        String pmName = "";
        for (JRadioButton x: options) {
        	if (x.isSelected()) {
        		String text = x.getText();
        		String[] split = text.split("/");
        		cateName = split[0];
        		pmName = split[1];
        		break;
        	}
        }  
        parameters.setCateName(cateName);
        parameters.setPmName(pmName);
        
	}
}























