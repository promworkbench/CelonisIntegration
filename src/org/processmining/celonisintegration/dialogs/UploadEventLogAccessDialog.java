
package org.processmining.celonisintegration.dialogs;

import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.processmining.celonisintegration.algorithms.CacheUtils;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMTextField;

import com.opencsv.exceptions.CsvValidationException;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class UploadEventLogAccessDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public UploadEventLogAccessDialog(UIPluginContext context, final UploadEventLogParameter parameters) throws CsvValidationException, IOException {
		String nameCache = "Data-Integration";
		String[] accessInfo = CacheUtils.getAccessInfo(nameCache);
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.MINIMUM, 40, 40,40 } };
		setLayout(new TableLayout(size));
		
		ProMTextField urlField = new ProMTextField(accessInfo[0]);
		ProMTextField tokenField = new ProMTextField(accessInfo[1]);
		parameters.setUrl(accessInfo[0]);
		parameters.setToken(accessInfo[1]);
		
		urlField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setUrl(urlField.getText());
				if (!urlField.getText().equals(accessInfo[0])) {
					try {
						CacheUtils.updateUrl(nameCache, urlField.getText());
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setUrl(urlField.getText());
				if (!urlField.getText().equals(accessInfo[0])) {
					try {
						CacheUtils.updateUrl(nameCache, urlField.getText());
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setUrl(urlField.getText());
				if (!urlField.getText().equals(accessInfo[0])) {
					try {
						CacheUtils.updateUrl(nameCache, urlField.getText());
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		
		tokenField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setToken(tokenField.getText());
				if (!tokenField.getText().equals(accessInfo[1])) {
					try {
						CacheUtils.updateToken(nameCache, tokenField.getText());
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setToken(tokenField.getText());
				if (!tokenField.getText().equals(accessInfo[1])) {
					try {
						CacheUtils.updateToken(nameCache, tokenField.getText());
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setToken(tokenField.getText());
				if (!tokenField.getText().equals(accessInfo[1])) {
					try {
						CacheUtils.updateToken(nameCache, tokenField.getText());
					} catch (CsvValidationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		JLabel url = new JLabel("Based URL:");
		JLabel token = new JLabel("API Token:");
		add(url, "0, 0");
		add(urlField, "0, 1");
		add(token, "0, 2");
		add(tokenField, "0, 3");
		
       
        
//        

        
	}
}
