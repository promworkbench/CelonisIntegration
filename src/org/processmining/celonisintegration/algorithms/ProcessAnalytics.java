package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.celonisintegration.objects.processanalytics.Analysis;
import org.processmining.celonisintegration.objects.processanalytics.OLAPTable;
import org.processmining.celonisintegration.objects.processanalytics.Sheet;
import org.processmining.celonisintegration.objects.processanalytics.Workspace;
import org.processmining.framework.plugin.PluginContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProcessAnalytics {
	private String url;
	private String apiToken;
	private List<Workspace> workspaces;
	private List<Analysis> analyses;
	private List<Sheet> sheets;
	private List<OLAPTable> tables;

	public List<Sheet> getSheets() {
		return sheets;
	}

	public void setSheets(List<Sheet> sheets) {
		this.sheets = sheets;
	}

	public List<OLAPTable> getTables() {
		return tables;
	}

	public void setTables(List<OLAPTable> tables) {
		this.tables = tables;
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

	public ProcessAnalytics(String url, String apiToken) throws UserException {
		this.url = url;
		this.apiToken = apiToken;
		this.workspaces = new ArrayList<Workspace>();
		this.analyses = new ArrayList<Analysis>();
		this.sheets = new ArrayList<Sheet>();
		this.tables = new ArrayList<OLAPTable>();
		this.updateWorkspaces();
		this.updateAnalyses();
		for (Analysis ana : this.analyses) {
			this.updateSheetAndTable(ana);
		}
	}

	public void updateWorkspaces() throws UserException {
		String targetUrl = this.url + "/process-mining/api/processes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Update workspace");

		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject ws = body.getJSONObject(i);
			String name = ws.getString("name");
			String id = ws.getString("id");
			String dmId = ws.getString("dataModelId");
			this.workspaces.add(new Workspace(name, id, dmId));
		}
	}

	public void updateAnalyses() throws UserException {

		String targetUrl = this.url + "/process-mining/api/analysis";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Update analyses");

		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject ana = body.getJSONObject(i);
			for (Workspace ws : workspaces) {
				if (ana.getString("parentObjectId").equals(ws.getId())) {
					String name = ana.getString("name");
					String id = ana.getString("id");
					this.analyses.add(new Analysis(name, id, ws));
					break;
				}
			}

		}
	}

	public void updateSheetAndTable(Analysis ana) throws UserException {

		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + ana.getId() + "/autosave";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jobRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> r = APIUtils.getWithPayloadRequest(targetUrl, jobRequest, "Get all sheet and table");

		JSONObject body = new JSONObject(r.getBody());

		if (body.getJSONObject("document").has("components")) {
			JSONArray sh = new JSONArray(body.getJSONObject("document").getJSONArray("components"));
			for (int i = 0; i < sh.length(); i++) {
				JSONObject sheetJson = sh.getJSONObject(i);
				String name = sheetJson.getString("name");
				String translatedName = translateName(name);
				String id = sheetJson.getString("id");
				Sheet sheet = new Sheet(name, translatedName, id, ana);
				this.sheets.add(sheet);
				JSONArray components = new JSONArray(sheetJson.getJSONArray("components"));
				for (int j = 0; j < components.length(); j++) {
					JSONObject comp = components.getJSONObject(j);
					if (comp.has("chartType")) {
						if (comp.getString("chartType").equals("table")) {
							String tableName = comp.getString("title");
							String translatedTableName = translateName(tableName);
							String tableId = comp.getString("id");
							this.tables.add(new OLAPTable(tableName, translatedTableName, tableId, sheet));
						}
					}
				}

			}
		}

	}

	public String getDataModelId(String anaId) throws UserException {
		String res = "";

		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/data_model";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "Get data model ID");
		JSONObject body = new JSONObject(response.getBody());
		res = body.getString("id");
		return res;
	}

	

	public String getAnalysisIdByName(String name) {
		String id = "";

		for (Analysis ana : this.getAnalyses()) {
			if (ana.getName().equals(name)) {
				id = ana.getId();
				break;
			}
		}

		return id;
	}

	public String getSheetIdByTranslatedName(String name, String anaId) {
		String id = "";

		for (Sheet sh : this.getSheets()) {
			Analysis ana = sh.getAnalysis();
			if (sh.getTranslatedName().equals(name) && ana.getId().equals(anaId)) {
				id = sh.getId();
				break;
			}
		}

		return id;
	}

	public String getTableIdByTranslatedName(String name, String sheetId) {
		String id = "";

		for (OLAPTable table : this.getTables()) {
			Sheet sh = table.getSheet();
			if (table.getTranslatedName().equals(name) && sh.getId().equals(sheetId)) {
				id = table.getId();
				break;
			}
		}

		return id;
	}
	
	public String getColTextFromStatement(String statement, HashMap<String, String> variableMap) throws Exception {
		String res = "unnamed";		
		String removed = statement.replaceAll("<%=([^}]+)%>", "$1").replace(" ", "");		
		for (String var: variableMap.keySet()) {
			if (removed.equals(var)) {
				res = variableMap.get(var);
				break;
			}
			else {
				throw new Exception("Variable not found: " + var + " in the analysis");
			}
		}
		return res;
		
	}
	
	public static String getColNameFromStatement(String statement, HashMap<String, String> variableMap) throws Exception {
		String res = "unnamed";
		

		String removed = statement.replaceAll("<%=([^}]+)%>", "$1");
		String var = removed.split("==")[0].replaceAll(" ", "");
		String value = "";
		for (String v: variableMap.keySet()) {
			if (v.equals(var)) {
				value = variableMap.get(v);
			}
		}
		if (value.equals("")) {
			throw new Exception("Variable not found: " + var + " in the analysis");
		}
		
		String search = var + "==" + value;
		String[] arg = removed.split(":");
		Map<String, String> args = new HashMap<String, String>();
		for (int i = 0; i < arg.length; i++) {
			String[] a = arg[i].split("\\?");
			System.out.println(a[0]);
			if (a.length > 1) {
				args.put(a[0].replaceAll(" ", "").replaceAll("'", ""), a[1].replaceAll("'", ""));
				System.out.println(a[0].replaceAll(" ", "").replaceAll("'", ""));
				System.out.println(a[1].replaceAll(" ", "").replaceAll("'", ""));
				
			}
			else {
				args.put("", a[0].replaceAll(" ", "").replaceAll("'", ""));
				System.out.println(a[0].replaceAll(" ", ""));
			}
			
		}
		for (String k: args.keySet()) {
			if (k.equals(search)) {
				res = args.get(k);
				break;
			}
		}
		System.out.println(search);
		if (res.equals("unnamed")) {
			res = args.get("");
		}
		return res;
	}

	public HashMap<String, String> getPullingQuery(String anaId, String sheetId, String tableId) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		HashMap<String, String> variableMap = new HashMap<String, String>();
		
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/autosave";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);

		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "get pulling query");
		JSONObject body = new JSONObject(response.getBody());

		JSONArray sheets = new JSONArray(body.getJSONObject("document").getJSONArray("components"));
		if (body.getJSONObject("document").has("variables")) {
			JSONArray variables = new JSONArray(body.getJSONObject("document").getJSONArray("variables"));
			for (int i = 0; i < variables.length(); i++) {
				variableMap.put(variables.getJSONObject(i).getString("name"), variables.getJSONObject(i).getString("value"));
			}
		}
		
		
		JSONObject sheet = new JSONObject();
		for (int i = 0; i < sheets.length(); i++) {
			if (sheets.getJSONObject(i).getString("id").equals(sheetId)) {
				sheet = sheets.getJSONObject(i);
				break;
			}
		}
		JSONArray components = new JSONArray(sheet.getJSONArray("components"));
		JSONObject table = new JSONObject();
		for (int i = 0; i < components.length(); i++) {
			String tabId = components.getJSONObject(i).getString("id");
			if (components.getJSONObject(i).has("chartType")) {
				String chartType = components.getJSONObject(i).getString("chartType");
				if (tabId.equals(tableId) && chartType.equals("table")) {
					table = components.getJSONObject(i);
					break;
				}
			}

		}
		JSONArray dimensions = new JSONArray(table.getJSONArray("axis0"));
		JSONArray kpis = new JSONArray(table.getJSONArray("axis2"));
		JSONArray kpis_inc = new JSONArray();
		for (int i = 0; i < kpis.length(); i++) {
			JSONObject kpi = kpis.getJSONObject(i);
			if (!kpi.has("notIncluded") || (kpi.has("notIncluded") && !kpi.getBoolean("notIncluded"))) {
				kpis_inc.put(kpi);
			}
		}
		String filter = "";
		if (table.has("componentFilter")) {
			filter += table.getString("componentFilter");
		}

		String columns = "";
		String order = "";
		HashMap<Integer, String> fi = new HashMap<Integer, String>();
		res.put("filter", filter);

		for (int i = 0; i < dimensions.length(); i++) {
			JSONObject dim = dimensions.getJSONObject(i);
			if (!dim.has("notIncluded") || (dim.has("notIncluded") && !dim.getBoolean("notIncluded"))) {
				String text = dim.getString("text");
				if (text.contains("<%=")) {
					text = this.getColTextFromStatement(text, variableMap);
					System.out.println(text);
				}
				String name = dim.getString("name").replaceAll("#\\{([^}]+)}", "$1");
				if (name.contains("<%=")) {
					name = this.getColNameFromStatement(name, variableMap);
					System.out.println(name);
				}
				else if (dim.getString("name").contains(".") && dim.has("translatedName")
						&& !dim.getString("translatedName").toLowerCase().contains("expression")) {

					name = dim.getString("translatedName");

				}
				String q = text + " AS " + "\"" + name + "\"";
				if (i != dimensions.length() - 1) {
					q += ", ";
				}
				columns += q;

				if (dim.has("sorting")) {
					String s = text + " " + dim.getString("sorting");
					fi.put(dim.getInt("sortingIndex"), s);
				}
			}

		}
		if (kpis_inc.length() != 0) {
			if (dimensions.length() != 0) {
				columns += ", ";
			}			
			for (int i = 0; i < kpis_inc.length(); i++) {
				JSONObject kpi = kpis_inc.getJSONObject(i);
				String text = kpi.getString("text");
				if (text.contains("<%=")) {
					text = this.getColTextFromStatement(text, variableMap);
					System.out.println(text);
				}
				String name = kpi.getString("name").replaceAll("#\\{([^}]+)}", "$1");
				if (name.contains("<%=")) {
					name = this.getColNameFromStatement(name, variableMap);
					System.out.println(name);
				}
				else if (kpi.getString("name").contains(".") && kpi.has("translatedName")
						&& !kpi.getString("translatedName").toLowerCase().contains("expression")) {

					name = kpi.getString("translatedName");

				}
				String q = text + " AS " + "\"" + name + "\"" + " ";
				q += "FORMAT";
				q += "\"";
				q += kpi.getString("valueFormat");
				q += "\"";
				if (i != kpis_inc.length() - 1) {
					q += ", ";
				}
				columns += q;

				if (kpi.has("sorting")) {
					String s = text + " " + kpi.getString("sorting");
					fi.put(kpi.getInt("sortingIndex"), s);
				}
			}

		}

		TreeMap<Integer, String> fiSorted = new TreeMap<Integer, String>(fi);
		for (int i : fiSorted.keySet()) {
			order += fiSorted.get(i);
			if (i != fiSorted.lastKey()) {
				order += ", ";
			}
		}

		String col1 = "TABLE( ";
		col1 += columns;
		col1 += ")";
		if (!order.equals("")) {
			col1 += " ORDER BY ";
			col1 += order;
		}

		col1 += " ";
		col1 += "NOLIMIT;";
		res.put("column", col1);
		return res;
	}

	public String getExportStatus(String anaId, String queryId) throws UserException {
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/exporting/query/"
				+ queryId + "/status";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "Get export status");
		JSONObject body = new JSONObject(response.getBody());
		return body.getString("exportStatus");
	}

	public String getPayload(List<String> query, String dataModelId) {

		JSONObject command = new JSONObject().put("queries", query).put("computationId", 0).put("isTransient", false);
		JSONArray commands = new JSONArray().put(command);
		JSONObject request = new JSONObject().put("commands", commands).put("cubeId", dataModelId);
		JSONObject dataReq = new JSONObject().put("variables", new JSONArray()).put("request", request);

		JSONObject payload = new JSONObject().put("dataCommandRequest", dataReq).put("exportType", "CSV");

		System.out.println(payload.toString());
		return payload.toString();
	}

	public String getQueryId(PluginContext context, String anaId, String sheetId, String tableId) throws Exception {
		context.log("Extracting PQL query of the table...");
		String dmId = this.getDataModelId(anaId);				
		HashMap<String, String> queryMap = this.getPullingQuery(anaId, sheetId, tableId);
		context.getProgress().inc();
		List<String> query = new ArrayList<String>();
		if (queryMap.get("filter").equals("")) {
			query.add(queryMap.get("column"));
		} else {
			query.add(queryMap.get("column"));
			query.add(queryMap.get("filter"));
		}
		context.log("Extract PQL query of the table done");
		context.log("Sending query...");
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/exporting/query";
		String payload = this.getPayload(query, dmId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> postRequest = new HttpEntity<String>(payload, headers);
		RestTemplate restTemplate = new RestTemplate();
		
		String res = ErrorUtils.checkDownloadOlap(targetUrl, postRequest, restTemplate);
		context.getProgress().inc();
		context.log("Send query done");
		return res;
	}

	public String getCsv(PluginContext context, String anaName, String sheetName, String tableName) throws Exception {
		String anaId = this.getAnalysisIdByName(anaName);
		String sheetId = this.getSheetIdByTranslatedName(sheetName, anaId);
		String tableId = this.getTableIdByTranslatedName(tableName, sheetId);

		String queryId = this.getQueryId(context, anaId, sheetId, tableId);
		context.log("Processing the query...");
		while (true) {
			String status = this.getExportStatus(anaId, queryId);
			context.log("Query status is " + status + "...");
			if (status.equals("DONE")) {
				break;
			}
			TimeUnit.SECONDS.sleep(1);
		}
		
		context.log("Downloading the table...");
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/exporting/query/"
				+ queryId + "/download";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> getRequest = new HttpEntity<String>(headers);
		ResponseEntity<String> response = APIUtils.getWithPayloadRequest(targetUrl, getRequest, "Get CSV file");
		String body = response.getBody();
		File tempFile = File.createTempFile("event_log_celonis_" + anaName, ".csv");
		context.log("Downloading the table done");
		XESUtils.writeToCsv(body, tempFile);
		return tempFile.getAbsolutePath().toString();

	}

	public static String translateName(String name) {
		return name.replaceAll("#\\{([^}]+)}", "$1");
	}

}
