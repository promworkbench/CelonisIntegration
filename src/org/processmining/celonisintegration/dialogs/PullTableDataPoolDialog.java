package org.processmining.celonisintegration.dialogs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.celonisintegration.algorithms.CelonisObject.DataModel;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTableType;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataPool;
import org.processmining.celonisintegration.algorithms.DataIntegration;
import org.processmining.celonisintegration.parameters.PullTableDataModelParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;

import com.opencsv.exceptions.CsvValidationException;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class PullTableDataPoolDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * 
	 * @throws IOException
	 * @throws CsvValidationException
	 */
	public PullTableDataPoolDialog(UIPluginContext context, final PullTableDataModelParameter parameters)
			throws CsvValidationException, IOException {
		double size[][] = { { TableLayoutConstants.FILL, 160, 160 },
				{ TableLayoutConstants.FILL, TableLayoutConstants.MINIMUM } };
		setLayout(new TableLayout(size));
		DefaultListModel<String> listTables = new DefaultListModel<String>();

		DataIntegration di = new DataIntegration(parameters.getUrl(), parameters.getToken());
		List<DataPool> dataPools = di.getDataPools();
		for (DataPool dp : dataPools) {
			String dpId = di.getDataPoolId(dp.getName());
			List<DataModel> dataModels = di.getDataModels();
			for (DataModel dm : dataModels) {
				String dmId = di.getDataModelId(dp.getName(), dm.getName());
				List<DataModelTable> tables = di.getDataModelTables();
				for (DataModelTable table : tables) {
					if (table.getDmId().equals(dmId) && table.getTableType() == DataModelTableType.ACTIVITY) {
						String res = dp.getName() + "/" + dm.getName() + "/" + table.getName();
						listTables.addElement(res);
					}
				}
			}

		}
		final ProMList<String> list = new ProMList<String>("Select a Table", listTables);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					String r = selected.get(0);
					String[] r1 = r.split("/");
					System.out.println(r1[0]);
					parameters.setDataPool(r1[0]);
					parameters.setDataModel(r1[1]);
					parameters.setTableName(r1[2]);

				} else {
					try {
						throw new Exception("Have to select a Table");
					} catch (Exception e1) {

					}

				}
			}
		});
		parameters.setIsMerged(true);
		JLabel label = new JLabel("Do you want to merge with the corresponding case table?");
		JButton bYes = new JButton("Yes");
		bYes.setBackground(new Color(80, 0, 0));
		bYes.setForeground(new Color(240, 240, 240));
		JButton bNo = new JButton("No");
		bNo.setBackground(new Color(40, 40, 40));
		bNo.setForeground(new Color(210, 210, 210));
		bYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				parameters.setIsMerged(true);
				bYes.setBackground(new Color(80, 0, 0));
				bYes.setForeground(new Color(240, 240, 240));
				bNo.setBackground(new Color(40, 40, 40));
				bNo.setForeground(new Color(210, 210, 210));
			}

		});
		bNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				parameters.setIsMerged(false);
				bNo.setBackground(new Color(80, 0, 0));
				bNo.setForeground(new Color(240, 240, 240));
				bYes.setBackground(new Color(40, 40, 40));
				bYes.setForeground(new Color(210, 210, 210));
			}

		});

		

		add(list, "0, 0, 2, 0");
		add(label, "0, 1");
		add(bYes, "1, 1");
		add(bNo, "2, 1");

	}
}
