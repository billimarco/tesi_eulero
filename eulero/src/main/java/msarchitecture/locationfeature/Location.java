package msarchitecture.locationfeature;

import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;
import msarchitecture.resourcesfeature.Resources;

public abstract class Location{
    Resources loc_res;
    HashMap<String,Microservice> ms_map;

    public abstract void addMicroservice(Microservice ms);
    public abstract void removeMicroservice(String name_ms);

}