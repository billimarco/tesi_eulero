
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.Resources;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
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
        mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        res_cloud = new Resources(12, 16);
        cloud = new CloudLocation(res_cloud);
        ms_1 = new Microservice(0, mst_1, cloud,new Resources(0,0));
        ms_2 = new Microservice(0, mst_2, cloud,new Resources(0,0));
        ms_3 = new Microservice(0, mst_3, cloud,new Resources(0,0));
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
