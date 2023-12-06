package msarchitecture.ui;

import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;

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
        System.out.println("--------------------------------------------");
        System.out.println("Service Mesh Connections");
        System.out.println("--------------------------------------------");
        ms.forEach((key, value) -> {
            System.out.println(value.toString());
        });
        System.out.println("--------------------------------------------");
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
}
