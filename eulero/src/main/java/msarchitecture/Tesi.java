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
        AnalysisViewer an = new AnalysisViewer(12,0.01,15,15);
        Resources res_cloud = new Resources(16, 16);
        Resources res_edge = new Resources(8, 8);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);

        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new TruncatedExponentialTime(1,3,5),new Resources(1, 1));
        
        mst_1.addConnection(mst_2, 1);
        mst_1.addConnection(mst_3, 1);
        mst_1.addConnection(mst_4, 1);
        mst_2.addConnection(mst_5, 1);

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1, mst_3, mst_4)), cloud, edge);

        an.printPairwiseComparisonDominanceResults(sm,0.02);
        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_cloud"));

        sm.get("5_cloud").setMs_res(new Resources(2, 2));
        an.printPairwiseComparisonDominanceResults(sm,0.02);
        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_cloud"));
    }
}
