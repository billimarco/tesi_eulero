package msarchitecture.locationfeature;

import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.resourcesfeature.Resources;

public abstract class Location{
    Resources loc_res;
    HashMap<String,Microservice> ms_map;

    public abstract void addMicroservice(Microservice ms);
    public abstract void removeMicroservice(String name_ms);
    public boolean verifyResourcesAvailability(Resources new_res){
        int cpu_taken = 0;
        int ram_taken = 0;
        for(Microservice ms:ms_map.values()){
            cpu_taken += ms.getMs_res().getCpu();
            ram_taken += ms.getMs_res().getRam();
        }
        if(loc_res.getCpu()<cpu_taken+new_res.getCpu() || loc_res.getRam()<ram_taken+new_res.getRam())
            return false;
        return true;
    };  

}