package org.processmining.celonisintegration.algorithms;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class APIUtils {
	public static ResponseEntity<String> getWithPayloadRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.exchange(url, HttpMethod.GET, jobRequest, String.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
		
	}
	
	public static ResponseEntity<String> getRequest(String url, HttpEntity<Void> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.exchange(url, HttpMethod.GET, jobRequest, String.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
		
	}	
	
	public static void postEntityWithPayloadRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.postForEntity(url, jobRequest, Object.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}
	
	public static void postEntityRequest(String url, HttpEntity<Object> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.postForEntity(url, jobRequest, Object.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}
	
	public static ResponseEntity<String> postStringRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException{
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.exchange(url, HttpMethod.POST, jobRequest, String.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
		
	}
	
	public static void deleteRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.exchange(url, HttpMethod.DELETE, jobRequest, String.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
	}
	
	public static void putRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.exchange(url, HttpMethod.PUT, jobRequest, String.class);
		}
		catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
	}
}
