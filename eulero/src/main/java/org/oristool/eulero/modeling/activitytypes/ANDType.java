package org.oristool.eulero.modeling.activitytypes;

import jakarta.xml.bind.annotation.XmlRootElement;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.Composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

@XmlRootElement(name = "and-type")
public class ANDType extends DAGType {
    public ANDType(ArrayList<Activity> children) {
        super(children);
    }
    public ANDType(){
        super(new ArrayList<>());
    }
    @Override
    public void initPreconditions(Composite activity, Activity... children) {
        for (Activity act : children) {
            act.addPrecondition(activity.begin());
            activity.end().addPrecondition(act);
        }
    }

    @Override
    public void setEnumType(Composite activity) {
        activity.setEnumType(ActivityEnumType.AND);
    }

    @Override
    public Activity copyRecursive(String suffix) {
        return null;
    }

    @Override
    public double[] analyze(BigDecimal timeLimit, BigDecimal timeTick, AnalysisHeuristicsVisitor visitor){
        return visitor.analyze(this, timeLimit, timeTick);
    }

    @Override
    public ActivityType clone() {
        ArrayList<Activity> clonedActivities = getChildren().stream().map(Activity::clone).collect(Collectors.toCollection(ArrayList::new));
        return new ANDType(clonedActivities);
    }
}
