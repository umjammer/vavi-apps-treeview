/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

/**
 * フォルダノードのツリーノードです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public abstract class FolderTreeNode extends FileTreeNode {

    /**
     * フォルダのノードを構築します．
     * 
     * @param userObject ノードのデータ
     */
    public FolderTreeNode(Object userObject) {
        super(userObject);
    }
}

/* */
