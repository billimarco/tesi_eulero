package msarchitecture.archsmodeling;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.heuristics.EvaluationResult;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;

import msarchitecture.locationfeature.CloudLocation;
import msarchitecture.locationfeature.EdgeLocation;
import msarchitecture.locationfeature.Location;
import msarchitecture.resourcesfeature.Resources;

public class Microservice{
    private String name_ms;
    private StochasticTime actual_time_distr;
    private int arrival_rate_req;
    private MicroserviceType ms_type;
    private ArrayList<Microservice> connected_ms_list;
    private Location location;
    private Resources ms_res;

    public Microservice(int arrival_rate_req,MicroserviceType ms_type,Location location,Resources ms_res){
        this.arrival_rate_req=arrival_rate_req;
        this.ms_type=ms_type;
        this.connected_ms_list = new ArrayList<>();
        this.location=location;
        if(this.location instanceof CloudLocation){
            this.name_ms=ms_type.getName_type()+"_cloud";
        }else if(this.location instanceof EdgeLocation){
            this.name_ms=ms_type.getName_type()+"_edge";
        }
        this.ms_res=ms_res;
        location.addMicroservice(this);
        this.variateDistribution();
    }

    public String getName_ms() {
        return name_ms;
    }

    public StochasticTime getActual_time_distr() {
		return this.actual_time_distr;
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
    
    public Resources getMs_res() {
        return ms_res;
    }

    public void setMs_res(Resources ms_res) {
        location.removeMicroservice(this.getName_ms());
        this.ms_res=ms_res;
        location.addMicroservice(this);
        this.variateDistribution();
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
        String line = getName_ms();
        line+=" : | ";
        for(int i=0;i<connected_ms_list.size();i++){
            line+=connected_ms_list.get(i).getName_ms();
            line+=" | ";
        }
        return line;
    }

    public Activity getSimpleActivity(){
        return new Simple(this.name_ms,this.actual_time_distr);
    }

    public Activity getCompositeActivity(){
        if(connected_ms_list.size()>0){
            ArrayList<Activity> act_list = new ArrayList<>();
            ArrayList<Double> act_prob_list = new ArrayList<>();
            for(int i=0;i<connected_ms_list.size();i++){
                act_list.add(connected_ms_list.get(i).getCompositeActivity());
                for(int j=0;j<ms_type.getConnections().size();j++)
                    if(this.ms_type.getConnections().get(j).getTo_MSType().equals(connected_ms_list.get(i).getMs_type()))
                        act_prob_list.add(this.ms_type.getConnections().get(j).getProbability());
            }
            Activity seq_comp_act = null;
            if(connected_ms_list.size()>1){
                seq_comp_act = ModelFactory.OR(act_prob_list, act_list.toArray(new Activity[connected_ms_list.size()]));
            } else if(connected_ms_list.size()==1){
                act_prob_list.add(1-act_prob_list.get(0));
                act_list.add(new Simple(connected_ms_list.get(0).getName_ms()+"_missed", new DeterministicTime(BigDecimal.valueOf(0))));
                seq_comp_act = ModelFactory.XOR(act_prob_list,act_list.toArray(new Activity[connected_ms_list.size()]));
            }
            return ModelFactory.sequence(getSimpleActivity(),seq_comp_act);
        }
        return getSimpleActivity();
    }

    private void variateDistribution(){
        String DistributionName = this.ms_type.getQos().getClass().getSimpleName();
        double EFT,LFT,rate;
        switch(DistributionName){
            case "TruncatedExponentialTime":
                EFT = ((TruncatedExponentialTime) this.ms_type.getQos()).getEFT().doubleValue() * (this.ms_type.getQos_res().getComputingPower() / this.ms_res.getComputingPower());
                LFT = ((TruncatedExponentialTime) this.ms_type.getQos()).getLFT().doubleValue() * (this.ms_type.getQos_res().getComputingPower() / this.ms_res.getComputingPower());
                rate = ((TruncatedExponentialTime) this.ms_type.getQos()).getRate().doubleValue() * (this.ms_res.getComputingPower() / this.ms_type.getQos_res().getComputingPower());
                this.actual_time_distr = new TruncatedExponentialTime(EFT, LFT, rate);
                break;
            case "ErlangTime":
                rate = ((ErlangTime) this.ms_type.getQos()).getRate() * (this.ms_res.getComputingPower() / this.ms_type.getQos_res().getComputingPower());
                this.actual_time_distr = new ErlangTime(((ErlangTime) this.ms_type.getQos()).getK(), rate);
                break;
            case "ExponentialTime":
                rate = ((ExponentialTime) this.ms_type.getQos()).getRate().doubleValue() * (this.ms_res.getComputingPower() / this.ms_type.getQos_res().getComputingPower());
                this.actual_time_distr = new ExponentialTime(BigDecimal.valueOf(rate));
                break;
            case "UniformTime":
                EFT = ((UniformTime) this.ms_type.getQos()).getEFT().doubleValue() * (this.ms_type.getQos_res().getComputingPower() / this.ms_res.getComputingPower());
                LFT = ((UniformTime) this.ms_type.getQos()).getLFT().doubleValue() * (this.ms_type.getQos_res().getComputingPower() / this.ms_res.getComputingPower());
                this.actual_time_distr = new UniformTime(EFT, LFT);
                break;
            default:
                this.actual_time_distr = null;
                break;
        }
    }

    public double getPairwiseComparisonDominanceValue(int timeLimit,double timeStep,int CThreshold,int QThreshold,boolean composed){
        Activity ms_act = getCompositeActivity();
        Activity mst_act = ms_type.getCompositeActivity();
        if(composed){
            ms_act = getCompositeActivity();
            mst_act = ms_type.getCompositeActivity();
        }else{
            ms_act = getSimpleActivity();
            mst_act = ms_type.getSimpleActivity();
        }
        double[] mst_act_cdf = mst_act.analyze(BigDecimal.valueOf(timeLimit), BigDecimal.valueOf(timeStep), new SDFHeuristicsVisitor(BigInteger.valueOf(CThreshold), BigInteger.valueOf(QThreshold), new TruncatedExponentialApproximation()));
        double[] ms_act_cdf = ms_act.analyze(BigDecimal.valueOf(timeLimit), BigDecimal.valueOf(timeStep), new SDFHeuristicsVisitor(BigInteger.valueOf(CThreshold), BigInteger.valueOf(QThreshold), new TruncatedExponentialApproximation()));
        double[] ms_act_pdf = new EvaluationResult("ms_act_pdf", ms_act_cdf, 0, ms_act_cdf.length, 0.01, 0).pdf();
        double result = 0;
        for(int i=0;i<mst_act_cdf.length;i++){
            result += (1-mst_act_cdf[i])*ms_act_pdf[i]*timeStep;
        }
        return result;
    }
}