package msarchitecture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.control.Controller;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.locationfeature.Resources;

public class Tesi {
    public static void main(String[] args) throws Exception {
        Controller cont = new Controller();
        Resources res_cloud = new Resources(12, 16);
        Resources res_edge = new Resources(4, 8);
        CloudLocation cloud = new CloudLocation(res_cloud);
        EdgeLocation edge = new EdgeLocation(res_edge);
        MicroserviceType mst_1 = new MicroserviceType("1", 5, 5, true);
        MicroserviceType mst_2 = new MicroserviceType("2", 5, 5, false);
        MicroserviceType mst_3 = new MicroserviceType("3", 5, 5, false);
        MicroserviceType mst_4 = new MicroserviceType("4", 5, 5, false);
        MicroserviceType mst_5 = new MicroserviceType("5", 5, 5, false);
        mst_1.addConnection(mst_2, 10);
        mst_1.addConnection(mst_3, 25);
        mst_1.addConnection(mst_4, 100);
        mst_3.addConnection(mst_5, 100);
        mst_4.addConnection(mst_5, 100);
        HashMap<String,Microservice> ms = cont.createServiceMesh(mst_1,new ArrayList<MicroserviceType>(Arrays.asList(mst_1,mst_2,mst_4)), cloud, edge);
        ms.forEach((key, value) -> {
            System.out.println(value.toString());
        });
        mst_1.removeConnection(mst_2);
        System.out.println("ServiceMesh Creata");
        
    }
}
