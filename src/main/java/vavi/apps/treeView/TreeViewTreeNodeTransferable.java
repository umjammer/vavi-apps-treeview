/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

import vavi.awt.dnd.BasicTransferable;


/**
 * The transferable object for TreeViewTreeNode.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 020609 nsano refine <br>
 */
public class TreeViewTreeNodeTransferable extends BasicTransferable {

    private static final Class<?> c = TreeViewTreeNode.class;

    /** this class's flavor */
    public static final DataFlavor flavor = new DataFlavor(c, c.getName());

    /** Flavors */
    {
        DataFlavor[] flavors = new DataFlavor[] {
            flavor
        };
        flavorList = Arrays.asList(flavors);
    }

    /**
     * Constructor. simply initializes instance variable
     */
    public TreeViewTreeNodeTransferable(TreeViewTreeNode node) {
        super(node);
    }

    /** Gets the transferable data. */
    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

        return super.getTransferData(flavor);
    }

    /** Gets the string representing this object. */
    public String toString() {
        return getClass().getName();
    }
}

/* */
