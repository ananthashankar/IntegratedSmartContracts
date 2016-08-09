/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.edu.Servlet;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 *
 * @author neera
 */
public class ElasticSearch_Backup extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    static Client client;

    public static String getSentence(String input) {
        String paragraph = input;
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        List<String> sentenceList = new ArrayList<String>();

        for (List<HasWord> sentence : dp) {
            String sentenceString = Sentence.listToString(sentence);
            sentenceList.add(sentenceString.toString());
        }
        String sent = "";
        for (String sentence : sentenceList) {
            System.out.println(sentence);
            sent = sent + " " + sentence + "\n";
        }
        try {

            FileWriter file = new FileWriter("Sentences.txt");
            file.write(sent.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sent;
    }

    public static String parseString(String documentText) throws FileNotFoundException {
        System.out.println("----INDEXOF----");
        int definedTermsStart = documentText.indexOf("ARTICLE 1");
        int definedTermsEnd = documentText.indexOf("ARTICLE 2");

        int start = documentText.indexOf('“', definedTermsStart);
        int end = documentText.indexOf('”', start);
        int delimiter = -1;
        int count = 0;
        JSONObject obj = new JSONObject();
        String url = "https://apikey:@account.region.cloud.facetflow.io";

        try {
            Node node = nodeBuilder().node();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        client = NodeBuilder.nodeBuilder()
                .client(true)
                .node()
                .client();

        while (start != -1 && end < definedTermsEnd) {

            System.out.println(documentText.substring(start + 1, end));

            delimiter = documentText.indexOf(".", end + 1);

            System.out.println(documentText.substring(end + 1, delimiter));

            System.out.println();

            obj.put(documentText.substring(start + 1, end), documentText.substring(end + 1, delimiter));
            String term = documentText.substring(start + 1, end);
            count++;
            try {
                client.prepareIndex(""
                        + "", "term", term)
                        .setSource(putJsonTerm(documentText.substring(start + 1, end), documentText.substring(end + 1, delimiter))).execute().actionGet();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            start = documentText.indexOf('“', end + 1);
            end = documentText.indexOf('”', start);

        }
        PrintWriter out = new PrintWriter("filename1.txt");
        out.println(obj.toString());

        System.out.println("stop");
        // node.close();
        return "GetSomeString";
    }

    public static String cleanStopWords(String inputText) {
        String[] stopwords = {"the", "-RRB-", "-LRB-", "a", "as", "able", "about", "WHEREAS",
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
        List<String> wordsList = new ArrayList<String>();
        //String tweet = "Feeling miserable with the cold? Here's WHAT you can do.";
        inputText = inputText.trim().replaceAll("\\s+", " ");
        System.out.println("After trim:  " + inputText);
        //Get all the words Tokenize rather than spliting
        String[] words = inputText.split(" ");
        for (String word : words) {
            wordsList.add(word);
        }
        System.out.println("After for loop:  " + wordsList);
        //remove stop words here from the temp list
        for (int i = 0; i < wordsList.size(); i++) {
            // get the item as string
            for (int j = 0; j < stopwords.length; j++) {
                if (stopwords[j].contains(wordsList.get(i)) || stopwords[j].toUpperCase().contains(wordsList.get(i))) {
                    wordsList.remove(i);
                }
            }
        }
        String cleanString = "";
        for (String str : wordsList) {
            System.out.print(str + " ");
            cleanString = cleanString.replaceAll(",", "");
            cleanString = cleanString + " " + str;
        }
        try {
            FileWriter file = new FileWriter("cleanDoc.txt");
            file.write(cleanString.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cleanString;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String filepath = request.getParameter("hiddenPath");
            String fileName = request.getParameter("hiddenFileName");
            /* TODO output your page here. You may use following sample code. */
            Tika tika = new Tika();
            final File folder = new File(filepath);
            String fileEntry = filepath + fileName;
            String filetype = tika.detect(fileEntry);
            System.out.println("FileType " + filetype);
            BodyContentHandler handler = new BodyContentHandler(-1);

            Metadata metadata = new Metadata();

            FileInputStream inputstream = null;
            try {
                inputstream = new FileInputStream(fileEntry);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
            ParseContext pcontext = new ParseContext();

            //parsing the document using PDF parser
            PDFParser pdfparser = new PDFParser();
            try {
                pdfparser.parse(inputstream, handler, metadata, pcontext);
            } catch (IOException ex) {
                Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TikaException ex) {
                Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
            }

            //getting the content of the document
            String docText = handler.toString().replaceAll("(/[^\\da-zA-Z.]/)", "");
            String outputArray[] = docText.split("Article|Section|Borrower|Agents");

            try {
                //Put The Defined Terms in the elastic search
                parseString(docText);
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet ElasticSearch</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet ElasticSearch at " + request.getContextPath() + "</h1>");
                getDocument(out, client, "definedterms", "term", "Accounts");
                getDocument(out, client, "definedterms", "term", "Accountant");

                out.println("</body>");
                out.println("</html>");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
            docText = cleanStopWords(docText);

            Set<String> allCapsWords = new HashSet<>();
            Pattern p = Pattern.compile("\\b[A-Z]{2,}\\b");
            Matcher m = p.matcher(docText);
            while (m.find()) {
                String word = m.group();
                // System.out.println(word);
                allCapsWords.add(word);
            }

            for (String allcaps : allCapsWords) {
                // System.out.println(allcaps);
            }
            System.out.println("Caps word count" + allCapsWords.size());
            org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
            int count = 0;
            for (String output : outputArray) {

                obj.put(String.valueOf(count), output.replaceAll("\\s+", " "));

                count++;
            }
            try {

                FileWriter file = new FileWriter("filename.json");
                file.write(obj.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Map<String, Object> putJsonTerm(String term, String content
    ) {

        Map<String, Object> jsonDocument = new HashMap<String, Object>();
        jsonDocument.put("term", term);
        jsonDocument.put("definition", content);
        return jsonDocument;
    }

    public static Map<String, Object> putJsonDocument(String title, String content, Date postDate,
            String[] tags, String author) {

        Map<String, Object> jsonDocument = new HashMap<String, Object>();

        jsonDocument.put("title", title);
        jsonDocument.put("content", content);
        jsonDocument.put("postDate", postDate);
        jsonDocument.put("tags", tags);
        jsonDocument.put("author", author);

        return jsonDocument;
    }

    public static void getTerm(Client client, String index, String type, String id) {

        GetResponse getResponse = client.prepareGet(index, type, id)
                .execute()
                .actionGet();
        Map<String, Object> source = getResponse.getSource();

        System.out.println("------------------------------");
        System.out.println("Index: " + getResponse.getIndex());
        System.out.println("Type: " + getResponse.getType());
        System.out.println("Id: " + getResponse.getId());
        System.out.println("Version: " + getResponse.getVersion());
        System.out.println(source);
        System.out.println("------------------------------");

    }

    public static void getDocument(PrintWriter out, Client client, String index, String type, String id) {

        GetResponse getResponse = client.prepareGet(index, type, id)
                .execute()
                .actionGet();
        Map<String, Object> source = getResponse.getSource();

        
        out.println("Index: " + getResponse.getIndex()+"<br>");
        out.println("Type: " + getResponse.getType()+"<br>");
        out.println("Id: " + getResponse.getId()+"<br>");
        out.println("Version: " + getResponse.getVersion()+"<br>");
        out.println(source+"<br>");
     

    }

    public static void updateDocument(Client client, String index, String type,
            String id, String field, String newValue) {

        Map<String, Object> updateObject = new HashMap<String, Object>();
        updateObject.put(field, newValue);

        client.prepareUpdate(index, type, id)
                .setScript("ctx._source." + field + "=" + field)
                .setScriptParams(updateObject).execute().actionGet();
    }

    public static void updateDocument(Client client, String index, String type,
            String id, String field, String[] newValue) {

        String tags = "";
        for (String tag : newValue) {
            tags += tag + ", ";
        }

        tags = tags.substring(0, tags.length() - 2);

        Map<String, Object> updateObject = new HashMap<String, Object>();
        updateObject.put(field, tags);

        client.prepareUpdate(index, type, id)
                .setScript("ctx._source." + field + "+=" + field)
                .setScriptParams(updateObject).execute().actionGet();
    }

    public static void searchDocument(Client client, String index, String type,
            String field, String value) {

        SearchResponse response = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(fieldQuery(field, value))
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();

        SearchHit[] results = response.getHits().getHits();

        System.out.println("Current results: " + results.length);
        for (SearchHit hit : results) {
            System.out.println("------------------------------");
            Map<String, Object> result = hit.getSource();
            System.out.println(result);
        }
    }

    public static void deleteDocument(Client client, String index, String type, String id) {

        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        System.out.println("Information on the deleted document:");
        System.out.println("Index: " + response.getIndex());
        System.out.println("Type: " + response.getType());
        System.out.println("Id: " + response.getId());
        System.out.println("Version: " + response.getVersion());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
