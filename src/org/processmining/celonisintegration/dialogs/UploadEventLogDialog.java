package org.processmining.celonisintegration.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.CacheUtils;
import org.processmining.celonisintegration.algorithms.XESUtils;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMScrollPane;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class UploadEventLogDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * 
	 * @throws Exception
	 */
	public UploadEventLogDialog(UIPluginContext context, final UploadEventLogParameter parameters, XLog log)
			throws Exception {
		//Celonis info
		int rowsNum = 40;
		double[] rows = new double[rowsNum];
		for (int i = 0; i < rowsNum; i++) {
			rows[i] = TableLayoutConstants.FILL;
		}
		double size[][] = { { 120, 150, TableLayoutConstants.FILL }, rows };
		setLayout(new TableLayout(size));

		String nameCache = "Data-Integration";
		String[] accessInfo = CacheUtils.getAccessInfo(nameCache);
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
		ProMTextField tableField = new ProMTextField();
		tableField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setTableName(tableField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setTableName(tableField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setTableName(tableField.getText());

			}
		});
		ProMTextField dpField = new ProMTextField("_DATAPOOL");
		parameters.setDataPool(dpField.getText());
		dpField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setDataPool(dpField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setDataPool(dpField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setDataPool(dpField.getText());

			}
		});
		ProMTextField dmField = new ProMTextField("_DATAMODEL");
		parameters.setDataModel(dmField.getText());
		dmField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setDataModel(dmField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setDataModel(dmField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setDataModel(dmField.getText());

			}
		});
		ProMTextField wsField = new ProMTextField("_WORKSPACE");
		parameters.setWorkspace(wsField.getText());
		wsField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setWorkspace(wsField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setWorkspace(wsField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setWorkspace(wsField.getText());

			}
		});
		ProMTextField anaField = new ProMTextField("_ANALYSIS");
		parameters.setAnalysis(anaField.getText());
		anaField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setAnalysis(anaField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setAnalysis(anaField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setAnalysis(anaField.getText());

			}
		});
		JLabel url = new JLabel("Based URL:");
		JLabel token = new JLabel("API Token:");
		JLabel tableName = new JLabel("Table name:");
		JLabel dataPool = new JLabel("Data Pool name:");
		JLabel dataModel = new JLabel("Data Model name:");
		JLabel workspace = new JLabel("Workspace name:");
		JLabel ana = new JLabel("Analysis name:");
		add(url, "0, 0");
		add(urlField, "1, 0");
		add(token, "0, 1");
		add(tokenField, "1, 1");
		add(tableName, "0, 2");
		add(tableField, "1, 2");
		add(dataPool, "0, 3");
		add(dpField, "1, 3");
		add(dataModel, "0, 4");
		add(dmField, "1, 4");
		add(workspace, "0, 5");
		add(wsField, "1, 5");
		add(ana, "0, 6");
		add(anaField, "1, 6");

		// Event log config info
		List<String> timestampCols = XESUtils.getTimestampCols(log, "case-");
		List<String> cols = XESUtils.getStringColumns(log, "case-");
		File csv = parameters.getActCSV();
		String[][] snippet = XESUtils.getSnippet(csv, 20);
		String[] header = XESUtils.getHeader(XESUtils.getColumnMap(log, "case-"));
		JTable t = new JTable(snippet, header);
		t.getTableHeader().setBackground(WidgetColors.HEADER_COLOR);
		t.getTableHeader().setForeground(WidgetColors.TEXT_COLOR);
		ProMScrollPane pane = new ProMScrollPane(t);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		add(pane, "2, 0, 2, 12");

		JLabel caseId = new JLabel("Case ID column:");
		JLabel act = new JLabel("Activity column:");
		JLabel timestamp = new JLabel("Timestamp column:");
		ProMComboBox<String> caseCombo = new ProMComboBox<String>(cols);
		caseCombo.setSelectedIndex(0);
		String caseCol = (String) caseCombo.getSelectedItem();
		parameters.setCaseCol(caseCol);
		caseCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				String caseCol = (String) cb.getSelectedItem();
				parameters.setCaseCol(caseCol);
			}
		});
		ProMComboBox<String> actCombo = new ProMComboBox<String>(cols);
		actCombo.setSelectedIndex(0);
		String actCol = (String) actCombo.getSelectedItem();
		parameters.setActCol(actCol);
		actCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				String actCol = (String) cb.getSelectedItem();
				parameters.setActCol(actCol);
			}
		});
		ProMComboBox<String> timeCombo = new ProMComboBox<String>(timestampCols);
		timeCombo.setSelectedIndex(0);
		String timeCol = (String) timeCombo.getSelectedItem();
		parameters.setTimeCol(timeCol);
		timeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				String timeCol = (String) cb.getSelectedItem();
				parameters.setTimeCol(timeCol);
			}
		});
		ProMTextField caseField = new ProMTextField("");
		parameters.setCaseColNew(caseField.getText());
		caseField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setCaseColNew(caseField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setCaseColNew(caseField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setCaseColNew(caseField.getText());

			}
		});
		ProMTextField actField = new ProMTextField("");
		parameters.setActColNew(actField.getText());
		actField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setActColNew(actField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setActColNew(actField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setActColNew(actField.getText());

			}
		});
		ProMTextField timeField = new ProMTextField("");
		parameters.setTimeColNew(timeField.getText());
		timeField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setTimeColNew(timeField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setTimeColNew(timeField.getText());

			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setTimeColNew(timeField.getText());

			}
		});

		add(caseId, "0, 7");
		add(caseCombo, "0, 8");
		add(caseField, "1, 8");
		add(act, "0, 9");
		add(actCombo, "0, 10");
		add(actField, "1, 10");
		add(timestamp, "0, 11");
		add(timeCombo, "0, 12");
		add(timeField, "1, 12");

	}
}
