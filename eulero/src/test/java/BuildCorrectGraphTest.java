
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
public class BuildCorrectGraphTest {
    AnalysisViewer an = new AnalysisViewer(12,0.01,15,15);
    Resources res_cloud;
    Resources res_edge;
    CloudLocation cloud;
    EdgeLocation edge;

    @Before
    public void initMethod() {
        res_cloud = new Resources(12, 16);
        res_edge = new Resources(4, 8);
        cloud = new CloudLocation(res_cloud);
        edge = new EdgeLocation(res_edge);
    }

    @Test
    public void createServiceMeshOfGraph1(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_1.addConnection(mst_2, 0.1);
        mst_1.addConnection(mst_3, 0.1);
        mst_1.addConnection(mst_4, 0.1);
        mst_2.addConnection(mst_5, 0.1);
        HashMap<String,Microservice> ms = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1, mst_3, mst_4)), cloud, edge);
        System.out.println("|----------------> Service Mesh of Graph 1 <----------------|\n");
        an.printServiceMeshConnections(ms);
        System.out.println("\n|----------------> Service Mesh of Graph 1 <----------------|");
        assertNotNull("ServiceMesh Creata",ms);
    }

    @Test
    public void createServiceMeshOfGraph2(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_6 = new MicroserviceType("6", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_1.addConnection(mst_2, 0.1);
        mst_1.addConnection(mst_3, 0.1);
        mst_3.addConnection(mst_4, 0.1);
        mst_3.addConnection(mst_5, 0.1);
        mst_5.addConnection(mst_6, 0.1);
        HashMap<String,Microservice> ms = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1, mst_3)), cloud, edge);
        System.out.println("|----------------> Service Mesh of Graph 2 <----------------|\n");
        an.printServiceMeshConnections(ms);
        System.out.println("\n|----------------> Service Mesh of Graph 2 <----------------|");
        assertNotNull("ServiceMesh Creata",ms);
    }

    @Test
    public void createServiceMeshOfGraph3(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_1.addConnection(mst_2, 0.1);
        mst_1.addConnection(mst_3, 0.1);
        HashMap<String,Microservice> ms = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_3)), cloud, edge);
        System.out.println("|----------------> Service Mesh of Graph 3 <----------------|\n");
        an.printServiceMeshConnections(ms);
        System.out.println("\n|----------------> Service Mesh of Graph 3 <----------------|");
        assertNotNull("ServiceMesh Creata",ms);
    }

    @Test
    public void createServiceMeshOfGraph4(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_5 = new MicroserviceType("5", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_1.addConnection(mst_2, 0.1);
        mst_1.addConnection(mst_3, 0.1);
        mst_1.addConnection(mst_4, 0.1);
        mst_3.addConnection(mst_5, 0.1);
        mst_4.addConnection(mst_5, 0.1);
        HashMap<String,Microservice> ms = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_4)), cloud, edge);
        System.out.println("|----------------> Service Mesh of Graph 4 <----------------|\n");
        an.printServiceMeshConnections(ms);
        System.out.println("\n|----------------> Service Mesh of Graph 4 <----------------|");
        assertNotNull("ServiceMesh Creata",ms);
    }

    @Test
    public void createServiceMeshOfGraph5(){
        MicroserviceType mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        MicroserviceType mst_4 = new MicroserviceType("4", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_1.addConnection(mst_2, 0.1);
        mst_2.addConnection(mst_3, 0.1);
        mst_2.addConnection(mst_4, 0.1);
        HashMap<String,Microservice> ms = SMFactory.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_2,mst_3)), cloud, edge);
        System.out.println("|----------------> Service Mesh of Graph 5 <----------------|\n");
        an.printServiceMeshConnections(ms);
        System.out.println("\n|----------------> Service Mesh of Graph 5 <----------------|");
        assertNotNull("ServiceMesh Creata",ms);
    }

    @After
    public void endMethod() {
    }
}
