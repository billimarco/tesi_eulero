package org.oristool.eulero.modeling.activitytypes;

import com.google.common.collect.Lists;
import org.oristool.eulero.evaluation.approximator.Approximator;
import org.oristool.eulero.evaluation.heuristics.AnalysisHeuristicsVisitor;

import org.oristool.eulero.modeling.deprecated.ActivityEnumType;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.Composite;
import org.oristool.eulero.modeling.DFSObserver;
import org.oristool.eulero.modeling.Simple;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.petrinet.Marking;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class BadNestedDAGType extends DAGType{
    public BadNestedDAGType(ArrayList<Activity> children) {
        super(children);
    }


    @Override
    public void initPreconditions(Composite activity, Activity... children) {
        for(Activity act: children){
            if(act.pre().isEmpty()){
                act.addPrecondition(activity.begin());
            }
            if(act.post().isEmpty()){
                activity.end().addPrecondition(act);
            }
        }
    }

    @Override
    public void setEnumType(Composite activity) {
        activity.setEnumType(ActivityEnumType.DAG);
    }

    @Override
    public Composite copyRecursive(String suffix) {
        return copyRecursive(getActivity().begin(), getActivity().end(), suffix);
    }

    @Override
    public double[] analyze(BigDecimal timeLimit, BigDecimal timeTick, AnalysisHeuristicsVisitor visitor){
        return visitor.analyze(this, timeLimit, timeTick);
    };

    public double[] forwardTransientAnalysis(BigDecimal timeLimit, BigDecimal step){
        TransientSolution<Marking, RewardRate> transientSolution =  getActivity().forwardAnalyze(timeLimit.toString(), step.toString(), "0.001");
        double[] CDF = new double[transientSolution.getSolution().length];

        for(int i = 0; i < CDF.length; i++){
            CDF[i] = transientSolution.getSolution()[i][0][0];
        }

        return CDF;
    }

    public double[] innerBlockReplication(BigDecimal timeLimit, BigDecimal step, BigInteger CThreshold, BigInteger QThreshold, AnalysisHeuristicsVisitor visitor){
        /*ArrayList<Activity> replicatedBlocks = new ArrayList<>();
        ArrayList<Composite> sortedReplicatedBlocks = new ArrayList<>();
        for(Activity activity: getActivity().end().pre()){
            Activity replicatedBlock = this.copyRecursive((getActivity()).begin(), activity, "_before_" + activity.name());
            //sortedReplicatedBlocks.add(replicatedBlock);
            if(checkWellNesting((Composite) replicatedBlock)){
                replicatedBlock = wellNestIt(replicatedBlock.activities());
            }
            replicatedBlock.C();
            replicatedBlock.Q();
            replicatedBlocks.add(replicatedBlock);
        }*/

        // TODO per ora sta trasformando tutto in albero (e questo può impattare sull'accuratezza, dovremmo cambiare alcune cose, ma lo potremmo fare più avanti)

        Activity newAND = dag2tree(getActivity().end().pre());

        return newAND.analyze(timeLimit, step, visitor);
    }

    public double[] innerBlockAnalysis(BigDecimal timeLimit, BigDecimal step, BigInteger CThreshold, BigInteger QThreshold, AnalysisHeuristicsVisitor visitor, Approximator approximator){
        Map<String, Activity> toBeSimplifiedActivityMap = getMostComplexChild(getActivity(), CThreshold, QThreshold);
        Activity toBeSimplifiedActivity = toBeSimplifiedActivityMap.get("activity");
        Activity toBeSimplifiedActivityParent = toBeSimplifiedActivityMap.get("parent");
        double aux = toBeSimplifiedActivity.max().doubleValue();
        int mag = 1;
        while (aux > 10) {
            mag = mag * 10;
            aux = aux / 10;
        }
        BigDecimal innerActivityStep = BigDecimal.valueOf(mag * Math.pow(10, -2));


        StochasticTime approximatedStochasticTime =  approximator.getApproximatedStochasticTime(
                toBeSimplifiedActivity.analyze(toBeSimplifiedActivity.max().precision() >= 309 ? timeLimit : toBeSimplifiedActivity.max(), step, visitor),
                toBeSimplifiedActivity.min().doubleValue(), (toBeSimplifiedActivity.max().precision() >= 309 ? timeLimit : toBeSimplifiedActivity.max()).doubleValue(), innerActivityStep);

        Activity newActivity = new Simple(toBeSimplifiedActivity.name() + "_N", approximatedStochasticTime);

        toBeSimplifiedActivity.replace(newActivity);
        int activityIndex = toBeSimplifiedActivityParent.activities().indexOf(toBeSimplifiedActivity);
        toBeSimplifiedActivityParent.activities().set(activityIndex, newActivity);
        toBeSimplifiedActivityParent.resetComplexityMeasure();

        getActivity().resetComplexityMeasure();

        return visitor.analyze(this, timeLimit, step);
    }

    public Map<String, Activity> getMostComplexChild(Composite model, BigInteger CThreshold, BigInteger QThreshold){
            ArrayList<Activity> innerActivities = model.activities().stream().filter(t -> (t.C().doubleValue() > 1 || t.Q().doubleValue() > 1)).distinct().sorted(Comparator.comparing(Activity::C).thenComparing(Activity::Q)).collect(Collectors.toCollection(ArrayList::new));
            Activity mostComplexActivity = innerActivities.get(innerActivities.size() - 1);
            boolean modelIsNotADag = mostComplexActivity.type().equals(ActivityEnumType.AND) || mostComplexActivity.type().equals(ActivityEnumType.SEQ) || mostComplexActivity.type().equals(ActivityEnumType.XOR) || mostComplexActivity.type().equals(ActivityEnumType.SIMPLE);

            if(!modelIsNotADag && mostComplexActivity.C().compareTo(CThreshold) > 0 && mostComplexActivity.Q().compareTo(QThreshold) > 0){
                // TODO forse c'è un modo, tramite i type, di non dover fare il cast
                return getMostComplexChild((Composite) mostComplexActivity, CThreshold, QThreshold);
            }

            return Map.ofEntries(
                    Map.entry("parent", model),
                    Map.entry("activity", mostComplexActivity)
            );

    }

    public Composite copyRecursive(Activity begin, Activity end, String suffix) {
        Composite copy = new Composite(getActivity().name() + suffix, new BadNestedDAGType(new ArrayList<>()), ActivityEnumType.DAG);

        Map<Activity, Activity> nodeCopies = new HashMap<>();

        if (getActivity().begin().equals(begin)) {
            nodeCopies.put(begin, copy.begin());
        } else {
            Activity ax = begin.copyRecursive(suffix);
            nodeCopies.put(begin, ax);
            ax.addPrecondition(copy.begin());
        }

        if (getActivity().end().equals(end)) {
            nodeCopies.put(end, copy.end());
        } else {
            Activity ax = end.copyRecursive(suffix);
            nodeCopies.put(end, ax);
            copy.end().addPrecondition(ax);
        }

        Set<Activity> activitiesBetween = activitiesBetween(begin, end);
        ArrayList<Activity> createdActivities = new ArrayList<>();
        for (Activity a : activitiesBetween) {
            Activity ax = nodeCopies.computeIfAbsent(a, k -> k.copyRecursive(suffix));
            if (!a.equals(begin)) {
                List<Activity> aprex = a.pre().stream()
                        .filter(p -> activitiesBetween.contains(p))
                        .map(p -> nodeCopies.computeIfAbsent(p, k -> k.copyRecursive(suffix)))
                        .collect(Collectors.toCollection(ArrayList::new));
                ax.setPre(aprex);

                createdActivities.add(ax);
            }

            if (!a.equals(end)) {
                List<Activity> apostx = a.post().stream()
                        .filter(p -> activitiesBetween.contains(p))
                        .map(p -> nodeCopies.computeIfAbsent(p, k -> k.copyRecursive(suffix)))
                        .collect(Collectors.toCollection(ArrayList::new));
                ax.setPost(apostx);

                createdActivities.add(ax);
            }
        }

        copy.setMin(copy.low());
        copy.setMax(copy.upp());
        copy.setActivities(createdActivities);
        return copy;
    }

    public Set<Activity> activitiesBetween(Activity begin, Activity end) {

        Set<Activity> activitiesBetween = new HashSet<>();
        Set<Activity> nodesOpen = new HashSet<>();
        activitiesBetween.add(begin);

        end.dfs(true, new DFSObserver() {
            @Override public boolean onOpen(Activity opened, Activity from) {
                nodesOpen.add(opened);
                if (activitiesBetween.contains(opened)) {
                    // all open nodes are between "begin" and "end"
                    activitiesBetween.addAll(nodesOpen);
                }

                return true;  // continue
            }

            @Override public boolean onSkip(Activity skipped, Activity from) {
                if (activitiesBetween.contains(skipped)) {
                    // all open nodes are between "begin" and "end"
                    activitiesBetween.addAll(nodesOpen);
                }

                return true;
            }

            @Override public boolean onClose(Activity closed) {
                nodesOpen.remove(closed);
                if (closed.equals(end))
                    return false;  // stop
                else
                    return true;  // continue
            }
        });

        return activitiesBetween;
    }

    public void removeBetween(Activity begin, Activity end, boolean removeShared) {

        Set<Activity> activitiesBetween = this.activitiesBetween(begin, end);

        if (!removeShared) {
            for (Activity p : new ArrayList<>(end.post())) {
                p.removePrecondition(end);
            }

            activitiesBetween.removeAll(activitiesBetween(getActivity().begin(), getActivity().end()));
        }

        List<Activity> all = this.nested();
        all.add(getActivity().begin());
        all.add(getActivity().end());

        for (Activity a : all) {
            List<Activity> pre = a.pre().stream()
                    .filter(x -> !activitiesBetween.contains(a) &&
                            !activitiesBetween.contains(x))
                    .collect(Collectors.toCollection(ArrayList::new));
            a.setPre(pre);

            List<Activity> post = a.post().stream()
                    .filter(x -> !activitiesBetween.contains(a) &&
                            !activitiesBetween.contains(x))
                    .collect(Collectors.toCollection(ArrayList::new));
            a.setPost(post);
        }
    }

    public List<Activity> nested() {
        List<Activity> activities = new ArrayList<>();

        getActivity().begin().dfs(false, new DFSObserver() {
            @Override public boolean onOpen(Activity opened, Activity from) {
                if (opened != getActivity().begin() && opened != getActivity().end()) {
                    activities.add(opened);
                }

                return true;  // continue
            }
        });

        return activities;
    }

    public Composite nest(Activity end) {
        Composite copy = this.copyRecursive(getActivity().begin(), end, "_up".replace(getActivity().name(),""));

        this.removeBetween(getActivity().begin(), end, false);
        Composite restOfDAG = this.copyRecursive("_down");

        Activity up = copy;
        Activity down = restOfDAG;

        if(this.checkWellNesting(getActivity())){
            up = wellNestIt(copy.begin().post());
        }

        if(this.checkWellNesting(restOfDAG)){
            down = wellNestIt(restOfDAG.begin().post());
        }

        return ModelFactory.forkJoin(up, down);
    }

    public boolean checkWellNesting(Composite modelToCheck){
        Deque<Set<Activity>> levelNode = new LinkedList<>();
        levelNode.push(Collections.singleton(modelToCheck.begin()));

        while(!levelNode.isEmpty()) {
            Set<Activity> thisLevelActivities = levelNode.pop();
            Set<Activity> nextLevelActivities = new HashSet<>();

            for (Activity act : thisLevelActivities) {
                Set<Activity> theOtherNodes = new HashSet<>(thisLevelActivities);
                theOtherNodes.remove(act);

                for (Activity other : theOtherNodes) {
                    boolean samePredecessors = act.pre().containsAll(other.pre()) && act.pre().size() == 1 && other.pre().size() == 1;
                    boolean disjointPrecedessors = act.pre().stream().filter(other.pre()::contains).collect(Collectors.toList()).isEmpty();

                    boolean isWellNested = (samePredecessors || disjointPrecedessors);

                    if(!isWellNested){
                        return false;
                    }
                }

                nextLevelActivities.addAll(act.post());
            }

            if(!nextLevelActivities.isEmpty()){
                levelNode.push(nextLevelActivities);
            }
        }

        return true;
    }

    /* Pass begin of dag as first parameter*/
    public static Activity wellNestIt(List<Activity> nodes){
        if (nodes.size() > 1){
            Set<Activity> theseNodeSuccessors = new HashSet<>();
            // Get nodes from successive level
            for(Activity act: nodes){
                theseNodeSuccessors.addAll(act.post());
            }

            // If nodes share a single successor, and this is an END, then create a fork-join
            if (theseNodeSuccessors.size() == 1){
                StringBuilder name = new StringBuilder("AND(");
                for(Activity act: nodes){
                    name.append((nodes.indexOf(act) == nodes.size() - 1) ? act.name() + ")" : act.name() + ", ");
                }
                Activity forkJoin = ModelFactory.forkJoin(nodes.toArray(Activity[]::new));

                if(!List.copyOf(theseNodeSuccessors).get(0).max().equals(BigDecimal.ZERO)){
                    ArrayList<Activity> theSequence = new ArrayList<>();
                    theSequence.add(forkJoin);
                    Activity sequenceLastNode = wellNestIt(Lists.newArrayList(theseNodeSuccessors));
                    theSequence.add(sequenceLastNode);

                    return ModelFactory.sequence(theSequence.toArray(Activity[]::new));
                }
                return forkJoin;
            }

            StringBuilder name = new StringBuilder("AND(");
            ArrayList<Activity> forkJoinNodes = new ArrayList<>();
            for(Activity act: nodes) {
                Activity andNode = wellNestIt(Lists.newArrayList(act));
                forkJoinNodes.add(andNode);
                name.append((nodes.indexOf(act) == nodes.size() - 1) ? andNode.name() + ")" : andNode.name() + ", ");
            }

            return ModelFactory.forkJoin(forkJoinNodes.toArray(Activity[]::new));
        }

        if(nodes.size() == 1 && nodes.get(0).post().size() > 1){
            List<Activity> sequenceNodes = new ArrayList<>();
            StringBuilder name = new StringBuilder("SEQ(");
            sequenceNodes.add(nodes.get(0));
            sequenceNodes.add(wellNestIt(nodes.get(0).post()));
            List<Activity> joinPoint = findJoinPoint(nodes.get(0));
            if(!joinPoint.isEmpty() && !joinPoint.get(0).max().equals(BigDecimal.ZERO)){
                sequenceNodes.add(wellNestIt(joinPoint));
            }

            for(Activity act: sequenceNodes){
                name.append((sequenceNodes.indexOf(act) == sequenceNodes.size() - 1) ? act.name() + ")" : act.name() + ", ");
            }
            return ModelFactory.sequence(sequenceNodes.toArray(Activity[]::new));
        }

        if(nodes.size() == 1 && nodes.get(0).post().size() == 1 && !nodes.get(0).post().get(0).max().equals(BigDecimal.ZERO)){
            List<Activity> sequenceNodes = new ArrayList<>();
            StringBuilder name = new StringBuilder("SEQ(");
            sequenceNodes.add(nodes.get(0));
            sequenceNodes.add(wellNestIt(nodes.get(0).post()));

            for(Activity act: sequenceNodes){
                act.pre().clear();
                act.post().clear();
                name.append((sequenceNodes.indexOf(act) == sequenceNodes.size() - 1) ? act.name() + ")" : act.name() + ", ");
            }
            return ModelFactory.sequence(sequenceNodes.toArray(Activity[]::new));
        }

        return nodes.get(0);
    }

    public static List<Activity> findJoinPoint(Activity startingNode){
        // La lista ritornata contiene o 0 o 1 elementi
        Set<Activity> theSetOfSuccessors = new HashSet<>();
        theSetOfSuccessors.addAll(startingNode.post());

        while(theSetOfSuccessors.size() > 1){
            Set<Activity> nextSetOfSuccessors = new HashSet<>();
            for(Activity act: theSetOfSuccessors){
                nextSetOfSuccessors.addAll(act.post().stream().filter(t -> !t.max().equals(BigDecimal.ZERO)).collect(Collectors.toList()));
            }

            theSetOfSuccessors = nextSetOfSuccessors;
        }
        return List.copyOf(theSetOfSuccessors);
    }

    public Activity dag2tree(List<Activity> nodes){
        if (nodes.size() > 1){
            StringBuilder name = new StringBuilder("AND(");
            ArrayList<Activity> forkJoinNodes = new ArrayList<>();
            for(Activity act: nodes){
                Activity andNode = dag2tree(Lists.newArrayList(act));
                forkJoinNodes.add(andNode);
            }

            for(Activity act: forkJoinNodes){
                name.append((forkJoinNodes.indexOf(act) == nodes.size() - 1) ? act.name() + ")" : act.name() + ", ");
            }

            return ModelFactory.forkJoin(forkJoinNodes.toArray(Activity[]::new));//ModelFactory.forkJoin(forkJoinNodes.toArray(Activity[]::new));
        }

        //!nodes.get(0).pre().get(0).max().equals(BigDecimal.ZERO
        if(nodes.size() == 1 && nodes.get(0).pre().size() > 1 && nodes.get(0).pre().stream().noneMatch(t -> t.max().equals(BigDecimal.ZERO))){
            List<Activity> sequenceNodes = new ArrayList<>();
            StringBuilder name = new StringBuilder("SEQ(");

            sequenceNodes.add(dag2tree(nodes.get(0).pre()));
            sequenceNodes.add(nodes.get(0));

            for(Activity act: sequenceNodes){
                act.pre().clear();
                act.post().clear();
                name.append((sequenceNodes.indexOf(act) == sequenceNodes.size() - 1) ? act.name() + ")" : act.name() + ", ");
            }
            return ModelFactory.sequence(sequenceNodes.toArray(Activity[]::new));
        }

        if(nodes.size() == 1 && nodes.get(0).pre().size() == 1 && !nodes.get(0).pre().get(0).max().equals(BigDecimal.ZERO)){
            List<Activity> sequenceNodes = new ArrayList<>();
            StringBuilder name = new StringBuilder("SEQ(");
            sequenceNodes.add(dag2tree(nodes.get(0).pre()));
            sequenceNodes.add(nodes.get(0));

            for(Activity act: sequenceNodes){
                act.pre().clear();
                act.post().clear();
                name.append((sequenceNodes.indexOf(act) == sequenceNodes.size() - 1) ? act.name() + ")" : act.name() + ", ");
            }
            return ModelFactory.sequence(sequenceNodes.toArray(Activity[]::new));
        }

        return nodes.get(0);
    }
}