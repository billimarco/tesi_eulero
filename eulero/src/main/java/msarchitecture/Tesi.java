package msarchitecture;

import java.beans.Transient;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
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
        Controller cont = new Controller();
        Resources res_cloud = new Resources(12, 16);
        Resources res_edge = new Resources(4, 8);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);
        MicroserviceType mst_1 = new MicroserviceType("1", true);
        MicroserviceType mst_2 = new MicroserviceType("2", false);
        MicroserviceType mst_3 = new MicroserviceType("3", false);
        MicroserviceType mst_4 = new MicroserviceType("4", false);
        MicroserviceType mst_5 = new MicroserviceType("5", false);
        mst_1.addConnection(mst_2, 10);
        mst_1.addConnection(mst_3, 25);
        mst_1.addConnection(mst_4, 100);
        mst_3.addConnection(mst_5, 100);
        mst_4.addConnection(mst_5, 100);
        HashMap<String,Microservice> ms = cont.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_4)), cloud, edge);
        ms.forEach((key, value) -> {
            System.out.println(value.toString());
        });
        mst_1.removeConnection(mst_2);
        System.out.println("ServiceMesh Creata");
        Activity DAGcomp = Mapper.mapServiceMesh(ms);
        double[] cdf = DAGcomp.analyze(DAGcomp.max().add(BigDecimal.ONE), DAGcomp.getFairTimeTick(), new SDFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation()));
        ActivityViewer.CompareResults("", List.of("Senza Repl", "Con Repl", "albero diretto"), List.of(
                new EvaluationResult("Senza Repl", cdf, 0, cdf.length, 0.01, 0),
                new EvaluationResult("Con Repl", cdf, 0, cdf.length, 0.01, 0)

        ));
        ActivityViewer.plot("CDF", List.of(""), 4, 0.01, cdf);
        ArrayList<MicroserviceType> mst_list = new ArrayList<>();
        mst_list.add(mst_1);
        mst_list.add(mst_2);
        mst_list.add(mst_3);
        mst_list.add(mst_4);
        mst_list.add(mst_5);
        Mapper.mapDependencyGraph(mst_list);
        mst_1.plotCompletation_Time_CDF();
        mst_2.plotCompletation_Time_CDF();
        mst_3.plotCompletation_Time_CDF();
        mst_4.plotCompletation_Time_CDF();
        mst_5.plotCompletation_Time_CDF();
        System.out.println("CDF Creata");
    }
}
