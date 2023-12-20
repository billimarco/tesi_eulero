package msarchitecture.ui;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.ui.ActivityViewer;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;

public class AnalysisViewer {
    private int timeLimit;
    private double timeStep;
    private int CThreshold;
    private int QThreshold;

    public AnalysisViewer(int timeLimit, double timeStep, int CThreshold, int QThreshold) {
        this.timeLimit = timeLimit;
        this.timeStep = timeStep;
        this.CThreshold = CThreshold;
        this.QThreshold = QThreshold;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public int getCThreshold() {
        return CThreshold;
    }

    public void setCThreshold(int cThreshold) {
        CThreshold = cThreshold;
    }

    public int getQThreshold() {
        return QThreshold;
    }

    public void setQThreshold(int qThreshold) {
        QThreshold = qThreshold;
    }

    public void printServiceMeshConnections(HashMap<String,Microservice> ms){
        System.out.println("------------------------------------------------------------");
        System.out.println("Service Mesh Connections");
        System.out.println("------------------------------------------------------------");
        ms.forEach((key, value) -> {
            System.out.println(value.toString());
        });
        System.out.println("------------------------------------------------------------");
    }

    public void printPairwiseComparisonDominanceResults(HashMap<String,Microservice> ms,double error){
        double acceptance = 0.5-error;
        System.out.println("------------------------------------------------------------");
        System.out.println("Pairwise-Comparison Dominance Analysis");
        System.out.println("------------------------------------------------------------");
        System.out.println("Error: "+error+"\tAcceptance Value: "+acceptance);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s   %-1s   %-15s   %-1s   %-15s%n", "node", "|", "pcd-value", "|", "qos satisfied");
        System.out.println("------------------------------------------------------------");
        ms.forEach((key, value) -> {
            double pcdvalue = value.getPairwiseComparisonDominanceValue(this.timeLimit,this.timeStep,this.CThreshold,this.QThreshold);
            System.out.printf("%-15s   %-1s   %-15.3f   %-1s   %-15s%n", key, "|", pcdvalue, "|", pcdvalue>acceptance);
        });
        System.out.println("------------------------------------------------------------");
    }

    public void plotMicroserviceTypeComparisonDistributions(MicroserviceType mst1, MicroserviceType mst2){
        Activity mst1act = mst1.getCompositeActivity();
        Activity mst2act = mst2.getCompositeActivity();
        double[] mst1qos = mst1act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        double[] mst2qos = mst2act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults("MicroserviceType Comparison Distribution", List.of(mst1.getName_type(), mst2.getName_type()), List.of(
                new EvaluationResult(mst1.getName_type(), mst1qos, 0, mst1qos.length, getTimeStep(), 0),
                new EvaluationResult(mst2.getName_type(), mst2qos, 0, mst2qos.length, getTimeStep(), 0)
        ));
    }

    public void plotMicroserviceComparisonDistributions(Microservice ms1, Microservice ms2){
        Activity ms1act = ms1.getCompositeActivity();
        Activity ms2act = ms2.getCompositeActivity();
        double[] ms1ad = ms1act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        double[] ms2ad = ms2act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults("Microservice Comparison Distribution", List.of(ms1.getName_ms(), ms2.getName_ms()), List.of(
                new EvaluationResult(ms1.getName_ms(), ms1ad, 0, ms1ad.length, getTimeStep(), 0),
                new EvaluationResult(ms2.getName_ms(), ms2ad, 0, ms2ad.length, getTimeStep(), 0)
        ));
    }

    public void plotMicroserviceQOSComparisonDistributions(Microservice ms){
        Activity msAct = ms.getCompositeActivity();
        Activity mstAct = ms.getMs_type().getCompositeActivity();
        double[] msAd = msAct.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        double[] mstQos = mstAct.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults(ms.getName_ms()+" QoS-ArrDist Distribution Comparison", List.of("QoS", "Arrival Distribution"), List.of(
                new EvaluationResult("QoS", mstQos, 0, mstQos.length, getTimeStep(), 0),
                new EvaluationResult("Arrival Distribution", msAd, 0, msAd.length, getTimeStep(), 0)
        ));
    }
}
