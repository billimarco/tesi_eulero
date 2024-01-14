package msarchitecture.mapping;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.ui.ActivityViewer;

import msarchitecture.msarchitecture.Microservice;
import msarchitecture.msarchitecture.MicroserviceType;

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

    public double getPairwiseComparisonDominanceValue(Microservice ms,int timeLimit,double timeStep,int CThreshold,int QThreshold,boolean composed){
        Activity ms_act,mst_act;
        if(composed){
            ms_act = ArchMapper.getCompositeActivity(ms);
            mst_act = ArchMapper.getCompositeActivity(ms.getMs_type());
        }else{
            ms_act = ArchMapper.getSimpleActivity(ms);
            mst_act = ArchMapper.getSimpleActivity(ms.getMs_type());
        }
        double[] mst_act_cdf = mst_act.analyze(BigDecimal.valueOf(timeLimit), BigDecimal.valueOf(timeStep), new SDFHeuristicsVisitor(BigInteger.valueOf(CThreshold), BigInteger.valueOf(QThreshold), new TruncatedExponentialApproximation()));
        double[] ms_act_cdf = ms_act.analyze(BigDecimal.valueOf(timeLimit), BigDecimal.valueOf(timeStep), new SDFHeuristicsVisitor(BigInteger.valueOf(CThreshold), BigInteger.valueOf(QThreshold), new TruncatedExponentialApproximation()));
        double[] ms_act_pdf = new EvaluationResult("ms_act_pdf", ms_act_cdf, 0, ms_act_cdf.length, 0.01, 0).pdf();
        double result = 0;
        for(int i=0;i<mst_act_cdf.length;i++){
            result += (1-mst_act_cdf[i])*ms_act_pdf[i]*timeStep;
        }
        return result;
    }

    public void printPairwiseComparisonDominanceResults(HashMap<String,Microservice> ms,double error){
        double acceptance = 0.5-error;
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Pairwise-Comparison Dominance Analysis");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Error: "+error+"\tAcceptance Value: >="+acceptance);
        System.out.println("------------------------------------------------------------------------");
        System.out.printf("%-15s   %-1s   %-20s   %-1s   %-20s%n", "node", "|", "pcd-value composite", "|", "pcd-value simple");
        System.out.println("------------------------------------------------------------------------");
        ms.forEach((key, value) -> {
            double pcdvaluecomp = getPairwiseComparisonDominanceValue(value,this.timeLimit,this.timeStep,this.CThreshold,this.QThreshold,true);
            double pcdvaluesimp = getPairwiseComparisonDominanceValue(value,this.timeLimit,this.timeStep,this.CThreshold,this.QThreshold,false);
            System.out.printf("%-15s   %-1s   %-20.3f   %-1s   %-20.3f%n", key, "|", pcdvaluecomp, "|", pcdvaluesimp);
        });
        System.out.println("------------------------------------------------------------------------");
    }

    public void plotMicroserviceTypeComparisonDistributions(MicroserviceType mst1, MicroserviceType mst2){
        Activity mst1act = ArchMapper.getCompositeActivity(mst1);
        Activity mst2act = ArchMapper.getCompositeActivity(mst2);
        double[] mst1qos = mst1act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        double[] mst2qos = mst2act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults("MicroserviceType Comparison Distribution", List.of(mst1.getName_type(), mst2.getName_type()), List.of(
                new EvaluationResult(mst1.getName_type(), mst1qos, 0, mst1qos.length, getTimeStep(), 0),
                new EvaluationResult(mst2.getName_type(), mst2qos, 0, mst2qos.length, getTimeStep(), 0)
        ));
    }

    public void plotMicroserviceComparisonDistributions(Microservice ms1, Microservice ms2){
        Activity ms1act = ArchMapper.getCompositeActivity(ms1);
        Activity ms2act = ArchMapper.getCompositeActivity(ms2);
        double[] ms1ad = ms1act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        double[] ms2ad = ms2act.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults("Microservice Comparison Distribution", List.of(ms1.getName_ms(), ms2.getName_ms()), List.of(
                new EvaluationResult(ms1.getName_ms(), ms1ad, 0, ms1ad.length, getTimeStep(), 0),
                new EvaluationResult(ms2.getName_ms(), ms2ad, 0, ms2ad.length, getTimeStep(), 0)
        ));
    }

    public void plotMicroserviceQOSComparisonDistributions(Microservice ms){
        Activity msAct = ArchMapper.getCompositeActivity(ms);
        Activity mstAct = ArchMapper.getCompositeActivity(ms.getMs_type());
        double[] msAd = msAct.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        double[] mstQos = mstAct.analyze(BigDecimal.valueOf(getTimeLimit()), BigDecimal.valueOf(getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(getCThreshold()), BigInteger.valueOf(getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults(ms.getName_ms()+" QoS-ArrDist Distribution Comparison", List.of("QoS", "Arrival Distribution"), List.of(
                new EvaluationResult("QoS", mstQos, 0, mstQos.length, getTimeStep(), 0),
                new EvaluationResult("Arrival Distribution", msAd, 0, msAd.length, getTimeStep(), 0)
        ));
    }
}
