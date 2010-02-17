package org.apache.tools.ant.taskdefs.condition;

import java.util.Enumeration;

import org.apache.tools.ant.taskdefs.ConditionTask;

public class AccessorHack {

    public static Enumeration getConditions(ConditionTask task) {
        return task.getConditions();
    }
}
