package msarchitecture.control;

import java.util.ArrayList;
import java.util.HashMap;
import msarchitecture.archsmodeling.ConnectionMSType;
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.resourcesfeature.Resources;

public class Controller{
    public static HashMap<String,Microservice> createServiceMesh(MicroserviceType type,ArrayList<MicroserviceType> microserviceType_list_edge,CloudLocation cloud,EdgeLocation edge){

        //add microservices to cloud and link them
        HashMap<String,Microservice> microservice_map = new HashMap<>();
        Microservice start_ms = new Microservice(20,type,cloud,new Resources(0,0));
        ArrayList<MicroserviceType> microserviceType_list_cloud = new ArrayList<>();
        takeMicroserviceTypeConnected(microserviceType_list_cloud, type);
        microservice_map.put(type.getName_type()+"_cloud",start_ms);
        for(MicroserviceType next_mst : microserviceType_list_cloud){
            for(ConnectionMSType conn : next_mst.getConnections()){
                MicroserviceType add_to_cloud_mst = conn.getTo_MSType();
                if(microservice_map.containsKey(add_to_cloud_mst.getName_type()+"_cloud")){
                    microservice_map.get(next_mst.getName_type()+"_cloud").addConnection(microservice_map.get(add_to_cloud_mst.getName_type()+"_cloud"));
                }else{
                    Microservice new_ms_to_cloud = new Microservice(20,add_to_cloud_mst,cloud,add_to_cloud_mst.getQos_res());
                    microservice_map.put(add_to_cloud_mst.getName_type()+"_cloud",new_ms_to_cloud);
                    microservice_map.get(next_mst.getName_type()+"_cloud").addConnection(new_ms_to_cloud);
                }
            }
        }
        
        //create microservices on edge
        for(MicroserviceType next_mst : microserviceType_list_edge){
            if(!microservice_map.containsKey(next_mst.getName_type()+"_edge")){
                Microservice new_ms_to_edge = new Microservice(20,next_mst,edge,new Resources(0,0));
                microservice_map.put(next_mst.getName_type()+"_edge",new_ms_to_edge);
            }
        }

        //link microservices on edge with each other or with microservices in the cloud
        for(Microservice ms_edge_from : edge.getMs_map().values()){
            for(ConnectionMSType conn : ms_edge_from.getMs_type().getConnections()){
                boolean check_conn_to_edge = false;
                for(Microservice ms_edge_to : edge.getMs_map().values()){
                    if(conn.getTo_MSType().equals(ms_edge_to.getMs_type())){
                        ms_edge_from.addConnection(ms_edge_to);
                        check_conn_to_edge = true;
                    }
                }
                if(!check_conn_to_edge){
                    for(Microservice ms_cloud_to : cloud.getMs_map().values()){
                        if(conn.getTo_MSType().equals(ms_cloud_to.getMs_type())){
                            ms_edge_from.addConnection(ms_cloud_to);
                        }
                    }
                }
            }
        }

        return microservice_map;
    }

    public static void printServiceMeshConnections(HashMap<String,Microservice> ms){
        System.out.println("--------------------------------------------");
        System.out.println("Service Mesh Connections");
        System.out.println("--------------------------------------------");
        ms.forEach((key, value) -> {
            System.out.println(value.toString());
        });
        System.out.println("--------------------------------------------");
    }

    public static void printPairwiseComparisonDominanceResults(HashMap<String,Microservice> ms,int timeLimit,double timeStep,int CThreshold,int QThreshold,double error){
        double acceptance = 0.5-error;
        System.out.println("------------------------------------------------------------");
        System.out.println("Pairwise-Comparison Dominance Analysis");
        System.out.println("------------------------------------------------------------");
        System.out.println("Error: "+error+"\tAcceptance Value: "+acceptance);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s   %-1s   %-15s   %-1s   %-15s%n", "node", "|", "pcd-value", "|", "qos satisfied");
        System.out.println("------------------------------------------------------------");
        ms.forEach((key, value) -> {
            double pcdvalue = value.getPairwiseComparisonDominanceValue(timeLimit,timeStep,CThreshold,QThreshold);
            System.out.printf("%-15s   %-1s   %-15.3f   %-1s   %-15s%n", key, "|", pcdvalue, "|", pcdvalue>acceptance);
        });
        System.out.println("------------------------------------------------------------");
    }

    private static ArrayList<MicroserviceType> takeMicroserviceTypeConnected(ArrayList<MicroserviceType> list,MicroserviceType mst){
        if(list.contains(mst)){
            list.remove(mst);
        }
        list.add(mst);
        ArrayList<ConnectionMSType> conn_list = mst.getConnections();
        for(int i=0;i<conn_list.size();i++){
            takeMicroserviceTypeConnected(list,conn_list.get(i).getTo_MSType());
        }
        return list;
    }
}