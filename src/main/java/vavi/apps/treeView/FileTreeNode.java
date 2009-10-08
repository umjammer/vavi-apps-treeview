/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

/**
 * �t�@�C���̃c���[�m�[�h�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public abstract class FileTreeNode extends TreeViewTreeNode {

    /**
     * �t�@�C���̃m�[�h���\�z���܂��D
     * 
     * @param data �m�[�h�̃f�[�^
     */
    public FileTreeNode(Object data) {
        super(data);
    }

    // -------------------------------------------------------------------------

    /**
     * �I���^�[�Q�b�g�̍폜�����܂��D
     * 
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    public void delete() throws TreeViewException {
        deleteController();
    }

    /**
     * �ҏW�^�[�Q�b�g��؂���Ώۂɐݒ肵�܂��D ����� paste �����ƈړ��ɂȂ�܂��D
     * 
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    public void cut() throws TreeViewException {
        // �؂���ł��邩�ǂ����̃`�F�b�N
        if (!canCut()) {
            throw new TreeViewException(rb.getString("action.cut.error"));
        }

        // �؂��胂�[�h�ɐݒ�
        isCut = true;
    }

    /**
     * �I������Ă���^�[�Q�b�g���o�b�t�@�ɃR�s�[���܂��D
     * 
     * @throws TreeViewException �R�s�[�ł��Ȃ�����
     */
    public void copy() throws TreeViewException {
        isCut = false;
    }

    /**
     * �R�s�[�o�b�t�@�̓��e��I���ʒu�ɓ\��t���܂��D <br>
     * �O�̑��삪�؂��肾�����ꍇ�͈ړ��ɂȂ�܂��D �J�b�g���R�s�[�ɂ��킹�� from �����̂��̂� �R�s�[�������̂������[�U���ݒ肷��K�v������܂��D
     * 
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    public void paste(TreeViewTreeNode from) throws TreeViewException {

        if (from.isNodeChild(this)) {
            from.isCut = false;
            throw new TreeViewException(rb.getString("action.paste.nest"));
        }

        if (from.isCut()) {
            // �O�̑��삪�؂��肾�����ꍇ�͈ړ�����
            from.move(this);
            return;
        }

        from.isCut = false;

        // ����t���ł��邩�ǂ����̃`�F�b�N
        if (!this.canPaste(from)) {
            throw new TreeViewException(rb.getString("action.paste.error"));
        }

        pasteController(from);
    }

    /**
     * �c���[�f�[�^���ړ����܂��D
     * 
     * @param to �ړ���
     * @throws TreeViewException �ł��Ȃ������ꍇ
     */
    protected void move(TreeViewTreeNode to) throws TreeViewException {

        isCut = false;

        // �ړ��Ώۂƈړ��悪�����ꍇ�͉������Ȃ�
        if (this.equals(to)) {
            throw new TreeViewException(rb.getString("action.paste.same"));
        }

        // �ړ��悪�\���ǂ���
        if (!to.canPaste(this)) {
            throw new TreeViewException(rb.getString("action.paste.error"));
        }

        moveController(to);
    }
}

/* */
