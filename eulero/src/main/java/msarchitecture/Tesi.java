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
        Resources res_cloud = new Resources(6000, 6000);
        Resources res_edge = new Resources(1000, 1000);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);

        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(0, 3.0,3),new Resources(2, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new UniformTime(1, 2.0),new Resources(2, 2));
        MicroserviceType mst_3 = new MicroserviceType("3", false, new TruncatedExponentialTime(2, 5.0,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false, new TruncatedExponentialTime(2, 5.0,5),new Resources(2, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false, new TruncatedExponentialTime(1, 3.0,2),new Resources(0, 0));
        
        mst_1.addConnection(mst_2, 0.1);
        mst_1.addConnection(mst_3, 0.3);
        mst_1.addConnection(mst_4, 0.5);
        mst_3.addConnection(mst_5, 0.1);
        mst_4.addConnection(mst_5, 0.9);
        
        HashMap<String,Microservice> ms = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_4)), cloud, edge);

        //AnalysisViewer Checker
        an.printServiceMeshConnections(ms);
        an.printPairwiseComparisonDominanceResults(ms,0.02);
        an.plotMicroserviceTypeComparisonDistributions(mst_1,mst_2);
        an.plotMicroserviceComparisonDistributions(ms.get("1_cloud"), ms.get("2_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(ms.get("1_cloud"));
    }
}
