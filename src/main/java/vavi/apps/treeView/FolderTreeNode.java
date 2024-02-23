/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

/**
 * This class represents a folder node.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public abstract class FolderTreeNode extends FileTreeNode {

    /**
     * Creates folder node.
     * 
     * @param userObject data of node
     */
    public FolderTreeNode(Object userObject) {
        super(userObject);
    }
}

/* */
