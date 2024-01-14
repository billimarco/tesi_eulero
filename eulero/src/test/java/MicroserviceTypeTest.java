
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;

import msarchitecture.msarchitecture.ConnectionMSType;
import msarchitecture.msarchitecture.MicroserviceType;
import msarchitecture.msarchitecture.Resources;
public class MicroserviceTypeTest {
    MicroserviceType mst_1;
    MicroserviceType mst_2;
    MicroserviceType mst_3;

    @Before
    public void initMethod() {
        mst_1 = new MicroserviceType("1", true,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_2 = new MicroserviceType("2", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
        mst_3 = new MicroserviceType("3", false,new TruncatedExponentialTime(1,3,5),new Resources(0, 0));
    }

    @Test
    public void addConnectionTest(){
        mst_1.addConnection(mst_2, 88);
        assertSame(mst_1.getConnections().get(0).getTo_MSType(),mst_2);
        assertEquals(mst_1.getConnections().get(0).getProbability(),88,0.00001);
    }

    @Test
    public void removeConnectionTest(){
        mst_1.addConnection(mst_2, 88);
        assertTrue(mst_1.getConnections().size()==1);
        mst_1.removeConnection(mst_2);
        assertTrue(mst_1.getConnections().size()==0);
    }

    @Test
    public void searchConnectionTest(){
        mst_1.addConnection(mst_2, 88);
        assertTrue(mst_1.searchConnection(mst_2));
        assertFalse(mst_1.searchConnection(mst_3));
    }

    @Test
    public void verifyDAGSubtreeConsistencyTest(){
        mst_1.addConnection(mst_2, 0);
        mst_2.addConnection(mst_3, 0);
        mst_3.addConnection(mst_1, 0);
        mst_3.addConnection(mst_2, 0);
        mst_3.addConnection(mst_3, 0);
        assertTrue(mst_3.getConnections().size()==0);
    }

    @After
    public void endMethod() {
    }
}