package org.oristool.eulero.modeling.updates.activitytypes;

import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ActivityEnumType;
import org.oristool.eulero.modeling.updates.Composite;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.models.tpn.ConcurrencyTransitionFeature;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Transition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XORType extends ActivityType {
    private final List<Double> probs;
    public XORType(List<Double> probs){
        this.probs = probs;
    }

    public List<Double> probs() {
        return probs;
    }
    @Override
    public void initActivity(Composite activity, Activity... children) {
        if (List.of(children).size() != probs.size())
            throw new IllegalArgumentException("Each alternative must have one probability");
        activity.setActivities(new ArrayList<>(List.of(children)));
        activity.setMin(Arrays.stream(children).reduce((a, b)-> a.low().compareTo(b.low()) != 1 ? a : b).get().low());
        activity.setMax(Arrays.stream(children).reduce((a, b)-> a.upp().compareTo(b.upp()) != -1 ? a : b).get().upp());
        activity.setEnumType(ActivityEnumType.XOR);
    }

    @Override
    public void buildTPN(Composite activity, PetriNet pn, Place in, Place out, int prio) {
        // input/output places of alternative activities
        List<Place> act_ins = new ArrayList<>();
        List<Place> act_outs = new ArrayList<>();

        for (int i = 0; i < activity.activities().size(); i++) {
            Transition branch = pn.addTransition(activity.name() + "_case" + i);
            // same priority for all branches to create conflict
            branch.addFeature(new Priority(prio));
            branch.addFeature(StochasticTransitionFeature
                    .newDeterministicInstance(BigDecimal.ZERO, MarkingExpr.of(this.probs.get(i))));
            branch.addFeature(new TimedTransitionFeature("0", "0"));

            Place act_in = pn.addPlace("p" + activity.name() + "_case" + i);
            pn.addPrecondition(in, branch);
            pn.addPostcondition(branch, act_in);
            act_ins.add(act_in);

            Place act_out = pn.addPlace("p" + activity.name() + "_end" + i);
            act_outs.add(act_out);
        }

        for (int i = 0; i < activity.activities().size(); i++) {
            Transition t = pn.addTransition(activity.activities().get(i).name() + "_timed");
            t.addFeature(StochasticTransitionFeature.newUniformInstance(activity.activities().get(i).min(), activity.activities().get(i).max()));
            t.addFeature(new TimedTransitionFeature(activity.activities().get(i).min().toString(), activity.activities().get(i).max().toString()));
            t.addFeature(new ConcurrencyTransitionFeature(activity.activities().get(i).C()));
            //t.addFeature(new RegenerationEpochLengthTransitionFeature(alternatives().get(i).R()));

            pn.addPrecondition(act_ins.get(i), t);
            pn.addPostcondition(t, act_outs.get(i));
        }

        for (int i = 0; i < activity.activities().size(); i++) {
            Transition merge = pn.addTransition(activity.name() + "_merge" + i);
            merge.addFeature(StochasticTransitionFeature
                    .newDeterministicInstance(BigDecimal.ZERO));
            merge.addFeature(new TimedTransitionFeature("0", "0"));
            // new priority not necessary: only one branch will be selected
            merge.addFeature(new Priority(prio++));
            pn.addPrecondition(act_outs.get(i), merge);
            pn.addPostcondition(merge, out);
        }
    }

    @Override
    public int buildSTPN(Composite activity, PetriNet pn, Place in, Place out, int prio) {
        // input/output places of alternative activities
        List<Place> act_ins = new ArrayList<>();
        List<Place> act_outs = new ArrayList<>();

        for (int i = 0; i < activity.activities().size(); i++) {
            Transition branch = pn.addTransition(activity.name() + "_case" + i);
            // same priority for all branches to create conflict
            branch.addFeature(new Priority(prio));
            branch.addFeature(StochasticTransitionFeature
                    .newDeterministicInstance(BigDecimal.ZERO, MarkingExpr.of(probs.get(i))));

            Place act_in = pn.addPlace("p" + activity.name() + "_case" + i);
            pn.addPrecondition(in, branch);
            pn.addPostcondition(branch, act_in);
            act_ins.add(act_in);

            Place act_out = pn.addPlace("p" + activity.name() + "_end" + i);
            act_outs.add(act_out);
        }

        for (int i = 0; i < activity.activities().size(); i++) {
            activity.activities().get(i).buildSTPN(pn, act_ins.get(i), act_outs.get(i), prio++);
        }

        for (int i = 0; i < activity.activities().size(); i++) {
            Transition merge = pn.addTransition(activity.name() + "_merge" + i);
            merge.addFeature(StochasticTransitionFeature
                    .newDeterministicInstance(BigDecimal.ZERO));
            // new priority not necessary: only one branch will be selected
            merge.addFeature(new Priority(prio++));
            pn.addPrecondition(act_outs.get(i), merge);
            pn.addPostcondition(merge, out);
        }

        return prio;
    }

    @Override
    public BigInteger computeQ(Composite activity, boolean getSimplified) {
        int maximumS = 0;
        for(Activity act: activity.activities()){
            maximumS = Math.max(maximumS, act.Q().intValue());
        }

        return getSimplified ? BigInteger.ONE : BigInteger.valueOf(maximumS);
    }
}