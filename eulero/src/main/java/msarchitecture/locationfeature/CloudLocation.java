package msarchitecture.locationfeature;

import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.resourcesfeature.Resources;

public class CloudLocation extends Location{
    public CloudLocation(Resources loc_res){
        this.loc_res=loc_res;
		this.ms_map = new HashMap<>();
    }

    public Resources getRes() {
		return this.loc_res;
	}

	public void setRes(Resources res) {
		this.loc_res = res;
	}

	public HashMap<String,Microservice> getMs_map() {
		return this.ms_map;
	}

	@Override
    public void addMicroservice(Microservice ms){
        ms_map.put(ms.getName_ms(),ms);
    }

    @Override
    public void removeMicroservice(String name_ms){
		if(ms_map.containsKey(name_ms))
        	ms_map.remove(name_ms);
    }
}