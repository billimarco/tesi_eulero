package msarchitecture.locationfeature;

import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.resourcesfeature.Resources;

public abstract class Location{
    Resources res;
    HashMap<String,Microservice> ms_map;
    HashMap<String,Resources> res_distribution_map;

    public abstract void addMicroservice(Microservice ms,Resources ms_res);
    public abstract void removeMicroservice(String name_ms);

}