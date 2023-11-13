package msarchitecture.control;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.units.qual.A;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.archsmodeling.MicroserviceType;
import msarchitecture.locationfeature.CloudLocation;

public class Mapper {
    public static void mapDependencyGraph(ArrayList<MicroserviceType> mst_list){
        HashMap<String,Activity> act_mst_map = new HashMap<>();
        for(MicroserviceType entry : mst_list){
            act_mst_map.put(entry.getName_type(),new Simple(entry.getName_type(),new UniformTime(0,1.0)));
        }
        for(MicroserviceType entry : mst_list){
            for(int i=0;i<entry.getConnections().size();i++){
                String mst_name=entry.getConnections().get(i).getTo_MSType().getName_type();
                act_mst_map.get(mst_name).addPrecondition(act_mst_map.get(entry.getName_type()));
            }
        }
        for(MicroserviceType entry : mst_list){
            Activity mst_simple_act = act_mst_map.get(entry.getName_type());
            double[] comp_time_cdf = mst_simple_act.analyze(mst_simple_act.max().add(BigDecimal.ONE), mst_simple_act.getFairTimeTick(), new SDFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation()));
            entry.setCompletation_time_CDF(comp_time_cdf);
        }
    }

    public static Activity mapServiceMesh(HashMap<String,Microservice> ms_map){
        HashMap<String,Activity> act_ms_map = new HashMap<>();
        for(Map.Entry<String,Microservice> entry : ms_map.entrySet()){
            act_ms_map.put(entry.getKey(),new Simple(entry.getKey(),new UniformTime(0, 1.0)));
        }
        for(Map.Entry<String,Microservice> entry : ms_map.entrySet()){
            ArrayList<Microservice> conn_list = entry.getValue().getConnections();
            for(int i=0;i<conn_list.size();i++){
                String ms_name = conn_list.get(i).getMs_type().getName_type()+"_";
                if(conn_list.get(i).getLocation() instanceof CloudLocation)
                    ms_name+="cloud";
                else
                    ms_name+="edge";

                act_ms_map.get(ms_name).addPrecondition(act_ms_map.get(entry.getKey()));
            }
        }
        for(Map.Entry<String,Microservice> entry : ms_map.entrySet()){
            Activity ms_simple_act = act_ms_map.get(entry.getKey());
            double[] comp_time_cdf = ms_simple_act.analyze(ms_simple_act.max().add(BigDecimal.ONE), ms_simple_act.getFairTimeTick(), new SDFHeuristicsVisitor(BigInteger.valueOf(4), BigInteger.TEN, new TruncatedExponentialMixtureApproximation()));
            entry.getValue().setActual_time_distr(comp_time_cdf);
        }
        return ModelFactory.DAG(act_ms_map.values().toArray(new Activity[act_ms_map.size()]));
    }
}
