
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import msarchitecture.mapping.AnalysisViewer;
import msarchitecture.msarchitecture.CloudLocation;
import msarchitecture.msarchitecture.EdgeLocation;
import msarchitecture.msarchitecture.Microservice;
import msarchitecture.msarchitecture.MicroserviceType;
import msarchitecture.msarchitecture.Resources;
import msarchitecture.msarchitecture.SMFactory;
public class Experiments {
    AnalysisViewer an = new AnalysisViewer(12,0.01,15,15);
    Resources res_cloud;
    Resources res_edge;
    CloudLocation cloud;
    EdgeLocation edge;

    @Before
    public void initMethod() {
        res_cloud = new Resources(100, 100);
        res_edge = new Resources(20, 16);
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

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList()), cloud, edge);

        sm.get("2_cloud").setMs_res(new Resources(0, 4));
        sm.get("3_cloud").setMs_res(new Resources(4, 3));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("2_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("4_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("5_cloud"));

        sm.get("1_cloud").setMs_res(new Resources(2, 2));
        sm.get("4_cloud").setMs_res(new Resources(1, 0));
        sm.get("5_cloud").setMs_res(new Resources(1, 1));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        an.plotMicroserviceQOSComparisonDistributions(sm.get("1_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("2_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("3_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("4_cloud"));
        an.plotMicroserviceQOSComparisonDistributions(sm.get("5_cloud"));
    }

    @Test
    public void experiment2(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new UniformTime(0,2),new Resources(4, 2));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new UniformTime(0,3),new Resources(1, 1));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new UniformTime(0,2),new Resources(2, 2));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new UniformTime(1,2),new Resources(0, 3));
        
        mst_1.addConnection(mst_2, 1);
        mst_2.addConnection(mst_3, 0.7);
        mst_2.addConnection(mst_4, 0.3);

        HashMap<String,Microservice> sm = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList()), cloud, edge);

        sm.get("1_cloud").setMs_res(new Resources(2, 3));
        sm.get("2_cloud").setMs_res(new Resources(1, 0));
        sm.get("3_cloud").setMs_res(new Resources(1, 5));
        sm.get("4_cloud").setMs_res(new Resources(4, 2));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        sm.get("4_cloud").setMs_res(new Resources(1, 2));

        sm.get("3_cloud").setMs_res(new Resources(2, 5));
        sm.get("1_cloud").setMs_res(new Resources(4, 3));

        an.printPairwiseComparisonDominanceResults(sm,0.02);

        sm.get("3_cloud").setMs_res(new Resources(2, 2));
        
        sm.get("2_cloud").setMs_res(new Resources(1, 3));

        an.printPairwiseComparisonDominanceResults(sm,0.02);
    }

    @Test
    public void experiment3(){
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

    @After
    public void endMethod() {
    }
}
