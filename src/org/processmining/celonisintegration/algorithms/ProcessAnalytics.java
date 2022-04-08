package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProcessAnalytics {
	private String url;
	private String apiToken;
	
	public ProcessAnalytics (String url, String apiToken) {
		this.url = url;
		this.apiToken = apiToken;
	}
	
	public List<String> getOlapTables(String sheetName, String anaId) throws Exception {
		List<String> res = new ArrayList<String>();
		
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/autosave";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	        
        HttpEntity<String> jobRequest = new HttpEntity<String>(headers);        
        ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
        
        JSONObject body = new JSONObject(r.getBody());
        JSONArray sheets = new JSONArray(body.getJSONObject("document").getJSONArray("components"));
        JSONObject sheet = new JSONObject();
        for (int i = 0; i < sheets.length(); i++) {
        	JSONObject s = sheets.getJSONObject(i);
        	if (s.getString("name").equals(sheetName)) {
        		sheet = sheets.getJSONObject(i);
        		break;
        	}
        }
        JSONArray components = new JSONArray(sheet.getJSONArray("components"));
        
        for (int i = 0; i < components.length(); i++) {
        	
        	String chartType = components.getJSONObject(i).getString("chartType");
        	if (chartType.equals("table")) {
        		String t = components.getJSONObject(i).getString("title");
        		if (t.equals("")) {
        			throw new Exception("The table does not have a name");
        		}
        		String title = t.substring(2, components.getJSONObject(i).getString("title").length()-1);
        		res.add(title);
        		
        	}
        }
        
        return res;
    }
	
	public List<String> getSheets(String anaId) {
		List<String> res = new ArrayList<String>();
		
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/autosave";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	        
        HttpEntity<String> jobRequest = new HttpEntity<String>(headers);        
        ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
        
        JSONObject body = new JSONObject(r.getBody());
       
        JSONArray sheets = new JSONArray(body.getJSONObject("document").getJSONArray("components"));
        
        for (int i = 0; i < sheets.length(); i++) {
        	JSONObject sheet = sheets.getJSONObject(i);
        	res.add(sheet.getString("name"));
        	}
        return res;
    }
	
	
	public String getAnalysisByName(String anaName) {
		String res = "";
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
        	if (ana.getString("name").equals(anaName)){
        		res = ana.getString("id");
        		break;
        	}
        }
		return res;
	}
	
	public List<String> getAnalyses(String wsId) {
		List<String> res = new ArrayList<String>();
		
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
        	if (ana.getString("parentObjectId").equals(wsId)) {
        		res.add(ana.getString("name"));
        	}
        }
		return res;
	}
	
	public String getWorkspaceByName(String wsName) {
		String res = "";
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
        	if (ws.getString("name").equals(wsName)){
        		res = ws.getString("id");
        		break;
        	}
        }
		return res;
	}
	
	public List<String> getWorkspaces() {
		List<String> res = new ArrayList<String>();
		
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
        	res.add(ws.getString("name"));
        }
		return res;
	}
	
	public String getAnalysis(String anaName) {
		String res = "";
		String targetUrl = this.url + "/process-mining/api/analysis";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONArray body = new JSONArray(response.getBody());
        for (int i = 0; i < body.length(); i++) {
        	String name = body.getJSONObject(i).getString("name");
        	if (name.equals(anaName)) {
        		return body.getJSONObject(i).getString("id");
        	}
        }
        return res;        
	}
	
	public String getDataModelId (String anaId) {
		String res = "";
		
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/data_model";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONObject body = new JSONObject(response.getBody());
        res = body.getString("id");
        return res;
	}
	
	public HashMap<String, String> getPullingQuery(String anaId, String sheetName, String tableName) throws Exception {
		HashMap<String, String> res = new HashMap<String, String>();
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/autosave";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONObject body = new JSONObject(response.getBody());
        
//        JSONObject document = new JSONObject(body.getJSONObject("document"));
        JSONArray sheets = new JSONArray(body.getJSONObject("document").getJSONArray("components"));
        JSONObject sheet = new JSONObject();
        for (int i = 0; i < sheets.length(); i++) {
        	if (sheets.getJSONObject(i).getString("name").equals(sheetName)) {
        		sheet = sheets.getJSONObject(i);
        		break;
        	}
        }
        JSONArray components = new JSONArray(sheet.getJSONArray("components"));
        JSONObject table = new JSONObject();
        for (int i = 0; i < components.length(); i++) {
        	String t = components.getJSONObject(i).getString("title");
    		if (t.equals("")) {
    			throw new Exception("The table does not have a name");
    		}
    		String title = t.substring(2, components.getJSONObject(i).getString("title").length()-1);
        	String chartType = components.getJSONObject(i).getString("chartType");
        	if (title.equals(tableName) && chartType.equals("table")) {
        		table = components.getJSONObject(i);
        		break;
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
            	String name = dim.getString("name");
            	name = name.substring(2, name.length() - 1);
            	String q = text + " AS " + "\"" + name + "\"" + ", ";
            	columns += q;
            	
            	if (dim.has("sorting")) {
            		String s = text + " " + dim.getString("sorting");
            		fi.put(dim.getInt("sortingIndex"), s);
            	}
        	}
        	
        }
        for (int i = 0; i < kpis_inc.length(); i++) {
        	JSONObject kpi = kpis.getJSONObject(i);        	
    		String text = kpi.getString("text");
        	String name = kpi.getString("name");
        	String q = text + " AS " + "\"" + name + "\"" + " ";
        	q += "FORMAT";
        	q += "\"";
        	q += kpi.getString("valueFormat");
        	q += "\"";
        	if (i != kpis_inc.length()-1) {
        		q += ", ";
        	}
        	columns += q;
        	
        	if (kpi.has("sorting")) {
        		String s = text + " " + kpi.getString("sorting");
        		fi.put(kpi.getInt("sortingIndex"), s);
        	}       	
        }
        TreeMap<Integer, String> fiSorted = new TreeMap<Integer, String>(fi);
        for(int i: fiSorted.keySet()) {
        	order += fiSorted.get(i);        	
        	if (i != fiSorted.lastKey()) {
        		order += ", ";
        	}
        }
    	
        String col1 = "TABLE( ";
        col1 += columns;
        col1 += ")";
        col1 += " ORDER BY ";
        col1 += order;
        col1 += " ";
        col1 += "NOLIMIT;";
        res.put("column", col1);
		return res;
	}
	
	public String getExportStatus (String anaId, String queryId) {
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/"
									+ anaId 
									+ "/exporting/query/"
									+ queryId
									+ "/status";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONObject body = new JSONObject(response.getBody());
		return body.getString("exportStatus");
	}
	
	public String getPayload(List<String> query, String dataModelId) {
		
		
		JSONObject command = new JSONObject()
							.put("queries", query)
							.put("computationId", 0)
							.put("isTransient", false);
		JSONArray commands = new JSONArray().put(command);
		JSONObject request = new JSONObject()
							.put("commands", commands)
							.put("cubeId", dataModelId);
		JSONObject dataReq = new JSONObject()
							.put("variables", new JSONArray())
							.put("request", request);
		
		JSONObject payload = new JSONObject()
							.put("dataCommandRequest", dataReq)
							.put("exportType", "CSV");
		
		System.out.println(payload.toString());
		return payload.toString();
	}
	
	public String getQueryId(String anaName, String sheetName, String tableName) throws Exception {
		String anaId = this.getAnalysis(anaName);
		String dmId = this.getDataModelId(anaId);
		
		HashMap<String, String> queryMap = this.getPullingQuery(anaId, sheetName, tableName);		
		List<String> query = new ArrayList<String>();
		if (queryMap.get("filter").equals("")) {
			query.add(queryMap.get("column"));
		}
		else {
			query.add(queryMap.get("column"));
			query.add(queryMap.get("filter"));
		}
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/"
									+ anaId 
									+ "/exporting/query";	
		String payload = this.getPayload(query, dmId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
								
		
		HttpEntity<String> postRequest = new HttpEntity<String>(payload, headers);    
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, postRequest, String.class);   
		JSONObject body = new JSONObject(response.getBody());
		return body.getString("id");			
	}
	
	
	public String getCsv(String anaName, String sheetName, String tableName) throws Exception {
		String queryId = this.getQueryId(anaName, sheetName, tableName);
		String anaId = this.getAnalysis(anaName);
		
		while (true) {
			String status = this.getExportStatus(anaId, queryId);
			if (status.equals("DONE")) {
				break;
			}
			TimeUnit.SECONDS.sleep(1);
		}
		
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/"
									+ anaId
									+ "/exporting/query/"
									+ queryId
									+ "/download";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
		HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
		String body = response.getBody();
		File tempFile = File.createTempFile("event_log_celonis_" + anaName, ".csv");
		CSVUtils.writeToCsv(body, tempFile);
		return tempFile.getAbsolutePath().toString();

	}
	
	
	
}



























