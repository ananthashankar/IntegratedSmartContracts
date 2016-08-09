package com.me.SmartContracts.Utils;
 
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
 
public class StanfordNER
{

	 public static LinkedHashMap <String,LinkedHashSet<String>> identifyNER(String text,String model){
		 LinkedHashMap <String,LinkedHashSet<String>> map=new LinkedHashMap<String,LinkedHashSet<String>>();
		 String serializedClassifier =model;
		 System.out.println(serializedClassifier);
		 CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		 List<List<CoreLabel>> classify = classifier.classify(text);
		 for (List<CoreLabel> coreLabels : classify){
			 for (CoreLabel coreLabel : coreLabels){
				 String word = coreLabel.word();
				 String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				 if(!"O".equals(category)) {
					 if(map.containsKey(category)){
						 map.get(category).add(word);
					 }
					 else{
						 LinkedHashSet<String> temp=new LinkedHashSet<String>();
						 temp.add(word);
						 map.put(category,temp);
					 }
				 }		 
			 }
		 }
		 return map;
	 }
	 
	 public static void main(String args[]){	 
		 File file = new File("/Users/Anantha/Desktop/NLP/Test Files/cleanDoc.txt");
		 StringBuilder content = new StringBuilder();
		 try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			    	content.append(line);
			    }
			}catch (IOException e) {
				e.printStackTrace();
			}
		 String result = identifyNER(content.toString(), 
				 "/Users/Anantha/Desktop/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz").toString(); 
		 try {
			 FileWriter out = new FileWriter("/Users/Anantha/Desktop/NLP/Test Files/stanfordNEROutput.json", false);
			 out.write(result);
			 out.close();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }		
	 }
 
}