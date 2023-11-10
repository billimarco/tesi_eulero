package msarchitecture.locationfeature;

import java.util.ArrayList;
import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;

public class EdgeLocation extends Location{
    public EdgeLocation(Resources res){
        this.res=res;
        ms_list = new ArrayList<>();
		res_distribution = new HashMap<>();
    }

    public Resources getRes() {
		return this.res;
	}

	public void setRes(Resources res) {
		this.res = res;
	}

	public ArrayList<Microservice> getMs_list() {
		return this.ms_list;
	}

	public HashMap<String,Resources> getRes_distribution() {
		return this.res_distribution;
	}
    
    @Override
    public void addMicroservice(Microservice ms){
        ms_list.add(ms);
    }

    @Override
    public void removeMicroservice(Microservice ms){
        ms_list.remove(ms);
    }
}