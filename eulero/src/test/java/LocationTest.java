
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;

import msarchitecture.msarchitecture.CloudLocation;
import msarchitecture.msarchitecture.EdgeLocation;
import msarchitecture.msarchitecture.Microservice;
import msarchitecture.msarchitecture.MicroserviceType;
import msarchitecture.msarchitecture.Resources;

public class LocationTest {
    MicroserviceType mst_1;
    MicroserviceType mst_2;
    MicroserviceType mst_3;
    Resources res_cloud;
    Resources res_edge;
    CloudLocation cloud;
    EdgeLocation edge;
    Microservice ms_1_cloud;
    Microservice ms_2_cloud;
    Microservice ms_3_cloud;
    Microservice ms_1_edge;
    Microservice ms_2_edge;
    Microservice ms_3_edge;

    @Before
    public void initMethod() {
        mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        res_cloud = new Resources(12, 16);
        res_edge = new Resources(12, 16);
        cloud = new CloudLocation(res_cloud);
        edge = new EdgeLocation(res_edge);
        ms_1_cloud = new Microservice(0, mst_1, cloud,new Resources(0,0));
        ms_2_cloud = new Microservice(0, mst_2, cloud,new Resources(0,0));
        ms_3_cloud = new Microservice(0, mst_3, cloud,new Resources(0,0));
        ms_1_edge = new Microservice(0, mst_1, edge,new Resources(0,0));
        ms_2_edge = new Microservice(0, mst_2, edge,new Resources(0,0));
    }
    
    @Test
    public void addMicroserviceTest(){
        assertTrue(cloud.getMs_map().size()==3);
        assertTrue(edge.getMs_map().size()==2);
    }

    @Test
    public void removeMicroserviceTest(){
        cloud.removeMicroservice("1_cloud");
        cloud.removeMicroservice("2_cloud");
        edge.removeMicroservice("1_edge");
        assertTrue(cloud.getMs_map().keySet().contains("3_cloud"));
        assertTrue(edge.getMs_map().keySet().contains("2_edge"));
        assertFalse(cloud.getMs_map().keySet().contains("1_cloud"));
        assertFalse(cloud.getMs_map().keySet().contains("1_edge"));
    }

    @Test
    public void verifyResourcesAvailabilityTest(){
        assertFalse(cloud.verifyResourcesAvailability(new Resources(20, 0)));
        assertFalse(cloud.verifyResourcesAvailability(new Resources(0, 20)));
        assertTrue(cloud.verifyResourcesAvailability(new Resources(10, 10)));
    }

    @After
    public void endMethod() {
    }
}
