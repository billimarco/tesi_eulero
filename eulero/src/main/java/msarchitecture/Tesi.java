package msarchitecture;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.ui.ActivityViewer;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.control.Orchestrator;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.resourcesfeature.Resources;

public class Tesi {
    public static void main(String[] args) throws Exception {
        Resources res_cloud = new Resources(6000, 6000);
        Resources res_edge = new Resources(6000, 6000);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);

        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(0, 3.0,3),new Resources(2, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1, 2.0,4),new Resources(0, 2));
        MicroserviceType mst_3 = new MicroserviceType("3", false, new TruncatedExponentialTime(2, 5.0,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false, new TruncatedExponentialTime(2, 5.0,5),new Resources(2, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false, new TruncatedExponentialTime(1, 3.0,2),new Resources(0, 0));
        
        mst_1.addConnection(mst_2, 0.1);
        mst_1.addConnection(mst_3, 0.3);
        mst_1.addConnection(mst_4, 0.5);
        mst_3.addConnection(mst_5, 0.1);
        mst_4.addConnection(mst_5, 0.9);

        HashMap<String,Microservice> ms = Orchestrator.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_4)), cloud, edge);
        Orchestrator.printServiceMeshConnections(ms);
        Orchestrator.printPairwiseComparisonDominanceResults(ms,0.02);
        /**
        Activity mstCom4= mst_4.getCompositeActivity();
        Activity mstCom3 = mst_3.getCompositeActivity();
        Activity msCom4edge = ms.get("4_edge").getCompositeActivity();
        Activity msCom3cloud = ms.get("3_cloud").getCompositeActivity();
        double[] mst4Qos = mstCom4.analyze(BigDecimal.valueOf(Orchestrator.getTimeLimit()), BigDecimal.valueOf(Orchestrator.getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(Orchestrator.getCThreshold()), BigInteger.valueOf(Orchestrator.getQThreshold()), new TruncatedExponentialApproximation()));
        double[] mst3Qos = mstCom3.analyze(BigDecimal.valueOf(Orchestrator.getTimeLimit()), BigDecimal.valueOf(Orchestrator.getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(Orchestrator.getCThreshold()), BigInteger.valueOf(Orchestrator.getQThreshold()), new TruncatedExponentialApproximation()));
        double[] ms4edgearr = msCom4edge.analyze(BigDecimal.valueOf(Orchestrator.getTimeLimit()), BigDecimal.valueOf(Orchestrator.getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(Orchestrator.getCThreshold()), BigInteger.valueOf(Orchestrator.getQThreshold()), new TruncatedExponentialApproximation()));
        double[] ms3cloudarr = msCom3cloud.analyze(BigDecimal.valueOf(Orchestrator.getTimeLimit()), BigDecimal.valueOf(Orchestrator.getTimeStep()), new SDFHeuristicsVisitor(BigInteger.valueOf(Orchestrator.getCThreshold()), BigInteger.valueOf(Orchestrator.getQThreshold()), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults("", List.of("mst4Qos", "ms4edgearr"), List.of(
                new EvaluationResult("mst4Qos", mst4Qos, 0, mst4Qos.length, 0.01, 0),
                new EvaluationResult("ms4edgearr", ms4edgearr, 0, ms4edgearr.length, 0.01, 0)
        ));
        */
    }
}
