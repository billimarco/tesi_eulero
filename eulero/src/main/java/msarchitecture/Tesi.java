package msarchitecture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.oristool.eulero.modeling.stochastictime.*;

import msarchitecture.mapping.AnalysisViewer;
import msarchitecture.msarchitecture.CloudLocation;
import msarchitecture.msarchitecture.EdgeLocation;
import msarchitecture.msarchitecture.Microservice;
import msarchitecture.msarchitecture.MicroserviceType;
import msarchitecture.msarchitecture.Resources;
import msarchitecture.msarchitecture.SMFactory;

public class Tesi {
    public static void main(String[] args) throws Exception {
        AnalysisViewer an = new AnalysisViewer(12,0.01,15,15);
        Resources res_cloud = new Resources(100, 100);
        Resources res_edge = new Resources(20, 16);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);

        MicroserviceType mst_1 = new MicroserviceType("1", true,new UniformTime(0,2),new Resources(7, 6));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new UniformTime(0,10),new Resources(8, 3));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new UniformTime(0,1),new Resources(8, 5));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new UniformTime(0,8),new Resources(6, 8));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new UniformTime(0,9),new Resources(7, 4));
        
        mst_1.addConnection(mst_2, 1);
        mst_1.addConnection(mst_3, 0.7);
        mst_3.addConnection(mst_4, 0.6);
        mst_3.addConnection(mst_5, 0.8);

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_3)), cloud, edge);

        sm.get("1_cloud").setMs_res(new Resources(9, 8));
        sm.get("2_cloud").setMs_res(new Resources(9, 4));
        sm.get("3_cloud").setMs_res(new Resources(9, 6));
        sm.get("4_cloud").setMs_res(new Resources(7, 9));
        sm.get("5_cloud").setMs_res(new Resources(7, 6));
        sm.get("1_edge").setMs_res(new Resources(10, 10));
        sm.get("3_edge").setMs_res(new Resources(10, 6));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_edge"));

        sm.get("1_edge").setMs_res(new Resources(4, 4));
        sm.get("3_edge").setMs_res(new Resources(5, 5));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_edge"));
    }
}
