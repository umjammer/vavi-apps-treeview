/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.beans.FeatureDescriptor;

import javax.swing.Action;


/**
 * ActionDescriptor
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020625 nsano initial version <br>
 */
public class ActionDescriptor extends FeatureDescriptor {

    @SuppressWarnings("unused")
    private String actionName;

    @SuppressWarnings("unused")
    private Class<?> actionClass;

    /** */
    public ActionDescriptor(String actionName, Class<?> actionClass) {
        this.actionName = actionName;
        this.actionClass = actionClass;

        this.setDisplayName(actionName);
        this.setShortDescription(actionName);
    }

    /** */
    public Action getAction() {
        return null;
    }
}

/* */
