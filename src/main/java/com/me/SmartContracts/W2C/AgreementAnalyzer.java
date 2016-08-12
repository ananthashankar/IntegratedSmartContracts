package com.me.SmartContracts.W2C;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.nd4j.linalg.io.ClassPathResource;

public class AgreementAnalyzer {

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

    public static String analyzeDocument(String filePath) throws Exception {
        ArrayList<String> stopWords = new ArrayList<>();
        for (int i = 0; i < stopwords.length; i++) {
            stopWords.add(stopwords[i]);
        }

        HashMap<String, Double[]> coll = new HashMap<String, Double[]>();
        WordVectors wordVectors = null;

        try {

            wordVectors = WordVectorSerializer.loadTxtVectors(new File("C:\\Word2VecVocabulary\\Vocab.txt"));
            int i = 0;
            String word = wordVectors.vocab().wordAtIndex(0);
            while (word != null) {
                double[] vec = wordVectors.getWordVector(word);
                Double[] doub = new Double[vec.length];
                for (int j = 0; j < doub.length; j++) {
                    doub[j] = vec[j];
                }
                coll.put(word, doub);
                i++;
                word = wordVectors.vocab().wordAtIndex(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SentenceIterator iter = new LineSentenceIterator(new File(filePath));
        iter.setPreProcessor(new SentencePreProcessor() {
            @Override
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });

        org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

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

        vec.fit();

        System.out.println(vec.getVocab().words().toArray()[0]);
        Collection<String> lst = vec.wordsNearest("number", 3);
        System.out.println("Nearest to number " + lst + " " + lst.size());

        System.out.println("Similarity : " + wordVectors.similarity("number", "capacity"));
        System.out.println("Word Vector " + vec.getWordVector("number")[0]);

        Object[] words = vec.getVocab().words().toArray();
        int totalWords = words.length;
        int foundWords = 0;
        int similarityWords = 0;

        for (Object o : words) {
            String wrd = o.toString();
            if (coll.containsKey(wrd)) {
                foundWords++;
                double[] vec1 = vec.getWordVector(wrd);
                double[] vec2 = wordVectors.getWordVector(wrd);
                double dotProduct = 0.0;
                double squareSum1 = 0.0;
                double squareSum2 = 0.0;
                for (int i = 0; i < vec1.length; i++) {
                    dotProduct += vec1[i] * vec2[i];
                    squareSum1 += vec1[i] * vec1[i];
                    squareSum2 += vec2[i] * vec2[i];
                }
                double sqrRoot1 = Math.sqrt(squareSum1);
                double sqrRoot2 = Math.sqrt(squareSum2);
                double cosineSimilarity = dotProduct / (sqrRoot1 * sqrRoot2);
                if (cosineSimilarity >= 0.5) {
                    similarityWords++;
                }
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("Total Number of Terms Analyzed: " + totalWords + "\n");
        report.append("Total Number of Terms Matched with The Vocabulary: " + foundWords + "\n");
        report.append("Total Number of Terms Matched on Semantic Analysis: " + similarityWords + "\n");
        report.append("Accuracy of the Document: " + (double) similarityWords / foundWords + "\n");
        report.append("Percentage Accuracy of the Document: " + ((double) similarityWords / foundWords) * 100 + "\n");

        System.out.println("Matched words " + foundWords + " Similar words " + similarityWords + " Accuracy " + (double) similarityWords / foundWords);

        // Update Vocabulary
        if ((double) similarityWords / foundWords >= 0.5) {
            Word2VecModel.updateVocabulary(coll, vec);
        }
        return report.toString();

    }

    public static ArrayList<String> getSynonym(String str) {
        ArrayList<String> list = new ArrayList<String>();
        WordVectors wordVectors = null;
        try {
            wordVectors = WordVectorSerializer.loadTxtVectors(new File("C:\\Word2VecVocabulary\\Vocab.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wordVectors.hasWord(str.toLowerCase())) {
            Collection<String> list1 = wordVectors.wordsNearest(str.toLowerCase(), 5);
            list.addAll(list1);
        }

        return list;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

//        String filePath = "/resources/Sentences/Contract1_Sentences.txt";
//        try {
//            String filePath1 = new ClassPathResource(filePath).getFile().getAbsolutePath();
//            String result;
//            try {
//                result = analyzeDocument(filePath1);
//                System.out.println(result);
//            } catch (Exception ex) {
//                Logger.getLogger(AgreementAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(AgreementAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      
    	ArrayList<String> list = getSynonym("number");
    	System.out.println(list);
    }

}
