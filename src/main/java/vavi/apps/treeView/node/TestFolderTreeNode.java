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
 * �t�H���_�̃c���[�m�[�h�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public class TestFolderTreeNode extends FolderTreeNode {

    /**
     * �t�H���_�̃m�[�h���\�z���܂��D
     * 
     * @param userObject �m�[�h�̃f�[�^
     */
    public TestFolderTreeNode(Object userObject) {
        super((userObject instanceof String) ? new Test((String) userObject, 100) : userObject);
    }

    // -------------------------------------------------------------------------

    /**
     * ���O��ύX���܂��D
     * 
     * @param name �ύX��̕\����
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    public void rename(String name) throws TreeViewException {
        ((Test) userObject).setName(name);
    }

    /**
     * �t�H���_��V�K�쐬���܂��D
     * 
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    public void newFolder() throws TreeViewException {
        addController(new TestFolderTreeNode("new TestFolder"));
    }

    /**
     * �t�@�C����V�K�쐬���܂��D
     * 
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    public void newFile() throws TreeViewException {
        addController(new TestFileTreeNode("new TestFile"));
    }

    // -------------------------------------------------------------------------

    /** �V�K�t�H���_�쐬�ł��邩�ǂ��� */
    public boolean canNewFolder() {
        return true;
    }

    /** �V�K�t�@�C���쐬�ł��邩�ǂ��� */
    public boolean canNewFile() {
        return true;
    }

    /** ���l�[���ł��邩�ǂ��� */
    public boolean canRename() {
        return true;
    }

    /** �J�b�g�ł��邩�ǂ��� */
    public boolean canCut() {
        return true;
    }

    /** �R�s�[�ł��邩�ǂ��� */
    public boolean canCopy() {
        return true;
    }

    /** �y�[�X�g�ł��邩�ǂ��� */
    public boolean canPaste(TreeViewTreeNode from) {
        // Debug.println(Debug.DEBUG, from);
        return from instanceof TestFolderTreeNode || from instanceof TestFileTreeNode;
    }
}

/* */
