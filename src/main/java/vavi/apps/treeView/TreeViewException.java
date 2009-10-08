/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

/**
 * TreeView �̗�O�����������ꍇ�X���[����܂��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 010822 nsano add inclusive exception <br>
 */
public final class TreeViewException extends Exception {

    /** ����G���[ */
    private Throwable throwable;

    /**
     * �ڍ׃��b�Z�[�W�������Ȃ� TreeViewException ���\�z���܂��D
     */
    public TreeViewException() {
        super();
    }

    /**
     * �ڍ׃��b�Z�[�W������ TreeViewException ���\�z���܂��D
     * 
     * @param s �ڍ׃��b�Z�[�W
     */
    public TreeViewException(String s) {
        super(s);
    }

    /**
     * ����G���[������ TreeViewException ���\�z���܂��D
     * 
     * @param t �����G���[
     */
    public TreeViewException(Throwable t) {
        this.throwable = t;
    }

    /**
     * ����G���[��Ԃ��܂��D
     */
    public Throwable getThrowable() {
        return throwable;
    }
}

/* */
