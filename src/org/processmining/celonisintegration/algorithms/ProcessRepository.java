package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProcessRepository {
	
	private String url;
	private String apiToken;
	
	public ProcessRepository(String url, String apiToken) {
		this.url = url;
		this.apiToken = apiToken;
	}
	
	/**
	 * Get all the category name and process model name in the process repository
	 * @return
	 */
	public HashMap<String, List<String>> getProcessRepoInfo() {
		HashMap<String, List<String>> res = new HashMap<String, List<String>>();
		String targetUrl = this.url + "process-repository/api/v1/categories";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONArray body = new JSONArray(response.getBody());
        for (int i = 0; i < body.length(); i++) {
        	String cateId = body.getJSONObject(i).getString("id");
        	String targetUrl1 = this.url + "/process-repository/api/v1/categories/" + cateId + "/process-models";		
        	ResponseEntity<String> response1 = restTemplate.exchange(targetUrl1, HttpMethod.GET, getRequest, String.class);   
            JSONArray body1 = new JSONArray(response1.getBody());
            List<String> processModel = new ArrayList<String>();
            for (int j = 0; j < body1.length(); j++) {
            	processModel.add(body1.getJSONObject(j).getString("name"));
            }
            res.put(body.getJSONObject(i).getString("name"), processModel);
        }
        return res;
	}
			
	/**
	 * Get the category ID via category name
	 * @param cateName
	 * @return
	 */
	public String getCategory(String cateName) {
		String result = "";
		String targetUrl = this.url + "process-repository/api/v1/categories";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONArray body = new JSONArray(response.getBody());
        for (int i = 0; i < body.length(); i++) {
        	String name = body.getJSONObject(i).getString("name");
        	if (name.equals(cateName)) {
        		return body.getJSONObject(i).getString("id");
        	}
        }
        return result;        
	}
	
	/**
	 * Get the process model ID via process model name
	 * @param cateName
	 * @param pmName
	 * @return
	 */
	public String getProcessModel(String cateName, String pmName) {
		String result = "";		
		String cateId = getCategory(cateName);
		String targetUrl = this.url + "/process-repository/api/v1/categories/" + cateId + "/process-models";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);    
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);   
        JSONArray body = new JSONArray(response.getBody());
        for (int i = 0; i < body.length(); i++) {
        	String name = body.getJSONObject(i).getString("name");
        	if (name.equals(pmName)) {
        		return body.getJSONObject(i).getString("id");
        	}
        }
        return result;        
	}
	
	/**
	 * Get the BPMN model 
	 * @param cateName
	 * @param pmName
	 * @return
	 * @throws IOException
	 */
	public String getBpmnFileLocation(String cateName, String pmName) throws IOException {
		String categoryId = getCategory(cateName);
		String processModelId = getProcessModel(cateName, pmName);
		
		String targetUrl = this.url + "/process-repository/api/v1/categories/" + 
							categoryId + "/process-models/" + processModelId + "/file";		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);	
        HttpEntity<String> getRequest = new HttpEntity<String>(headers);   
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, getRequest, String.class);          
        JSONObject body = new JSONObject(response.getBody());        
        String content = body.getString("content");
        byte[] xmlDecoded = Base64.getDecoder().decode(content);
        
        File tempFile = File.createTempFile(pmName, ".bpmn");
        FileWriter outputFile = new FileWriter(tempFile);
        outputFile.write(new String(xmlDecoded));
        outputFile.close();
        
        return tempFile.getPath();
        
	}
	
}






























