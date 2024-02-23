/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package sample;

import java.io.Serializable;


/**
 * Test UserObject.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010908 nsano initial version <br>
 */
public class Test implements Serializable {

    private String name;
    private int i;

    public Test() {
    }

    public Test(String name, int i) {
        this.name = name;
        this.i = i;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }

    public String toString() {
        return name;
    }
}
