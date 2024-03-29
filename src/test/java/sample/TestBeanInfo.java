/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package sample;

import java.awt.*;
import java.beans.*;
import vavi.apps.treeView.*;


/**
 * TestBeanInfo です．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020625 nsano initial version <br>
 */
public class TestBeanInfo extends SimpleBeanInfo implements TreeNodeInfo {

    private final Class<?> clazz = Test.class;

    /** */
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(clazz, null);
    }

    /** */
    public Image getIcon(int iconKind) {
        switch (iconKind) {
        case ICON_COLOR_16x16_EXT2:
            return loadImage("/vavi/apps/treeView/node/default_open.png");
        case ICON_COLOR_16x16_EXT1:
            return loadImage("/vavi/apps/treeView/node/default_close.png");
        default:
            return loadImage("/vavi/apps/treeView/node/default_file.png");
        }
    }

    /** */
    public ActionDescriptor[] getActionDescriptors() {
        ActionDescriptor[] ads = new ActionDescriptor[1];
        ads[0] = new ActionDescriptor("", clazz);

        return ads;
    }
}

/* */
