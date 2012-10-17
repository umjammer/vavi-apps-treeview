/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import vavi.util.Debug;


/**
 * ツリーの初期データの XML ローダです．
 * 
 * 0.0.0 ノードの名前のみ <name> を使用
 * 0.1.0 ノードの userObject は XMLEncoder でシリアライズされている <url> を使用
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010906 nsano initial version <br>
 *          0.01 030606 nsano change error trap <br>
 */
public class DomXMLLoader implements XMLLoader {

    /** ルートのツリーノード */
    private TreeViewTreeNode rootTreeNode = null;

    /** XML データのバージョン */
    private String version;

    /** ツリーの初期データの XML ローダを構築します． */
    public DomXMLLoader(InputStream is) throws IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }

        factory.setValidating(true);

        Document document;
        try {
            document = builder.parse(is);
        } catch (SAXException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        Element root = document.getDocumentElement();
// Debug.println(root);
        String rootName = root.getTagName();
Debug.println(rootName);
        if (!"treeview".equals(rootName)) {
            throw new IllegalArgumentException("invalid xml: " + rootName);
        }
        if (root.hasAttribute("version")) {
            version = root.getAttribute("version");
Debug.println(root.getAttribute("version"));
        }
        if (!root.hasChildNodes()) {
            throw new IllegalArgumentException("need at least one root");
        }

        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
// Debug.println("---- " + i + " ----: " + type(node.getNodeType()) + "(" + node.getNodeType() + ")");
// Debug.println(node);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("node".equals(node.getNodeName())) {
// Debug.println("node: " + node.getNodeName());
                    if (rootTreeNode != null) {
                        throw new IllegalArgumentException("multiple root");
                    } else {
                        rootTreeNode = getChildTreeNode(node);
                    }
                } else { // XML ルートの次はツリーのルートノードのみ
Debug.println("invalid node: " + node.getNodeName());
                }
            } else { // 改行等をとばす
// Debug.println("invalid node: " + node.getNodeType());
            }
        }
    }

    /** ルートのツリーノードを取得します． */
    public TreeViewTreeNode readRootTreeNode() {
        return rootTreeNode;
    }

    /** 子供のノードを取得します． */
    private TreeViewTreeNode getChildTreeNode(Node node) {

        Vector<TreeViewTreeNode> childTreeNodes = new Vector<TreeViewTreeNode>();

        Object userObject = null;
        String className = null;

        String urlString = null;

        NodeList children = node.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if ("class".equals(child.getNodeName())) { // 0.x.x
                    className = child.getFirstChild().getNodeValue();
Debug.println("child: " + child.getNodeName() + ": " + className);
                } else if ("name".equals(child.getNodeName())) { // 0.0.0
                    userObject = child.getFirstChild().getNodeValue();
Debug.println("child: " + child.getNodeName() + ": " + userObject);
                } else if ("url".equals(child.getNodeName())) { // 0.1.0
                    urlString = child.getFirstChild().getNodeValue();
Debug.println("child: " + child.getNodeName() + ": " + urlString);
                } else if ("node".equals(child.getNodeName())) {
                    childTreeNodes.add(getChildTreeNode(child));
                } else {
Debug.println("invalid child: " + child.getNodeName());
                }
            }
        }

        TreeViewTreeNode treeNode;
        if ("0.0.0".equals(version)) {
            treeNode = newInstance(className, userObject);
        } else if ("0.1.0".equals(version)) {
            treeNode = newInstance(className, urlString);
        } else {
            throw new IllegalStateException("unknown version: " + version);
        }

        for (int j = 0; j < childTreeNodes.size(); j++) {
            treeNode.add(childTreeNodes.elementAt(j));
        }
        return treeNode;
    }

    /**
     * 新しい TreeViewTreeNode のインスタンスを返します．
     * 
     * @version 0.1.0
     */
    private static TreeViewTreeNode newInstance(String className, String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            XMLDecoder xd = new XMLDecoder(is);
            Object userObject = xd.readObject();
            xd.close();

            return newInstance(className, userObject);
        } catch (Exception e) {
Debug.println(e);
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }
    }

    /**
     * 新しい TreeViewTreeNode のインスタンスを返します．
     * 
     * @version 0.0.0
     */
    private static TreeViewTreeNode newInstance(String className, Object userObject) {
        try {
// Debug.println(Debug.DEBUG, className);
            @SuppressWarnings("unchecked")
            Class<? extends TreeViewTreeNode> clazz = (Class<? extends TreeViewTreeNode>) Class.forName(className);
            Constructor<? extends TreeViewTreeNode> c = clazz.getConstructor(Object.class);
            return c.newInstance(userObject);
        } catch (Exception e) {
Debug.println(e);
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }
    }

    /** Tests this class. */
    public static void main(String[] args) throws Exception {
        InputStream is = new BufferedInputStream(new FileInputStream(args[0]));
        new DomXMLLoader(is);
    }
}

/* */
