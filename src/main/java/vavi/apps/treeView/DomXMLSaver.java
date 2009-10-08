/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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


/**
 * �c���[�̏����f�[�^�� XML �Z�[�o�ł��D XML �f�[�^�̃o�[�W������ 0.1.0 �ł��D
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
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010917 nsano initial version <br>
 *          0.01 030606 nsano chnage error trap <br>
 */
public class DomXMLSaver implements XMLSaver {

    /** XML �̃��[�g�m�[�h�̃^�C�g�� */
    public static final String title = "treeview";

    /** XML ���o�͂��郉�C�^ */
    private Writer writer;

    /** XML �h�L�������g */
    private Document document;

    /** XML �̉��s�� \n */
    private static final String LS = "\n";

    /** �c���[�̏����f�[�^�� XML �Z�[�o���\�z���܂��D */
    public DomXMLSaver(OutputStream os) throws IOException {
        writer = new OutputStreamWriter(os, "UTF-8");
    }

    /** ���[�g�ȉ��̃c���[�m�[�h���������݂܂��D */
    public void writeRootTreeNode(TreeViewTreeNode root) throws IOException {

        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }

        document = builder.newDocument(); // �V�K�h�L�������g�̍쐬

        Element rootElement = document.createElement(title);
        rootElement.setAttribute("version", "0.1.0");
        rootElement.appendChild(document.createTextNode(LS));

        setChildElement(root, rootElement);

        document.appendChild(rootElement); // �h�L�������g�ɒǉ�

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

    /** �c���[�m�[�h���� XML �m�[�h���쐬���܂��D */
    private Element createElement(TreeViewTreeNode node) throws IOException {

        File file = new File(node.toString() + ".xml");
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        XMLEncoder xe = new XMLEncoder(os);
        xe.writeObject(node.getUserObject());
        xe.close();

        Element clazz = document.createElement("class");
        clazz.appendChild(document.createTextNode(node.getClass().getName()));
        Element url = document.createElement("url");
        url.appendChild(document.createTextNode(file.toURL().toString()));
        Element element = document.createElement("node");
        element.appendChild(document.createTextNode(LS));
        element.appendChild(clazz);
        element.appendChild(document.createTextNode(LS));
        element.appendChild(url);
        element.appendChild(document.createTextNode(LS));

        return element;
    }

    /** �q���̃m�[�h���擾���܂��D */
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
