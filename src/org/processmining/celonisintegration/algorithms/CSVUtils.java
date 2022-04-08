package org.processmining.celonisintegration.algorithms;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;

import com.opencsv.CSVWriter;

public class CSVUtils {
	static void createActCSV (XLog log, File output, String casePrefix, String timestamp) throws Exception {	
		Map<String, Set<String>> keyDict = new HashMap<String, Set<String>>();		
		Set<String> keyEvent = new HashSet<String>();
		Set<String> keyTrace = new HashSet<String>();
		
		for (XTrace trace : log) {
			Set<String> key_trace = trace.getAttributes().keySet();
			if (!keyTrace.containsAll(key_trace)) {
				key_trace.removeAll(keyTrace);
				for (String tr: key_trace) {
					String add = casePrefix + tr;
					keyTrace.add(add);
				}
			}
		}	
		for (XAttribute att : log.getGlobalEventAttributes()) {
			keyEvent.add(att.getKey());
		}
		keyDict.put("keyEvent", keyEvent);
		keyDict.put("keyTrace", keyTrace);
		FileWriter outputFile = new FileWriter(output);
		CSVWriter writer = new CSVWriter(outputFile);	
		List<String> header = new ArrayList<>();
		for (String key: keyDict.keySet()) {
			header.addAll(keyDict.get(key));
		}
		String[] r = header.toArray(new String[header.size()]);
		
		writer.writeNext(r);
		System.out.println(header);
		for (XTrace trace: log) {		
			for (XEvent event: trace) {
				List<Object> line = new ArrayList<>();
				for (String key: keyDict.get("keyTrace")) {
					key = key.substring(5);
					if (trace.getAttributes().get(key) == null) {
						line.add("");
					}
					else {
						line.add(trace.getAttributes().get(key).toString());
					}
				}
				for (String key: keyDict.get("keyEvent")) {		
					if (key.equals(timestamp)) {
						String time = event.getAttributes().get(key).toString();
						if (!time.contains(".")) {							
							String[] time1 = time.split("\\+");
							String add = time1[0] + ".000" + "+" + time1[1]; 
							line.add(add);
						}
						else {
							line.add(event.getAttributes().get(key).toString());
						}
						
					}
					else {
						line.add(event.getAttributes().get(key).toString());
					}
				}
				String[] line_add = line.toArray(new String[line.size()]);
				writer.writeNext(line_add);
			}
			
		}
		writer.close();
		
	}
	
	static void createCaseCSV(XLog log, File output, String caseId, String casePrefix) throws Exception {
		FileWriter outputFile = new FileWriter(output);
		CSVWriter writer = new CSVWriter(outputFile);	
		String [] header = new String[] {caseId};
		
		String caseName = caseId.substring(casePrefix.length());
		System.out.println(caseName);
		writer.writeNext(header);
		List<String> listCase = new ArrayList<String>();
		for (XTrace trace : log) {
			String val = trace.getAttributes().get(caseName).toString();
			if (val.equals("")) {
				throw new Exception("Case ID column and case prefix do not match or invalid");
			}
			if (!listCase.contains(val)) {
				listCase.add(trace.getAttributes().get(caseName).toString());
				String [] line = new String[] {val};
				writer.writeNext(line);				
			}
		}		
		writer.close();
	}
	
	public static List<String> getColumns(XLog log, String casePrefix) {		
		Set<String> keyEvent = new HashSet<String>();
		Set<String> keyTrace = new HashSet<String>();
		
		for (XTrace trace : log) {
			Set<String> key_trace = trace.getAttributes().keySet();
			if (!keyTrace.containsAll(key_trace)) {
				key_trace.removeAll(keyTrace);
				for (String tr: key_trace) {
					String add = casePrefix + tr;
					keyTrace.add(add);
				}
			}
		}	
		for (XAttribute att : log.getGlobalEventAttributes()) {
			keyEvent.add(att.getKey());
		}
		List<String> cols = new ArrayList<String>();
		for(String c: keyEvent) {
			cols.add(c);
		}
		for(String c: keyTrace) {
			cols.add(c);
		}
		return cols;
	}
	
	static void writeToCsv(String content, File output) throws IOException {
		FileWriter outputFile = new FileWriter(output);
		CSVWriter writer = new CSVWriter(outputFile);	
		String[] content1 = content.split("\\n");
		for (int i = 0; i<content1.length; i++) {
			String [] row = content1[i].split(",");
			writer.writeNext(row);
		}
		writer.close();
	}
	
	static CSVFile  importFromStream(final Path input, final String filename,
			final long fileSizeInBytes) throws Exception {		
		return new CSVFileReferenceUnivocityImpl(input);
	}
}






























