package msarchitecture;

import java.beans.Transient;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.eulero.ui.ActivityViewer;
import org.oristool.models.stpn.TransientSolution;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.control.Controller;
import msarchitecture.control.Mapper;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.locationfeature.Resources;

public class Tesi {
    public static void main(String[] args) throws Exception {
        Resources res_cloud = new Resources(12, 16);
        Resources res_edge = new Resources(4, 8);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(0, 3.0,3));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1, 2.0,4));
        MicroserviceType mst_3 = new MicroserviceType("3", false, new TruncatedExponentialTime(2, 5.0,5));
        MicroserviceType mst_4 = new MicroserviceType("4", false, new TruncatedExponentialTime(0, 1.0,2));
        MicroserviceType mst_5 = new MicroserviceType("5", false, new TruncatedExponentialTime(0, 1.0,2));
        mst_1.addConnection(mst_2, 10);
        mst_1.addConnection(mst_3, 25);
        mst_1.addConnection(mst_4, 100);
        mst_3.addConnection(mst_5, 100);
        mst_4.addConnection(mst_5, 100);
        HashMap<String,Microservice> ms = Controller.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_4)), cloud, edge);
        ms.forEach((key, value) -> {
            System.out.println(value.toString());
        });
        Controller.calculateQOS_DG(mst_1);
        System.out.println("ServiceMesh Creata");
        BigDecimal timeLimit = BigDecimal.valueOf(6);
        double[] mst1Qos = mst_1.getQos().analyze(timeLimit, mst_1.getQos().getFairTimeTick(), new SDFHeuristicsVisitor(BigInteger.valueOf(5), BigInteger.valueOf(5), new TruncatedExponentialApproximation()));
        double[] mst1CT = mst_1.getCompletion_time().analyze(timeLimit, mst_1.getCompletion_time().getFairTimeTick(), new SDFHeuristicsVisitor(BigInteger.valueOf(1), BigInteger.valueOf(1), new TruncatedExponentialApproximation()));
        ActivityViewer.CompareResults("", List.of("mst1Qos", "mst1CT"), List.of(
                new EvaluationResult("mst1Qos", mst1Qos, 0, mst1Qos.length, 0.01, 0),
                new EvaluationResult("mst1CT", mst1CT, 0, mst1CT.length, 0.01, 0)
        ));
        System.out.println("CDF Creata");
    }
}
