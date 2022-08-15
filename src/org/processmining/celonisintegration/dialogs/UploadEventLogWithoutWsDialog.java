package org.processmining.celonisintegration.dialogs;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.CelonisObject;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModel;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTableType;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataPool;
import org.processmining.celonisintegration.algorithms.DataIntegration;
import org.processmining.celonisintegration.algorithms.XESUtils;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataModelStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataPoolStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.TableStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.WorkspaceStatus;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMScrollPane;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class UploadEventLogWithoutWsDialog  extends JPanel{
	private static List<String> dataPoolNames;
	private static List<String> dataModelNames;
	private static List<String> tableNames;
	private static List<DataModel> dataModels;
	private static String currentDataPool;
	private static DataModel currentDataModel;

	public UploadEventLogWithoutWsDialog(UIPluginContext context, final UploadEventLogParameter parameters, XLog log,
			DataIntegration di) throws Exception {
		//Celonis info
		parameters.setWorkspaceStatus(WorkspaceStatus.FORBIDDEN);
		String logName = XConceptExtension.instance().extractName(log) != null
				? XConceptExtension.instance().extractName(log).replaceAll(" ", "_").replace(".xes", "").replace(".",
						"_")
				: "Anonymous";
		int rowsNum = 40;
		int rowTable = 2;
		double[] rows = new double[rowsNum];
		for (int i = 0; i < rowTable - 1; i++) {
			rows[i] = TableLayoutConstants.FILL;
		}
		for (int i = rowTable - 1; i < rowsNum; i++) {
			rows[i] = TableLayoutConstants.MINIMUM;
		}
		//		double size[][] = { {  TableLayoutConstants.FILL,  TableLayoutConstants.FILL, TableLayoutConstants.FILL,  TableLayoutConstants.FILL }, rows };
		double size[][] = { { 180, 60, 90, 60, 180, 190 }, rows };
		setLayout(new TableLayout(size));

		dataPoolNames = new ArrayList<String>();
		dataModelNames = new ArrayList<String>();
		tableNames = new ArrayList<String>();
		dataModels = new ArrayList<DataModel>();
		currentDataPool = "";
		currentDataModel = new CelonisObject().new DataModel();

		updateDataPool(di);
		if (dataPoolNames.size() > 0) {
			currentDataPool = dataPoolNames.get(0);
		}
		updateDataModel(di);
		if (dataModelNames.size() > 0) {
			currentDataModel = dataModels.get(0);
		}
		updateTable(di);
		


		// Table Layout
		CardLayout layoutCardTable = new CardLayout();
		JPanel cardTableCont = new JPanel();
		cardTableCont.setLayout(layoutCardTable);

		JButton bTableNew = new JButton("New");
		bTableNew.setBackground(new Color(80, 0, 0));
		bTableNew.setForeground(new Color(240, 240, 240));
		JButton bTableSelect = new JButton("Replace");
		bTableSelect.setBackground(new Color(40, 40, 40));
		bTableSelect.setForeground(new Color(210, 210, 210));
		bTableNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardTable.show(cardTableCont, "2");
				parameters.setTableStatus(TableStatus.NEW);
				bTableNew.setBackground(new Color(80, 0, 0));
				bTableNew.setForeground(new Color(240, 240, 240));
				bTableSelect.setBackground(new Color(40, 40, 40));
				bTableSelect.setForeground(new Color(210, 210, 210));
			}

		});
		bTableSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardTable.show(cardTableCont, "1");
				parameters.setTableStatus(TableStatus.REPLACE);
				bTableSelect.setBackground(new Color(80, 0, 0));
				bTableSelect.setForeground(new Color(240, 240, 240));
				bTableNew.setBackground(new Color(40, 40, 40));
				bTableNew.setForeground(new Color(210, 210, 210));
			}

		});

		ProMComboBox<String> tableCombo = new ProMComboBox<String>(tableNames);
		String tableCol = (String) tableCombo.getSelectedItem();
		parameters.setTableNameReplace(tableCol);
		tableCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				cb.getSelectedItem();
				parameters.setTableNameReplace(tableCol);
			}
		});

		ProMTextField tableField = new ProMTextField("_TABLE_" + logName);
		parameters.setTableName(tableField.getText());
		parameters.setTableStatus(TableStatus.NEW);
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
		cardTableCont.add(tableCombo, "1");
		cardTableCont.add(tableField, "2");
		cardTableCont.setBackground(new Color(150, 150, 150));
		layoutCardTable.show(cardTableCont, "2");

		// DM layout
		CardLayout layoutCardDataModel = new CardLayout();

		JPanel cardDataModelCont = new JPanel();
		cardDataModelCont.setLayout(layoutCardDataModel);

		JButton bDataModelNew = new JButton("New");
		bDataModelNew.setBackground(new Color(40, 40, 40));
		bDataModelNew.setForeground(new Color(210, 210, 210));
		JButton bDataModelSelect = new JButton("Replace");
		bDataModelSelect.setBackground(new Color(40, 40, 40));
		bDataModelSelect.setForeground(new Color(210, 210, 210));
		JButton bDataModelAdd = new JButton("Add");
		bDataModelAdd.setBackground(new Color(80, 0, 0));
		bDataModelAdd.setForeground(new Color(240, 240, 240));

		bDataModelNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				layoutCardDataModel.show(cardDataModelCont, "2");
				layoutCardTable.show(cardTableCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.NEW);

				bDataModelNew.setBackground(new Color(80, 0, 0));
				bDataModelNew.setForeground(new Color(240, 240, 240));
				bDataModelSelect.setBackground(new Color(40, 40, 40));
				bDataModelSelect.setForeground(new Color(210, 210, 210));
				bDataModelAdd.setBackground(new Color(40, 40, 40));
				bDataModelAdd.setForeground(new Color(210, 210, 210));


				bTableSelect.setVisible(false);

			}

		});
		bDataModelSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataModel.show(cardDataModelCont, "1");
				layoutCardTable.show(cardTableCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.REPLACE);

				bDataModelSelect.setBackground(new Color(80, 0, 0));
				bDataModelSelect.setForeground(new Color(240, 240, 240));
				bDataModelNew.setBackground(new Color(40, 40, 40));
				bDataModelNew.setForeground(new Color(210, 210, 210));
				bDataModelAdd.setBackground(new Color(40, 40, 40));
				bDataModelAdd.setForeground(new Color(210, 210, 210));


				bTableSelect.setVisible(false);
			}

		});
		bDataModelAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataModel.show(cardDataModelCont, "1");
				layoutCardTable.show(cardTableCont, "2");

				parameters.setDataModelStatus(DataModelStatus.ADD);
				parameters.setTableStatus(TableStatus.NEW);

				bDataModelAdd.setBackground(new Color(80, 0, 0));
				bDataModelAdd.setForeground(new Color(240, 240, 240));
				bDataModelNew.setBackground(new Color(40, 40, 40));
				bDataModelNew.setForeground(new Color(210, 210, 210));
				bDataModelSelect.setBackground(new Color(40, 40, 40));
				bDataModelSelect.setForeground(new Color(210, 210, 210));

				bTableNew.setBackground(new Color(80, 0, 0));
				bTableNew.setForeground(new Color(240, 240, 240));
				bTableSelect.setBackground(new Color(40, 40, 40));
				bTableSelect.setForeground(new Color(210, 210, 210));


				bTableSelect.setVisible(true);

			}

		});

		ProMComboBox<String> dmCombo = new ProMComboBox<String>(dataModelNames);
		String dmCol = (String) dmCombo.getSelectedItem();
		parameters.setDataModelReplace(dmCol);
		parameters.setDataModelStatus(DataModelStatus.ADD);
		dmCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				String dmC = (String) cb.getSelectedItem();
				parameters.setDataModelReplace(dmC);
				for (DataModel dm : di.getDataModels()) {
					if (dm.getName() == dmC) {
						currentDataModel = dm;
						break;
					}
				}

				tableNames = new ArrayList<String>();
				updateTable(di);
				tableCombo.removeAllItems();
				tableCombo.addAllItems(tableNames);
			}
		});

		ProMTextField dmField = new ProMTextField("_DATAMODEL_" + logName);
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
		cardDataModelCont.add(dmCombo, "1");
		cardDataModelCont.add(dmField, "2");
		cardDataModelCont.setBackground(new Color(150, 150, 150));
		layoutCardDataModel.show(cardDataModelCont, "1");

		// DP Layout
		CardLayout layoutCardDataPool = new CardLayout();
		JPanel cardDataPoolCont = new JPanel();
		cardDataPoolCont.setLayout(layoutCardDataPool);

		JButton bDataPoolNew = new JButton("New");
		bDataPoolNew.setBackground(new Color(40, 40, 40));
		bDataPoolNew.setForeground(new Color(210, 210, 210));
		JButton bDataPoolSelect = new JButton("Replace");
		bDataPoolSelect.setBackground(new Color(40, 40, 40));
		bDataPoolSelect.setForeground(new Color(210, 210, 210));
		JButton bDataPoolAdd = new JButton("Add");
		bDataPoolAdd.setBackground(new Color(80, 0, 0));
		bDataPoolAdd.setForeground(new Color(240, 240, 240));

		bDataPoolNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataPool.show(cardDataPoolCont, "2");
				layoutCardDataModel.show(cardDataModelCont, "2");
				layoutCardTable.show(cardTableCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.NEW);
				parameters.setDataPoolStatus(DataPoolStatus.NEW);

				bDataPoolNew.setBackground(new Color(80, 0, 0));
				bDataPoolNew.setForeground(new Color(240, 240, 240));
				bDataPoolSelect.setBackground(new Color(40, 40, 40));
				bDataPoolSelect.setForeground(new Color(210, 210, 210));
				bDataPoolAdd.setBackground(new Color(40, 40, 40));
				bDataPoolAdd.setForeground(new Color(210, 210, 210));

				bDataModelNew.setBackground(new Color(80, 0, 0));
				bDataModelNew.setForeground(new Color(240, 240, 240));
				bDataModelSelect.setBackground(new Color(40, 40, 40));
				bDataModelSelect.setForeground(new Color(210, 210, 210));
				bDataModelAdd.setBackground(new Color(40, 40, 40));
				bDataModelAdd.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(false);
				bDataModelSelect.setVisible(false);
				bDataModelAdd.setVisible(false);
			}

		});
		bDataPoolSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataPool.show(cardDataPoolCont, "1");
				layoutCardDataModel.show(cardDataModelCont, "2");
				layoutCardTable.show(cardTableCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.NEW);
				parameters.setDataPoolStatus(DataPoolStatus.REPLACE);

				bDataPoolSelect.setBackground(new Color(80, 0, 0));
				bDataPoolSelect.setForeground(new Color(240, 240, 240));
				bDataPoolNew.setBackground(new Color(40, 40, 40));
				bDataPoolNew.setForeground(new Color(210, 210, 210));
				bDataPoolAdd.setBackground(new Color(40, 40, 40));
				bDataPoolAdd.setForeground(new Color(210, 210, 210));

				bDataModelNew.setBackground(new Color(80, 0, 0));
				bDataModelNew.setForeground(new Color(240, 240, 240));
				bDataModelSelect.setBackground(new Color(40, 40, 40));
				bDataModelSelect.setForeground(new Color(210, 210, 210));
				bDataModelAdd.setBackground(new Color(40, 40, 40));
				bDataModelAdd.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(false);
				bDataModelSelect.setVisible(false);
				bDataModelAdd.setVisible(false);
			}

		});
		bDataPoolAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				layoutCardDataPool.show(cardDataPoolCont, "1");
				layoutCardDataModel.show(cardDataModelCont, "1");
				layoutCardTable.show(cardTableCont, "2");

				parameters.setDataPoolStatus(DataPoolStatus.ADD);
				parameters.setDataModelStatus(DataModelStatus.ADD);
				parameters.setTableStatus(TableStatus.NEW);

				bDataPoolAdd.setBackground(new Color(80, 0, 0));
				bDataPoolAdd.setForeground(new Color(240, 240, 240));
				bDataPoolNew.setBackground(new Color(40, 40, 40));
				bDataPoolNew.setForeground(new Color(210, 210, 210));
				bDataPoolSelect.setBackground(new Color(40, 40, 40));
				bDataPoolSelect.setForeground(new Color(210, 210, 210));
				bTableSelect.setVisible(true);

				bDataModelAdd.setBackground(new Color(80, 0, 0));
				bDataModelAdd.setForeground(new Color(240, 240, 240));
				bDataModelNew.setBackground(new Color(40, 40, 40));
				bDataModelNew.setForeground(new Color(210, 210, 210));
				bDataModelSelect.setBackground(new Color(40, 40, 40));
				bDataModelSelect.setForeground(new Color(210, 210, 210));

				bTableNew.setBackground(new Color(80, 0, 0));
				bTableNew.setForeground(new Color(240, 240, 240));
				bTableSelect.setBackground(new Color(40, 40, 40));
				bTableSelect.setForeground(new Color(210, 210, 210));


				bDataModelSelect.setVisible(true);
				bDataModelAdd.setVisible(true);
				bTableSelect.setVisible(true);
			}

		});

		ProMComboBox<String> dpCombo = new ProMComboBox<String>(dataPoolNames);
		String dpCol = (String) dpCombo.getSelectedItem();
		parameters.setDataPoolReplace(dpCol);
		parameters.setDataPoolStatus(DataPoolStatus.ADD);
		dpCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				String dpC = (String) cb.getSelectedItem();
				parameters.setDataPoolReplace(dpC);

				currentDataPool = dpC;
				dataModelNames = new ArrayList<String>();
				dataModels = new ArrayList<DataModel>();
				updateDataModel(di);
				dmCombo.removeAllItems();
				dmCombo.addAllItems(dataModelNames);

				tableNames = new ArrayList<String>();
				if (dataModelNames.size() > 0) {
					for (DataModel dm : di.getDataModels()) {
						if (dm.getName() == dataModelNames.get(0)) {
							currentDataModel = dm;
							break;
						}
					}
					updateTable(di);
					System.out.println("Update dp");
				}

				tableCombo.removeAllItems();
				tableCombo.addAllItems(tableNames);

			}
		});

		ProMTextField dpField = new ProMTextField("_DATAPOOL_" + logName);
		parameters.setDataPool(dpField.getText());
		//	parameters.setDataPoolStatus(DataPoolStatus.NEW);
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

		cardDataPoolCont.add(dpCombo, "1");
		cardDataPoolCont.add(dpField, "2");
		cardDataPoolCont.setBackground(new Color(150, 150, 150));
		layoutCardDataPool.show(cardDataPoolCont, "1");

		//######################################################################################
		//######################################################################################
		//######################################################################################

		JLabel tableName = new JLabel("Table name:");
		JLabel dataPool = new JLabel("Data Pool name:");
		JLabel dataModel = new JLabel("Data Model name:");
		JLabel workspace = new JLabel("Workspace name:");
		JLabel ana = new JLabel("Analysis name:");

		add(dataPool, "0, " + Integer.toString(rowTable - 1));
		add(bDataPoolNew, "1, " + Integer.toString(rowTable - 1));
		add(bDataPoolSelect, "2, " + Integer.toString(rowTable - 1));
		add(bDataPoolAdd, "3, " + Integer.toString(rowTable - 1));
		add(cardDataPoolCont, "0, " + Integer.toString(rowTable) + ", 3," + Integer.toString(rowTable));

		add(dataModel, "0," + Integer.toString(rowTable + 1));
		add(bDataModelNew, "1, " + Integer.toString(rowTable + 1));
		add(bDataModelSelect, "2, " + Integer.toString(rowTable + 1));
		add(bDataModelAdd, "3, " + Integer.toString(rowTable + 1));
		add(cardDataModelCont, "0, " + Integer.toString(rowTable + 2) + ", 3, " + Integer.toString(rowTable + 2));

		add(tableName, "0," + Integer.toString(rowTable + 3));
		add(bTableNew, "1, " + Integer.toString(rowTable + 3));
		add(bTableSelect, "2, " + Integer.toString(rowTable + 3));
		add(cardTableCont, "0, " + Integer.toString(rowTable + 4) + ", 3, " + Integer.toString(rowTable + 4));


		// Event log config info
		List<String> timestampCols = XESUtils.getTimestampCols(log, "case-");
		List<String> caseCols = XESUtils.getCaseStringColumns(log, "case-");
		File csv = parameters.getActCSV();
		String[][] snippet = XESUtils.getSnippet(csv, 28);
		String[] header = XESUtils.getHeader(XESUtils.getColumnMap(log, "case-"));
		JTable t = new JTable(snippet, header);
		t.getTableHeader().setBackground(WidgetColors.HEADER_COLOR);
		t.getTableHeader().setForeground(WidgetColors.TEXT_COLOR);
		t.setBackground(WidgetColors.COLOR_LIST_BG);
		t.setForeground(WidgetColors.COLOR_LIST_FG);
		ProMScrollPane pane = new ProMScrollPane(t);

		add(pane, "0, 0, 5, " + Integer.toString(rowTable - 2));

		JLabel caseId = new JLabel("Case ID column:");
		JLabel act = new JLabel("Activity column:");
		JLabel timestamp = new JLabel("Timestamp column:");
		ProMComboBox<String> caseCombo = new ProMComboBox<String>(caseCols);
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
		List<String> actCols = XESUtils.getAllStringColumns(log, "case-");
		ProMComboBox<String> actCombo = new ProMComboBox<String>(actCols);
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
		JLabel caseNewName = new JLabel("New name:");
		JLabel actNewName = new JLabel("New name:");
		JLabel timeNewName = new JLabel("New name:");
		add(caseId, "4, " + Integer.toString(rowTable - 1));
		add(caseCombo, "4, " + Integer.toString(rowTable));
		add(caseNewName, "5, " + Integer.toString(rowTable - 1));
		add(caseField, "5, " + Integer.toString(rowTable));
		add(act, "4, " + Integer.toString(rowTable + 1));
		add(actCombo, "4, " + Integer.toString(rowTable + 2));
		add(actNewName, "5, " + Integer.toString(rowTable + 1));
		add(actField, "5, " + Integer.toString(rowTable + 2));
		add(timestamp, "4, " + Integer.toString(rowTable + 3));
		add(timeCombo, "4, " + Integer.toString(rowTable + 4));
		add(timeNewName, "5, " + Integer.toString(rowTable + 3));
		add(timeField, "5, " + Integer.toString(rowTable + 4));

	}

	public static void updateDataPool(DataIntegration di) {
		for (DataPool dp : di.getDataPools()) {
			dataPoolNames.add(dp.getName());
		}
	}

	public static void updateDataModel(DataIntegration di) {
		for (DataModel dm : di.getDataModels()) {
			if (dm.getDp().getName().equals(currentDataPool)) {
				dataModelNames.add(dm.getName());
				dataModels.add(dm);
			}
		}

	}

	public static void updateTable(DataIntegration di) {
		for (DataModelTable table : di.getDataModelTables()) {
			if (currentDataModel.getId() == table.getDmId()) {
				if (table.getTableType() == DataModelTableType.ACTIVITY) {
					tableNames.add(table.getName());
				}
			}
		}

	}
}