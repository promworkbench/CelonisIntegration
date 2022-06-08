package org.processmining.celonisintegration.dialogs;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.celonisintegration.algorithms.CelonisObject.Analysis;
import org.processmining.celonisintegration.algorithms.CelonisObject.OLAPTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.Sheet;
import org.processmining.celonisintegration.algorithms.CelonisObject.Workspace;
import org.processmining.celonisintegration.algorithms.ProcessAnalytics;
import org.processmining.celonisintegration.parameters.PullOlapTableParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class PullOlapTableWsDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PullOlapTableWsDialog(UIPluginContext context, final PullOlapTableParameter parameters) throws Exception {
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, 30, 30 } };
		setLayout(new TableLayout(size));

		DefaultListModel<String> listOlaps = new DefaultListModel<String>();
		ProcessAnalytics pa = new ProcessAnalytics(parameters.getUrl(), parameters.getToken());

		for (OLAPTable table : pa.getTables()) {
			Sheet sheet = table.getSheet();
			Analysis ana = sheet.getAnalysis();
			Workspace ws = ana.getWorkspace();
			String res = ws.getName() + " / " + ana.getName() + " / " + sheet.getTranslatedName() + " / " + table.getTranslatedName();
			listOlaps.addElement(res);
		}
		final ProMList<String> list = new ProMList<String>("Select an OLAP Table", listOlaps);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					String r = selected.get(0);
					String[] r1 = r.split(" / ");
					parameters.setWorkspaceName(r1[0]);
					parameters.setAnalysisName(r1[1]);
					parameters.setSheetName(r1[2]);
					parameters.setTableName(r1[3]);
				} else {
					try {
						throw new Exception("Have to select an OLAP Table");
					} catch (Exception e1) {

					}

				}
			}
		});

		add(list, "0,0");

	}
}
