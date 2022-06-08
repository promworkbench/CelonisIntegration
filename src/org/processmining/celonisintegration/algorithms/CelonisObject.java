package org.processmining.celonisintegration.algorithms;

import java.util.ArrayList;
import java.util.List;

public class CelonisObject {

	public class DataPool {
		private String name;
		private String id;

		public DataPool(String name, String id) {
			this.name = name;
			this.id = id;
		}

		public DataPool() {
			this.name = "";
			this.id = "";
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
	}

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
	
	public enum DataModelTableType{
		ACTIVITY,
		CASE,
		NORMAL
	}
	
	public class DataModelTable {
		private String name;
		private String id;		
		private String dmId;
		private DataModelTableType tableType;

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

	public class Workspace {
		private String name;
		private String id;
		private String dmId;

		public Workspace(String name, String id, String dmId) {
			this.name = name;
			this.id = id;
			this.dmId = dmId;
		}

		public Workspace() {
			// TODO Auto-generated constructor stub
			this.name = "";
			this.id = "";
			this.dmId = "";
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

		public void setDmId(String dmId) {
			this.dmId = dmId;
		}
		

	}

	public class Analysis {
		private String name;
		private String id;
		private Workspace workspace;

		public Analysis(String name, String id, Workspace workspace) {
			this.name = name;
			this.id = id;
			this.workspace = workspace;
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

		public Workspace getWorkspace() {
			return workspace;
		}

		public void setWorkspace(Workspace workspace) {
			this.workspace = workspace;
		}

	}

	public class Sheet {
		private String name;
		private String id;
		private Analysis analysis;
		private String translatedName;

		public Sheet(String name, String translatedName, String id, Analysis analysis) {
			this.name = name;
			this.id = id;
			this.analysis = analysis;
			this.translatedName = translatedName;
		}

		public String getTranslatedName() {
			return translatedName;
		}

		public void setTranslatedName(String translatedName) {
			this.translatedName = translatedName;
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

		public Analysis getAnalysis() {
			return analysis;
		}

		public void setAnalysis(Analysis analysis) {
			this.analysis = analysis;
		}
	}

	public class OLAPTable {
		private String name;
		private String translatedName;
		private String id;
		private Sheet sheet;

		public OLAPTable(String name, String translatedName, String id, Sheet sheet) {
			this.name = name;
			this.id = id;
			this.sheet = sheet;
			this.translatedName = translatedName;
		}

		public String getTranslatedName() {
			return translatedName;
		}

		public void setTranslatedName(String translatedName) {
			this.translatedName = translatedName;
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

		public Sheet getSheet() {
			return sheet;
		}

		public void setSheet(Sheet sheet) {
			this.sheet = sheet;
		}
	}
}
