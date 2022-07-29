package org.processmining.celonisintegration.algorithms;

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
			throw new Exception("Access error - URL and Token must not be empty");
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
			throw new IllegalArgumentException(e.getMessage());
		}
		catch(HttpClientErrorException e) {
			throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
		}		
		
	}
	
	public static void checkDuplicateParameterUpload(DataIntegration celonis, String dp, String dm, String tableName) throws Exception {
		if (!dp.equals("")) {
			for (DataPool dataPool: celonis.getDataPools()) {
				if (dp.equals(dataPool.getName())) {
					throw new Exception("Data Pool name is already taken");
				}
			}
		}		
		
		if (!dm.equals("")) {
			for (DataModel dataModel: celonis.getDataModels()) {
				if (dm.equals(dataModel.getName())) {
					throw new Exception("Data Model name is already taken");
				}
			}
		}
		
		if (!tableName.equals("")) {
			for (DataModelTable table: celonis.getDataModelTables()) {
				if (tableName.equals(table.getName())) {
					throw new Exception("Table name is already taken");
				}
			}	
		}
		
	}
}
