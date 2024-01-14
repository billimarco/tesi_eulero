package msarchitecture.msarchitecture;

import java.util.ArrayList;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;

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
}