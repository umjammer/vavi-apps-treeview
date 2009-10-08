/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView.node;

import vavi.apps.treeView.FolderTreeNode;
import vavi.apps.treeView.TreeViewException;
import vavi.apps.treeView.TreeViewTreeNode;


/**
 * フォルダのツリーノードです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public class TestFolderTreeNode extends FolderTreeNode {

    /**
     * フォルダのノードを構築します．
     * 
     * @param userObject ノードのデータ
     */
    public TestFolderTreeNode(Object userObject) {
        super((userObject instanceof String) ? new Test((String) userObject, 100) : userObject);
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

    /**
     * フォルダを新規作成します．
     * 
     * @throws TreeViewException できなかった場合
     */
    public void newFolder() throws TreeViewException {
        addController(new TestFolderTreeNode("new TestFolder"));
    }

    /**
     * ファイルを新規作成します．
     * 
     * @throws TreeViewException できなかった場合
     */
    public void newFile() throws TreeViewException {
        addController(new TestFileTreeNode("new TestFile"));
    }

    // -------------------------------------------------------------------------

    /** 新規フォルダ作成できるかどうか */
    public boolean canNewFolder() {
        return true;
    }

    /** 新規ファイル作成できるかどうか */
    public boolean canNewFile() {
        return true;
    }

    /** リネームできるかどうか */
    public boolean canRename() {
        return true;
    }

    /** カットできるかどうか */
    public boolean canCut() {
        return true;
    }

    /** コピーできるかどうか */
    public boolean canCopy() {
        return true;
    }

    /** ペーストできるかどうか */
    public boolean canPaste(TreeViewTreeNode from) {
        // Debug.println(Debug.DEBUG, from);
        return from instanceof TestFolderTreeNode || from instanceof TestFileTreeNode;
    }
}

/* */
