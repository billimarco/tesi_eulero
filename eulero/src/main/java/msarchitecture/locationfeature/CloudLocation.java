package msarchitecture.locationfeature;

import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.resourcesfeature.Resources;

public class CloudLocation extends Location{
    public CloudLocation(Resources res){
        this.res=res;
		this.ms_map = new HashMap<>();
		this.res_distribution_map = new HashMap<>();
    }

    public Resources getRes() {
		return this.res;
	}

	public void setRes(Resources res) {
		this.res = res;
	}

	public HashMap<String,Microservice> getMs_map() {
		return this.ms_map;
	}

	public HashMap<String,Resources> getRes_distribution() {
		return this.res_distribution_map;
	}

	@Override
    public void addMicroservice(Microservice ms,Resources res_ms){
        ms_map.put(ms.getName_ms(),ms);
		res_distribution_map.put(ms.getName_ms(), res_ms);
    }

    @Override
    public void removeMicroservice(String name_ms){
		if(ms_map.containsKey(name_ms))
        	ms_map.remove(name_ms);
		if(res_distribution_map.containsKey(name_ms))
			res_distribution_map.remove(name_ms);
    }
}