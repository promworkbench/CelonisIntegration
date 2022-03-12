package org.processmining.celonisintegration.dialogs;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.processmining.celonisintegration.algorithms.CacheUtils;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

import com.opencsv.exceptions.CsvValidationException;

public class UploadEventLogDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public UploadEventLogDialog(UIPluginContext context, final UploadEventLogParameter parameters) throws CsvValidationException, IOException {
		String nameCache = "Celonis-Connection";
		String[] accessInfo = CacheUtils.getAccessInfo(nameCache);
		
		
        JTextField urlField = new JTextField(accessInfo[0]);
        JTextField tokenField = new JTextField(accessInfo[1]);
        JTextField tableName = new JTextField();

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));        
        myPanel.add(new JLabel("Based URL:"));
        myPanel.add(urlField);        
        myPanel.add(new JLabel("API Token:"));
        myPanel.add(tokenField);
        myPanel.add(new JLabel("Table name:"));
        myPanel.add(tableName);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Please Enter Based URL, API Token and your table name", JOptionPane.OK_CANCEL_OPTION);
        // change your result label
        context.getFutureResult(0).setLabel("Celonis access");
        if (!urlField.getText().equals(accessInfo[0]) || ! tokenField.getText().equals(accessInfo[1])) {
        	CacheUtils.updateAccessInfo(nameCache, urlField.getText(), tokenField.getText());
        }
        
        
        parameters.setUrl(urlField.getText());
        parameters.setToken(tokenField.getText());
        parameters.setTableName(tableName.getText());
        
	}
}
