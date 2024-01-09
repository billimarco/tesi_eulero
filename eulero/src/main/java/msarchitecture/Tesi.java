package msarchitecture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.oristool.eulero.modeling.stochastictime.*;
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.resourcesfeature.Resources;
import msarchitecture.ui.AnalysisViewer;
import msarchitecture.utils.SMFactory;

public class Tesi {
    public static void main(String[] args) throws Exception {
        AnalysisViewer an = new AnalysisViewer(8,0.01,15,15);
        Resources res_cloud = new Resources(16, 16);
        Resources res_edge = new Resources(4, 4);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);

        MicroserviceType mst_1 = new MicroserviceType("1", true,new UniformTime(0,4),new Resources(0, 2));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new UniformTime(0,2),new Resources(1, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new UniformTime(0,2),new Resources(1, 1));

        mst_1.addConnection(mst_2, 1);
        mst_1.addConnection(mst_3, 0.5);

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_3)), cloud, edge);

        
        sm.get("1_edge").setMs_res(new Resources(0, 1));
        sm.get("2_edge").setMs_res(new Resources(0, 1));
        sm.get("3_edge").setMs_res(new Resources(2, 0));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("2_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_edge"));

        sm.get("1_edge").setMs_res(new Resources(0, 2));
        sm.get("2_edge").setMs_res(new Resources(1, 0));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        sm.get("1_edge").setMs_res(new Resources(1, 2));
        sm.get("2_edge").setMs_res(new Resources(1, 1));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("2_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_edge"));
    }
}
