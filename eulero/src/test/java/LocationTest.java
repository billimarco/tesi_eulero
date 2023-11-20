
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.locationfeature.Resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;

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
        assertTrue(cloud.getMs_list().size()==3);
        assertTrue(edge.getMs_list().size()==2);
    }

    @Test
    public void removeMicroserviceTest(){
        cloud.removeMicroservice(ms_1_cloud);
        cloud.removeMicroservice(ms_2_cloud);
        edge.removeMicroservice(ms_1_edge);
        assertEquals(cloud.getMs_list().get(0),ms_3_cloud);
        assertEquals(edge.getMs_list().get(0),ms_2_edge);
    }

    @After
    public void endMethod() {
    }
}
