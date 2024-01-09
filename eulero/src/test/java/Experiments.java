
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.resourcesfeature.Resources;
import msarchitecture.ui.AnalysisViewer;
import msarchitecture.utils.SMFactory;

import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
public class Experiments {
    AnalysisViewer an = new AnalysisViewer(12,0.01,15,15);
    Resources res_cloud;
    Resources res_edge;
    CloudLocation cloud;
    EdgeLocation edge;

    @Before
    public void initMethod() {
        res_cloud = new Resources(16, 16);
        res_edge = new Resources(4, 4);
        cloud = new CloudLocation(res_cloud);
        edge = new EdgeLocation(res_edge);
    }

    @Test
    public void experiment1(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new UniformTime(0,2),new Resources(2, 2));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new UniformTime(1,3),new Resources(0, 3));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new UniformTime(0,2),new Resources(4, 2));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new UniformTime(0,1),new Resources(1, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new UniformTime(0,1),new Resources(1, 1));
        
        mst_1.addConnection(mst_2, 1);
        mst_1.addConnection(mst_3, 1);
        mst_1.addConnection(mst_4, 1);
        mst_2.addConnection(mst_5, 1);

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1, mst_3, mst_4)), cloud, edge);
        
        sm.get("1_cloud").setMs_res(new Resources(1, 3));
        sm.get("2_cloud").setMs_res(new Resources(1, 5));
        sm.get("3_cloud").setMs_res(new Resources(2, 4));
        sm.get("4_cloud").setMs_res(new Resources(3, 0));
        sm.get("5_cloud").setMs_res(new Resources(1, 0));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("2_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("4_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("5_cloud"));

        sm.get("1_cloud").setMs_res(new Resources(2, 2));
        sm.get("2_cloud").setMs_res(new Resources(0, 4));
        sm.get("3_cloud").setMs_res(new Resources(4, 3));
        sm.get("4_cloud").setMs_res(new Resources(1, 1));
        sm.get("5_cloud").setMs_res(new Resources(1, 2));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("2_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("4_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("5_cloud"));

        sm.get("2_cloud").setMs_res(new Resources(0, 3));
        sm.get("3_cloud").setMs_res(new Resources(4, 2));
        sm.get("4_cloud").setMs_res(new Resources(1, 0));
        sm.get("5_cloud").setMs_res(new Resources(1, 1));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        System.out.println("|----------------> Service Mesh of Graph 1 <----------------|\n");
        an.printServiceMeshConnections(sm);
        System.out.println("\n|----------------> Service Mesh of Graph 1 <----------------|");
        assertNotNull("ServiceMesh Creata",sm);
    }

    @Test
    public void experiment2(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(0,2,3),new Resources(1, 1));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new UniformTime(0,2),new Resources(4, 1));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new UniformTime(0,2),new Resources(2, 2));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new TruncatedExponentialTime(0,2,3),new Resources(3, 1));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new TruncatedExponentialTime(0,2,3),new Resources(2, 6));
        MicroserviceType mst_6 = new MicroserviceType("6", false,new UniformTime(0,2),new Resources(4, 5));
        
        mst_1.addConnection(mst_2, 0.8);
        mst_1.addConnection(mst_3, 0.7);
        mst_3.addConnection(mst_4, 0.3);
        mst_3.addConnection(mst_5, 0.4);
        mst_5.addConnection(mst_6, 0.5);

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1, mst_3)), cloud, edge);

        sm.get("1_cloud").setMs_res(new Resources(0, 0));
        sm.get("2_cloud").setMs_res(new Resources(4, 1));
        sm.get("3_cloud").setMs_res(new Resources(1, 1));
        sm.get("4_cloud").setMs_res(new Resources(3, 2));
        sm.get("5_cloud").setMs_res(new Resources(2, 4));
        sm.get("6_cloud").setMs_res(new Resources(3, 4));
        sm.get("1_edge").setMs_res(new Resources(1, 2));
        sm.get("3_edge").setMs_res(new Resources(3, 2));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        sm.get("1_cloud").setMs_res(new Resources(1, 1));
        sm.get("2_cloud").setMs_res(new Resources(4, 1));
        sm.get("3_cloud").setMs_res(new Resources(2, 2));
        sm.get("4_cloud").setMs_res(new Resources(3, 1));
        sm.get("5_cloud").setMs_res(new Resources(2, 6));
        sm.get("6_cloud").setMs_res(new Resources(4, 5));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_edge"));

        sm.get("1_edge").setMs_res(new Resources(1, 1));
        sm.get("3_edge").setMs_res(new Resources(2, 2));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_edge"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_edge"));

        System.out.println("|----------------> Service Mesh of Graph 2 <----------------|\n");
        an.printServiceMeshConnections(sm);
        System.out.println("\n|----------------> Service Mesh of Graph 2 <----------------|");
        assertNotNull("ServiceMesh Creata",sm);
    }

    @Test
    public void experiment3(){
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

        System.out.println("|----------------> Service Mesh of Graph 3 <----------------|\n");
        an.printServiceMeshConnections(sm);
        System.out.println("\n|----------------> Service Mesh of Graph 3 <----------------|");
        assertNotNull("ServiceMesh Creata",sm);
    }

    @After
    public void endMethod() {
    }
}
