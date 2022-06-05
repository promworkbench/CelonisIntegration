package org.processmining.celonisintegration.dialogs;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.celonisintegration.algorithms.ProcessRepository;
import org.processmining.celonisintegration.parameters.PullBpmnParameter;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ui.widgets.ProMList;

import com.opencsv.exceptions.CsvValidationException;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

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
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL} };
		setLayout(new TableLayout(size));
		DefaultListModel<String> listBpmns = new DefaultListModel<String>();
		
		ProcessRepository repo = new ProcessRepository(parameters.getUrl(), parameters.getToken());
		HashMap<String, List<String>> repoInfo = repo.getProcessRepoInfo();
		
		for (String cate: repoInfo.keySet()) {			
			for (String pm: repoInfo.get(cate)) {
				String name = cate + "/" + pm;
				listBpmns.addElement(name);
			}		
		}	
		final ProMList<String> list = new ProMList<String>("Select a Table", listBpmns);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				List<String> selected = list.getSelectedValuesList();
				if (selected.size() == 1) {
					String r = selected.get(0);
					String[] r1 = r.split("/");
					System.out.println(r1[0]);
					parameters.setCateName(r1[0]);
					parameters.setPmName(r1[1]);					
				} 
				else {
					try {
						throw new Exception("Have to select a Model");
					}
					catch (Exception e1) {						
					}
					
				}
			}
		});
		
		add(list, "0,0");

        
	}
}























