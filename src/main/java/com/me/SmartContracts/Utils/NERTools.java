 package com.me.SmartContracts.Utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.LinkedHashSet;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreAnnotations;


public class NERTools {

	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	public static JSONObject nerToJSON(String classifierPath, String content, String outFileName)
			throws JsonParseException, JsonMappingException, IOException {

		@SuppressWarnings("rawtypes")
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);
		JSONObject nerObj = new JSONObject();
		String resultInXml = classifier.classifyToString(content, "xml", true);
		try {
			JSONObject xmlJSONObj = XML.toJSONObject(resultInXml);
			JSONArray entities = xmlJSONObj.getJSONArray("wi");
			String dateValue = "";
			String personValue = "";
			String timeValue = "";
			String locationValue = "";
			String orgValue = "";
			String percentValue = "";
			String moneyValue = "";
			int pre1 = 0, pre2 = 0, pre3 = 0, pre4 = 0, pre5 = 0, pre6 = 0, pre7 = 0;
			for (int i = 0; i < entities.length(); i++) {
				JSONObject obj = entities.getJSONObject(i);
				if (obj.getString("entity").equals("DATE")) {
					if (dateValue.equals("")) {
						dateValue = obj.get("content").toString();
						pre1 = obj.getInt("num");
					} else {
						if (obj.get("content").toString().equals(",")) {
							pre1 = obj.getInt("num");
						} else {
							if (obj.getInt("num") - pre1 == 1) {
								dateValue = dateValue + "," + obj.get("content").toString();
							} else {
								dateValue = dateValue + ";" + obj.get("content").toString();
							}
							pre1 = obj.getInt("num");
						}
					}
				} else if (obj.getString("entity").equals("PERSON")) {
					if (personValue.equals("")) {
						personValue = obj.get("content").toString();
						pre2 = obj.getInt("num");
					} else {
						if (obj.getInt("num") - pre2 == 1) {
							personValue = personValue + " " + obj.get("content").toString();
						} else {
							personValue = personValue + ";" + obj.get("content").toString();
						}
						pre2 = obj.getInt("num");
					}
				} else if (obj.getString("entity").equals("ORGANIZATION")) {
					if (orgValue.equals("")) {
						orgValue = obj.get("content").toString();
						pre3 = obj.getInt("num");
					} else {
						if (obj.getInt("num") - pre3 == 1) {
							orgValue = orgValue + " " + obj.get("content").toString();
						} else {
							orgValue = orgValue + ";" + obj.get("content").toString();
						}
						pre3 = obj.getInt("num");
					}
				} else if (obj.getString("entity").equals("TIME")) {

					if (timeValue.equals("")) {
						timeValue = obj.get("content").toString();
						pre4 = obj.getInt("num");
					} else {
						if (obj.getInt("num") - pre4 == 1) {
							timeValue = timeValue + " " + obj.get("content").toString();
						} else {
							timeValue = timeValue + ";" + obj.get("content").toString();
						}
						pre4 = obj.getInt("num");
					}
				} else if (obj.getString("entity").equals("LOCATION")) {
					if (locationValue.equals("")) {
						locationValue = obj.get("content").toString();
						pre5 = obj.getInt("num");
					} else {
						if (obj.getInt("num") - pre5 == 1) {
							locationValue = locationValue + " " + obj.get("content").toString();
						} else {
							locationValue = locationValue + ";" + obj.get("content").toString();
						}
						pre5 = obj.getInt("num");
					}
				} else if (obj.getString("entity").equals("PERCENT")) {
					if (percentValue.equals("")) {
						percentValue = obj.get("content").toString();
						pre6 = obj.getInt("num");
					} else {
						if (obj.getInt("num") - pre6 == 1) {
							percentValue = percentValue + obj.get("content").toString();
						} else {
							percentValue = percentValue + ";" + obj.get("content").toString();
						}
						pre6 = obj.getInt("num");
					}
				} else if (obj.getString("entity").equals("MONEY")) {
					if (moneyValue.equals("")) {
						moneyValue = obj.get("content").toString();
						pre7 = obj.getInt("num");
					} else {
						if (obj.getInt("num") - pre7 == 1) {
							moneyValue = moneyValue + obj.get("content").toString();
						} else {
							moneyValue = moneyValue + ";" + obj.get("content").toString();
						}
						pre7 = obj.getInt("num");
					}
				}
			}

			JSONArray jEntArray = new JSONArray();
			JSONObject dateJSONObj = new JSONObject();
			dateJSONObj.put("DATE", dateValue);
			jEntArray.put(dateJSONObj);
			JSONObject personJSONObj = new JSONObject();
			personJSONObj.put("PERSON", personValue);
			jEntArray.put(personJSONObj);
			JSONObject orgJSONObj = new JSONObject();
			orgJSONObj.put("ORGANIZATION", orgValue);
			jEntArray.put(orgJSONObj);
			JSONObject timeJSONObj = new JSONObject();
			timeJSONObj.put("TIME", timeValue);
			jEntArray.put(timeJSONObj);
			JSONObject locJSONObj = new JSONObject();
			locJSONObj.put("LOCATION", locationValue);
			jEntArray.put(locJSONObj);
			JSONObject percentJSONObj = new JSONObject();
			percentJSONObj.put("PERCENT", percentValue);
			jEntArray.put(percentJSONObj);
			JSONObject moneyJSONObj = new JSONObject();
			moneyJSONObj.put("MONEY", moneyValue);
			jEntArray.put(moneyJSONObj);

			nerObj.put("NER", jEntArray);

			PrintWriter writer = new PrintWriter(outFileName, "UTF-8");
			writer.print(nerObj.toString(PRETTY_PRINT_INDENT_FACTOR));
			writer.close();

			// System.out.println(nerObj.toString(PRETTY_PRINT_INDENT_FACTOR));
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return nerObj;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, LinkedHashSet<String>> nerToMap(String classifierPath, String content) {
		HashMap<String, LinkedHashSet<String>> result = new HashMap<String, LinkedHashSet<String>>();
		@SuppressWarnings("rawtypes")
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);
		List<List<CoreLabel>> entities = classifier.classify(content);
		LinkedHashSet<String> dateList = new LinkedHashSet<String>();
		LinkedHashSet<String> personList = new LinkedHashSet<String>();
		LinkedHashSet<String> orgList = new LinkedHashSet<String>();
		LinkedHashSet<String> timeList = new LinkedHashSet<String>();
		LinkedHashSet<String> locList = new LinkedHashSet<String>();
		LinkedHashSet<String> percentList = new LinkedHashSet<String>();
		LinkedHashSet<String> moneyList = new LinkedHashSet<String>();
		int pre1 = 0, pre2 = 0, pre3 = 0, pre4 = 0, pre5 = 0, pre6 = 0, pre7 = 0;
		String dateStr = "", personStr = "", orgStr = "", timeStr = "", locStr = "", percentStr = "", moneyStr = "";

		for (List<CoreLabel> coreLabels : entities) {
			for (CoreLabel label : coreLabels) {
				String word = label.word();
				String category = label.get(CoreAnnotations.AnswerAnnotation.class);
				int index = Integer.parseInt(label.get(CoreAnnotations.PositionAnnotation.class));
				if (category.equals("DATE")) {
					if (index - pre1 != 1 && (!dateStr.equals(""))) {
						dateList.add(dateStr);
						dateStr = "";
					}
					if (word.equals(",")) {
						pre1 = index;
					} else {
						if (pre1 == 0 || index - pre1 != 1) {
							dateStr = word;
						} else if (index - pre1 == 1) {
							dateStr = dateStr + " " + word;
						}
						pre1 = index;
					}
				} else if (category.equals("PERSON")) {
					if (index - pre2 != 1 && (!personStr.equals(""))) {
						personList.add(personStr);
						personStr = "";
					}
					if (word.equals(",")) {
						pre2 = index;
					} else {
						if (pre2 == 0 || index - pre2 != 1) {
							personStr = word;
						} else if (index - pre2 == 1) {
							personStr = personStr + " " + word;
						}
						pre2 = index;
					}
				} else if (category.equals("ORGANIZATION")) {
					if (index - pre3 != 1 && (!orgStr.equals(""))) {
						orgList.add(orgStr);
						orgStr = "";
					}
					if (pre3 == 0 || index - pre3 != 1) {
						orgStr = word;
					} else if (index - pre3 == 1) {
						orgStr = orgStr + " " + word;
					}
					pre3 = index;
				} else if (category.equals("TIME")) {
					if (index - pre4 != 1 && (!timeStr.equals(""))) {
						timeList.add(timeStr);
						timeStr = "";
					}
					if (pre4 == 0 || index - pre4 != 1) {
						timeStr = word;
					} else if (index - pre4 == 1) {
						timeStr = timeStr + " " + word;
					}
					pre4 = index;
				} else if (category.equals("LOCATION")) {
					if (index - pre5 != 1 && (!locStr.equals(""))) {
						locList.add(locStr);
						locStr = "";
					}
					if (pre5 == 0 || index - pre5 != 1) {
						locStr = word;
					} else if (index - pre5 == 1) {
						locStr = locStr + " " + word;
					}
					pre5 = index;
				} else if (category.equals("PERCENT")) {
					if (index - pre6 != 1 && (!percentStr.equals(""))) {
						percentList.add(percentStr);
						percentStr = "";
					}
					if (pre6 == 0 || index - pre6 != 1) {
						percentStr = word;
					} else if (index - pre6 == 1) {
						percentStr = percentStr + word;
					}
					pre6 = index;
				} else if (category.equals("MONEY")) {
					if (index - pre7 != 1 && (!moneyStr.equals(""))) {
						moneyList.add(moneyStr);
						moneyStr = "";
					}
					if (pre7 == 0 || index - pre7 != 1) {
						moneyStr = word;
					} else if (index - pre7 == 1) {
						moneyStr = moneyStr + " " + word;
					}
					pre7 = index;
				}
			}
		}

		if (dateList.size() == 0 && !dateStr.equals("")) {
			dateList.add(dateStr);
		}
		if (personList.size() == 0 && !personStr.equals("")) {
			personList.add(personStr);
		}
		if (orgList.size() == 0 && !orgStr.equals("")) {
			orgList.add(orgStr);
		}
		if (timeList.size() == 0 && !timeStr.equals("")) {
			timeList.add(timeStr);
		}
		if (locList.size() == 0 && !locStr.equals("")) {
			locList.add(locStr);
		}
		if (percentList.size() == 0 && !percentStr.equals("")) {
			percentList.add(percentStr);
		}
		if (moneyList.size() == 0 && !moneyStr.equals("")) {
			moneyList.add(moneyStr);
		}
		result.put("DATE", dateList);
		result.put("PERSON", personList);
		result.put("ORGANIZATION", orgList);
		result.put("TIME", timeList);
		result.put("LOCATION", locList);
		result.put("PERCENT", percentList);
		result.put("MONEY", moneyList);

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, LinkedHashSet<String>> matuToMap(String classifierPath, String content) {
		HashMap<String, LinkedHashSet<String>> result = new HashMap<String, LinkedHashSet<String>>();
		@SuppressWarnings("rawtypes")
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);
		List<List<CoreLabel>> entities = classifier.classify(content);
		LinkedHashSet<String> matuList = new LinkedHashSet<String>();

		int pre1 = 0;
		String matuStr = "";

		for (List<CoreLabel> coreLabels : entities) {
			for (CoreLabel label : coreLabels) {
				String word = label.word();
				String category = label.get(CoreAnnotations.AnswerAnnotation.class);
				int index = Integer.parseInt(label.get(CoreAnnotations.PositionAnnotation.class));
				if (category.equals("MATUR")) {
					if (index - pre1 != 1 && (!matuStr.equals(""))) {
						matuList.add(matuStr);
						matuStr = "";
					}
					if (word.equals(",")) {
						pre1 = index;
					} else {
						if (pre1 == 0 || index - pre1 != 1) {
							matuStr = word;
						} else if (index - pre1 == 1) {
							matuStr = matuStr + " " + word;
						}
						pre1 = index;
					}
				}
			}
		}

		if (matuList.size() == 0 && !matuStr.equals("")) {
			matuList.add(matuStr);
		}

		result.put("MATUR", matuList);

		return result;
	}

	public static JSONObject maturToJSON(String classifierPath, String content, String outFileName)
			throws FileNotFoundException, UnsupportedEncodingException {
		JSONObject matuObj = new JSONObject();

		@SuppressWarnings("rawtypes")
		AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);
		String resultInXml = classifier.classifyToString(content, "xml", true);
		String maturityDates = "";
		try {
			JSONObject xmlJSONObj = XML.toJSONObject(resultInXml);
			JSONArray entities = xmlJSONObj.getJSONArray("wi");

			int pre = 0;
			for (int i = 0; i < entities.length(); i++) {
				JSONObject obj = entities.getJSONObject(i);
				if (obj.getString("entity").equals("MATUR")) {
					if (maturityDates.equals("")) {
						maturityDates = obj.get("content").toString();
						pre = obj.getInt("num");
					} else {
						if (obj.get("content").toString().equals(",")) {
							pre = obj.getInt("num");
						} else {
							if (obj.getInt("num") - pre == 1) {
								maturityDates = maturityDates + " " + obj.get("content").toString();
							} else {
								maturityDates = maturityDates + ";" + obj.get("content").toString();
							}
							pre = obj.getInt("num");
						}
					}
				}
			}
		} catch (JSONException je) {
			System.out.println(je.toString());
		}

		matuObj.put("MATURITY DATE", maturityDates);
		PrintWriter writer = new PrintWriter(outFileName, "UTF-8");
		writer.print(matuObj.toString(PRETTY_PRINT_INDENT_FACTOR));
		writer.close();

		return matuObj;
	}

}
