package msarchitecture.msarchitecture;

import java.util.HashMap;

public class EdgeLocation extends Location{
    public EdgeLocation(Resources loc_res){
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
        if(!ms_map.containsValue(ms) && !ms_map.containsKey(ms.getName_ms())){
            if(!verifyResourcesAvailability(ms.getMs_res())){
                ms.setMs_res(new Resources(0, 0));
                System.err.println("Requested resources not available. Set all resources of the microservice "+ms.getName_ms()+" to 0");
            }
            ms_map.put(ms.getName_ms(),ms);
        }
    }

    @Override
    public void removeMicroservice(String name_ms){
		if(ms_map.containsKey(name_ms))
        	ms_map.remove(name_ms);
    }
}