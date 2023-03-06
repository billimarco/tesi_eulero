package org.oristool.eulero.modeling.updates.activitytypes;

import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ActivityEnumType;
import org.oristool.eulero.modeling.DAGEdge;
import org.oristool.eulero.modeling.updates.Composite;

import java.util.ArrayList;
import java.util.List;

public class SEQType extends DAGType{
    /*@Override
    public void initActivity(Composite activity, Activity... children){
        if (children.length == 0)
            throw new IllegalArgumentException("Sequence cannot be empty");

        initPreconditions(activity, children);

        activity.setMin(activity.low());
        activity.setMax(activity.upp());
        activity.setActivities(new ArrayList<>(List.of(children)));
        activity.setEnumType(ActivityEnumType.SEQ);
    }*/

    @Override
    public void initPreconditions(Composite activity, Activity... children) {
        Activity prev = activity.begin();
        for (Activity a : children) {
            a.addPrecondition(prev);
            prev = a;
        }
        activity.end().addPrecondition(prev);
    }

    @Override
    public void setEnumType(Composite activity) {
        activity.setEnumType(ActivityEnumType.SEQ);
    }
}