
package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.processmining.celonisintegration.algorithms.CelonisObject.Analysis;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModel;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTableType;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataPool;
import org.processmining.celonisintegration.algorithms.CelonisObject.Workspace;
import org.processmining.framework.plugin.PluginContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class DataIntegration {
	private String url;
	private String apiToken;
	private List<Workspace> workspaces;
	private List<Analysis> analyses;
	private List<DataPool> dataPools;
	private List<DataModel> dataModels;
	private List<DataModelTable> dataModelTables;
	private Boolean permissionDataIntegration;
	private Boolean permissionProcessAnalytics;

	public static final String SERVICE_NAME_DATA_INTEGRATION = "event-collection";
	public static final String SERVICE_NAME_PROCESS_ANALYTICS = "process-analytics";
	public static final String PERMISSION_CREATE_DATA_POOL = "CREATE_DATA_POOL";
	public static final String PERMISSION_CREATE_WORKSPACE = "CREATE_WORKSPACE";

	public DataIntegration(String url, String apiToken) throws UserException {
		this.url = url;
		this.apiToken = apiToken;
		this.workspaces = new ArrayList<Workspace>();
		this.analyses = new ArrayList<Analysis>();
		this.dataPools = new ArrayList<DataPool>();
		this.dataModels = new ArrayList<DataModel>();
		this.dataModelTables = new ArrayList<DataModelTable>();
		this.permissionDataIntegration = true;
		this.permissionProcessAnalytics = true;

		this.updatePermission();
		if (this.permissionProcessAnalytics) {
			this.updateWorkspaces();
			this.updateAnalyses();
		}		
		this.updateDataPools();
		this.updateDataModels();
		
	}

	public Boolean getPermissionDataIntegration() {
		return permissionDataIntegration;
	}

	public void setPermissionDataIntegration(Boolean permissionDataIntegration) {
		this.permissionDataIntegration = permissionDataIntegration;
	}

	public Boolean getPermissionProcessAnalytics() {
		return permissionProcessAnalytics;
	}

	public void setPermissionProcessAnalytics(Boolean permissionProcessAnalytics) {
		this.permissionProcessAnalytics = permissionProcessAnalytics;
	}

	public List<Workspace> getWorkspaces() {
		return workspaces;
	}

	public void setWorkspaces(List<Workspace> workspaces) {
		this.workspaces = workspaces;
	}

	public List<Analysis> getAnalyses() {
		return analyses;
	}

	public void setAnalyses(List<Analysis> analyses) {
		this.analyses = analyses;
	}

	public List<DataPool> getDataPools() {
		return dataPools;
	}

	public void setDataPools(List<DataPool> dataPools) {
		this.dataPools = dataPools;
	}

	public List<DataModel> getDataModels() {
		return dataModels;
	}

	public void setDataModels(List<DataModel> dataModels) {
		this.dataModels = dataModels;
	}

	public List<DataModelTable> getDataModelTables() {
		return dataModelTables;
	}

	public void setDataModelTables(List<DataModelTable> dataModelTables) {
		this.dataModelTables = dataModelTables;
	}

	public String getDataPoolId(String dpName) {
		String res = "";
		for (DataPool dp : this.getDataPools()) {
			if (dp.getName().equals(dpName)) {
				return dp.getId();
			}
		}
		return res;
	}

	public String getDataModelId(String dpName, String dmName) {
		String res = "";
		for (DataModel dm : this.getDataModels()) {
			if (dm.getDp().getName().equals(dpName) && dm.getName().equals(dmName)) {
				return dm.getId();
			}
		}
		return res;
	}

	public String getDataModeTablelId(String dpName, String dmName, String table) {
		String res = "";
		DataModel dmO = new CelonisObject().new DataModel();
		for (DataModel dm : this.getDataModels()) {
			if (dm.getDp().getName().equals(dpName) && dm.getName().equals(dmName)) {
				dmO = dm;
			}
		}
		for (DataModelTable tab : this.getDataModelTables()) {
			if (dmO.getId().equals(tab.getDmId())) {
				return tab.getId();
			}
		}
		return res;
	}

	public String getWorkspaceId(String wsName) {
		String res = "";
		for (Workspace ws : this.getWorkspaces()) {
			if (ws.getName().equals(wsName)) {
				return ws.getId();
			}
		}
		return res;
	}

	public String getAnalysisId(String wsName, String anaName) {
		String res = "";
		for (Analysis ana : this.getAnalyses()) {
			if (ana.getWorkspace().getName().equals(wsName) && ana.getName().equals(anaName)) {
				return ana.getId();
			}
		}
		return res;
	}

	public void updatePermission() throws UserException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> r;

		String targetUrl = this.url + "/integration/api/pools";
		try {
			r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
		} catch (Exception e) {
			this.permissionDataIntegration = false;
		}
		if (!this.permissionDataIntegration) {
			throw new UserException("No permission to access Data Integration");
		} else {
			targetUrl = this.url + "/process-mining/api/processes";
			try {
				r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
			} catch (Exception e) {
				this.permissionProcessAnalytics = false;
			}
			if (this.permissionProcessAnalytics) {
				targetUrl = this.url + "/api/cloud/permissions";
				r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get permissions");
				JSONArray body = new JSONArray(r.getBody());
				Boolean perDI = false;
				Boolean perPA = false;
				for (int i = 0; i < body.length(); i++) {
					JSONObject perObject = body.getJSONObject(i);
					if (perObject.getString("serviceName").equals(SERVICE_NAME_DATA_INTEGRATION)) {
						JSONArray permissions = perObject.getJSONArray("permissions");
						for (int j = 0; j < permissions.length(); j++) {
							if (permissions.get(j).toString().equals(PERMISSION_CREATE_DATA_POOL)) {
								perDI = true;
							}
						}
					}
					if (perObject.getString("serviceName").equals(SERVICE_NAME_PROCESS_ANALYTICS)) {
						JSONArray permissions = perObject.getJSONArray("permissions");
						for (int j = 0; j < permissions.length(); j++) {
							if (permissions.get(j).toString().equals(PERMISSION_CREATE_WORKSPACE)) {
								perPA = true;
							}
						}
					}

				}
				if (!perDI) {
					this.permissionDataIntegration = false;
				}
				if (!perPA) {
					this.permissionProcessAnalytics = false;
				}
			}
		}

	}

	public void updateWorkspaces() throws UserException {
		String targetUrl = this.url + "/process-mining/api/processes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get workspaces");

		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject ws = body.getJSONObject(i);
			String name = ws.getString("name");
			String id = ws.getString("id");
			String dmId = ws.getString("dataModelId");
			this.workspaces.add(new CelonisObject().new Workspace(name, id, dmId));
		}
	}

	public void updateAnalyses() throws UserException {
		String targetUrl = this.url + "/process-mining/api/analysis";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get analyses");

		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject ana = body.getJSONObject(i);
			for (Workspace ws : workspaces) {
				if (ana.getString("parentObjectId").equals(ws.getId())) {
					String name = ana.getString("name");
					String id = ana.getString("id");
					this.analyses.add(new CelonisObject().new Analysis(name, id, ws));
					break;
				}
			}

		}
	}

	public void updateDataPools() throws UserException {
		String targetUrl = String.format(this.url + "/integration/api/pools");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get data pools");
		JSONArray body = new JSONArray(r.getBody());

		for (int i = 0; i < body.length(); i++) {
			JSONObject obj = body.getJSONObject(i);
			String name = obj.getString("name");
			String id = obj.getString("id");
			this.dataPools.add(new CelonisObject().new DataPool(name, id));
		}

	}

	public void updateDataModels() throws UserException {

		for (DataPool dp : this.dataPools) {
			String dpId = dp.getId();
			String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models";
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + this.apiToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> jobRequest = new HttpEntity<>(headers);
			ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get data models");
			JSONArray body = new JSONArray(r.getBody());

			for (int i = 0; i < body.length(); i++) {
				JSONObject obj = body.getJSONObject(i);
				String name = obj.getString("name");
				String id = obj.getString("id");
				JSONArray tablesJSON = obj.getJSONArray("tables");
				List<String> actTableId = new ArrayList<String>();
				List<String> caseTableId = new ArrayList<String>();
				if (obj.getJSONArray("processConfigurations").length() > 0) {
					for (int j = 0; j < obj.getJSONArray("processConfigurations").length(); j++) {
						JSONObject processConfig = obj.getJSONArray("processConfigurations").getJSONObject(j);
						if (processConfig.has("activityTableId")) {
							if (processConfig.get("activityTableId") != JSONObject.NULL) {
								actTableId.add(processConfig.getString("activityTableId"));
							}

						}

						if (processConfig.has("caseTableId")) {
							if (processConfig.get("caseTableId") != JSONObject.NULL) {
								caseTableId.add(processConfig.getString("caseTableId"));
							}

						}
					}

				}

				List<DataModelTable> tables = new ArrayList<DataModelTable>();

				for (int j = 0; j < tablesJSON.length(); j++) {
					String nameTable = tablesJSON.getJSONObject(j).getString("aliasOrName");
					String idTable = tablesJSON.getJSONObject(j).getString("id");
					DataModelTableType tableType = DataModelTableType.NORMAL;
					if (actTableId.contains(idTable)) {
						tableType = DataModelTableType.ACTIVITY;
					} else if (caseTableId.contains(idTable)) {
						tableType = DataModelTableType.CASE;
					}
					tables.add(new CelonisObject().new DataModelTable(nameTable, idTable, id, tableType));
					this.dataModelTables.add(new CelonisObject().new DataModelTable(nameTable, idTable, id, tableType));
				}
				this.dataModels.add(new CelonisObject().new DataModel(name, id, dp, tables));
			}
		}

	}

	public List<String[]> getForeignKey(String dpId, String dmId, String actTableName, String caseTableName)
			throws UserException {
		List<String[]> res = new ArrayList<String[]>();
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models/" + dmId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get foreign key");
		JSONObject body = new JSONObject(r.getBody());
		JSONArray foreignKeys = body.getJSONArray("foreignKeys");
		JSONArray tables = body.getJSONArray("tables");
		String caseTableId = "";
		String actTableId = "";

		for (int i = 0; i < tables.length(); i++) {
			JSONObject table = tables.getJSONObject(i);
			if (table.getString("aliasOrName").equals(actTableName)) {
				actTableId = table.getString("id");
			}
			if (table.getString("aliasOrName").equals(caseTableName)) {
				caseTableId = table.getString("id");
			}
		}

		if (foreignKeys.length() > 0) {
			for (int i = 0; i < foreignKeys.length(); i++) {
				JSONObject obj = foreignKeys.getJSONObject(i);
				if (obj.getString("sourceTableId").equals(actTableId)
						&& obj.getString("targetTableId").equals(caseTableId)) {
					JSONArray colsJson = obj.getJSONArray("columns");

					for (int j = 0; j < colsJson.length(); j++) {
						String[] cols = new String[2];
						JSONObject obj1 = colsJson.getJSONObject(j);
						cols[0] = obj1.getString("sourceColumnName");
						cols[1] = obj1.getString("targetColumnName");
						res.add(cols);
					}

				} else if (obj.getString("targetTableId").equals(actTableId)
						&& obj.getString("sourceTableId").equals(caseTableId)) {
					JSONArray colsJson = obj.getJSONArray("columns");

					for (int j = 0; j < colsJson.length(); j++) {
						String[] cols = new String[2];
						JSONObject obj1 = colsJson.getJSONObject(j);
						cols[1] = obj1.getString("sourceColumnName");
						cols[0] = obj1.getString("targetColumnName");
						System.out.println(cols[0]);
						System.out.println(cols[1]);
						res.add(cols);
					}

				}
			}
		}

		return res;

	}

	public String getCaseTableName(String dpId, String dmId, String tableName) throws UserException {
		String res = "";
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models/" + dmId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get case table name");
		JSONObject body = new JSONObject(r.getBody());
		JSONArray processConfigs = body.getJSONArray("processConfigurations");
		JSONArray tables = body.getJSONArray("tables");
		JSONObject processConfig = new JSONObject();
		String caseTableId = "";
		String actTableId = "";

		for (int i = 0; i < tables.length(); i++) {
			JSONObject table = tables.getJSONObject(i);
			if (table.getString("aliasOrName").equals(tableName)) {
				actTableId = table.getString("id");
				break;
			}
		}
		for (int i = 0; i < processConfigs.length(); i++) {
			JSONObject config = processConfigs.getJSONObject(i);
			if (config.getString("activityTableId").equals(actTableId)) {
				processConfig = config;
				break;
			}
		}

		if (processConfig.getString("caseTableId") != JSONObject.NULL) {
			caseTableId = processConfig.getString("caseTableId");
		}

		for (int i = 0; i < tables.length(); i++) {
			JSONObject table = tables.getJSONObject(i);
			if (table.getString("id").equals(caseTableId)) {
				return table.getString("aliasOrName");
			}
		}
		return res;

	}

	public String getCsv(PluginContext context, String dpId, String dmId, String tableName, Boolean isMerged,
			String caseTable, List<String> keyCaseCol) throws Exception {
		String queryId = this.getQueryId(context, dpId, dmId, tableName, isMerged, caseTable, keyCaseCol);
		String status = "";
		int delay = 0;
		while (true) {
			context.log("In queue...");
			status = this.getExportStatus(dmId, queryId);
			delay += 1;
			if (status.equals("DONE") || status.equals("FAILED") || delay == 80) {
				break;
			}
			TimeUnit.SECONDS.sleep(2);
		}
		if (status.equals("FAILED")) {
			throw new Exception(this.getExportMessage(dmId, queryId));
		}
		if (delay == 80) {
			throw new UserException("The table is too big");
		}
		context.log("Send query for the table done");
		context.getProgress().inc();
		context.log("Requesting to download table...");
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/" + queryId + "/result";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "Get CSV file");
		context.log("Request to download table done");
		context.getProgress().inc();
		context.log("Downloading the table...");
		String log = response.getBody();
		File tempFile = File.createTempFile("event_log_celonis_" + tableName, ".csv");
		XESUtils.writeToCsv(log, tempFile);
		context.log("Download the table done");
		return tempFile.getAbsolutePath().toString();
	}

	public String getExportMessage(String dmId, String queryId) throws UserException {
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/" + queryId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "Get export message");
		JSONObject body = new JSONObject(response.getBody());

		return body.getString("message");
	}

	public String getExportStatus(String dmId, String queryId) throws UserException {
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/" + queryId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "Get export status");
		JSONObject body = new JSONObject(response.getBody());

		return body.getString("exportStatus");
	}

	public String getQueryId(PluginContext context, String dpId, String dmId, String tableName, Boolean isMerged,
			String caseTable, List<String> keyCaseCol) throws UserException {
		String query = "";
		context.log("Extracting PQL query of the table...");
		if (isMerged) {
			query = this.getMergePullingQuery(dpId, dmId, tableName, caseTable, keyCaseCol);
		} else {
			query = this.getPullingQuery(dpId, dmId, tableName);
		}
		context.log("Extract PQL query of the table done");
		context.getProgress().inc();
		context.log("Sending query for the table...");
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/query";
		String payload = this.getPullingPayload(query);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> postRequest = new HttpEntity<String>(payload, headers);
		ResponseEntity<String> response = APIUtils.postStringRequest(targetUrl, postRequest, "Get query ID");
		JSONObject body = new JSONObject(response.getBody());
		return body.getString("id");

	}

	public String getPullingPayload(String query) {
		JSONArray query1 = new JSONArray();
		query1.put(query);

		JSONObject command = new JSONObject().put("queries", query1);
		JSONArray commands = new JSONArray().put(command);
		JSONObject request = new JSONObject().put("commands", commands);
		JSONObject payload = new JSONObject().put("dataCommand", request).put("exportType", "CSV");
		System.out.println(payload.toString());
		return payload.toString();
	}

	public String getPullingQuery(String dpId, String dmId, String tableName) throws UserException {
		String res = "TABLE( ";
		String tableId = this.getTableByName(tableName, dmId, dpId);
		List<String> cols = getColumns(dpId, dmId, tableId);
		String q = "";
		for (int i = 0; i < cols.size(); i++) {
			String col = cols.get(i);
			q += "\"";
			q += tableName;
			q += "\"";
			q += ".";
			q += "\"";
			q += col;
			q += "\"";
			q += " AS ";
			q += "\"";
			q += col;
			q += "\"";
			if (i != cols.size() - 1) {
				q += ", ";
			}
		}

		res += q;
		res += ") NOLIMIT;";

		return res;
	}

	public String getMergePullingQuery(String dpId, String dmId, String actTable, String caseTable,
			List<String> keyCaseCol) throws UserException {
		String res = "TABLE( ";
		String tableId = this.getTableByName(actTable, dmId, dpId);
		String caseTableId = this.getTableByName(caseTable, dmId, dpId);

		List<String> actCols = getColumns(dpId, dmId, tableId);
		List<String> caseCols = getColumns(dpId, dmId, caseTableId);
		String q = "";
		for (int i = 0; i < actCols.size(); i++) {
			String col = actCols.get(i);
			q += "\"";
			q += actTable;
			q += "\"";
			q += ".";
			q += "\"";
			q += col;
			q += "\"";
			q += " AS ";
			q += "\"";
			q += col;
			q += "\"";
			if (i != actCols.size() - 1) {
				q += ", ";
			} else {
				if (caseCols.size() != 0) {
					q += ", ";
				}
			}
		}
		for (int i = 0; i < caseCols.size(); i++) {
			String col = caseCols.get(i);
			System.out.println(col);
			if (!keyCaseCol.contains(col)) {
				System.out.println("### inside ###");
				System.out.println(col);
				q += "\"";
				q += caseTable;
				q += "\"";
				q += ".";
				q += "\"";
				q += col;
				q += "\"";
				q += " AS ";
				q += "\"";
				q += col;
				q += "\"";
				if (i != caseCols.size() - 1) {
					q += ", ";
				}
			}

		}

		res += q;
		res += ") NOLIMIT;";
		System.out.println(res);
		return res;
	}

	public List<String> getColumns(String dpId, String dmId, String tableId) throws UserException {
		List<String> res = new ArrayList<String>();
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-model/" + dmId + "/tables/" + tableId
				+ "/columns";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest,
				"Get columns in tables in data model");
		JSONArray body = new JSONArray(r.getBody());

		for (int i = 0; i < body.length(); i++) {
			JSONObject obj = body.getJSONObject(i);
			res.add(obj.getString("name"));
		}
		return res;
	}

	public void deleteDataPool(String dpId) throws UserException {
		String targetUrl = this.url + "/integration/api/pools/" + dpId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		APIUtils.deleteRequest(targetUrl, jobRequest, "Delete Data Pool");
	}

	public void deleteDataModel(String dpId, String dmId) throws UserException {
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models/" + dmId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		APIUtils.deleteRequest(targetUrl, jobRequest, "Delete Data Model");
	}

	public void deleteDataModelTable(String dpId, String dmId, String tableId) throws UserException {
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-model/" + dmId + "/tables/" + tableId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		APIUtils.deleteRequest(targetUrl, jobRequest, "Delete Table in Data Model");
	}

	public void deleteWorkspace(String wsId) throws UserException {
		String targetUrl = this.url + "/process-mining/api/processes" + wsId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		APIUtils.deleteRequest(targetUrl, jobRequest, "Delete Workspace");
	}

	public void deleteAnalysis(String anaId) throws UserException {
		String targetUrl = this.url + "/process-mining/api/analysis" + anaId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		APIUtils.deleteRequest(targetUrl, jobRequest, "Delete Analysis");
	}

	/**
	 * Create a new data pool in Celonis
	 * 
	 * @param name
	 * @return
	 * @throws UserException
	 */
	public String createDataPool(String name) throws UserException {
		String targetUrl = this.url + "/integration/api/pools";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", name);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Data Pool");

		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");
	}

	/**
	 * Create a new data model in Celonis
	 * 
	 * @param modelName
	 * @param dataPoolId
	 * @return
	 * @throws UserException
	 */
	public String createDataModel(String modelName, String dataPoolId) throws UserException {
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-models", dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", modelName);
		request.put("poolId", dataPoolId);
		request.put("configurationSkipped", true);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Data Model");
		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");
	}

	/**
	 * Create a new workspace in process analytics
	 * 
	 * @param dataModelId
	 * @param name
	 * @return
	 * @throws UserException
	 */
	public String createWorkspace(String dataModelId, String name) throws UserException {
		String targetUrl = this.url + "/process-mining/api/processes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", name);
		request.put("dataModelId", dataModelId);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Workspace");

		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");

	}

	public String createAnalysis(String workspaceId, String name) throws UserException {
		String targetUrl = this.url + "/process-mining/api/analysis";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", name);
		request.put("processId", workspaceId);
		request.put("applicationId", JSONObject.NULL);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Analysis");

		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");
	}

	/**
	 * Configure the data model
	 * 
	 * @param dataModelId
	 * @param dataPoolId
	 * @param actTable
	 * @param caseTable
	 * @param caseIdColumn
	 * @param actColumn
	 * @param timestampColumn
	 * @throws UserException
	 */
	public void addProcessConfiguration(String dataModelId, String dataPoolId, String actTable, String caseTable,
			String caseIdColumn, String actColumn, String timestampColumn) throws UserException {
		String actTableId = this.getTableByName(actTable, dataModelId, dataPoolId);
		String caseTableId = this.getTableByName(caseTable, dataModelId, dataPoolId);

		String targetUrl = String.format(
				this.url + "/integration/api/pools/%s/data-models/" + dataModelId + "/process-configurations",
				dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("activityTableId", actTableId);
		request.put("caseTableId", caseTableId);
		request.put("caseIdColumn", caseIdColumn);
		request.put("activityColumn", actColumn);
		request.put("timestampColumn", timestampColumn);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		APIUtils.putRequest(targetUrl, jobRequest, "Configure the data model");

	}

	/**
	 * Reload the data model
	 * 
	 * @param dataModelId
	 * @param dataPoolId
	 * @throws InterruptedException
	 * @throws UserException
	 * @throws JSONException
	 */
	public String reloadDataModel(String dataModelId, String dataPoolId)
			throws InterruptedException, JSONException, UserException {
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-models/" + dataModelId + "/reload",
				dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("forceComplete", true);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		APIUtils.postEntityWithPayloadRequest(targetUrl, jobRequest, "Reload data model");

		String status = "RUNNING";
		String message = "";

		while (status.equals("RUNNING")) {
			String loadUrl = String.format(
					this.url + "/integration/api/pools/%s/data-models/" + dataModelId + "/load-history/load-info-sync",
					dataPoolId);
			HttpEntity<String> loadRequest = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate1 = new RestTemplate();
			ResponseEntity<String> r = restTemplate1.exchange(loadUrl, HttpMethod.GET, loadRequest, String.class);

			JSONObject body = new JSONObject(r.getBody());
			status = body.getJSONObject("loadInfo").getJSONObject("currentComputeLoad").getString("loadStatus");
			if (!body.getJSONObject("loadInfo").getJSONObject("currentComputeLoad").get("message").equals(null) ) {
				message = body.getJSONObject("loadInfo").getJSONObject("currentComputeLoad").getString("message");
			}
			
			TimeUnit.SECONDS.sleep(3);

		}
		if (status.equals("ERROR")) {
			throw new UserException(message);
		} else {
			return message;
		}
	}

	/**
	 * Get a table ID via table name
	 * 
	 * @param tableName
	 * @param dataModelId
	 * @param dataPoolId
	 * @return
	 * @throws UserException
	 */
	public String getTableByName(String tableName, String dataModelId, String dataPoolId) throws UserException {
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-model/" + dataModelId + "/tables",
				dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get Table ID");
		JSONArray body = new JSONArray(r.getBody());

		String res = "";
		for (int i = 0; i < body.length(); i++) {
			JSONObject obj = body.getJSONObject(i);
			if (obj.getString("aliasOrName").equals(tableName)) {
				res = obj.getString("id");
				break;
			}
		}

		return res;

	}

	/**
	 * Set foreign keys between 2 tables with 2 given columns
	 * 
	 * @param table1
	 * @param column1
	 * @param table2
	 * @param column2
	 * @param dataModelId
	 * @param dataPoolId
	 * @throws UserException
	 */
	public void addForeignKeys(String table1, String column1, String table2, String column2, String dataModelId,
			String dataPoolId) throws UserException {
		String table1Id = this.getTableByName(table1, dataModelId, dataPoolId);
		String table2Id = this.getTableByName(table2, dataModelId, dataPoolId);

		String targetUrl = String.format(
				this.url + "/integration/api/pools/%s/data-models/" + dataModelId + "/foreign-keys", dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		JSONObject column = new JSONObject();
		column.put("sourceColumnName", column1);
		column.put("targetColumnName", column2);
		JSONArray columns = new JSONArray();
		columns.put(column);
		request.put("dataModelId", dataModelId);
		request.put("sourceTableId", table1Id);
		request.put("targetTableId", table2Id);
		request.put("columns", columns);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		APIUtils.postEntityWithPayloadRequest(targetUrl, jobRequest, "Add foreign keys");

	}

	/**
	 * Add a table to a given data pool via table name
	 * 
	 * @param tableName
	 * @param dataPoolId
	 * @param dataModelId
	 * @throws UserException
	 */
	public void addTableFromPool(String tableName, String dataPoolId, String dataModelId) throws UserException {
		//		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-model/" + dataModelId + "/tables",
				dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", tableName);
		request.put("alias", tableName);
		request.put("dataModelId", dataModelId);
		request.put("dataSourceId", JSONObject.NULL);

		List<JSONObject> request1 = new ArrayList<JSONObject>();
		request1.add(request);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request1.toString(), headers);
		APIUtils.postEntityWithPayloadRequest(targetUrl, jobRequest, "Add table to Data Pool");
	}

	/**
	 * Upload the CSV file to Celonis
	 * 
	 * @param dataPoolId
	 * @param fileLocation
	 * @param tableName
	 * @param timestampColumn
	 * @param chunkSize
	 * @throws CsvValidationException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws UserException
	 */
	public void uploadCSV(PluginContext context, String dataPoolId, String fileLocation, String tableName,
			String timestampColumn, int chunkSize)
			throws CsvValidationException, IOException, InterruptedException, ExecutionException, UserException {
		TableSchema tableSchema = getTableConfig(fileLocation, timestampColumn, tableName);
		String jobId = this.createPushJob(dataPoolId, tableName, tableSchema);
		this.uploadCsvChunk(context, chunkSize, dataPoolId, jobId, fileLocation);
		this.executeJob(jobId, dataPoolId);
		while (true) {
			String status = getStatus(dataPoolId, jobId);
			context.log("In queue...");
			if (status.equals("DONE")) {
				break;
			}
			TimeUnit.SECONDS.sleep(3);
		}
		context.log("Pushing event log done");

	}

	/**
	 * Get the current status of the execution of the data job.
	 * 
	 * @param dataPoolId
	 * @param jobId
	 * @return
	 * @throws UserException
	 */
	private String getStatus(String dataPoolId, String jobId) throws UserException {
		String targetUrl = String.format(this.url + "/integration/api/v1/data-push/%s/jobs/" + jobId, dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest,
				"Get status of the execution of data job");
		JSONObject body = new JSONObject(r.getBody());
		return body.getString("status");
	}

	/**
	 * Register a data push job
	 * 
	 * @param dataPoolId
	 * @param tableName
	 * @param tableSchema
	 * @return
	 */
	private String createPushJob(String dataPoolId, String tableName, TableSchema tableSchema) {
		DataPushJob job = new DataPushJob();
		CSVParsingOptions csvOption = new CSVParsingOptions();
		job.setDataPoolId(dataPoolId);
		job.setType(DataPushJob.type.REPLACE);
		job.setFileType(DataPushJob.fileType.CSV);
		job.setTargetName(tableName);
		job.setUpsertStrategy(DataPushJob.upsertStrategy.UPSERT_WITH_NULLIFICATION);
		job.setTableSchema(tableSchema);
		job.setFallbackVarcharLength(80);
		job.setCsvParsingOptions(csvOption);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<DataPushJob> jobRequest = new HttpEntity<>(job, headers);
		String targetUrl = String.format(this.url + "/integration/api/v1/data-push/%s/jobs/", dataPoolId);
		// Prepare HTTP POST
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(getJsonMessageConverters());
		job = restTemplate.postForObject(targetUrl, jobRequest, DataPushJob.class);
		return job.getId();
	}

	/**
	 * Configure the data job to upload the CSV file in chunks
	 * 
	 * @param chunkSize
	 * @param dataPoolId
	 * @param jobId
	 * @param fileLocation
	 * @throws CsvValidationException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void uploadCsvChunk(PluginContext context, int chunkSize, String dataPoolId, String jobId,
			String fileLocation) throws CsvValidationException, IOException, InterruptedException, ExecutionException {
		context.log("Dividing event log into chunks...");
		List<String> chunksLocation = divideChunks(fileLocation, chunkSize);
		context.log("Divide event log into chunks done");
		int chunkLength = chunksLocation.size();
		for (String chunkLocation : chunksLocation) {
			context.log(chunkLength + " chunks left...");
			uploadFile(dataPoolId, jobId, chunkLocation);
			context.log("Finish pushing chunk at " + chunkLocation);
			chunkLength -= 1;
		}
		for (String chunk : chunksLocation) {
			File tempFile = new File(chunk);
			tempFile.delete();
		}
	}

	/**
	 * Divide a file to a list of chunks with the given chunk size
	 * 
	 * @param fileLocation
	 * @param chunkSize
	 * @return
	 * @throws CsvValidationException
	 * @throws IOException
	 */
	private List<String> divideChunks(String fileLocation, int chunkSize) throws CsvValidationException, IOException {
		List<String> chunksLocation = new ArrayList<String>();

		CSVReader reader = new CSVReader(new FileReader(fileLocation));
		String[] tableHeader = reader.readNext();
		int chunkIndex = 0;
		List<String[]> subLog = new ArrayList<>();
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (chunkIndex != chunkSize) {
				subLog.add(nextLine);
				chunkIndex += 1;
			} else {
				System.out.println(subLog.get(4)[1]);
				File tempFile = File.createTempFile("chunk", ".csv");
				System.out.println(tempFile.toPath().toString());
				FileWriter outputFile = new FileWriter(tempFile);
				CSVWriter writer = new CSVWriter(outputFile);
				writer.writeNext(tableHeader);
				writer.writeAll(subLog);
				writer.close();
				chunksLocation.add(tempFile.toString());
				chunkIndex = 1;
				subLog.clear();
				subLog.add(nextLine);
			}
		}
		if (chunkIndex != 0) {
			File tempFile = File.createTempFile("chunk", ".csv");
			System.out.println(tempFile.toPath().toString());
			FileWriter outputFile = new FileWriter(tempFile);
			CSVWriter writer = new CSVWriter(outputFile);
			writer.writeNext(tableHeader);
			writer.writeAll(subLog);
			writer.close();
			chunksLocation.add(tempFile.toString());
		}

		return chunksLocation;
	}

	/**
	 * Configure the data job to upload a file to the given data pool
	 * 
	 * @param dataPoolId
	 * @param jobId
	 * @param fileLocation
	 */
	private void uploadFile(String dataPoolId, String jobId, String fileLocation) {
		String pushUrl = String.format(this.url + "/integration/api/v1/data-push/%s/jobs/" + jobId + "/chunks/upserted",
				dataPoolId);
		LinkedMultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<String, Object>();
		requestMap.add("file", new FileSystemResource(fileLocation));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(getFormMessageConverters());
		restTemplate.postForEntity(pushUrl, requestEntity, Object.class);
	}

	/**
	 * Execute the registered data job in a given data pool
	 * 
	 * @param jobId
	 * @param dataPoolId
	 */
	private void executeJob(String jobId, String dataPoolId) {
		String sealUrl = String.format(this.url + "/integration/api/v1/data-push/%s/jobs/" + jobId, dataPoolId);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> sealRequest = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(getJsonMessageConverters());

		restTemplate.postForEntity(sealUrl, sealRequest, Object.class);
	}

	/**
	 * Get a table schema based on the given file, name of the timestamp column
	 * 
	 * @param fileLocation
	 * @param timestampColumn
	 * @param tableName
	 * @return table schema
	 * @throws CsvValidationException
	 * @throws IOException
	 */
	private static TableSchema getTableConfig(String fileLocation, String timestampColumn, String tableName)
			throws CsvValidationException, IOException {
		CSVReader reader = new CSVReader(new FileReader(fileLocation));
		String[] tableHeader = reader.readNext();
		TableSchema tableSchema = new TableSchema();
		ColumnTransport[] tableCol = new ColumnTransport[tableHeader.length];

		for (int i = 0; i < tableHeader.length; i++) {
			if (tableHeader[i].equals(timestampColumn)) {
				ColumnTransport column = new ColumnTransport();
				column.setColumnName(tableHeader[i]);
				column.setColumnType(ColumnTransport.columnType.DATETIME);
				tableCol[i] = column;
			} else {
				ColumnTransport column = new ColumnTransport();
				column.setColumnName(tableHeader[i]);
				column.setColumnType(ColumnTransport.columnType.STRING);
				tableCol[i] = column;
			}

		}
		tableSchema.setColumns(tableCol);
		tableSchema.setTableName(tableName);
		return tableSchema;
	}

	private static List<HttpMessageConverter<?>> getJsonMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new MappingJackson2HttpMessageConverter());
		return converters;
	}

	private static List<HttpMessageConverter<?>> getFormMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new AllEncompassingFormHttpMessageConverter());
		return converters;
	}

}
