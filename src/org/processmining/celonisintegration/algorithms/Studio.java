package org.processmining.celonisintegration.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.celonisintegration.objects.studio.Analysis;
import org.processmining.celonisintegration.objects.studio.Package;
import org.processmining.celonisintegration.objects.studio.Space;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class Studio {
	private String url;
	private String apiToken;
	private List<Package> listPackage;
	private HashMap<Space, List<Package>> mapSpace;
	private HashMap<Package, List<Analysis>> mapPackage;
	private boolean canCreateSpace;
	private boolean canReplaceSpace;
	private List<Space> listEditableSpaces;
	private final String editAllSpaces = "EDIT_ALL_SPACES";
	private final String managePermissions = "MANAGE_PERMISSIONS";
	private final String createSpace = "CREATE_SPACE";
	private final String deleteAllSpaces = "DELETE_ALL_SPACES";
	private final String createPackage = "CREATE_PACKAGE";
	private final String deleteAllPackages = "DELETE_ALL_PACKAGES";
	private final String editAllPackages = "EDIT_ALL_PACKAGES";
	private final String serviceName = "package-manager";

	public Studio(String url, String apiToken) throws UserException {
		this.url = url;
		this.apiToken = apiToken;
		this.canCreateSpace = false;
		this.canReplaceSpace = false;
		this.checkPermission();
		this.mapSpace = new HashMap<Space, List<Package>>();
		this.mapPackage = new HashMap<Package, List<Analysis>>();
		this.listPackage = new ArrayList<Package>();
		this.listEditableSpaces = new ArrayList<Space>();
		this.updateStudio();

	}

	private void checkPermission() throws UserException {
		// get permission from /api/cloud/permissions to update listPermission
		checkStudioPermission();
		// get permission for each space from /package-manager/api/spaces to update mapSpacePermission
	}

	private void checkStudioPermission() throws UserException {
		List<String> listPermission = new ArrayList<>();

		String targetUrl = this.url + "/api/cloud/permissions";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<Void>(headers);
		ResponseEntity<String> r = APIUtils.getRequest(targetUrl, jobRequest, "Check studio permission");
		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject obj = body.getJSONObject(i);
			if (obj.getString("serviceName").equals(serviceName)) {
				JSONArray allPermission = obj.getJSONArray("permissions");
				for (int j = 0; j < allPermission.length(); j++) {
					listPermission.add(allPermission.getString(j));
				}
				break;
			}
		}
		if (listPermission.contains(createSpace)) {
			this.canCreateSpace = true;
		}
		if (listPermission.contains(editAllSpaces) && listPermission.contains(createSpace)
				&& listPermission.contains(deleteAllSpaces)) {
			this.canReplaceSpace = true;
		}

	}

	private void updateStudio() throws UserException {
		List<Space> listSpace = this.getListSpace();
		for (Space space : listSpace) {
			if (this.listEditableSpaces.contains(space)) {
				List<Package> listPackage = this.getListPackage(space.getId());
				if (!this.mapSpace.containsKey(space)) {
					this.mapSpace.put(space, listPackage);
				} else {
					for (Package p : listPackage) {
						if (!this.mapSpace.get(space).contains(p)) {
							this.mapSpace.get(space).add(p);
						}
					}
				}
				for (Package p : listPackage) {
					if (!this.listPackage.contains(p)) {
						this.listPackage.add(p);
					}
					List<Analysis> listAnalysis = this.getListAnalysis(p.getId());
					if (!this.mapPackage.containsKey(p)) {
						this.mapPackage.put(p, listAnalysis);
					} else {
						for (Analysis ana : listAnalysis) {
							if (!this.mapPackage.get(p).contains(ana)) {
								this.mapPackage.get(p).add(ana);
							}
						}
					}
				}
			}

		}
	}

	public void deleteSpace(String spaceId) throws UserException {
		String spaceName = this.getSpaceNameById(spaceId);
		String targetUrl = this.url + "/package-manager/api/spaces/delete/" + spaceId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("id", spaceId);
		request.put("name", spaceName);
		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Delete Space " + spaceName);
		List<Space> listSpace = new ArrayList<>();
		for (Space space : this.listEditableSpaces) {
			if (space.getId().equals(spaceId)) {
				List<Package> listPackages = this.mapSpace.get(space);
				for (Package p : listPackages) {
					this.mapPackage.remove(p);
				}
				this.mapSpace.remove(space);
			} else {
				listSpace.add(space);
			}
		}
		this.listEditableSpaces = listSpace;
	}

	public void deletePackage(String packageId) throws UserException {
		String packageName = this.getPackageNameById(packageId);
		String targetUrl = this.url + "/package-manager/api/packages/" + packageId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("id", packageId);
		request.put("name", packageName);
		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		APIUtils.deleteRequest(targetUrl, jobRequest, "Delete Package " + packageName);
		for (Space space : this.listEditableSpaces) {
			List<Package> listPackages = this.mapSpace.get(space);
			List<Package> newListPackages = new ArrayList<>();
			for (Package p : listPackages) {
				if (!p.getId().equals(packageId)) {
					newListPackages.add(p);
				}
			}
			this.mapSpace.replace(space, newListPackages);
		}
		HashMap<Package, List<Analysis>> newMap = new HashMap<>();
		for (Package p : this.mapPackage.keySet()) {
			if (!p.getId().equals(packageId)) {
				newMap.put(p, this.mapPackage.get(p));
			}
		}
		this.mapPackage = newMap;
		List<Package> newListPackage = new ArrayList<>();
		for (Package p: this.listPackage) {
			if (!p.getId().equals(packageId)) {
				newListPackage.add(p);
			}
		}
		this.listPackage = newListPackage;
	}

	public String getPackageNameById(String packageId) {
		for (Package p : this.listPackage) {
			if (p.getId().equals(packageId)) {
				return p.getName();
			}
		}
		return "-1";
	}

	public String getSpaceNameById(String spaceId) {
		for (Space space : this.mapSpace.keySet()) {
			if (space.getId().equals(spaceId)) {
				return space.getName();
			}
		}
		return "-1";
	}

	public List<Space> getListSpace() throws UserException {
		List<Space> res = new ArrayList<Space>();
		String targetUrl = this.url + "/package-manager/api/spaces";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<Void>(headers);
		ResponseEntity<String> r = APIUtils.getRequest(targetUrl, jobRequest, "Get all Spaces");
		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject spaceObj = body.getJSONObject(i);
			Space space = new Space(spaceObj.getString("name"), spaceObj.getString("id"));
			JSONArray allPermissionArray = spaceObj.getJSONArray("permissions");
			List<String> allPermission = new ArrayList<>();
			for (int j = 0; j < allPermissionArray.length(); j++) {
				allPermission.add(allPermissionArray.getString(j));
			}
			if (allPermission.contains(editAllPackages) && allPermission.contains(createPackage)
					&& allPermission.contains(deleteAllPackages)) {
				this.listEditableSpaces.add(space);
			}
			res.add(space);
		}
		return res;
	}

	public List<Package> getListPackage(String spaceId) throws UserException {
		List<Package> res = new ArrayList<Package>();
		String targetUrl = this.url + "/package-manager/api/nodes/tree?spaceId=" + spaceId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<Void>(headers);
		ResponseEntity<String> r = APIUtils.getRequest(targetUrl, jobRequest, "Get all Packages in Space " + spaceId);
		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject node = body.getJSONObject(i);
			if (node.getString("nodeType").equals("PACKAGE")) {
				res.add(new Package(node.getString("key"), node.getString("name"), node.getString("id"), spaceId));
			}
		}

		return res;
	}

	public List<Analysis> getListAnalysis(String packageId) throws UserException {
		List<Analysis> res = new ArrayList<Analysis>();
		String targetUrl = this.url + "/package-manager/api/nodes/by-root-id/" + packageId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> jobRequest = new HttpEntity<Void>(headers);
		ResponseEntity<String> r = APIUtils.getRequest(targetUrl, jobRequest,
				"Get all Analyses in Package " + packageId);
		JSONArray body = new JSONArray(r.getBody());
		for (int i = 0; i < body.length(); i++) {
			JSONObject node = body.getJSONObject(i);
			if (node.getString("nodeType").equals("ASSET") && node.getString("assetType").equals("ANALYSIS")) {
				res.add(new Analysis(node.getString("key"), node.getString("name"), node.getString("id"),
						node.getString("spaceId"), packageId));
			}
		}

		return res;
	}

	public String createSpace(String spaceName) throws UserException {
		String targetUrl = this.url + "/package-manager/api/spaces";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", spaceName);
		request.put("iconReference", "earth");
		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Space");
		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");
	}

	public String createPackage(String packageKey, String packageName, String spaceId) throws UserException {
		if (this.isPackageKeyAlreadyThere(packageKey)) {
			throw new UserException("A package with key " + packageKey + " already exists.");
		}
		String targetUrl = this.url + "/package-manager/api/nodes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("key", packageKey);
		request.put("name", packageName);
		request.put("nodeType", "PACKAGE");
		request.put("serializedContent", "packageType: APP\nvariables: []\ndependencies: []\n");
		request.put("spaceId", spaceId);

		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Package");
		JSONObject body = new JSONObject(r.getBody());
		return body.getString("id");
	}

	public String createAnalysis(String anaKey, String anaName, String packageName, String packageId,
			String dataModelId) throws UserException {
		if (this.isAnalysisKeyInPackage(anaKey, packageId)) {
			throw new UserException("An analysis with key " + anaKey + " already exists.");
		}

		String targetUrl = this.url + "/process-analytics/analysis/v2/api/analysis";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject request = new JSONObject();
		request.put("name", anaName);
		request.put("key", anaKey);
		request.put("dataModelId", dataModelId);
		request.put("knowledgeModelId", "");
		request.put("rootNodeKey", packageName);
		request.put("rootNodeId", packageId);
		request.put("parentNodeKey", packageName);
		request.put("parentNodeId", packageId);
		request.put("rootWithKey", packageName);
		System.out.println(request);
		HttpEntity<String> jobRequest = new HttpEntity<String>(request.toString(), headers);
		ResponseEntity<String> r = APIUtils.postStringRequest(targetUrl, jobRequest, "Create new Analysis");
		JSONObject body = new JSONObject(r.getBody());
		return body.getJSONObject("analysis").getString("id");
	}

	public Boolean isAnalysisKeyInPackage(String analysisKey, String packageId) {
		for (Package p : this.listPackage) {
			if (p.getId().equals(packageId)) {
				List<Analysis> listAna = this.mapPackage.get(p);
				for (Analysis ana : listAna) {
					if (ana.getKey().equals(analysisKey)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Boolean isPackageKeyAlreadyThere(String packageKey) {
		for (Package p : this.listPackage) {
			if (p.getKey().equals(packageKey)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) throws UserException {
		Studio s = new Studio("https://academic-hieu-le-rwth-aachen-de.eu-2.celonis.cloud/",
				"MmQ3YWQyZGUtM2I5NC00ZjZlLWFlYzYtYzBhM2U3OTJkNjgyOndwbmhjS0RNdVdPZkJMNHUwQlNEVWJ6cXFyU3oxRHNoSW5Pb21MQjloTzhW");
		//		String spaceId = s.createSpace("test-eclipse");
		//		String packageId = s.createPackage("test-pack-eclipse", "test-pack-eclipse", spaceId);
		//		String anaId = s.createAnalysis("test-ana-eclipse", "test-ana-eclipse", "test-pack-eclipse", packageId,
		//				"ce6c7f38-c357-4218-918a-8c4a2a9141cd");

		//		JFrame f = new JFrame("frame");
		//		Space s1[] = { new Space("a", "12a"),new Space("b", "12b") };
		//
		//		// create checkbox
		//		JComboBox c1 = new JComboBox(s1);
		//		c1.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				Space s = (Space) c1.getSelectedItem();
		//				System.out.println(s.getId());
		//			}
		//		});
		//		JPanel p = new JPanel();
		//		p.add(c1);
		//		f.add(p);
		//
		//		// set the size of frame
		//		f.setSize(400, 300);
		//
		//		f.show();

		for (Space sp : s.getListEditableSpaces()) {
			System.out.println(sp.getName());
		}
		System.out.println(s.isCanCreateSpace());
		System.out.println(s.isCanReplaceSpace());
	}

	public boolean isCanCreateSpace() {
		return canCreateSpace;
	}

	public boolean isCanReplaceSpace() {
		return canReplaceSpace;
	}

	public List<Package> getListPackage() {
		return listPackage;
	}

	public HashMap<Space, List<Package>> getMapSpace() {
		return mapSpace;
	}

	public HashMap<Package, List<Analysis>> getMapPackage() {
		return mapPackage;
	}

	public List<Space> getListEditableSpaces() {
		return listEditableSpaces;
	}

}
