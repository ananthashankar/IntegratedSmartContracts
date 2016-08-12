package com.me.SmartContracts.W2C;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.EndingPreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.ui.UiServer;
import org.json.JSONObject;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;

import opennlp.tools.tokenize.TokenizerFactory;

public class Word2VecModel {

	private static Logger log = LoggerFactory.getLogger(Word2VecModel.class);
	private static String[] stopwords = {"the", "-RRB-", "-LRB-", "a", "as", "able", "about", "WHEREAS",
            "above", "according", "accordingly", "across", "actually",
            "after", "afterwards", "again", "against", "aint", "all",
            "allow", "allows", "almost", "alone", "along", "already",
            "also", "although", "always", "am", "among", "amongst", "an",
            "and", "another", "any", "anybody", "anyhow", "anyone", "anything",
            "anyway", "anyways", "anywhere", "apart", "appear", "appreciate",
            "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking",
            "associated", "at", "available", "away", "awfully", "be", "became", "because",
            "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being",
            "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both",
            "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes",
            "certain", "certainly", "changes", "clearly", "co", "com", "come",
            "comes", "concerning", "consequently", "consider", "considering", "contain",
            "containing", "contains", "corresponding", "could", "couldnt", "course", "currently",
            "definitely", "described", "despite", "did", "didnt", "different", "do", "does",
            "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu",
            "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially",
            "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere",
            "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed",
            "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further",
            "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have",
            "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};

	public static void trainVocabulary() throws Exception {
    	// Retrieve the vocabulary to update
    	WordVectors wordVectors = null;
    	HashMap<String, Double[]> coll = new HashMap<String, Double[]>();
    	Map<String, Double[]> coll1 = new HashMap<String, Double[]>();
    	Map<String, Double[]> coll2 = new HashMap<String, Double[]>();

		try {
			ObjectMapper mapper = new ObjectMapper();

			// read JSON from a file
			coll2 = mapper.readValue(
					new File("C:\\Word2VecVocabulary\\Vocab.json"), 
					new TypeReference<Map<String, Double[]>>() {
			});
			
			// read Map values from a map file
//			ObjectInput objectInputStream = new ObjectInputStream(new BufferedInputStream(
//						new FileInputStream("/Users/Anantha/Desktop/NLP/TestFiles/TrainingOutputDoc/VocabMap.txt")));
//			coll1 = (Map<String, Double[]>) objectInputStream.readObject();
//			objectInputStream.close();
			
			wordVectors = WordVectorSerializer.loadTxtVectors(new File("C:\\Word2VecVocabulary\\Vocab.txt"));
			int i=0;
			String word = wordVectors.vocab().wordAtIndex(0);
			while(word != null){
				double[] vec = wordVectors.getWordVector(word);
				Double[] doub = new Double[vec.length];
				for(int j=0; j<doub.length; j++){
					doub[j] = vec[j];
				}
				coll.put(word, doub);
				i++;
				word = wordVectors.vocab().wordAtIndex(i);
			}
			System.out.println("coll size: " + coll.size());
			System.out.println("coll1 size: " + coll1.size());
			System.out.println("coll2 size: " + coll2.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	            
        ArrayList<String> stopWords = new ArrayList<>();
        for(int i=0;i<stopwords.length;i++) {
        	stopWords.add(stopwords[i]);
        }
    	//
    	File folder = new File("C:\\Word2VecVocabulary\\Sentences");
    	File[] listOfFiles = folder.listFiles();
    	int count = 0;
    	for(File f : listOfFiles){
    		@SuppressWarnings("unused")
			String fileName = f.getName();
    		if(!fileName.substring(fileName.length()-3).equals("txt")){
    			continue;
    		}
    		System.out.println(fileName + " file number " + ++count);
	        log.info("Load & Vectorize Sentences....");
	        // Strip white space before and after for each line
	        SentenceIterator iter = new LineSentenceIterator(new File("C:\\Word2VecVocabulary\\Sentences\\" + fileName));
	        
	        iter.setPreProcessor(new SentencePreProcessor() {
	            @Override
	            public String preProcess(String sentence) {
	                return sentence.toLowerCase();
	            }
	        });
	        
	        log.info("Tokenize data....");
	        
	        // Split on white spaces in the line to get words
	        org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory t = new DefaultTokenizerFactory();
	        t.setTokenPreProcessor(new CommonPreprocessor());
	        
	        log.info("Building model....");
	        Word2Vec vec = new Word2Vec.Builder()
	        		.batchSize(1000) //# words per minibatch.
	                .minWordFrequency(1) // 
	                .useAdaGrad(false) //
	                .layerSize(16) // word feature vector size
	                .iterations(2) // # iterations to train
	                .learningRate(0.025) // 
	                .minLearningRate(1e-3) // learning rate decays wrt # words. floor learning
	                .negativeSample(10) // sample size 10 words
	                .iterate(iter) //
	                .epochs(1)
	                .tokenizerFactory(t)
	                .stopWords(stopWords)
	                .seed(142)
	                .build();
	
	        log.info("Fitting Word2Vec model....");
	       
	        vec.fit();
	
	        log.info("Writing word vectors to text file....");
	        	
	        log.info("Closest Words:");
	        
	        Object[] words = vec.getVocab().words().toArray();
	        
	        for(Object o : words){
	        	String wrd = o.toString();
	        	if(coll.containsKey(wrd)){
	        		Double[] vecValues1 = coll.get(wrd);
	        		double[] vecValues2 = vec.getWordVector(wrd);
	        		for(int i=0; i< vecValues1.length; i++){
	        			vecValues1[i] = (Double)(vecValues1[i]+vecValues2[i]/2);
	        		}
	        		coll.put(wrd, vecValues1);
	        	} else {
	        		double[] vecValues2 = vec.getWordVector(wrd);
	        		Double[] vecValues1 = new Double[vecValues2.length];
	        		for(int i=0; i< vecValues2.length; i++){
	        			vecValues1[i] = vecValues2[i];
	        		}
	        		coll.put(wrd, vecValues1);
	        	}
	        }
	        
    	}
    	
    	// create vocabulary
    	FileWriter fw = null;
		try {
				
				ObjectMapper mapper1 = new ObjectMapper();
	
				// write JSON to a file
				mapper1.writeValue(new File("C:\\Word2VecVocabulary\\Vocab.json"), coll);

//				ObjectOutput objectOutputStream = new ObjectOutputStream(new BufferedOutputStream
//	            		(new FileOutputStream("/Users/Anantha/Desktop/NLP/TestFiles/TrainingOutputDoc/VocabMap.txt", false)));
//	            objectOutputStream.writeObject(coll);
//	            objectOutputStream.close();
	            
				fw = new FileWriter("C:\\Word2VecVocabulary\\Vocab.txt", false);
				fw.write("SkipLine" + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1
									+ " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1);
				fw.write(System.lineSeparator());
				fw.write("SmartContracts" + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1
										  + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1);
				fw.write(System.lineSeparator());
				for(String s : coll.keySet()){
					fw.write(s);
					for(Double d : coll.get(s)){
						fw.write(" " + d);
					}
					fw.write(System.lineSeparator());
				}
				fw.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
    public static void updateVocabulary(HashMap<String, Double[]> coll, Word2Vec vec) throws Exception{
    	
    	Object[] words = vec.getVocab().words().toArray();
        
        for(Object o : words){
        	String wrd = o.toString();
        	if(coll.containsKey(wrd)){
        		Double[] vecValues1 = coll.get(wrd);
        		double[] vecValues2 = vec.getWordVector(wrd);
        		for(int i=0; i< vecValues1.length; i++){
        			vecValues1[i] = (Double)(vecValues1[i]+vecValues2[i]/2);
        		}
        		coll.put(wrd, vecValues1);
        	} else {
        		double[] vecValues2 = vec.getWordVector(wrd);
        		Double[] vecValues1 = new Double[vecValues2.length];
        		for(int i=0; i< vecValues2.length; i++){
        			vecValues1[i] = vecValues2[i];
        		}
        		coll.put(wrd, vecValues1);
        	}
        }
        
     // Update Vocabulary
    	FileWriter fw = null;
		try {
				
				ObjectMapper mapper1 = new ObjectMapper();
	
				// write JSON to a file
				mapper1.writeValue(new File("C:\\Word2VecVocabulary\\Vocab.json"), coll);

//				ObjectOutput objectOutputStream = new ObjectOutputStream(new BufferedOutputStream
//	            		(new FileOutputStream("/Users/Anantha/Desktop/NLP/TestFiles/TrainingOutputDoc/VocabMap.txt", false)));
//	            objectOutputStream.writeObject(coll);
//	            objectOutputStream.close();
	            
				fw = new FileWriter("C:\\Word2VecVocabulary\\Vocab.txt", false);
				fw.write("SkipLine" + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1
									+ " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1);
				fw.write(System.lineSeparator());
				fw.write("SmartContracts" + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1
										  + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1 + " " + 1);
				fw.write(System.lineSeparator());
				for(String s : coll.keySet()){
					fw.write(s);
					for(Double d : coll.get(s)){
						fw.write(" " + d);
					}
					fw.write(System.lineSeparator());
				}
				fw.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  	
    }
	
	public static void main(String[] args){
    	
    }
}

