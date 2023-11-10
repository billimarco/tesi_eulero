package msarchitecture.archsmodeling;

public class ConnectionMSType{
    private MicroserviceType from_MSType;
    private MicroserviceType to_MSType;
    private float probability;

    public ConnectionMSType(MicroserviceType from_MSType,MicroserviceType to_MSType,float probability){
        this.from_MSType=from_MSType;
        this.to_MSType=to_MSType;
        this.probability=probability;
    }

    public MicroserviceType getFrom_MSType() {
		return this.from_MSType;
	}

	public void setFrom_MSType(MicroserviceType from_MSType) {
		this.from_MSType = from_MSType;
	}

	public MicroserviceType getTo_MSType() {
		return this.to_MSType;
	}

	public void setTo_MSType(MicroserviceType to_MSType) {
		this.to_MSType = to_MSType;
	}

	public float getProbability() {
		return this.probability;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}
}