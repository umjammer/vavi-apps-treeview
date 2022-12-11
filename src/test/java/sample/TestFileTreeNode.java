/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package sample;

import vavi.apps.treeView.FileTreeNode;
import vavi.apps.treeView.TreeViewException;


/**
 * ファイルのツリーノードです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public class TestFileTreeNode extends FileTreeNode {

    /**
     * ファイルのノードを構築します．
     * 
     * @param userObject ノードのデータ
     */
    public TestFileTreeNode(Object userObject) {
        super((userObject instanceof String) ? new Test((String) userObject, 1) : userObject);
    }

    // -------------------------------------------------------------------------

    /**
     * 名前を変更します．
     * 
     * @param name 変更後の表示名
     * @throws TreeViewException できなかった場合
     */
    public void rename(String name) throws TreeViewException {
        ((Test) userObject).setName(name);
    }

    // -------------------------------------------------------------------------

    /** カットできるかどうか */
    public boolean canCut() {
        return true;
    }

    /** コピーできるかどうか */
    public boolean canCopy() {
        return true;
    }

    /** 削除できるかどうか */
    public boolean canDelete() {
        return true;
    }

    /** リネームできるかどうか */
    public boolean canRename() {
        return true;
    }
}

/* */
