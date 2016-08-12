/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.SmartContracts.Utils;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 *
 * @author hakdogan
 */
public class ClientProvider {

    private static ClientProvider instance = null;
    private static Object lock = new Object();

    private Client client;
    private Node node;

    public static ClientProvider instance() {

        if (instance == null) {
            synchronized (lock) {
                if (null == instance) {
                    instance = new ClientProvider();
                }
            }
        }
        return instance;
    }

    public synchronized Client prepareClient() {
        try {
            //synchronized (lock) {
                node = nodeBuilder().node();
           // }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        client = node.client();
        return client;

    }

    public void closeNode() {

        if (!node.isClosed()) {
            node.close();
        }

    }

    public Client getClient() {
        return client;
    }

    public void printThis() {
        System.out.println(this);
    }

}
