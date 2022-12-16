package org.processmining.celonisintegration.objects.dataintegration;



public class DataModelTable {
	private String name;
	private String id;
	private String dmId;
	private DataModelTableType tableType;
	
	public enum DataModelTableType {
		ACTIVITY, CASE, NORMAL
	}
	public DataModelTable(String name, String id, String dmId, DataModelTableType tableType) {
		this.name = name;
		this.id = id;
		this.dmId = dmId;
		this.tableType = tableType;
	}

	public DataModelTable() {
		this.name = "";
		this.id = "";
		this.dmId = "";
		this.tableType = null;
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

	public String getDmId() {
		return dmId;
	}

	public DataModelTableType getTableType() {
		return tableType;
	}

	public void setTableType(DataModelTableType tableType) {
		this.tableType = tableType;
	}

	public void setDmId(String dmId) {
		this.dmId = dmId;
	}

}
