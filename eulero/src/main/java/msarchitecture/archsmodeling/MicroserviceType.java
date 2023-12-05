package msarchitecture.archsmodeling;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;

import msarchitecture.resourcesfeature.Resources;

public class MicroserviceType{
    private String name_type;
    private StochasticTime qos;
    private boolean entry_point;
    private Resources qos_res;

    private ArrayList<ConnectionMSType> connections;

    public MicroserviceType(String name_type,boolean entry_point,StochasticTime qos,Resources qos_res){
        this.name_type = name_type;
        this.qos = qos;
        this.entry_point = entry_point;
        this.qos_res = qos_res;
        this.connections = new ArrayList<>();
    }

    public String getName_type() {
		return this.name_type;
	}

	public void setName_type(String name_type) {
		this.name_type = name_type;
        //TODO call ms linked for changing
	}

	public StochasticTime getQos() {
		return this.qos;
	}

	public void setQos(TruncatedExponentialTime qos) {
		this.qos = qos;
        //TODO call ms linked for changing
	}

	public boolean is_entry_point() {
		return this.entry_point;
	}

	public void set_entry_point(boolean entry_point) {
		this.entry_point = entry_point;
        //TODO call ms linked for changing
	}

    public Resources getQos_res() {
        return qos_res;
    }

    public void setQos_res(Resources qos_res) {
        this.qos_res = qos_res;
        //TODO call ms linked for changing
    }

    public void addConnection(MicroserviceType to_mst,double probability){
        ConnectionMSType new_conn = new ConnectionMSType(this,to_mst,probability);
        connections.add(new_conn);
        //TODO DAG verify
    }

    public void removeConnection(MicroserviceType to_mst){
        for(ConnectionMSType conn: connections) {
            if(conn.getTo_MSType().equals(to_mst)){
                connections.remove(conn);
                //TODO call ms linked for changing
                break;
            }
        }
    }

    public ArrayList<ConnectionMSType> getConnections() {
		return this.connections;
	}

    public boolean searchConnection(MicroserviceType to_mst){
        for(ConnectionMSType conn: connections) {
            if(conn.getTo_MSType().equals(to_mst))
                return true;
        }
        return false;
    }

    public Activity getSimpleActivity(){
        return new Simple(this.name_type,this.qos);
    }

    public Activity getCompositeActivity(){
        if(connections.size()>0){
            ArrayList<Activity> act_list = new ArrayList<>();
            ArrayList<Double> act_prob_list = new ArrayList<>();
            for(int i=0;i<connections.size();i++){
                act_list.add(connections.get(i).getTo_MSType().getCompositeActivity());
                act_prob_list.add(connections.get(i).getProbability());
            }
            Activity seq_comp_act = null;
            if(connections.size()>1){
                seq_comp_act = ModelFactory.OR(act_prob_list, act_list.toArray(new Activity[connections.size()]));
            } else if(connections.size()==1){
                act_prob_list.add(1-act_prob_list.get(0));
                act_list.add(new Simple(connections.get(0).getTo_MSType().getName_type()+"_missed", new DeterministicTime(BigDecimal.valueOf(0))));
                seq_comp_act = ModelFactory.XOR(act_prob_list,act_list.toArray(new Activity[connections.size()]));
            }
            return ModelFactory.sequence(getSimpleActivity(),seq_comp_act);
        }
        return getSimpleActivity();
    }
}