package msarchitecture.archsmodeling;

import java.util.ArrayList;

public class MicroserviceType{
    private String name_type;
    private float qos_CDF;
    private float completation_time_CDF;
    private boolean entry_point;

    private ArrayList<ConnectionMSType> connections;

    public MicroserviceType(String name_type,float qos_CDF,float completation_time_CDF,boolean entry_point){
        this.name_type=name_type;
        this.qos_CDF=qos_CDF;
        this.completation_time_CDF=completation_time_CDF;
        this.entry_point=entry_point;
        this.connections = new ArrayList<>();
    }

    public String getName_type() {
		return this.name_type;
	}

	public void setName_type(String name_type) {
		this.name_type = name_type;
	}

	public float getQos_CDF() {
		return this.qos_CDF;
	}

	public void setQos_CDF(float qos_CDF) {
		this.qos_CDF = qos_CDF;
	}

	public float getCompletation_time_CDF() {
		return this.completation_time_CDF;
	}

	public void setCompletation_time_CDF(float completation_time_CDF) {
		this.completation_time_CDF = completation_time_CDF;
	}

	public boolean is_entry_point() {
		return this.entry_point;
	}

	public void set_entry_point(boolean entry_point) {
		this.entry_point = entry_point;
	}

    public void addConnection(MicroserviceType to_mst,float probability){
        ConnectionMSType new_conn = new ConnectionMSType(this,to_mst,probability);
        connections.add(new_conn);
        //TODO DAG verify
    }

    public void removeConnection(MicroserviceType to_mst){
        for(ConnectionMSType conn: connections) {
            if(conn.getTo_MSType().equals(to_mst)){
                connections.remove(conn);
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