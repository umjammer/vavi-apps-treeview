/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package sample;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import vavi.apps.treeView.TreeViewTreeNode;
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
public class XmlDao implements Dao {

    /** ルートのツリーノード */
    private TreeViewTreeNode rootTreeNode = null;

    /** XML データのバージョン */
    private String version;

    public TreeViewTreeNode read(InputStream is) throws IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
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

        return rootTreeNode;
    }

    /** 子供のノードを取得します． */
    private TreeViewTreeNode getChildTreeNode(Node node) {

        Vector<TreeViewTreeNode> childTreeNodes = new Vector<>();

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
Debug.println("child: " + child.getNodeName() + ": " + urlString.trim());
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
     * @since 0.1.0
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
            throw new IllegalStateException(e);
        }
    }

    /**
     * 新しい TreeViewTreeNode のインスタンスを返します．
     * 
     * @since 0.0.0
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

    /** XML のルートノードのタイトル */
    public static final String title = "treeview";

    /** XML を出力するライタ */
    private Writer writer;

    /** XML ドキュメント */
    private Document document;

    /** XML の改行は \n */
    private static final String LS = "\n";

    /**
     * ツリーの初期データの XML セーバです． XML データのバージョンは 0.1.0 です．
     * 
     * <pre><tt>
     * 
     *  &lt;node&gt;
     *   &lt;class&gt;class name&lt;/class&gt;
     *   &lt;url&gt;file://...&lt;/url&gt;
     *   &lt;node&gt;
     *    &lt;class&gt;child class name&lt;/class&gt;
     *    &lt;url&gt;file://...&lt;/url&gt;
     *    &lt;node&gt;
     *    ...
     *    &lt;/node&gt;
     *    &lt;node&gt;
     *    ...
     *    &lt;/node&gt;
     *  &lt;/node&gt;
     *  
     * </tt></pre>
     */
    public void write(TreeViewTreeNode root, OutputStream os) throws IOException {

        writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);

        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }

        document = builder.newDocument(); // 新規ドキュメントの作成

        Element rootElement = document.createElement(title);
        rootElement.setAttribute("version", "0.1.0");
        rootElement.appendChild(document.createTextNode(LS));

        setChildElement(root, rootElement);

        document.appendChild(rootElement); // ドキュメントに追加

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /** ツリーノードから XML ノードを作成します． */
    private Element createElement(TreeViewTreeNode node) throws IOException {

        File file = new File(node.toString() + ".xml");
        OutputStream os = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
        XMLEncoder xe = new XMLEncoder(os);
        xe.writeObject(node.getUserObject());
        xe.close();

        Element clazz = document.createElement("class");
        clazz.appendChild(document.createTextNode(node.getClass().getName()));
        Element url = document.createElement("url");
        url.appendChild(document.createTextNode(file.toURI().toString()));
        Element element = document.createElement("node");
        element.appendChild(document.createTextNode(LS));
        element.appendChild(clazz);
        element.appendChild(document.createTextNode(LS));
        element.appendChild(url);
        element.appendChild(document.createTextNode(LS));

        return element;
    }

    /** 子供のノードを取得します． */
    private void setChildElement(TreeViewTreeNode node, Element parent) throws IOException {

        Element element = createElement(node);
        parent.appendChild(element);
        parent.appendChild(document.createTextNode(LS));

        for (int i = 0; i < node.getChildCount(); i++) {
            TreeViewTreeNode childNode = (TreeViewTreeNode) node.getChildAt(i);

            setChildElement(childNode, element);
        }
    }
}

/* */
