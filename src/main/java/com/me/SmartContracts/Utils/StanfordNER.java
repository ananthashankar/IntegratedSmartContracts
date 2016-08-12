package com.me.SmartContracts.Utils;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class StanfordNER {

    public static LinkedHashMap<String, LinkedHashSet<String>> identifyNER(String text, String model) {
        LinkedHashMap<String, LinkedHashSet<String>> map = new LinkedHashMap<String, LinkedHashSet<String>>();
        String serializedClassifier = model;
        System.out.println(serializedClassifier);
        CRFClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
        List<List<CoreLabel>> classify = classifier.classify(text);
        for (List<CoreLabel> coreLabels : classify) {
            for (CoreLabel coreLabel : coreLabels) {
                String word = coreLabel.word();
                String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
                if (!"O".equals(category)) {
                    if (map.containsKey(category)) {
                        map.get(category).add(word);
                    } else {
                        LinkedHashSet<String> temp = new LinkedHashSet<String>();
                        temp.add(word);
                        map.put(category, temp);
                    }
                }
            }
        }
        return map;
    }
   
}
