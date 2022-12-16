package org.processmining.celonisintegration.algorithms;

import java.io.IOException;
import java.nio.charset.Charset;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class APIUtils {
	public static ResponseEntity<String> getWithPayloadRequest(String url, HttpEntity<String> jobRequest,
			String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.exchange(url, HttpMethod.GET, jobRequest, String.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}

	public static ResponseEntity<String> getRequest(String url, HttpEntity<Void> jobRequest, String message)
			throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.exchange(url, HttpMethod.GET, jobRequest, String.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}

	public static void postEntityWithPayloadRequest(String url, HttpEntity<String> jobRequest, String message)
			throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.postForEntity(url, jobRequest, Object.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}

	public static void postEntityRequest(String url, HttpEntity<Object> jobRequest, String message)
			throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.postForEntity(url, jobRequest, Object.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}

	public static ResponseEntity<String> postStringRequest(String url, HttpEntity<String> jobRequest, String message)
			throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		//		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
		restTemplate.setErrorHandler(new APIUtils().new MyErrorHandler());
		try {
			return restTemplate.exchange(url, HttpMethod.POST, jobRequest, String.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}

	}

	public static void deleteRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.exchange(url, HttpMethod.DELETE, jobRequest, String.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
	}

	public static void putRequest(String url, HttpEntity<String> jobRequest, String message) throws UserException {
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.exchange(url, HttpMethod.PUT, jobRequest, String.class);
		} catch (Exception e) {
			throw new UserException(message + ": " + e.getMessage());
		}
	}

	public class MyErrorHandler extends DefaultResponseErrorHandler {
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			String error = new String(getResponseBody(response), getCharset(response));
			JSONObject errObj = new JSONObject(error);
			String mes = "";
			if (errObj.has("errors")) {
				JSONObject obj = errObj.getJSONArray("errors").getJSONObject(0);
				if (obj.has("error")) {
					mes = obj.getString("error");
				}
			}

			throw HttpClientErrorException.create(mes, response.getStatusCode(), response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		}
	}

	protected Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return (contentType != null ? contentType.getCharset() : null);
	}

	protected byte[] getResponseBody(ClientHttpResponse response) {
		try {
			return FileCopyUtils.copyToByteArray(response.getBody());
		} catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}
}
