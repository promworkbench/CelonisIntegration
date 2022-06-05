package org.processmining.celonisintegration.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.deckfour.xes.info.XAttributeInfo;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class XESUtils {

	public static File createActCSV(XLog log, String casePrefix, int length) throws IOException {
		File csv = File.createTempFile("actCSV", ".csv");
		Map<String, Integer> columnMap = getColumnMap(log, casePrefix);
		int csvLength = length != 0 ? length : Integer.MAX_VALUE;
		int currentRow = 1;

		FileWriter outputFile = new FileWriter(csv);
		CSVWriter writer = new CSVWriter(outputFile);

		String[] header = getHeader(columnMap);
		writer.writeNext(header);
		String[] row = new String[header.length];
		outerloop: for (XTrace trace : log) {
			for (XEvent event : trace) {
				if (currentRow > csvLength) {
					break outerloop;
				}
				for (XAttribute attr : trace.getAttributes().values()) {
					assert columnMap.containsKey(casePrefix + attr.getKey()) : "Column unkown " + attr.getKey();
					row[columnMap.get(casePrefix + attr.getKey())] = convertAttribute(attr);

				}
				for (XAttribute attr : event.getAttributes().values()) {
					assert columnMap.containsKey(attr.getKey()) : "Column unkown " + attr.getKey();
					row[columnMap.get(attr.getKey())] = convertAttribute(attr);

				}
				writer.writeNext(row);
				currentRow++;
			}
		}
		writer.close();
		return csv;
	}

	public static File createCaseCSV(XLog log, String casePrefix) throws Exception {
		File csv = File.createTempFile("caseCSV", ".csv");
		FileWriter outputFile = new FileWriter(csv);
		CSVWriter writer = new CSVWriter(outputFile);
		Map<String, Integer> columnMap = getColumnMap(log, casePrefix);
		Map<String, Integer> columnCaseMap = new HashMap<String, Integer>();

		int index = 0;
		for (String att : columnMap.keySet()) {
			if (att.startsWith(casePrefix)) {
				columnCaseMap.put(att, index);
				index++;
			}
		}
		String[] header = new String[columnCaseMap.size()];
		for (String att : columnCaseMap.keySet()) {
			header[columnCaseMap.get(att)] = att;
		}
		writer.writeNext(header);
		String[] row = new String[header.length];
		for (XTrace trace : log) {
			for (XAttribute attr : trace.getAttributes().values()) {
				assert columnCaseMap.containsKey(casePrefix + attr.getKey()) : "Column unkown " + attr.getKey();
				row[columnCaseMap.get(casePrefix + attr.getKey())] = convertAttribute(attr);

			}
			writer.writeNext(row);
		}

		writer.close();
		return csv;
	}

	public static Map<String, Integer> getColumnMap(XLog log, String casePrefix) {
		Map<String, Integer> columnMap = new HashMap<String, Integer>();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);

		List<String> headerList = new ArrayList<String>();

		int i = 0;
		XAttributeInfo traceAttributeInfo = logInfo.getTraceAttributeInfo();
		for (XAttribute attr : traceAttributeInfo.getAttributes()) {

			headerList.add(attr.getKey());
			columnMap.put(casePrefix + attr.getKey(), i);
			i++;

		}
		XAttributeInfo eventAttributeInfo = logInfo.getEventAttributeInfo();
		for (XAttribute attr : eventAttributeInfo.getAttributes()) {

			if (headerList.contains(attr.getKey())) {
				headerList.add("event_" + attr.getKey());
				columnMap.put(attr.getKey(), i);
			} else {
				headerList.add(attr.getKey());
				columnMap.put(attr.getKey(), i);
			}
			i++;

		}
		return columnMap;
	}

	public static List<String> getStringColumns(XLog log, String casePrefix) {
		List<String> res = new ArrayList<String>();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
		XAttributeInfo traceAttributeInfo = logInfo.getTraceAttributeInfo();
		for (XAttribute attr : traceAttributeInfo.getAttributes()) {
			if (!(attr instanceof XAttributeTimestamp)) {
				res.add(casePrefix + attr.getKey());
			}

		}
		XAttributeInfo eventAttributeInfo = logInfo.getEventAttributeInfo();
		for (XAttribute attr : eventAttributeInfo.getAttributes()) {
			if (!(attr instanceof XAttributeTimestamp)) {
				res.add(attr.getKey());
			}
		}

		return res;
	}

	public static List<String> getTimestampCols(XLog log, String casePrefix) {
		List<String> res = new ArrayList<String>();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);
		XAttributeInfo traceAttributeInfo = logInfo.getTraceAttributeInfo();
		for (XAttribute attr : traceAttributeInfo.getAttributes()) {
			if (attr instanceof XAttributeTimestamp) {
				res.add(attr.getKey());
			}

		}
		XAttributeInfo eventAttributeInfo = logInfo.getEventAttributeInfo();
		for (XAttribute attr : eventAttributeInfo.getAttributes()) {
			if (attr instanceof XAttributeTimestamp) {
				res.add(attr.getKey());
			}
		}

		return res;
	}

	public static String[] getHeader(Map<String, Integer> columnMap) {
		String[] res = new String[columnMap.size()];

		for (String i : columnMap.keySet()) {
			res[columnMap.get(i)] = i;
		}
		return res;
	}

	public static String[][] getSnippet(File csv, int length) throws IOException, CsvValidationException {
		Reader reader = Files.newBufferedReader(Paths.get(csv.getAbsolutePath()));
		CSVReader csvReader = new CSVReader(reader);
		String[] header = csvReader.readNext();
		String[][] res = new String[length][header.length];
		String[] row;
		for (int i = 0; i < length; i++) {
			if ((row = csvReader.readNext()) != null) {
				res[i] = row;
			}
			else {
				break;
			}
		}
		return res;
	}

	static void writeToCsv(String content, File output) throws IOException {
		FileWriter outputFile = new FileWriter(output);
		CSVWriter writer = new CSVWriter(outputFile);
		String[] content1 = content.split("\\n");
		for (int i = 0; i < content1.length; i++) {
			String[] row = content1[i].split(",");
			writer.writeNext(row);
		}
		writer.close();
	}

	static CSVFile importFromStream(final Path input, final String filename, final long fileSizeInBytes)
			throws Exception {
		return new CSVFileReferenceUnivocityImpl(input);
	}

	public static File changeColumnName(File file, HashMap<String, String> mapping)
			throws IOException, CsvValidationException {
		File tempFile = File.createTempFile("column_name_updated", ".csv");
		FileWriter outputFile = new FileWriter(tempFile);
		CSVWriter writer = new CSVWriter(outputFile);

		Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
		CSVReader csvReader = new CSVReader(reader);
		String[] header = csvReader.readNext();

		for (int i = 0; i < header.length; i++) {
			String col = header[i];
			System.out.println(col);
			if (mapping.containsKey(col)) {
				System.out.println(mapping.get(col));
				header[i] = mapping.get(col);
			}
		}
		writer.writeNext(header);
		String[] next;
		while ((next = csvReader.readNext()) != null) {
			writer.writeNext(next);
		}
		writer.close();
		return tempFile;
	}

	private static String convertAttribute(XAttribute attribute) {
		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		if (attribute instanceof XAttributeTimestamp) {
			Date timestamp = ((XAttributeTimestamp) attribute).getValue();
			return dateFormat.format(timestamp);
		} else {
			return attribute.toString();
		}
	}
}
