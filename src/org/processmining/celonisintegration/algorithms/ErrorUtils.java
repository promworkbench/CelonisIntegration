package org.processmining.celonisintegration.algorithms;

import org.json.JSONObject;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModel;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataModelTable;
import org.processmining.celonisintegration.algorithms.CelonisObject.DataPool;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.AnalysisStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataModelStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataPoolStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.TableStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.WorkspaceStatus;
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
	
	public static void checkParameterUpload(DataIntegration celonis, UploadEventLogParameter parameters) throws Exception {
		if (parameters.getDataPoolStatus() == DataPoolStatus.NEW) {
			for (DataPool dp: celonis.getDataPools()) {
				if (dp.getName().equals(parameters.getDataPool())) {
					throw new UserException("Data Pool name \"" + parameters.getDataPool() + "\" is already taken.");
				}
			}
		}
		else if (parameters.getDataPoolStatus() == DataPoolStatus.ADD){
			if (parameters.getDataModelStatus() == DataModelStatus.NEW) {
				for (DataModel dm: celonis.getDataModels()) {
					if (dm.getDp().getName().equals(parameters.getDataPoolReplace()) && dm.getName().equals(parameters.getDataModel())) {
						throw new UserException("Data Model name \"" + parameters.getDataModel() + 
								"\" is already taken in the " + parameters.getDataPoolReplace() + " Data Pool");
					}
				}
			}
			else if (parameters.getDataModelStatus() == DataModelStatus.ADD) {
				if (parameters.getTableStatus() == TableStatus.NEW) {
					for (DataModelTable table: celonis.getDataModelTables()) {
						String dmId = celonis.getDataModelId(parameters.getDataPoolReplace(), parameters.getDataModelReplace());
						if (table.getDmId().equals(dmId) && table.getName().equals(parameters.getTableName())) {
							throw new UserException("Table name \"" + parameters.getTableName() + 
									"\" is already taken in the " + parameters.getDataModelReplace() + " Data Model");
						}
					}
				}
			}
		}
		if (parameters.getDataPoolStatus() == DataPoolStatus.REPLACE || parameters.getDataPoolStatus() == DataPoolStatus.ADD) {
			if (parameters.getDataPoolReplace() == null) {
				throw new UserException("Data Pool name can not be empty");
			}
		}
		else {
			if (parameters.getDataPool().replaceAll(" ", "").equals("")) {
				throw new UserException("Data Pool name can not be empty");
			}
		}
		if (parameters.getDataModelStatus() == DataModelStatus.REPLACE || parameters.getDataModelStatus() == DataModelStatus.ADD) {
			if (parameters.getDataModelReplace() == null) {
				throw new UserException("Data Model name can not be empty");
			}
		}
		else {
			if (parameters.getDataModel().replaceAll(" ", "").equals("")) {
				throw new UserException("Data Model name can not be empty");
			}
		}
		if (parameters.getTableStatus() == TableStatus.REPLACE) {
			if (parameters.getTableNameReplace() == null) {
				throw new UserException("Table name can not be empty");
			}
		}
		else {
			if (parameters.getTableName().replaceAll(" ", "").equals("")) {
				throw new UserException("Table name can not be empty");
			}
		}
		if (parameters.getWorkspaceStatus() == WorkspaceStatus.REPLACE || parameters.getWorkspaceStatus() == WorkspaceStatus.ADD) {
			if (parameters.getWorkspaceReplace() == null) {
				throw new UserException("Workspace name can not be empty");
			}
		}
		else {
			if (parameters.getWorkspace().replaceAll(" ", "").equals("")) {
				throw new UserException("Workspace name can not be empty");
			}
		}
		if (parameters.getAnalysisStatus() == AnalysisStatus.REPLACE) {
			if (parameters.getAnalysisReplace() == null) {
				throw new UserException("Analysis name can not be empty");
			}
		}
		else {
			if (parameters.getAnalysis().replaceAll(" ", "").equals("")) {
				throw new UserException("Analysis name can not be empty");
			}
		}
		
	}
	
	
	public static void checkParameterWithoutWsUpload(DataIntegration celonis, UploadEventLogParameter parameters) throws Exception {
		if (parameters.getDataPoolStatus() == DataPoolStatus.NEW) {
			for (DataPool dp: celonis.getDataPools()) {
				if (dp.getName().equals(parameters.getDataPool())) {
					throw new UserException("Data Pool name \"" + parameters.getDataPool() + "\" is already taken.");
				}
			}
		}
		else if (parameters.getDataPoolStatus() == DataPoolStatus.ADD){
			if (parameters.getDataModelStatus() == DataModelStatus.NEW) {
				for (DataModel dm: celonis.getDataModels()) {
					if (dm.getDp().getName().equals(parameters.getDataPoolReplace()) && dm.getName().equals(parameters.getDataModel())) {
						throw new UserException("Data Model name \"" + parameters.getDataModel() + 
								"\" is already taken in the " + parameters.getDataPoolReplace() + " Data Pool");
					}
				}
			}
			else if (parameters.getDataModelStatus() == DataModelStatus.ADD) {
				if (parameters.getTableStatus() == TableStatus.NEW) {
					for (DataModelTable table: celonis.getDataModelTables()) {
						String dmId = celonis.getDataModelId(parameters.getDataPoolReplace(), parameters.getDataModelReplace());
						if (table.getDmId().equals(dmId) && table.getName().equals(parameters.getTableName())) {
							throw new UserException("Table name \"" + parameters.getTableName() + 
									"\" is already taken in the " + parameters.getDataModelReplace() + " Data Model");
						}
					}
				}
			}
		}
		if (parameters.getDataPoolStatus() == DataPoolStatus.REPLACE || parameters.getDataPoolStatus() == DataPoolStatus.ADD) {
			if (parameters.getDataPoolReplace() == null) {
				throw new UserException("Data Pool name can not be empty");
			}
		}
		else {
			if (parameters.getDataPool().replaceAll(" ", "").equals("")) {
				throw new UserException("Data Pool name can not be empty");
			}
		}
		if (parameters.getDataModelStatus() == DataModelStatus.REPLACE || parameters.getDataModelStatus() == DataModelStatus.ADD) {
			if (parameters.getDataModelReplace() == null) {
				throw new UserException("Data Model name can not be empty");
			}
		}
		else {
			if (parameters.getDataModel().replaceAll(" ", "").equals("")) {
				throw new UserException("Data Model name can not be empty");
			}
		}
		if (parameters.getTableStatus() == TableStatus.REPLACE) {
			if (parameters.getTableNameReplace() == null) {
				throw new UserException("Table name can not be empty");
			}
		}
		else {
			if (parameters.getTableName().replaceAll(" ", "").equals("")) {
				throw new UserException("Table name can not be empty");
			}
		}
		
	}
	
	public static void checkUniqueColumn(String caseCol, String actCol) throws UserException {
		if (caseCol.equals(actCol)) {
			throw new UserException("Case ID column and Activity column must be different");
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































