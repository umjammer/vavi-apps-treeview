/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

/**
 * �t�H���_�m�[�h�̃c���[�m�[�h�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public abstract class FolderTreeNode extends FileTreeNode {

    /**
     * �t�H���_�̃m�[�h���\�z���܂��D
     * 
     * @param userObject �m�[�h�̃f�[�^
     */
    public FolderTreeNode(Object userObject) {
        super(userObject);
    }
}

/* */
