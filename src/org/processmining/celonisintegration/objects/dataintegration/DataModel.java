package org.processmining.celonisintegration.objects.dataintegration;

import java.util.ArrayList;
import java.util.List;


public class DataModel {
	private String name;
	private String id;
	private DataPool dp;
	private List<DataModelTable> tables;

	public DataModel(String name, String id, DataPool dp, List<DataModelTable> tables) {
		this.name = name;
		this.id = id;
		this.dp = dp;
		this.tables = tables;
	}

	public DataModel() {
		this.name = "";
		this.id = "";
		this.dp = new DataPool();
		this.tables = new ArrayList<DataModelTable>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DataPool getDp() {
		return dp;
	}

	public void setDp(DataPool dp) {
		this.dp = dp;
	}

	public List<DataModelTable> getTables() {
		return tables;
	}

	public void setTables(List<DataModelTable> tables) {
		this.tables = tables;
	}
}