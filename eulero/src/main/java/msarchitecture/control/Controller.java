package msarchitecture.control;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.Composite;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.activitytypes.DAGType;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;

import msarchitecture.archsmodeling.ConnectionMSType;
import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;

public class Controller{
    public static HashMap<String,Microservice> createServiceMesh(MicroserviceType type,ArrayList<MicroserviceType> microserviceType_list_edge,CloudLocation cloud,EdgeLocation edge){

        //add microservices to cloud and link them
        HashMap<String,Microservice> microservice_map = new HashMap<>();
        Microservice start_ms = new Microservice(20,type,cloud);
        ArrayList<MicroserviceType> microserviceType_list_cloud = new ArrayList<>();
        takeMicroserviceTypeConnected(microserviceType_list_cloud, type);
        microservice_map.put(type.getName_type()+"_cloud",start_ms);
        for(MicroserviceType next_mst : microserviceType_list_cloud){
            for(ConnectionMSType conn : next_mst.getConnections()){
                MicroserviceType add_to_cloud_mst = conn.getTo_MSType();
                if(microservice_map.containsKey(add_to_cloud_mst.getName_type()+"_cloud")){
                    microservice_map.get(next_mst.getName_type()+"_cloud").addConnection(microservice_map.get(add_to_cloud_mst.getName_type()+"_cloud"));
                }else{
                    Microservice new_ms_to_cloud = new Microservice(20,add_to_cloud_mst,cloud);
                    microservice_map.put(add_to_cloud_mst.getName_type()+"_cloud",new_ms_to_cloud);
                    microservice_map.get(next_mst.getName_type()+"_cloud").addConnection(new_ms_to_cloud);
                }
            }
        }
        
        //create microservices on edge
        for(MicroserviceType next_mst : microserviceType_list_edge){
            if(!microservice_map.containsKey(next_mst.getName_type()+"_edge")){
                Microservice new_ms_to_edge = new Microservice(20,next_mst,edge);
                microservice_map.put(next_mst.getName_type()+"_edge",new_ms_to_edge);
            }
        }

        //link microservices on edge with each other or with microservices in the cloud
        for(Microservice ms_edge_from : edge.getMs_list()){
            for(ConnectionMSType conn : ms_edge_from.getMs_type().getConnections()){
                boolean check_conn_to_edge = false;
                for(Microservice ms_edge_to : edge.getMs_list()){
                    if(conn.getTo_MSType().equals(ms_edge_to.getMs_type())){
                        ms_edge_from.addConnection(ms_edge_to);
                        check_conn_to_edge = true;
                    }
                }
                if(!check_conn_to_edge){
                    for(Microservice ms_cloud_to : cloud.getMs_list()){
                        if(conn.getTo_MSType().equals(ms_cloud_to.getMs_type())){
                            ms_edge_from.addConnection(ms_cloud_to);
                        }
                    }
                }
            }
        }

        return microservice_map;
    }

    public static void calculateQOS_DG(MicroserviceType mst){
        LinkedList<MicroserviceType> mst_list = new LinkedList<>();
        mst_list = takeMicroserviceTypeConnected2(mst_list, mst);
        for(int i=0;i<mst_list.size();i++){
            LinkedList<MicroserviceType> mst_list_internal = new LinkedList<>();
            mst_list_internal = takeMicroserviceTypeConnected2(mst_list_internal, mst_list.get(i));
            HashMap<String,Activity> act_map = new HashMap<>();
            for(int j=0;j<mst_list_internal.size();j++){
                act_map.put(mst_list_internal.get(j).getName_type(),mst_list_internal.get(j).getCompletion_time().clone());
            }
            for(int j=0;j<mst_list_internal.size();j++){
                for(int k=0;k<mst_list_internal.get(j).getConnections().size();k++){
                    String from_mst=mst_list_internal.get(j).getConnections().get(k).getFrom_MSType().getName_type();
                    String to_mst=mst_list_internal.get(j).getConnections().get(k).getTo_MSType().getName_type();
                    act_map.get(to_mst).addPrecondition(act_map.get(from_mst));
                }
            }
            Composite dgt = ModelFactory.DAG(act_map.values().toArray(new Activity[act_map.size()]));
            mst_list.get(i).setQos(dgt);
        }
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

    private static LinkedList<MicroserviceType> takeMicroserviceTypeConnected2(LinkedList<MicroserviceType> list,MicroserviceType mst){
        if(list.contains(mst)){
            list.remove(mst);
        }
        list.addFirst(mst);
        ArrayList<ConnectionMSType> conn_list = mst.getConnections();
        for(int i=0;i<conn_list.size();i++){
            takeMicroserviceTypeConnected2(list,conn_list.get(i).getTo_MSType());
        }
        return list;
    }


}