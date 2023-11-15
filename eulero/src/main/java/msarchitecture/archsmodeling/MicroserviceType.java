package msarchitecture.archsmodeling;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.oristool.eulero.evaluation.approximator.SplineBodyEXPTailApproximation;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialApproximation;
import org.oristool.eulero.evaluation.approximator.TruncatedExponentialMixtureApproximation;
import org.oristool.eulero.evaluation.heuristics.RBFHeuristicsVisitor;
import org.oristool.eulero.evaluation.heuristics.SDFHeuristicsVisitor;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.Composite;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.activitytypes.DAGType;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.TruncatedExponentialTime;
import org.oristool.eulero.ui.ActivityViewer;

public class MicroserviceType{
    private String name_type;
    private Activity qos;
    private Simple completation_time;
    private boolean entry_point;

    private ArrayList<ConnectionMSType> connections;

    public MicroserviceType(String name_type,boolean entry_point,StochasticTime st){
        this.name_type = name_type;
        this.entry_point = entry_point;
        this.completation_time = new Simple(name_type,st);
        this.connections = new ArrayList<>();
    }

    public String getName_type() {
		return this.name_type;
	}

	public void setName_type(String name_type) {
		this.name_type = name_type;
	}

	public Activity getQos() {
		return this.qos;
	}

	public void setQos(Activity qos) {
		this.qos = qos;
	}

	public Simple getCompletation_time() {
		return this.completation_time;
	}

	public void setCompletation_time(Simple completation_time) {
		this.completation_time = completation_time;
	}

	public boolean is_entry_point() {
		return this.entry_point;
	}

	public void set_entry_point(boolean entry_point) {
		this.entry_point = entry_point;
	}

    public void addConnection(MicroserviceType to_mst,float probability){
        ConnectionMSType new_conn = new ConnectionMSType(this,to_mst,probability);
        connections.add(new_conn);
        //TODO DAG verify
    }

    public void removeConnection(MicroserviceType to_mst){
        for(ConnectionMSType conn: connections) {
            if(conn.getTo_MSType().equals(to_mst)){
                connections.remove(conn);
                break;
            }
        }
    }

    public ArrayList<ConnectionMSType> getConnections() {
		return this.connections;
	}

    public boolean searchConnection(MicroserviceType to_mst){
        for(ConnectionMSType conn: connections) {
            if(conn.getTo_MSType().equals(to_mst))
                return true;
        }
        return false;
    }

    public double[] calculateCompletation_Time_CDF(BigDecimal timeLimit, BigInteger CThreshold , BigInteger QThreshold){
        return completation_time.analyze(timeLimit, completation_time.getFairTimeTick(), new SDFHeuristicsVisitor(CThreshold, QThreshold, new TruncatedExponentialApproximation()));
    }

    public double[] calculateQos_CDF(BigDecimal timeLimit, BigInteger CThreshold , BigInteger QThreshold){
        return qos.analyze(timeLimit, completation_time.getFairTimeTick(), new SDFHeuristicsVisitor(CThreshold, QThreshold, new TruncatedExponentialApproximation()));
    }
}