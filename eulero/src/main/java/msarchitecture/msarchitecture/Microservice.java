package msarchitecture.msarchitecture;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;

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
}