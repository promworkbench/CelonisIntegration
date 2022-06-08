package org.processmining.celonisintegration.dialogs;

import java.io.IOException;

import javax.swing.JPanel;

import org.processmining.celonisintegration.parameters.PullTableDataModelParameter;
import org.processmining.contexts.uitopia.UIPluginContext;

import com.opencsv.exceptions.CsvValidationException;

public class PullTableDataPoolDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -60087716353524468L;

	/**
	 * The JPanel that allows the user to set (a subset of) the parameters.
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public PullTableDataPoolDialog(UIPluginContext context, final PullTableDataModelParameter parameters) throws CsvValidationException, IOException {
//		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL} };
//		setLayout(new TableLayout(size));
//		DefaultListModel<String> listTables = new DefaultListModel<String>();
//		
//		DataIntegration di = new DataIntegration(parameters.getUrl(), parameters.getToken());
//		List<String> dataPools = di.getDataPools();
//		for (String dp: dataPools) {
//			String dpId = di.getDataPoolIdByName(dp);
//			List<String> dataModels = di.getDataModels(dpId);
//			for (String dm: dataModels) {
//				String dmId = di.getDataModelIdByName(dm, dpId);
//				List<String> tables = di.getTables(dpId, dmId);
//				for (String table: tables) {
//					String res = dp + "/" + dm + "/" + table;
//					listTables.addElement(res);
//				}
//			}
//			
//		}
//		final ProMList<String> list = new ProMList<String>("Select a Table", listTables);
//		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		list.addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent e) {
//				List<String> selected = list.getSelectedValuesList();
//				if (selected.size() == 1) {
//					String r = selected.get(0);
//					String[] r1 = r.split("/");
//					System.out.println(r1[0]);
//					parameters.setDataPool(r1[0]);
//					parameters.setDataModel(r1[1]);
//					parameters.setTableName(r1[2]);
//					
//				} else {
//					try {
//						throw new Exception("Have to select a Table");
//					}
//					catch (Exception e1) {
//						
//					}
//					
//				}
//			}
//		});
//		
//		add(list, "0,0");
        
	}
}























