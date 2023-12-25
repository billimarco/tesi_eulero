package msarchitecture.resourcesfeature;

public class Resources{
    private int cpu;
    private int ram;

    public Resources(int cpu,int ram){
        this.cpu=cpu;
        this.ram=ram;
    }

    public int getCpu() {
		return this.cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getRam() {
		return this.ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public double getComputingPower(){
		return cpu*0.8 + ram*0.2 + 1;//that +1 rappresent the minimal yield of the function. Minimal return equals to 1.
	}
}