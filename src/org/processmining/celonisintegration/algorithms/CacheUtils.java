package org.processmining.celonisintegration.algorithms;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;


public class CacheUtils {	
	
	public static void updateAccessInfo(String nameCache, String url, String token) throws IOException {
		String cachePath = Paths.get("").toAbsolutePath().toString() + "\\cache";
		String fileCache = nameCache + ".csv";
		boolean file = new File(cachePath).mkdirs();		
		
		List<String[]> update = new ArrayList<String[]>();
		String[] row = {url, token};
		update.add(row);
		FileWriter outputfile = new FileWriter(cachePath + "\\" + fileCache);
		CSVWriter writer = new CSVWriter(outputfile);
		writer.writeAll(update);			
		writer.close();
		
	}
	
	private static String getInfo(String filePath, String obj) throws IOException, CsvValidationException {
		Reader reader = Files.newBufferedReader(Paths.get(filePath));
        CSVReader csvReader = new CSVReader(reader);
        String[] nextLine = csvReader.readNext();
        if (obj.equals("URL")) {
        	return nextLine[0];
        }
        if (obj.equals("Token")) {
        	return nextLine[1];
        }
		return "";
	}
	
	
	
	public static String[] getAccessInfo(String nameCache) throws IOException, CsvValidationException {		
		String cachePath = Paths.get("").toAbsolutePath().toString() + "\\cache";
		String fileCache = nameCache + ".csv";
		String[] res = new String[2];
		boolean file = new File(cachePath).mkdirs();
		
		if (new File(cachePath, fileCache).exists()) {
			res[0] = getInfo(cachePath + "\\" + fileCache, "URL");
			res[1] = getInfo(cachePath + "\\" + fileCache, "Token");			
		}	
		return res;		
	}
}
