
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.Resources;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class MicroserviceTest {
    MicroserviceType mst_1;
    MicroserviceType mst_2;
    MicroserviceType mst_3;
    Resources res_cloud;
    CloudLocation cloud;
    Microservice ms_1;
    Microservice ms_2;
    Microservice ms_3;

    @Before
    public void initMethod() {
        mst_1 = new MicroserviceType("1", 5, 5, true);
        mst_2 = new MicroserviceType("2", 5, 5, false);
        mst_3 = new MicroserviceType("3", 5, 5, false);
        res_cloud = new Resources(12, 16);
        cloud = new CloudLocation(res_cloud);
        ms_1 = new Microservice(0, 0, mst_1, cloud);
        ms_2 = new Microservice(0, 0, mst_2, cloud);
        ms_3 = new Microservice(0, 0, mst_3, cloud);
    }

    @Test
    public void addConnectionTest(){
        ms_1.addConnection(ms_2);
        assertSame(ms_1.getConnections().get(0),ms_2);
    }

    @Test
    public void removeConnectionTest(){
        ms_1.addConnection(ms_2);
        assertTrue(ms_1.getConnections().size()==1);
        ms_1.removeConnection(ms_2);
        assertTrue(ms_1.getConnections().size()==0);
    }

    @After
    public void endMethod() {
    }
}