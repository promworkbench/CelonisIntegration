package org.processmining.celonisintegration.algorithms;

import org.json.JSONObject;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModel;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataPool;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ErrorUtils {	
	
	
	public static void checkLoginValidation(String url, String token) throws Exception {
		if (url.replaceAll(" ", "").equals("") || token.replaceAll(" ", "").equals("")) {
			throw new AccessException("URL and Token must not be empty");
		}
		RestTemplate restTemplate = new RestTemplate();
		String targetUrl = String.format(url + "/integration/api/pools");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> jobRequest = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> r = restTemplate.exchange(targetUrl, HttpMethod.GET, jobRequest, String.class);
		}
		catch(IllegalArgumentException e) {
			throw new AccessException("URL is not in a correct form");
		}
		catch(HttpClientErrorException e) {
			throw new AccessException("Invalid API token");
		}		
		
	}
	
	public static void checkDuplicateParameterUpload(DataIntegration celonis, String dp, String dm, String tableName) throws Exception {
		if (!dp.equals("")) {
			for (DataPool dataPool: celonis.getDataPools()) {
				if (dp.equals(dataPool.getName())) {
					throw new UserException("Data Pool name is already taken");
				}
			}
		}		
		
		if (!dm.equals("")) {
			for (DataModel dataModel: celonis.getDataModels()) {
				if (dm.equals(dataModel.getName())) {
					throw new UserException("Data Model name is already taken");
				}
			}
		}
		
		if (!tableName.equals("")) {
			for (DataModelTable table: celonis.getDataModelTables()) {
				if (tableName.equals(table.getName())) {
					throw new UserException("Table name is already taken");
				}
			}	
		}
		
	}
	
	public static String checkDownloadOlap(String targetUrl, HttpEntity<String> postRequest, RestTemplate restTemplate) throws Exception{
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, postRequest, String.class);
		JSONObject body = new JSONObject(response.getBody());
		if (body.getString("exportStatus").equals("FAILED")) {
			String message = "No permission to download table from the requested data model. Original message from Celonis: \"" + body.getString("message") + "\"";
			throw new UserException(message);
		}
		else {
			return body.getString("id");
		}
	}
}































