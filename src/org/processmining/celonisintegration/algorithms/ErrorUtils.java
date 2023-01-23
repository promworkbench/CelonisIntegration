package org.processmining.celonisintegration.algorithms;

import org.json.JSONObject;
import org.processmining.celonisintegration.objects.dataintegration.DataModel;
import org.processmining.celonisintegration.objects.dataintegration.DataModelTable;
import org.processmining.celonisintegration.objects.dataintegration.DataPool;
import org.processmining.celonisintegration.objects.studio.Package;
import org.processmining.celonisintegration.objects.studio.Space;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.AnalysisStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataModelStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.DataPoolStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.PackageStatus;
import org.processmining.celonisintegration.parameters.UploadEventLogParameter.SpaceStatus;
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
		} catch (IllegalArgumentException e) {
			throw new AccessException("URL is not in a correct form");
		} catch (HttpClientErrorException e) {
			throw new AccessException("Invalid API token");
		}

	}

	public static void checkParameterUpload(DataIntegration dataIntegration, Studio studio,
			UploadEventLogParameter parameters) throws Exception {
		if (parameters.getDataPoolStatus() == DataPoolStatus.NEW) {
			for (DataPool dp : dataIntegration.getDataPools()) {
				if (dp.getName().equals(parameters.getDataPool())) {
					throw new UserException("Data Pool name \"" + parameters.getDataPool() + "\" is already taken.");
				}
			}
		} else if (parameters.getDataPoolStatus() == DataPoolStatus.ADD) {
			if (parameters.getDataModelStatus() == DataModelStatus.NEW) {
				for (DataModel dm : dataIntegration.getDataModels()) {
					if (dm.getDp().getName().equals(parameters.getDataPoolReplace())
							&& dm.getName().equals(parameters.getDataModel())) {
						throw new UserException("Data Model name \"" + parameters.getDataModel()
								+ "\" is already taken in the " + parameters.getDataPoolReplace() + " Data Pool");
					}
				}
			} else if (parameters.getDataModelStatus() == DataModelStatus.ADD) {
				if (parameters.getTableStatus() == TableStatus.NEW) {
					for (DataModelTable table : dataIntegration.getDataModelTables()) {
						String dmId = dataIntegration.getDataModelId(parameters.getDataPoolReplace(),
								parameters.getDataModelReplace());
						if (table.getDmId().equals(dmId) && table.getName().equals(parameters.getTableName())) {
							throw new UserException("Table name \"" + parameters.getTableName()
									+ "\" is already taken in the " + parameters.getDataModelReplace() + " Data Model");
						}
					}
				}
			}
		}
		if (parameters.getDataPoolStatus() == DataPoolStatus.REPLACE
				|| parameters.getDataPoolStatus() == DataPoolStatus.ADD) {
			if (parameters.getDataPoolReplace() == null) {
				throw new UserException("Data Pool name can not be empty");
			}
		} else {
			if (parameters.getDataPool().replaceAll(" ", "").equals("")) {
				throw new UserException("Data Pool name can not be empty");
			}
		}
		if (parameters.getDataModelStatus() == DataModelStatus.REPLACE
				|| parameters.getDataModelStatus() == DataModelStatus.ADD) {
			if (parameters.getDataModelReplace() == null) {
				throw new UserException("Data Model name can not be empty");
			}
		} else {
			if (parameters.getDataModel().replaceAll(" ", "").equals("")) {
				throw new UserException("Data Model name can not be empty");
			}
		}
		if (parameters.getTableStatus() == TableStatus.REPLACE) {
			if (parameters.getTableNameReplace() == null) {
				throw new UserException("Table name can not be empty");
			}
		} else {
			if (parameters.getTableName().replaceAll(" ", "").equals("")) {
				throw new UserException("Table name can not be empty");
			}
		}
		if (dataIntegration.getPermissionProcessAnalytics()) {
			if (parameters.getWorkspaceStatus() == WorkspaceStatus.REPLACE
					|| parameters.getWorkspaceStatus() == WorkspaceStatus.ADD) {
				if (parameters.getWorkspaceReplace() == null) {
					throw new UserException("Workspace name can not be empty");
				}
			} else {
				if (parameters.getWorkspace().replaceAll(" ", "").equals("")) {
					throw new UserException("Workspace name can not be empty");
				}
			}
			if (parameters.getAnalysisStatus() == AnalysisStatus.REPLACE) {
				if (parameters.getAnalysisReplace() == null) {
					throw new UserException("Analysis name can not be empty");
				}
			} else {
				if (parameters.getAnalysis().replaceAll(" ", "").equals("")) {
					throw new UserException("Analysis name can not be empty");
				}
			}
		}
		
		if (parameters.getSpaceStatus() == SpaceStatus.NEW) {
			if (parameters.getSpaceNew() == null) {
				throw new UserException("Space name can not be empty");
			}
		}
		if (parameters.getSpaceStatus() == SpaceStatus.ADD) {
			if (parameters.getPackageStatus() == PackageStatus.NEW) {
				if (parameters.getPackageNameNew() == null) {
					throw new UserException("Package name can not be empty");
				}
				if (parameters.getPackageKeyNew() == null) {
					throw new UserException("Package key can not be empty");
				}
				for (Space space : studio.getMapSpace().keySet()) {
					if (space.getId().equals(parameters.getSpaceCombo().getId())) {
						for (Package p : studio.getMapSpace().get(space)) {
							if (p.getKey().equals(parameters.getPackageKeyNew())) {
								throw new UserException("Package key " + parameters.getPackageKeyNew()
										+ " is already taken in the Space " + parameters.getSpaceCombo().getName()
										+ ".");
							}
						}
						break;
					}
				}

			}
		}
		if (parameters.getPackageStatus() == PackageStatus.NEW) {
			if (parameters.getPackageNameNew() == null) {
				throw new UserException("Package name can not be empty");
			}
			if (parameters.getPackageKeyNew() == null) {
				throw new UserException("Package key can not be empty");
			}
			for (Package p : studio.getListPackage()) {
				if (p.getKey().equals(parameters.getPackageKeyNew())) {
					throw new UserException("A package with key " + parameters.getPackageKeyNew() + " already exists.");
				}
			}
			if (parameters.getsAnalysisNew().equals(parameters.getPackageKeyNew())) {
				throw new UserException(
						"Analysis name " + parameters.getsAnalysisNew() + " can not be the same as the Package key.");
			}
		}
		if (parameters.getPackageStatus() == PackageStatus.REPLACE) {
			if (parameters.getsAnalysisNew().equals(parameters.getPackageCombo().getName())) {
				throw new UserException(
						"Analysis name " + parameters.getsAnalysisNew() + " can not be the same as the Package key.");
			}
		}
		if (parameters.getPackageStatus() == PackageStatus.ADD) {
			for (Package pack : studio.getMapPackage().keySet()) {
				if (pack.getId().equals(parameters.getPackageCombo().getId())) {
					for (org.processmining.celonisintegration.objects.studio.Analysis ana : studio.getMapPackage()
							.get(pack)) {
						if (ana.getName().equals(parameters.getsAnalysisNew())) {
							throw new UserException("Analysis name " + parameters.getsAnalysisNew()
									+ " is already taken in the Package " + parameters.getPackageCombo().getName()
									+ ".");
						}
					}
				}
			}
		}

	}

	public static void checkUniqueColumn(String caseCol, String actCol) throws UserException {
		if (caseCol.equals(actCol)) {
			throw new UserException("Case ID column and Activity column must be different");
		}
	}

	public static String checkDownloadOlap(String targetUrl, HttpEntity<String> postRequest, RestTemplate restTemplate)
			throws Exception {
		ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.POST, postRequest, String.class);
		JSONObject body = new JSONObject(response.getBody());
		if (body.getString("exportStatus").equals("FAILED")) {
			String message = "No permission to download table from the requested data model. Original message from Celonis: \""
					+ body.getString("message") + "\"";
			throw new UserException(message);
		} else {
			return body.getString("id");
		}
	}
}
