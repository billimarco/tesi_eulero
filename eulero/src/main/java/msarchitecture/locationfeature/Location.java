package msarchitecture.locationfeature;

import java.util.ArrayList;
import java.util.HashMap;

import msarchitecture.archsmodeling.Microservice;

public abstract class Location{
    Resources res;
    ArrayList<Microservice> ms_list;
    HashMap<String,Resources> res_distribution;

    public abstract void addMicroservice(Microservice ms);
    public abstract void removeMicroservice(Microservice ms);
}