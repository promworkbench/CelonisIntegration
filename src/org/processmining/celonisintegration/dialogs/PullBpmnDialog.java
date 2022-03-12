package org.processmining.celonisintegration.dialogs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
		String repoInfoText = "";
		for (String cate: repoInfo.keySet()) {			
			repoInfoText += cate + ": \n     ";
			for (String pm: repoInfo.get(cate)) {				
				repoInfoText += pm +", ";
			}		
			repoInfoText += "\n";
		}
		
		JTextField categoryField = new JTextField();
        JTextField processModelField = new JTextField();
		JPanel myPanel = new JPanel();
		JTextArea textArea = new JTextArea(repoInfoText);
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));   
        myPanel.add(new JLabel("Process Repository:"));
        myPanel.add(new JScrollPane(textArea));
        myPanel.add(new JLabel("Category Name:"));
        myPanel.add(categoryField);        
        myPanel.add(new JLabel("Process Model Name:"));
        myPanel.add(processModelField);
        int w = JOptionPane.showConfirmDialog(null, myPanel,
                        "Enter your category and process model name",JOptionPane.OK_CANCEL_OPTION);
        // change your result label
        context.getFutureResult(0).setLabel("Category and process model name");
        parameters.setCateName(categoryField.getText());
        parameters.setPmName(processModelField.getText());
        
	}
}
