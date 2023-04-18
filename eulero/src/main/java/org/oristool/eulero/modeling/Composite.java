package org.oristool.eulero.modeling;

import jakarta.xml.bind.annotation.XmlElement;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;
import org.oristool.eulero.modeling.deprecated.ActivityEnumType;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.activitytypes.ActivityType;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Composite extends Activity {
    @XmlElement(name = "begin", required = true)
    private Activity begin;

    @XmlElement(name = "end", required = true)
    private Activity end;

    public Composite(String name, ActivityType type, ActivityEnumType enumType){
        super(name, type, enumType);
        this.getType().setActivity(this);
        this.begin = new Simple(name + "_BEGIN",
                new DeterministicTime(BigDecimal.ZERO));
        this.end = new Simple(name + "_END",
                new DeterministicTime(BigDecimal.ZERO));
    }
    @Override
    public Activity copyRecursive(String suffix) {
        return this.getType().copyRecursive(suffix);
    }

    @Override
    public BigInteger computeQ(boolean getSimplified) {
        return this.getType().computeQ(this, getSimplified);
    }

    @Override
    public void resetSupportBounds() {

    }

    @Override
    public void buildTPN(PetriNet pn, Place in, Place out, int prio) {
        getType().buildTPN(pn, in, out, prio);
    }

    @Override
    public int buildSTPN(PetriNet pn, Place in, Place out, int prio) {
        return getType().buildSTPN(pn, in, out, prio);
    }

    @Override
    public double[] analyze(BigDecimal timeLimit, BigDecimal timeStep, AnalysisHeuristicsVisitor visitor) {
        return this.getType().analyze(timeLimit, timeStep, visitor);
    }

    @Override
    public BigDecimal low() {
        return getMinBound(this.end);
    }

    @Override
    public BigDecimal upp() {
        return this.getType().upp();
    }

    public Activity begin() {
        return begin;
    }

    /**
     * Fictitious final activity for this DAG.
     *
     * @return end activity
     */
    public Activity end() {
        return end;
    }

    @Override
    public boolean isWellNested() {
        return false;
    }

    public BigDecimal getMinBound(Activity activity){
        if(activity.equals(this.begin)){
            return activity.low();
        }

        BigDecimal maximumPredecessorLow = BigDecimal.ZERO;
        for(Activity predecessor: activity.pre()){
            maximumPredecessorLow = maximumPredecessorLow.max(getMinBound(predecessor));
        }

        return activity.low().add(maximumPredecessorLow);
    }

    public BigDecimal getMaxBound(Activity activity){
        // TODO something not working here
        //System.out.println("\nCurrent activity: " + activity.name());
        if(activity.equals(this.begin)){
            return activity.upp();
        }

        BigDecimal maximumPredecessorUpp = BigDecimal.ZERO;
        for(Activity predecessor: activity.pre()){
            //System.out.println("\nPredecessor of activity " + activity.name() + ": is " + predecessor.name());
            //System.out.println("\nIts bound is " + getMaxBound(predecessor));
            maximumPredecessorUpp = maximumPredecessorUpp.max(getMaxBound(predecessor));
            if(maximumPredecessorUpp.doubleValue() >= Double.MAX_VALUE){
                return BigDecimal.valueOf(Double.MAX_VALUE);
            }
        }

        return activity.upp().add(maximumPredecessorUpp);
    }
}