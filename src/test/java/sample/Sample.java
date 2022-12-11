/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;

import vavi.apps.treeView.TreeViewFrame;
import vavi.apps.treeView.TreeViewTreeNode;
import vavi.swing.binding.treeview.TreeView;
import vavi.util.Debug;


/**
 * Sample.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-12-11 nsano initial version <br>
 */
@TreeView
public class Sample {

    private Dao dao = new XmlDao();

    private TreeViewTreeNode root;

    public TreeViewTreeNode load(InputStream is) throws IOException {
        root = dao.read(is);
        return root;
    }

    public void save(OutputStream os, TreeViewTreeNode root) throws IOException {
        dao.write(root, os);
    }

    public InputStream init() {
        InputStream is = Test.class.getResourceAsStream(System.getProperty("tv.resource.tree"));
        Debug.println(System.getProperty("tv.resource.tree") + ": " + is);
        return is;
    }

    /**
     * The program entry pont.
     */
    public static void main(String[] args) throws Exception {

        vavi.apps.treeView.TreeView tree = new vavi.apps.treeView.TreeView(new Sample());

        JFrame frame = new TreeViewFrame(tree);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
