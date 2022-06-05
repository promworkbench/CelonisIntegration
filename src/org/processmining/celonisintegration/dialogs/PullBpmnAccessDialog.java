package org.processmining.celonisintegration.dialogs;

import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.processmining.celonisintegration.algorithms.CacheUtils;
import org.processmining.celonisintegration.parameters.PullBpmnParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMTextField;

import com.opencsv.exceptions.CsvValidationException;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class PullBpmnAccessDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public PullBpmnAccessDialog(UIPluginContext context, final PullBpmnParameter parameters) throws CsvValidationException, IOException {
		String nameCache = "Process-Repository";
		String[] accessInfo = CacheUtils.getAccessInfo(nameCache);
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM,TableLayoutConstants.MINIMUM } };
		setLayout(new TableLayout(size));
		
		ProMTextField urlField = new ProMTextField(accessInfo[0]);
		parameters.setUrl(accessInfo[0]);
		urlField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setUrl(urlField.getText());		
			  }

			public void changedUpdate(DocumentEvent e) {
				parameters.setUrl(urlField.getText());		
				
			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setUrl(urlField.getText());		
				
			}
		});
		ProMTextField tokenField = new ProMTextField(accessInfo[1]);
		parameters.setToken(accessInfo[1]);	
		tokenField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setToken(tokenField.getText());		
			  }

			public void changedUpdate(DocumentEvent e) {
				parameters.setToken(tokenField.getText());		
				
			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setToken(tokenField.getText());		
				
			}
		});

		JLabel url = new JLabel("Based URL:");
		JLabel token = new JLabel("API Token:");
		add(url, "0, 0");
		add(urlField, "0, 1");
		add(token, "0, 2");
		add(tokenField, "0, 3");          
	}
}
