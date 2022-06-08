
package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.celonisintegration.algorithms.CelonisObject.Analysis;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModel;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTableType;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataPool;
import org.processmining.celonisintegration.algorithms.CelonisObject.Workspace;
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

	public DataIntegration(String url, String apiToken) {
		this.url = url;
		this.apiToken = apiToken;
		this.workspaces = new ArrayList<Workspace>();
		this.analyses = new ArrayList<Analysis>();
		this.dataPools = new ArrayList<DataPool>();
		this.dataModels = new ArrayList<DataModel>();
		this.dataModelTables = new ArrayList<DataModelTable>();
		
		this.updateWorkspaces();
		this.updateAnalyses();
		this.updateDataPools();
		this.updateDataModels();
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
		for (DataModel dm: this.getDataModels()) {
			if (dm.getDp().getName().equals(dpName) && dm.getName().equals(dmName)) {
				return dm.getId();
			}
		}
		return res;
	}
	
	public String getDataModeTablelId(String dpName, String dmName, String table) {
		String res = "";
		DataModel dmO = new CelonisObject().new DataModel();
		for (DataModel dm: this.getDataModels()) {
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
		for (Workspace ws: this.getWorkspaces()) {
			if (ws.getName().equals(wsName)) {
				return ws.getId();
			}
		}
		return res;
	}
	
	public String getAnalysisId(String wsName, String anaName) {
		String res = "";		
		for (Analysis ana: this.getAnalyses()) {
			if (ana.getWorkspace().getName().equals(wsName) && ana.getName().equals(anaName)) {
				return ana.getId();
			}
		}
		return res;
	}

	public void updateWorkspaces() {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/api/processes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);

		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject ws = body.getJSONObject(i);
			String name = ws.getString("name");
			String id = ws.getString("id");
			String dmId = ws.getString("dataModelId");
			this.workspaces.add(new CelonisObject().new Workspace(name, id, dmId));
		}
	}

	public void updateAnalyses() {

		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/api/analysis";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);

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

	public void updateDataPools() {

		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(this.url + "/integration/api/pools");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);

		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
		JSONArray body = new JSONArray(r.getBody());

		for (int i = 0; i < body.length(); i++) {
			JSONObject obj = body.getJSONObject(i);
			String name = obj.getString("name");
			String id = obj.getString("id");
			this.dataPools.add(new CelonisObject().new DataPool(name, id));
		}

	}

	public void updateDataModels() {

		for (DataPool dp : this.dataPools) {
			String dpId = dp.getId();
			RestTemplate restTemplate = new RestTemplate();
			String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models";
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + this.apiToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Void> jobRequest = new HttpEntity<>(headers);

			ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
			JSONArray body = new JSONArray(r.getBody());

			for (int i = 0; i < body.length(); i++) {
				JSONObject obj = body.getJSONObject(i);
				String name = obj.getString("name");
				String id = obj.getString("id");
				JSONArray tablesJSON = obj.getJSONArray("tables");
				String actTableId = "";
				String caseTableId = "";
				if (obj.getJSONArray("processConfigurations").length() > 0) {
					if (obj.getJSONArray("processConfigurations").getJSONObject(0).has("activityTableId")) {
						actTableId = obj.getJSONArray("processConfigurations").getJSONObject(0).getString("activityTableId");
					}
					
					if (obj.getJSONArray("processConfigurations").getJSONObject(0).has("caseTableId")) {
						caseTableId = obj.getJSONArray("processConfigurations").getJSONObject(0).getString("activityTableId");
					}
				}
				
				List<DataModelTable> tables = new ArrayList<DataModelTable>();

				for (int j = 0; j < tablesJSON.length(); j++) {
					String nameTable = tablesJSON.getJSONObject(j).getString("name");
					String idTable = tablesJSON.getJSONObject(j).getString("id");
					DataModelTableType tableType = DataModelTableType.NORMAL;
					if (idTable.equals(actTableId)) {
						tableType = DataModelTableType.ACTIVITY;
					}
					else if (idTable.equals(caseTableId)) {
						tableType = DataModelTableType.CASE;
					}
					tables.add(new CelonisObject().new DataModelTable(nameTable, idTable, id, tableType));
					this.dataModelTables.add(new CelonisObject().new DataModelTable(nameTable, idTable, id, tableType));
				}
				this.dataModels.add(new CelonisObject().new DataModel(name, id, dp, tables));
			}
		}

	}

	public String getCsv(String dpId, String dmId, String tableName) throws Exception {
		String queryId = this.getQueryId(dpId, dmId, tableName);
		System.out.println(queryId);
		System.out.println("quering");
		String status = "";
		int delay = 0;
		while (true) {
			status = this.getExportStatus(dmId, queryId);
			delay += 1;
			if (status.equals("DONE") || status.equals("FAILED") || delay == 60) {
				break;
			}
			TimeUnit.SECONDS.sleep(2);
		}
		System.out.println("done quering");
		if (status.equals("FAILED")) {

			throw new Exception(this.getExportMessage(dmId, queryId));
		}
		if (delay == 60) {
			throw new Exception("The table is too big");
		}
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/" + queryId + "/result";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);
		System.out.println("finish requesting");
		String log = response.getBody();
		File tempFile = File.createTempFile("event_log_celonis_" + tableName, ".csv");
		XESUtils.writeToCsv(log, tempFile);
		System.out.println("finish writing");
		return tempFile.getAbsolutePath().toString();
	}

	public String getExportMessage(String dmId, String queryId) {
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/" + queryId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);
		JSONObject body = new JSONObject(response.getBody());

		return body.getString("message");
	}

	public String getExportStatus(String dmId, String queryId) {
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/" + queryId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);
		JSONObject body = new JSONObject(response.getBody());

		return body.getString("exportStatus");
	}

	public String getQueryId(String dpId, String dmId, String tableName) {
		String query = this.getPullingQuery(dpId, dmId, tableName);
		String targetUrl = this.url + "/integration/api/v1/compute/" + dmId + "/export/query";
		String payload = this.getPullingPayload(query);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> postRequest = new HttpEntity<String>(payload, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, postRequest, String.class);
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

	public String getPullingQuery(String dpId, String dmId, String tableName) {
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

	public List<String> getColumns(String dpId, String dmId, String tableId) {
		List<String> res = new ArrayList<String>();

		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-model/" + dmId + "/tables/" + tableId
				+ "/columns";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);

		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
		JSONArray body = new JSONArray(r.getBody());

		for (int i = 0; i < body.length(); i++) {
			JSONObject obj = body.getJSONObject(i);
			res.add(obj.getString("name"));
		}
		return res;
	}

//	public String getDataModelIdByName(String dmName, String dpId) {
//		String res = "";
//		RestTemplate restTemplate = new RestTemplate();
//		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models";
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Authorization", "Bearer " + this.apiToken);
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);
//
//		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
//		JSONArray body = new JSONArray(r.getBody());
//		for (int i = 0; i < body.length(); i++) {
//			JSONObject obj = body.getJSONObject(i);
//			if (obj.getString("name").equals(dmName)) {
//				res = obj.getString("id");
//			}
//		}
//		return res;
//	}

	//	public String getDataPoolIdByName(String name) {
	//		String res = "";
	//		RestTemplate restTemplate = new RestTemplate();
	//		String targetUrl = String.format(this.url + "/integration/api/pools");
	//		HttpHeaders headers = new HttpHeaders();
	//		headers.add("Authorization", "Bearer " + this.apiToken);
	//		headers.setContentType(MediaType.APPLICATION_JSON);
	//
	//		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);
	//
	//		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
	//		JSONArray body = new JSONArray(r.getBody());
	//
	//		for (int i = 0; i < body.length(); i++) {
	//			JSONObject obj = body.getJSONObject(i);
	//			if (obj.getString("name").equals(name)) {
	//				res = obj.getString("id");
	//			}
	//		}
	//		return res;
	//	}

	
	
	
	
	public void deleteDataPool(String dpId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/integration/api/pools/" + dpId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);

		restTemplate.exchange(targetUrl, HttpMethod.DELETE, jobRequest, String.class);
	}
	
	public void deleteDataModel(String dpId, String dmId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models/" + dmId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);

		restTemplate.exchange(targetUrl, HttpMethod.DELETE, jobRequest, String.class);
	}
	
	public void deleteDataModelTable(String dpId, String dmId, String tableId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/integration/api/pools/" + dpId + "/data-models/" + dmId + "/tables/" + tableId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);

		restTemplate.exchange(targetUrl, HttpMethod.DELETE, jobRequest, String.class);
	}
	
	public void deleteWorkspace(String wsId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/api/processes" + wsId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);

		restTemplate.exchange(targetUrl, HttpMethod.DELETE, jobRequest, String.class);
	}
	
	public void deleteAnalysis(String anaId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/api/analysis" + anaId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);

		restTemplate.exchange(targetUrl, HttpMethod.DELETE, jobRequest, String.class);
	}
	
	/**
	 * Create a new data pool in Celonis
	 * 
	 * @param name
	 * @return
	 */
	public String createDataPool(String name) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/integration/api/pools";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", name);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);

		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.POST, jobRequest, String.class);

		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");
	}

	/**
	 * Create a new data model in Celonis
	 * 
	 * @param modelName
	 * @param dataPoolId
	 * @return
	 */
	public String createDataModel(String modelName, String dataPoolId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-models", dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", modelName);
		request.put("poolId", dataPoolId);
		request.put("configurationSkipped", true);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);

		String r = restTemplate.postForObject(targetUrl, jobRequest, String.class);
		JSONObject body = new JSONObject(r);
		return body.getString("id");
	}

	/**
	 * Create a new workspace in process analytics
	 * 
	 * @param dataModelId
	 * @param name
	 * @return
	 */
	public String createWorkspace(String dataModelId, String name) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/api/processes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", name);
		request.put("dataModelId", dataModelId);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);

		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.POST, jobRequest, String.class);

		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");

	}
	
	public String createAnalysis(String workspaceId, String name) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/api/analysis";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", name);
		request.put("processId", workspaceId);
		request.put("applicationId", JSONObject.NULL);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);

		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.POST, jobRequest, String.class);

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
	 */
	public void addProcessConfiguration(String dataModelId, String dataPoolId, String actTable, String caseTable,
			String caseIdColumn, String actColumn, String timestampColumn) {
		String actTableId = this.getTableByName(actTable, dataModelId, dataPoolId);
		String caseTableId = this.getTableByName(caseTable, dataModelId, dataPoolId);

		RestTemplate restTemplate = new RestTemplate();
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
		restTemplate.exchange(targetUrl, HttpMethod.PUT, jobRequest, String.class);

	}

	/**
	 * Reload the data model
	 * 
	 * @param dataModelId
	 * @param dataPoolId
	 * @throws InterruptedException
	 */
	public void reloadDataModel(String dataModelId, String dataPoolId) throws InterruptedException {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-models/" + dataModelId + "/reload",
				dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("forceComplete", true);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		restTemplate.postForEntity(targetUrl, jobRequest, Object.class);

		while (true) {
			String loadUrl = String.format(
					this.url + "/integration/api/pools/%s/data-models/" + dataModelId + "/load-history/load-info-sync",
					dataPoolId);
			HttpEntity<String> loadRequest = new HttpEntity<String>(null, headers);
			RestTemplate restTemplate1 = new RestTemplate();
			ResponseEntity<String> r = restTemplate1.exchange(loadUrl, HttpMethod.GET, loadRequest, String.class);

			JSONObject body = new JSONObject(r.getBody());
			String status = body.getJSONObject("loadInfo").getJSONObject("currentComputeLoad").getString("loadStatus");
			if (status.equals("SUCCESS")) {
				break;
			}
			TimeUnit.SECONDS.sleep(3);

		}
	}

	/**
	 * Get a table ID via table name
	 * 
	 * @param tableName
	 * @param dataModelId
	 * @param dataPoolId
	 * @return
	 */
	public String getTableByName(String tableName, String dataModelId, String dataPoolId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(this.url + "/integration/api/pools/%s/data-model/" + dataModelId + "/tables",
				dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
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
	 */
	public void addForeignKeys(String table1, String column1, String table2, String column2, String dataModelId,
			String dataPoolId) {
		String table1Id = this.getTableByName(table1, dataModelId, dataPoolId);
		String table2Id = this.getTableByName(table2, dataModelId, dataPoolId);

		RestTemplate restTemplate = new RestTemplate();
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
		restTemplate.postForEntity(targetUrl, jobRequest, Object.class);

	}

	/**
	 * Add a table to a given data pool via table name
	 * 
	 * @param tableName
	 * @param dataPoolId
	 * @param dataModelId
	 */
	public void addTableFromPool(String tableName, String dataPoolId, String dataModelId) {
		RestTemplate restTemplate = new RestTemplate();
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

		restTemplate.postForEntity(targetUrl, jobRequest, Object.class);
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
	 */
	public void uploadCSV(String dataPoolId, String fileLocation, String tableName, String timestampColumn,
			int chunkSize) throws CsvValidationException, IOException, InterruptedException, ExecutionException {
		TableSchema tableSchema = getTableConfig(fileLocation, timestampColumn, tableName);
		String jobId = this.createPushJob(dataPoolId, tableName, tableSchema);
		this.uploadCsvChunk(chunkSize, dataPoolId, jobId, fileLocation);
		this.executeJob(jobId, dataPoolId);
		System.out.println("start getting status");

		while (true) {
			String status = getStatus(dataPoolId, jobId);
			System.out.println(status);
			if (status.equals("DONE")) {
				break;
			}
			TimeUnit.SECONDS.sleep(3);
		}
		System.out.println("done");

	}

	/**
	 * Get the current status of the execution of the data job.
	 * 
	 * @param dataPoolId
	 * @param jobId
	 * @return
	 */
	private String getStatus(String dataPoolId, String jobId) {
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(this.url + "/integration/api/v1/data-push/%s/jobs/" + jobId, dataPoolId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);
		ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
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
	private void uploadCsvChunk(int chunkSize, String dataPoolId, String jobId, String fileLocation)
			throws CsvValidationException, IOException, InterruptedException, ExecutionException {
		System.out.println("Start devide chunks");
		long startDivide = System.currentTimeMillis();
		List<String> chunksLocation = divideChunks(fileLocation, chunkSize);
		System.out.println("Done devide chunks");
		long endDivide = System.currentTimeMillis();
		System.out.println((endDivide - startDivide) / 60000);

		ExecutorService executorService = Executors.newFixedThreadPool(100);
		Set<Callable<String>> callables = new HashSet<Callable<String>>();
		System.out.println("Start push chunks");
		long startPush = System.currentTimeMillis();
		for (String chunk : chunksLocation) {
			callables.add(new Callable<String>() {
				public String call() throws Exception {
					uploadFile(dataPoolId, jobId, chunk);
					return "finish uploading " + chunk;
				}
			});
		}
		List<Future<String>> futures = executorService.invokeAll(callables);

		for (Future<String> future : futures) {
			System.out.println(future.get());
		}
		executorService.shutdown();
		long endPush = System.currentTimeMillis();
		System.out.println("End push chunks");
		System.out.println((endPush - startPush) / 60000);

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
		Object re = restTemplate.postForEntity(sealUrl, sealRequest, Object.class);
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
