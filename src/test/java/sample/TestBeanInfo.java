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
 * TestBeanInfo.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020625 nsano initial version <br>
 */
public class TestBeanInfo extends SimpleBeanInfo implements TreeNodeInfo {

    private final Class<?> clazz = Test.class;

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(clazz, null);
    }

    @Override
    public Image getIcon(int iconKind) {
        return switch (iconKind) {
            case ICON_COLOR_16x16_EXT2 -> loadImage("/vavi/apps/treeView/node/default_open.png");
            case ICON_COLOR_16x16_EXT1 -> loadImage("/vavi/apps/treeView/node/default_close.png");
            default -> loadImage("/vavi/apps/treeView/node/default_file.png");
        };
    }

    /** */
    public ActionDescriptor[] getActionDescriptors() {
        ActionDescriptor[] ads = new ActionDescriptor[1];
        ads[0] = new ActionDescriptor("", clazz);

        return ads;
    }
}
