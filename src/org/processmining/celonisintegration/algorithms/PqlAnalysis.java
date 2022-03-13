package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PqlAnalysis {
	private String url;
	private String apiToken;
	
	public PqlAnalysis (String url, String apiToken) {
		this.url = url;
		this.apiToken = apiToken;
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
	
	public HashMap<String, String> getDataModel (String anaId) {
		HashMap<String, String> res = new HashMap<String, String>();
		
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/" + anaId + "/data_model";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONObject body = new JSONObject(response.getBody());
        res.put("id", body.getString("id"));
        
        JSONArray tables = new JSONArray(body.getJSONArray("tables"));
        for (int i = 0; i < tables.length(); i++) {
        	JSONObject table = tables.getJSONObject(i);
        	JSONObject config = table.getJSONObject("tableConfiguration");
        	String actColId = "";
        	String caseColId = "";
        	String timeColId = "";
        	if (config.getString("type").equals("ACTIVITY")) {
        		res.put("name", table.getString("name"));
        		actColId = config.getString("activityColumnId");
        		caseColId = config.getString("caseIdColumnId");
        		timeColId = config.getString("timestampColumnId");
        	}
        	
        	JSONArray cols = table.getJSONArray("columns");
        	for (int j = 0; j < cols.length(); j++) {
        		JSONObject col = cols.getJSONObject(j);
        		if (col.getString("id").equals(actColId)) {
        			res.put("actCol", col.getString("name"));
        		}
        		if (col.getString("id").equals(caseColId)) {
        			res.put("caseCol", col.getString("name"));
        		}
        		if (col.getString("id").equals(timeColId)) {
        			res.put("timeCol", col.getString("name"));
        		}
        	}
        }
        
		return res;
	}
	
	public String getQuery(HashMap<String, String> dataModel) {
		String res = "";
		res += "TABLE(";
		res += "\"" + dataModel.get("name") + "\".\"" + dataModel.get("actCol") + "\" AS \" concept:name \", ";
		res += "\"" + dataModel.get("name") + "\".\"" + dataModel.get("caseCol") + "\" AS \" case:concept:name \", ";
		res += "\"" + dataModel.get("name") + "\".\"" + dataModel.get("timeCol") + "\" AS \" time:timestamp \") NOLIMIT; ";		
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
	
	public String getPayload(String query, String dataModelId) {
		JSONArray query1 = new JSONArray();
		query1.put(query);
		
		JSONObject command = new JSONObject()
							.put("queries", query1)
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
		
		return payload.toString();
	}
	
	public String getQueryId(String anaName) {
		String anaId = this.getAnalysis(anaName);
		HashMap<String, String> dataModel = this.getDataModel(anaId);
		String query = this.getQuery(dataModel);		
		
		String targetUrl = this.url + "/process-mining/analysis/v1.2/api/analysis/"
									+ anaId 
									+ "/exporting/query";	
		String payload = this.getPayload(query, dataModel.get("id"));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);	
		
								
		
		HttpEntity<String> postRequest = new HttpEntity<String>(payload, headers);    
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, postRequest, String.class);   
		JSONObject body = new JSONObject(response.getBody());
		return body.getString("id");			
	}
	
	
	public String getCsv(String anaName) throws InterruptedException {
		String queryId = this.getQueryId(anaName);
		String anaId = this.getAnalysis(anaName);
		String status = this.getExportStatus(anaId, queryId);
		while (true) {
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
		return body;
	}
	
	public String downloadCsv(String anaName) throws InterruptedException, IOException {
		String log = this.getCsv(anaName);
		File tempFile = File.createTempFile("event_log_celonis_" + anaName, ".csv");
		CSVUtils.writeToCsv(log, tempFile);
		return tempFile.getAbsolutePath().toString();		
	}
	
}



























