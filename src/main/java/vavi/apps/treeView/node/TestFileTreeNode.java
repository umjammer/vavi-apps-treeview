/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView.node;

import vavi.apps.treeView.FileTreeNode;
import vavi.apps.treeView.TreeViewException;


/**
 * �t�@�C���̃c���[�m�[�h�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public class TestFileTreeNode extends FileTreeNode {

    /**
     * �t�@�C���̃m�[�h���\�z���܂��D
     * 
     * @param userObject �m�[�h�̃f�[�^
     */
    public TestFileTreeNode(Object userObject) {
        super((userObject instanceof String) ? new Test((String) userObject, 1) : userObject);
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

    // -------------------------------------------------------------------------

    /** �J�b�g�ł��邩�ǂ��� */
    public boolean canCut() {
        return true;
    }

    /** �R�s�[�ł��邩�ǂ��� */
    public boolean canCopy() {
        return true;
    }

    /** �폜�ł��邩�ǂ��� */
    public boolean canDelete() {
        return true;
    }

    /** ���l�[���ł��邩�ǂ��� */
    public boolean canRename() {
        return true;
    }
}

/* */
