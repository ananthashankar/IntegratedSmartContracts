/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.edu.Servlet;

import com.me.SmartContracts.Utils.DocumentReader;
import com.me.SmartContracts.Utils.Elastic;
import com.me.SmartContracts.Utils.Elastic_Old;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author neera
 */
@Controller
public class ElasticSearch extends HttpServlet {

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
    static String docText;

    @RequestMapping("/ElasticSearch")
    @ResponseBody
    public void performLogin(@RequestBody String Zip, HttpServletRequest request, HttpServletResponse response) {
        String abc = Zip;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            String myParseFileClicked = request.getParameter("myParseFileClicked");
            /* TODO output your page here. You may use following sample code. */

            //Parsing the document
            //Insert the Defined Terms
            try {
                Node node = nodeBuilder().node();
                if (myParseFileClicked.equalsIgnoreCase("true")) {

                    String filepath = request.getParameter("path");
                    String fileName = request.getParameter("filename");
                    try {

                        client = node.client();

                        docText = DocumentReader.readDocument(filepath, fileName);
                        DocumentReader.parseString(docText, client);
                        //"Borrowing should be replaced by the user input key"

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Elastic.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    String txtSearch = request.getParameter("txtSearch");
                    Map<String, Object> definedTerms = Elastic.getDefinedTerm(client, "definedterms", "term", "1", txtSearch);
                    System.out.println(txtSearch);
                    response.setContentType("text/plain");
                    Iterator it = definedTerms.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        response.getWriter().write("Defined Terms-->" + pair.getKey() + " = " + pair.getValue());
                    }
                    node.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException ex) {
            Logger.getLogger(ElasticSearch.class.getName()).log(Level.SEVERE, null, ex);
        }

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
