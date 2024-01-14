package msarchitecture.mapping;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;

import msarchitecture.msarchitecture.Microservice;
import msarchitecture.msarchitecture.MicroserviceType;

public class ArchMapper {    
    public static Activity getSimpleActivity(MicroserviceType mst){
        return new Simple(mst.getName_type(),mst.getQos());
    }

    public static Activity getSimpleActivity(Microservice ms){
        return new Simple(ms.getName_ms(),ms.getActual_time_distr());
    }

    public static Activity getCompositeActivity(MicroserviceType mst){
        if(mst.getConnections().size()>0){
            ArrayList<Activity> act_list = new ArrayList<>();
            ArrayList<Double> act_prob_list = new ArrayList<>();
            for(int i=0;i<mst.getConnections().size();i++){
                act_list.add(getCompositeActivity(mst.getConnections().get(i).getTo_MSType()));
                act_prob_list.add(mst.getConnections().get(i).getProbability());
            }
            Activity seq_comp_act = null;
            if(mst.getConnections().size()>1){
                seq_comp_act = ModelFactory.OR(act_prob_list, act_list.toArray(new Activity[mst.getConnections().size()]));
            } else if(mst.getConnections().size()==1){
                act_prob_list.add(1-act_prob_list.get(0));
                act_list.add(new Simple(mst.getConnections().get(0).getTo_MSType().getName_type()+"_missed", new DeterministicTime(BigDecimal.valueOf(0))));
                seq_comp_act = ModelFactory.XOR(act_prob_list,act_list.toArray(new Activity[mst.getConnections().size()]));
            }
            return ModelFactory.sequence(getSimpleActivity(mst),seq_comp_act);
        }
        return getSimpleActivity(mst);
    }

    public static Activity getCompositeActivity(Microservice ms){
        if(ms.getConnections().size()>0){
            ArrayList<Activity> act_list = new ArrayList<>();
            ArrayList<Double> act_prob_list = new ArrayList<>();
            for(int i=0;i<ms.getConnections().size();i++){
                act_list.add(getCompositeActivity(ms.getConnections().get(i)));
                for(int j=0;j<ms.getMs_type().getConnections().size();j++)
                    if(ms.getMs_type().getConnections().get(j).getTo_MSType().equals(ms.getConnections().get(i).getMs_type()))
                        act_prob_list.add(ms.getMs_type().getConnections().get(j).getProbability());
            }
            Activity seq_comp_act = null;
            if(ms.getConnections().size()>1){
                seq_comp_act = ModelFactory.OR(act_prob_list, act_list.toArray(new Activity[ms.getConnections().size()]));
            } else if(ms.getConnections().size()==1){
                act_prob_list.add(1-act_prob_list.get(0));
                act_list.add(new Simple(ms.getConnections().get(0).getName_ms()+"_missed", new DeterministicTime(BigDecimal.valueOf(0))));
                seq_comp_act = ModelFactory.XOR(act_prob_list,act_list.toArray(new Activity[ms.getConnections().size()]));
            }
            return ModelFactory.sequence(getSimpleActivity(ms),seq_comp_act);
        }
        return getSimpleActivity(ms);
    }
}
