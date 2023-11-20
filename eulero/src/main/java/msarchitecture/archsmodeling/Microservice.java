package msarchitecture.archsmodeling;

import java.util.ArrayList;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.Location;
import msarchitecture.locationfeature.Resources;

public class Microservice{
    private StochasticTime actual_time_distr;//equivalente qos
    private int arrival_rate_req;
    private MicroserviceType ms_type;
    private ArrayList<Microservice> connected_ms_list;
    private Location location;

    public Microservice(int arrival_rate_req,MicroserviceType ms_type,Location location,Resources res){
        this.arrival_rate_req=arrival_rate_req;
        this.ms_type=ms_type;
        this.connected_ms_list = new ArrayList<>();
        this.location=location;
        location.addMicroservice(this);
    }

    public StochasticTime getActual_time_distr() {
		return this.actual_time_distr;
	}

	public void setActual_time_distr(StochasticTime actual_time_distr) {
		this.actual_time_distr = actual_time_distr;
	}

	public int getArrival_rate_req() {
		return this.arrival_rate_req;
	}

	public void setArrival_rate_req(int arrival_rate_req) {
		this.arrival_rate_req = arrival_rate_req;
	}

	public MicroserviceType getMs_type() {
		return this.ms_type;
	}

    public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

    public void addConnection(Microservice to_ms){
        connected_ms_list.add(to_ms);
    }

    public void removeConnection(Microservice to_ms){
        for(Microservice ms: connected_ms_list) {
            if(ms.equals(to_ms)){
                connected_ms_list.remove(ms);
                break;
            }
        }
    }

    public ArrayList<Microservice> getConnections() {
		return this.connected_ms_list;
	}

    public String toString(){
        String line = ms_type.getName_type()+"_";
        if(location instanceof CloudLocation)
            line+="cloud";
        else
            line+="edge";
        line+=" connected to : | ";
        for(int i=0;i<connected_ms_list.size();i++){
            line+=connected_ms_list.get(i).getMs_type().getName_type()+"_";
            if(connected_ms_list.get(i).location instanceof CloudLocation)
                line+="cloud";
            else
                line+="edge";
            line+=" | ";
        }
        return line;
    }
}