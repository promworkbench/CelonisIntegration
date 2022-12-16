package org.processmining.celonisintegration.dialogs;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.celonisintegration.algorithms.DataIntegration;
import org.processmining.celonisintegration.algorithms.Studio;
import org.processmining.celonisintegration.algorithms.XESUtils;
import org.processmining.celonisintegration.objects.dataintegration.DataModel;
import org.processmining.celonisintegration.objects.dataintegration.DataModelTable;
import org.processmining.celonisintegration.objects.dataintegration.DataModelTable.DataModelTableType;
import org.processmining.celonisintegration.objects.dataintegration.DataPool;
import org.processmining.celonisintegration.objects.processanalytics.Analysis;
import org.processmining.celonisintegration.objects.processanalytics.Workspace;
import org.processmining.celonisintegration.objects.studio.Package;
import org.processmining.celonisintegration.objects.studio.Space;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.AnalysisStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataModelStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataPoolStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.PackageStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.SAnalysisStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.SpaceStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.TableStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.WorkspaceStatus;
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
	private List<String> dataPoolNames;
	private List<String> dataModelNames;
	private List<String> tableNames;
	private List<String> wsNames;
	private List<String> anaNames;
	private List<DataModel> dataModels;
	private List<Workspace> workspaces;
	private String currentDataPool;
	private DataModel currentDataModel;
	private Workspace currentWorkspace;
	private List<Space> spaces;
	private List<Package> curPackages;
	private List<org.processmining.celonisintegration.objects.studio.Analysis> curSAnalyses;
	private HashMap<Space, List<Package>> mapSpacePackage;
	private HashMap<Package, List<org.processmining.celonisintegration.objects.studio.Analysis>> mapPackageSAnalysis;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * 
	 * @throws Exception
	 */
	public UploadEventLogDialog(UIPluginContext context, final UploadEventLogParameter parameters, XLog log,
			DataIntegration di, Studio stu) throws Exception {
		//Celonis info
		String logName = XConceptExtension.instance().extractName(log) != null
				? XConceptExtension.instance().extractName(log).replaceAll(" ", "_").replace(".xes", "").replace(".",
						"_")
				: "Anonymous";
		int rowsNum = 50;
		int rowTable = 2;
		double[] rows = new double[rowsNum];
		for (int i = 0; i < rowTable - 1; i++) {
			rows[i] = TableLayoutConstants.FILL;
		}
		for (int i = rowTable - 1; i < rowsNum; i++) {
			if (i == 7) {
				rows[i] = TableLayoutConstants.PREFERRED;
			} else {
				rows[i] = TableLayoutConstants.MINIMUM;
			}
		}
		double size[][] = { { 180, 60, 90, 60, 180, 190 }, rows };
		setLayout(new TableLayout(size));

		dataPoolNames = new ArrayList<String>();
		dataModelNames = new ArrayList<String>();
		tableNames = new ArrayList<String>();
		dataModels = new ArrayList<DataModel>();
		wsNames = new ArrayList<String>();
		workspaces = new ArrayList<Workspace>();
		anaNames = new ArrayList<String>();
		currentDataPool = "";
		currentDataModel = new DataModel();
		currentWorkspace = new Workspace();

		/* ---- studio init ---- */
		if (stu.getListEditableSpaces().size() > 0) {
			this.spaces = stu.getListEditableSpaces();
			this.mapSpacePackage = stu.getMapSpace();
			this.mapPackageSAnalysis = stu.getMapPackage();
			Space sp = this.spaces.get(0);
			this.curPackages = this.mapSpacePackage.get(sp);
			if (this.curPackages.size() > 0) {
				Package p = this.curPackages.get(0);
				this.curSAnalyses = this.mapPackageSAnalysis.get(p);
			} else {
				this.curSAnalyses = new ArrayList<>();
			}

		} else {
			this.spaces = new ArrayList<>();
			this.curPackages = new ArrayList<>();
			this.curSAnalyses = new ArrayList<>();
			this.mapSpacePackage = new HashMap<>();
			this.mapPackageSAnalysis = new HashMap<>();
		}
		/* ---- studio init ---- */
		/*---- studio panel ----*/
		JPanel studioTab = new JPanel();
		double studioTabSize[][] = { { 544, 60, 90, 60, 6 },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM,
						TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM,
						TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM } };
		studioTab.setLayout(new TableLayout(studioTabSize));
		// package, ana
		CardLayout layoutCardSAnalysis = new CardLayout();
		JPanel cardSAnalysisCont = new JPanel();
		cardSAnalysisCont.setLayout(layoutCardSAnalysis);

		JButton bSAnalysisNew = new JButton("New");
		bSAnalysisNew.setBackground(new Color(80, 0, 0));
		bSAnalysisNew.setForeground(new Color(240, 240, 240));
		ProMTextField sAnalysisField = new ProMTextField("_ANALYSIS_" + logName);
		cardSAnalysisCont.add(sAnalysisField, "2");
		layoutCardSAnalysis.show(cardSAnalysisCont, "2");

		// Package layout
		CardLayout layoutCardPackage = new CardLayout();
		JPanel cardPackageCont = new JPanel();
		cardPackageCont.setLayout(layoutCardPackage);
		JButton bPackageNew = new JButton("New");
		bPackageNew.setBackground(new Color(40, 40, 40));
		bPackageNew.setForeground(new Color(210, 210, 210));
		JButton bPackageSelect = new JButton("Replace");
		bPackageSelect.setBackground(new Color(40, 40, 40));
		bPackageSelect.setForeground(new Color(210, 210, 210));
		bPackageSelect.setVisible(false);
		JButton bPackageAdd = new JButton("Add");
		bPackageAdd.setBackground(new Color(40, 40, 40));
		bPackageAdd.setForeground(new Color(210, 210, 210));
		bPackageAdd.setVisible(false);
		ProMComboBox<Package> packageCombo = new ProMComboBox<Package>(curPackages);
		ProMTextField packageField = new ProMTextField("_PACKAGE_" + logName);

		cardPackageCont.add(packageCombo, "1");
		cardPackageCont.add(packageField, "2");
		layoutCardPackage.show(cardPackageCont, "2");
		// package key
		JPanel cardPackageKeyCont = new JPanel();
		CardLayout layoutCardPackageKey = new CardLayout();
		cardPackageKeyCont.setLayout(layoutCardPackageKey);
		ProMTextField packageKeyFieldDisplay = new ProMTextField("");
		packageKeyFieldDisplay.setEditable(false);
		if (packageCombo.getItemCount() > 0) {
			packageKeyFieldDisplay.setText(((Package) packageCombo.getSelectedItem()).getKey());
		}
		ProMTextField packageKeyFieldEdit = new ProMTextField("_PACKAGE_" + logName);
		
		cardPackageKeyCont.add(packageKeyFieldDisplay, "1");
		cardPackageKeyCont.add(packageKeyFieldEdit, "2");
		layoutCardPackageKey.show(cardPackageKeyCont, "2");

		// space Layout
		CardLayout layoutCardSpace = new CardLayout();
		JPanel cardSpaceCont = new JPanel();
		cardSpaceCont.setLayout(layoutCardSpace);

		JButton bSpaceNew = new JButton("New");
		bSpaceNew.setBackground(new Color(40, 40, 40));
		bSpaceNew.setForeground(new Color(210, 210, 210));
		JButton bSpaceSelect = new JButton("Replace");
		bSpaceSelect.setBackground(new Color(40, 40, 40));
		bSpaceSelect.setForeground(new Color(210, 210, 210));
		JButton bSpaceAdd = new JButton("Add");
		bSpaceAdd.setBackground(new Color(40, 40, 40));
		bSpaceAdd.setForeground(new Color(210, 210, 210));

		ProMComboBox<Space> spaceCombo = new ProMComboBox<Space>(spaces);
		ProMTextField spaceField = new ProMTextField("_SPACE_" + logName);
		cardSpaceCont.add(spaceCombo, "1");
		cardSpaceCont.add(spaceField, "2");
		layoutCardSpace.show(cardSpaceCont, "2");
		JLabel space = new JLabel("Space name:");
		JLabel sPackageName = new JLabel("Package name:");
		JLabel sPackageKey = new JLabel("Package key:");
		JLabel sAna = new JLabel("Analysis name:");

		/*---- default ----*/
		parameters.setSpaceStatus(SpaceStatus.NEW);
		parameters.setSpaceNew(spaceField.getText());
		parameters.setPackageStatus(PackageStatus.NEW);
		parameters.setPackageNameNew(packageField.getText());
		parameters.setPackageKeyNew(packageKeyFieldEdit.getText());
		parameters.setsAnalysisStatus(SAnalysisStatus.NEW);
		parameters.setsAnalysisNew(sAnalysisField.getText());
		/*---- event listener for buttons ----*/
		// new space
		bSpaceNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButton(bSpaceNew, new ArrayList<JButton>() {
					{
						add(bSpaceSelect);
						add(bSpaceAdd);
					}
				});
				highlightButton(bPackageNew, new ArrayList<JButton>() {
					{
						add(bPackageSelect);
						add(bPackageAdd);
					}
				});
				highlightButton(bSAnalysisNew, new ArrayList<JButton>());
				parameters.setSpaceStatus(SpaceStatus.NEW);
				parameters.setPackageStatus(PackageStatus.NEW);
				bPackageSelect.setVisible(false);
				bPackageAdd.setVisible(false);
				layoutCardSpace.show(cardSpaceCont, "2");
				layoutCardPackage.show(cardPackageCont, "2");
				layoutCardPackageKey.show(cardPackageKeyCont, "2");
				layoutCardSAnalysis.show(cardSAnalysisCont, "2");
			}
		}

		);
		// replace space
		bSpaceSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButton(bSpaceSelect, new ArrayList<JButton>() {
					{
						add(bSpaceNew);
						add(bSpaceAdd);
					}
				});
				highlightButton(bPackageNew, new ArrayList<JButton>() {
					{
						add(bPackageSelect);
						add(bPackageAdd);
					}
				});
				highlightButton(bSAnalysisNew, new ArrayList<JButton>() );
				parameters.setSpaceStatus(SpaceStatus.REPLACE);
				parameters.setPackageStatus(PackageStatus.NEW);
				parameters.setSpaceCombo((Space) spaceCombo.getSelectedItem());
				bPackageSelect.setVisible(false);
				bPackageAdd.setVisible(false);
				layoutCardSpace.show(cardSpaceCont, "1");
				layoutCardPackage.show(cardPackageCont, "2");
				layoutCardPackageKey.show(cardPackageKeyCont, "2");
				layoutCardSAnalysis.show(cardSAnalysisCont, "2");
			}
		}

		);
		// add space
		bSpaceAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButton(bSpaceAdd, new ArrayList<JButton>() {
					{
						add(bSpaceNew);
						add(bSpaceSelect);
					}
				});
				parameters.setSpaceStatus(SpaceStatus.ADD);
				parameters.setSpaceCombo((Space) spaceCombo.getSelectedItem());
				bPackageSelect.setVisible(true);
				bPackageAdd.setVisible(true);
				layoutCardSpace.show(cardSpaceCont, "1");
			}
		}

		);
		// new package
		bPackageNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButton(bPackageNew, new ArrayList<JButton>() {
					{
						add(bPackageSelect);
						add(bPackageAdd);
					}
				});
				highlightButton(bSAnalysisNew, new ArrayList<JButton>());
				parameters.setPackageStatus(PackageStatus.NEW);
				layoutCardPackage.show(cardPackageCont, "2");
				layoutCardPackageKey.show(cardPackageKeyCont, "2");
				layoutCardSAnalysis.show(cardSAnalysisCont, "2");
			}
		}

		);
		// replace package
		bPackageSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButton(bPackageSelect, new ArrayList<JButton>() {
					{
						add(bPackageNew);
						add(bPackageAdd);
					}
				});
				highlightButton(bSAnalysisNew, new ArrayList<JButton>());
				parameters.setPackageStatus(PackageStatus.REPLACE);
				parameters.setPackageCombo((Package) packageCombo.getSelectedItem());
				layoutCardPackage.show(cardPackageCont, "1");
				layoutCardPackageKey.show(cardPackageKeyCont, "1");
				layoutCardSAnalysis.show(cardSAnalysisCont, "2");
			}
		}

		);
		// add package
		bPackageAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				highlightButton(bPackageAdd, new ArrayList<JButton>() {
					{
						add(bPackageNew);
						add(bPackageSelect);
					}
				});
				parameters.setPackageStatus(PackageStatus.ADD);
				parameters.setPackageCombo((Package) packageCombo.getSelectedItem());
				layoutCardPackage.show(cardPackageCont, "1");
				layoutCardPackageKey.show(cardPackageKeyCont, "1");
			}
		}

		);
		// new analysis
		bSAnalysisNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				highlightButton(bSAnalysisNew, new ArrayList<JButton>());
				parameters.setsAnalysisStatus(SAnalysisStatus.NEW);
				layoutCardSAnalysis.show(cardSAnalysisCont, "2");
			}

		});


		// space combo
		spaceCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (spaceCombo.getItemCount() > 0) {
					Space sp = (Space) spaceCombo.getSelectedItem();
					parameters.setSpaceCombo(sp);
					curPackages = mapSpacePackage.get(sp);

					packageKeyFieldDisplay.setText("");
					if (curPackages.size() > 0) {
						Package p = curPackages.get(0);
						parameters.setPackageCombo(p);
						packageKeyFieldDisplay.setText(p.getKey());
						curSAnalyses = mapPackageSAnalysis.get(p);
					}
					packageCombo.removeAllItems();
					packageCombo.addAllItems(curPackages);
				}

			}

		});
		// space text field
		spaceField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setSpaceNew(spaceField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setSpaceNew(spaceField.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setSpaceNew(spaceField.getText());
			}
		});
		// package combo
		packageCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (packageCombo.getItemCount() > 0) {
					Package p = (Package) packageCombo.getSelectedItem();
					parameters.setPackageCombo(p);
					packageKeyFieldDisplay.setText(p.getKey());
					curSAnalyses = mapPackageSAnalysis.get(p);
				}

			}
		});
		// packge text field
		packageField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setPackageNameNew(packageField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setPackageNameNew(packageField.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setPackageNameNew(packageField.getText());
			}
		});
		// package key text field 
		packageKeyFieldEdit.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setPackageKeyNew(packageKeyFieldEdit.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setPackageKeyNew(packageKeyFieldEdit.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setPackageKeyNew(packageKeyFieldEdit.getText());
			}
		});
		// ana text field
		sAnalysisField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				parameters.setsAnalysisNew(sAnalysisField.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				parameters.setsAnalysisNew(sAnalysisField.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				parameters.setsAnalysisNew(sAnalysisField.getText());
			}
		});
		/*---- event listener for buttons ----*/

		/*---- paint the studio panel ----*/
		if (stu.isCanCreateSpace()) {
			studioTab.add(space, "0, 0");
			studioTab.add(bSpaceNew, "1, 0");
			studioTab.add(cardSpaceCont, " 0, 1, 3, 1");
			highlightButton(bSpaceNew, new ArrayList<JButton>());
			if (stu.isCanReplaceSpace() && stu.getListEditableSpaces().size() > 0) {
				studioTab.add(bSpaceSelect, "2, 0");
				studioTab.add(bSpaceAdd, "3, 0");
				studioTab.add(bPackageSelect, "2, 2");
				studioTab.add(bPackageAdd, "3, 2");
			}

			else {
				// new + add space feature
				if (stu.getListEditableSpaces().size() > 0) {
					studioTab.add(bSpaceAdd, "3, 0");
					studioTab.add(bPackageSelect, "2, 2");
					studioTab.add(bPackageAdd, "3, 2");

				}
			}
			studioTab.add(sPackageName, "0, 2");
			studioTab.add(bPackageNew, "1, 2");
			studioTab.add(cardPackageCont, " 0, 3, 3, 3");
			studioTab.add(sPackageKey, "0, 4");
			studioTab.add(cardPackageKeyCont, " 0, 5, 3, 5");
			studioTab.add(sAna, "0, 6");
			studioTab.add(bSAnalysisNew, "1, 6");
			studioTab.add(cardSAnalysisCont, "0, 7, 3, 7");
			highlightButton(bPackageNew, new ArrayList<JButton>());
			highlightButton(bSAnalysisNew, new ArrayList<JButton>());

		} else {
			// add space feature

			if (stu.getListEditableSpaces().size() > 0) {
				studioTab.add(space, "0, 0");
				studioTab.add(cardSpaceCont, " 0, 1, 3, 1");
				studioTab.add(bSpaceAdd, "3, 0");
				studioTab.add(sPackageName, "0, 2");
				studioTab.add(bPackageNew, "1, 2");
				studioTab.add(bPackageSelect, "2, 2");
				studioTab.add(bPackageAdd, "3, 2");
				studioTab.add(cardPackageCont, " 0, 3, 3, 3");
				studioTab.add(sPackageKey, "0, 4");
				studioTab.add(cardPackageKeyCont, " 0, 5, 3, 5");
				studioTab.add(sAna, "0, 6");
				studioTab.add(bSAnalysisNew, "1, 6");
				studioTab.add(cardSAnalysisCont, "0, 7, 3, 7");
				highlightButton(bSpaceAdd, new ArrayList<JButton>());
				highlightButton(bPackageNew, new ArrayList<JButton>());
				highlightButton(bSAnalysisNew, new ArrayList<JButton>());
				layoutCardSpace.show(cardSpaceCont, "1");
				bPackageSelect.setVisible(true);
				bPackageAdd.setVisible(true);
				parameters.setSpaceStatus(SpaceStatus.ADD);
				parameters.setSpaceCombo((Space) spaceCombo.getSelectedItem());
			}
			// no studio permission
			else {
				JLabel noP = new JLabel("No permission for Space component");
				parameters.setSpaceStatus(SpaceStatus.FORBIDDEN);
				studioTab.add(noP, "0,0,0,4");
			}
		}

		studioTab.setBackground(Color.WHITE);
		/*---- paint the studio panel ----*/

		/*---- studio panel ----*/
		updateDataPool(di);
		if (dataPoolNames.size() > 0) {
			currentDataPool = dataPoolNames.get(0);
		}
		updateDataModel(di);
		if (dataModelNames.size() > 0) {
			currentDataModel = dataModels.get(0);
		}
		updateTable(di);

		// workspace
		updateWorkspace(di);
		if (workspaces.size() > 0) {
			currentWorkspace = workspaces.get(0);
		}
		updateAnalysis(di);

		// ANA Layout
		CardLayout layoutCardAnalysis = new CardLayout();
		JPanel cardAnalysisCont = new JPanel();
		cardAnalysisCont.setLayout(layoutCardAnalysis);

		JButton bAnalysisNew = new JButton("New");
		bAnalysisNew.setBackground(new Color(80, 0, 0));
		bAnalysisNew.setForeground(new Color(240, 240, 240));
		JButton bAnalysisSelect = new JButton("Replace");
		bAnalysisSelect.setBackground(new Color(40, 40, 40));
		bAnalysisSelect.setForeground(new Color(210, 210, 210));
		bAnalysisNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardAnalysis.show(cardAnalysisCont, "2");
				parameters.setAnalysisStatus(AnalysisStatus.NEW);
				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));
			}

		});
		bAnalysisSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardAnalysis.show(cardAnalysisCont, "1");
				parameters.setAnalysisStatus(AnalysisStatus.REPLACE);
				bAnalysisSelect.setBackground(new Color(80, 0, 0));
				bAnalysisSelect.setForeground(new Color(240, 240, 240));
				bAnalysisNew.setBackground(new Color(40, 40, 40));
				bAnalysisNew.setForeground(new Color(210, 210, 210));
			}

		});

		ProMComboBox<String> anaCombo = new ProMComboBox<String>(anaNames);
		String anaCol = (String) anaCombo.getSelectedItem();
		parameters.setAnalysisReplace(anaCol);
		anaCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				cb.getSelectedItem();
				parameters.setAnalysisReplace(anaCol);
			}
		});

		ProMTextField anaField = new ProMTextField("_ANALYSIS_" + logName);
		parameters.setAnalysis(anaField.getText());
		parameters.setAnalysisStatus(AnalysisStatus.NEW);
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
		cardAnalysisCont.add(anaCombo, "1");
		cardAnalysisCont.add(anaField, "2");
		layoutCardAnalysis.show(cardAnalysisCont, "2");

		// WS layout
		CardLayout layoutCardWorkspace = new CardLayout();

		JPanel cardWorkspaceCont = new JPanel();
		cardWorkspaceCont.setLayout(layoutCardWorkspace);

		JButton bWorkspaceNew = new JButton("New");
		bWorkspaceNew.setBackground(new Color(40, 40, 40));
		bWorkspaceNew.setForeground(new Color(210, 210, 210));
		JButton bWorkspaceSelect = new JButton("Replace");
		bWorkspaceSelect.setBackground(new Color(40, 40, 40));
		bWorkspaceSelect.setForeground(new Color(210, 210, 210));
		JButton bWorkspaceAdd = new JButton("Add");
		bWorkspaceAdd.setBackground(new Color(80, 0, 0));
		bWorkspaceAdd.setForeground(new Color(240, 240, 240));

		bWorkspaceNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				layoutCardWorkspace.show(cardWorkspaceCont, "2");
				layoutCardAnalysis.show(cardAnalysisCont, "2");
				parameters.setTableStatus(TableStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.NEW);
				bWorkspaceNew.setBackground(new Color(80, 0, 0));
				bWorkspaceNew.setForeground(new Color(240, 240, 240));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));
				bWorkspaceAdd.setBackground(new Color(40, 40, 40));
				bWorkspaceAdd.setForeground(new Color(210, 210, 210));
				bAnalysisSelect.setVisible(false);

			}

		});
		bWorkspaceSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardWorkspace.show(cardWorkspaceCont, "1");
				layoutCardAnalysis.show(cardAnalysisCont, "2");
				parameters.setAnalysisStatus(AnalysisStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.REPLACE);
				bWorkspaceSelect.setBackground(new Color(80, 0, 0));
				bWorkspaceSelect.setForeground(new Color(240, 240, 240));
				bWorkspaceNew.setBackground(new Color(40, 40, 40));
				bWorkspaceNew.setForeground(new Color(210, 210, 210));
				bWorkspaceAdd.setBackground(new Color(40, 40, 40));
				bWorkspaceAdd.setForeground(new Color(210, 210, 210));
				bAnalysisSelect.setVisible(false);
			}

		});
		bWorkspaceAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardWorkspace.show(cardWorkspaceCont, "1");
				layoutCardAnalysis.show(cardAnalysisCont, "2");
				parameters.setAnalysisStatus(AnalysisStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.ADD);
				bWorkspaceAdd.setBackground(new Color(80, 0, 0));
				bWorkspaceAdd.setForeground(new Color(240, 240, 240));
				bWorkspaceNew.setBackground(new Color(40, 40, 40));
				bWorkspaceNew.setForeground(new Color(210, 210, 210));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bAnalysisSelect.setVisible(true);
			}

		});

		ProMComboBox<String> wsCombo = new ProMComboBox<String>(wsNames);
		String wsCol = (String) wsCombo.getSelectedItem();
		parameters.setWorkspaceReplace(wsCol);
		parameters.setWorkspaceStatus(WorkspaceStatus.ADD);
		wsCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProMComboBox<String> cb = (ProMComboBox<String>) e.getSource();
				String dmC = (String) cb.getSelectedItem();
				parameters.setWorkspaceReplace(dmC);
				for (Workspace ws : di.getWorkspaces()) {
					if (ws.getName() == dmC) {
						currentWorkspace = ws;
						break;
					}
				}

				anaNames = new ArrayList<String>();
				updateAnalysis(di);
				anaCombo.removeAllItems();
				anaCombo.addAllItems(anaNames);
			}
		});

		ProMTextField wsField = new ProMTextField("_WORKSPACE_" + logName);
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
		cardWorkspaceCont.add(wsCombo, "1");
		cardWorkspaceCont.add(wsField, "2");
		layoutCardWorkspace.show(cardWorkspaceCont, "1");

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
				layoutCardWorkspace.show(cardWorkspaceCont, "2");
				layoutCardAnalysis.show(cardAnalysisCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.NEW);
				parameters.setAnalysisStatus(AnalysisStatus.NEW);

				bDataModelNew.setBackground(new Color(80, 0, 0));
				bDataModelNew.setForeground(new Color(240, 240, 240));
				bDataModelSelect.setBackground(new Color(40, 40, 40));
				bDataModelSelect.setForeground(new Color(210, 210, 210));
				bDataModelAdd.setBackground(new Color(40, 40, 40));
				bDataModelAdd.setForeground(new Color(210, 210, 210));

				bWorkspaceNew.setBackground(new Color(80, 0, 0));
				bWorkspaceNew.setForeground(new Color(240, 240, 240));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));
				bWorkspaceAdd.setBackground(new Color(40, 40, 40));
				bWorkspaceAdd.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(false);
				bWorkspaceSelect.setVisible(false);
				bWorkspaceAdd.setVisible(false);
				bAnalysisSelect.setVisible(false);

			}

		});
		bDataModelSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataModel.show(cardDataModelCont, "1");
				layoutCardTable.show(cardTableCont, "2");
				layoutCardWorkspace.show(cardWorkspaceCont, "2");
				layoutCardAnalysis.show(cardAnalysisCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.REPLACE);
				parameters.setWorkspaceStatus(WorkspaceStatus.NEW);
				parameters.setAnalysisStatus(AnalysisStatus.NEW);

				bDataModelSelect.setBackground(new Color(80, 0, 0));
				bDataModelSelect.setForeground(new Color(240, 240, 240));
				bDataModelNew.setBackground(new Color(40, 40, 40));
				bDataModelNew.setForeground(new Color(210, 210, 210));
				bDataModelAdd.setBackground(new Color(40, 40, 40));
				bDataModelAdd.setForeground(new Color(210, 210, 210));

				bWorkspaceNew.setBackground(new Color(80, 0, 0));
				bWorkspaceNew.setForeground(new Color(240, 240, 240));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));
				bWorkspaceAdd.setBackground(new Color(40, 40, 40));
				bWorkspaceAdd.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(false);
				bWorkspaceSelect.setVisible(false);
				bWorkspaceAdd.setVisible(false);
				bAnalysisSelect.setVisible(false);
			}

		});
		bDataModelAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataModel.show(cardDataModelCont, "1");
				layoutCardTable.show(cardTableCont, "2");
				layoutCardWorkspace.show(cardWorkspaceCont, "1");
				layoutCardAnalysis.show(cardAnalysisCont, "2");

				parameters.setDataModelStatus(DataModelStatus.ADD);
				parameters.setTableStatus(TableStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.ADD);
				parameters.setAnalysisStatus(AnalysisStatus.NEW);

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

				bWorkspaceAdd.setBackground(new Color(80, 0, 0));
				bWorkspaceAdd.setForeground(new Color(240, 240, 240));
				bWorkspaceNew.setBackground(new Color(40, 40, 40));
				bWorkspaceNew.setForeground(new Color(210, 210, 210));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(true);
				bWorkspaceSelect.setVisible(true);
				bWorkspaceAdd.setVisible(true);
				bAnalysisSelect.setVisible(true);

			}

		});

		ProMComboBox<String> dmCombo = new ProMComboBox<String>(dataModelNames);
		String dmCol = (String) dmCombo.getSelectedItem();
		parameters.setDataModelReplace(dmCol);
		parameters.setDataModelStatus(DataModelStatus.ADD);
		dmCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("dm listen");
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
				wsNames = new ArrayList<String>();
				updateWorkspace(di);
				tableCombo.removeAllItems();
				tableCombo.addAllItems(tableNames);
				wsCombo.removeAllItems();
				wsCombo.addAllItems(wsNames);
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
				layoutCardWorkspace.show(cardWorkspaceCont, "2");
				layoutCardAnalysis.show(cardAnalysisCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.NEW);
				parameters.setDataPoolStatus(DataPoolStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.NEW);
				parameters.setAnalysisStatus(AnalysisStatus.NEW);

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

				bWorkspaceNew.setBackground(new Color(80, 0, 0));
				bWorkspaceNew.setForeground(new Color(240, 240, 240));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));
				bWorkspaceAdd.setBackground(new Color(40, 40, 40));
				bWorkspaceAdd.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(false);
				bDataModelSelect.setVisible(false);
				bDataModelAdd.setVisible(false);
				bWorkspaceSelect.setVisible(false);
				bWorkspaceAdd.setVisible(false);
				bAnalysisSelect.setVisible(false);
			}

		});
		bDataPoolSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				layoutCardDataPool.show(cardDataPoolCont, "1");
				layoutCardDataModel.show(cardDataModelCont, "2");
				layoutCardTable.show(cardTableCont, "2");
				layoutCardWorkspace.show(cardWorkspaceCont, "2");
				layoutCardAnalysis.show(cardAnalysisCont, "2");

				parameters.setTableStatus(TableStatus.NEW);
				parameters.setDataModelStatus(DataModelStatus.NEW);
				parameters.setDataPoolStatus(DataPoolStatus.REPLACE);
				parameters.setWorkspaceStatus(WorkspaceStatus.NEW);
				parameters.setAnalysisStatus(AnalysisStatus.NEW);

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

				bWorkspaceNew.setBackground(new Color(80, 0, 0));
				bWorkspaceNew.setForeground(new Color(240, 240, 240));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));
				bWorkspaceAdd.setBackground(new Color(40, 40, 40));
				bWorkspaceAdd.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bTableSelect.setVisible(false);
				bDataModelSelect.setVisible(false);
				bDataModelAdd.setVisible(false);
				bWorkspaceSelect.setVisible(false);
				bWorkspaceAdd.setVisible(false);
				bAnalysisSelect.setVisible(false);
			}

		});
		bDataPoolAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				layoutCardDataPool.show(cardDataPoolCont, "1");
				layoutCardDataModel.show(cardDataModelCont, "1");
				layoutCardTable.show(cardTableCont, "2");
				layoutCardWorkspace.show(cardWorkspaceCont, "1");
				layoutCardAnalysis.show(cardAnalysisCont, "2");

				parameters.setDataPoolStatus(DataPoolStatus.ADD);
				parameters.setDataModelStatus(DataModelStatus.ADD);
				parameters.setTableStatus(TableStatus.NEW);
				parameters.setWorkspaceStatus(WorkspaceStatus.ADD);
				parameters.setAnalysisStatus(AnalysisStatus.NEW);

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

				bWorkspaceAdd.setBackground(new Color(80, 0, 0));
				bWorkspaceAdd.setForeground(new Color(240, 240, 240));
				bWorkspaceNew.setBackground(new Color(40, 40, 40));
				bWorkspaceNew.setForeground(new Color(210, 210, 210));
				bWorkspaceSelect.setBackground(new Color(40, 40, 40));
				bWorkspaceSelect.setForeground(new Color(210, 210, 210));

				bAnalysisNew.setBackground(new Color(80, 0, 0));
				bAnalysisNew.setForeground(new Color(240, 240, 240));
				bAnalysisSelect.setBackground(new Color(40, 40, 40));
				bAnalysisSelect.setForeground(new Color(210, 210, 210));

				bDataModelSelect.setVisible(true);
				bDataModelAdd.setVisible(true);
				bTableSelect.setVisible(true);
				bWorkspaceSelect.setVisible(true);
				bWorkspaceAdd.setVisible(true);
				bAnalysisSelect.setVisible(true);
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
				System.out.println("dm remove all");
				dmCombo.removeAllItems();
				dmCombo.addAllItems(dataModelNames);

				tableNames = new ArrayList<String>();
				wsNames = new ArrayList<String>();
				if (dataModelNames.size() > 0) {
					for (DataModel dm : di.getDataModels()) {
						if (dm.getName() == dataModelNames.get(0)) {
							currentDataModel = dm;
							break;
						}
					}
					updateTable(di);
					updateWorkspace(di);
				}

				tableCombo.removeAllItems();
				tableCombo.addAllItems(tableNames);
				wsCombo.removeAllItems();
				wsCombo.addAllItems(wsNames);

			}
		});

		ProMTextField dpField = new ProMTextField("_DATAPOOL_" + logName);
		parameters.setDataPool(dpField.getText());
		//		parameters.setDataPoolStatus(DataPoolStatus.NEW);
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
		layoutCardDataPool.show(cardDataPoolCont, "1");

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

		// ######################################################################################
		// ######################################################################################
		// ######################################################################################

		JLabel tableName = new JLabel("Table name:");
		JLabel dataPool = new JLabel("Data Pool name:");
		JLabel dataModel = new JLabel("Data Model name:");
		JLabel workspace = new JLabel("Workspace name:");
		JLabel ana = new JLabel("Analysis name:");

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		double tabSize[][] = { { 544, 60, 90, 60, 6 },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM,
						TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM } };

		double configTabSize[][] = { { 374, 380, 6 },
				{ TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM,
						TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM, TableLayoutConstants.MINIMUM } };

		JPanel configTab = new JPanel();
		configTab.setLayout(new TableLayout(configTabSize));
		configTab.add(caseId, "0, 0");
		configTab.add(caseCombo, "0, 1");
		configTab.add(caseNewName, "1, 0");
		configTab.add(caseField, "1, 1");
		configTab.add(act, "0, 2");
		configTab.add(actCombo, "0, 3");
		configTab.add(actNewName, "1, 2");
		configTab.add(actField, "1, 3");
		configTab.add(timestamp, "0, 4");
		configTab.add(timeCombo, " 0, 5");
		configTab.add(timeNewName, "1, 4");
		configTab.add(timeField, "1, 5");
		configTab.setBackground(Color.WHITE);
		tabbedPane.addTab("Configuration", configTab);

		JPanel dataIntTab = new JPanel();
		dataIntTab.setLayout(new TableLayout(tabSize));
		dataIntTab.add(dataPool, "0, 0");
		dataIntTab.add(bDataPoolNew, "1, 0");
		dataIntTab.add(bDataPoolSelect, "2, 0");
		dataIntTab.add(bDataPoolAdd, "3, 0");
		dataIntTab.add(cardDataPoolCont, " 0, 1, 3, 1");
		dataIntTab.add(dataModel, "0, 2");
		dataIntTab.add(bDataModelNew, "1, 2");
		dataIntTab.add(bDataModelSelect, "2, 2");
		dataIntTab.add(bDataModelAdd, "3, 2");
		dataIntTab.add(cardDataModelCont, " 0, 3, 3, 3");
		dataIntTab.add(tableName, "0, 4");
		dataIntTab.add(bTableNew, "1, 4");
		dataIntTab.add(bTableSelect, "2, 4");
		dataIntTab.add(cardTableCont, "0, 5, 3, 5");
		dataIntTab.setBackground(Color.WHITE);
		tabbedPane.addTab("Data Integration", dataIntTab);

		tabbedPane.addTab("Studio", studioTab);

		JPanel processAnaTab = new JPanel();
		processAnaTab.setLayout(new TableLayout(tabSize));
		if (!di.getPermissionProcessAnalytics()) {
			JLabel noP = new JLabel("No permission for Process Analytics component");
			parameters.setWorkspaceStatus(WorkspaceStatus.FORBIDDEN);
			processAnaTab.add(noP, "0,0,0,4");
		} else {
			processAnaTab.add(workspace, "0, 0");
			processAnaTab.add(bWorkspaceNew, "1, 0");
			processAnaTab.add(bWorkspaceSelect, "2, 0");
			processAnaTab.add(bWorkspaceAdd, "3, 0");
			processAnaTab.add(cardWorkspaceCont, " 0, 1, 3, 1");
			processAnaTab.add(ana, "0, 2");
			processAnaTab.add(bAnalysisNew, "1, 2");
			processAnaTab.add(bAnalysisSelect, "2, 2");
			processAnaTab.add(cardAnalysisCont, "0, 3, 3, 3");
		}
		processAnaTab.setBackground(Color.WHITE);
		tabbedPane.addTab("Process Analytics", processAnaTab);
		tabbedPane.setPreferredSize(new Dimension(200, 250));
		add(tabbedPane, "0, " + Integer.toString(rowTable - 1) + ", 5, " + Integer.toString(rowTable - 1));
	}

	private void updateDataPool(DataIntegration di) {
		for (DataPool dp : di.getDataPools()) {
			dataPoolNames.add(dp.getName());
		}
	}

	private void updateDataModel(DataIntegration di) {
		for (DataModel dm : di.getDataModels()) {
			if (dm.getDp().getName().equals(currentDataPool)) {
				dataModelNames.add(dm.getName());
				dataModels.add(dm);
			}
		}

	}

	private void updateTable(DataIntegration di) {
		for (DataModelTable table : di.getDataModelTables()) {
			if (currentDataModel.getId() == table.getDmId()) {
				if (table.getTableType() == DataModelTableType.ACTIVITY) {
					tableNames.add(table.getName());
				}
			}
		}

	}

	private void updateWorkspace(DataIntegration di) {
		for (Workspace ws : di.getWorkspaces()) {
			if (ws.getDmId().equals(currentDataModel.getId())) {
				wsNames.add(ws.getName());
				workspaces.add(ws);
			}

		}
	}

	private void updateAnalysis(DataIntegration di) {
		for (Analysis ana : di.getAnalyses()) {
			if (ana.getWorkspace().getId() == currentWorkspace.getId()) {
				anaNames.add(ana.getName());
			}
		}

	}

	private void highlightButton(JButton highlight, List<JButton> lowlight) {
		highlight.setBackground(new Color(80, 0, 0));
		highlight.setForeground(new Color(240, 240, 240));
		for (JButton b : lowlight) {
			b.setBackground(new Color(40, 40, 40));
			b.setForeground(new Color(210, 210, 210));
		}

	}

}
