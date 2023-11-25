package msarchitecture.resourcesfeature;

public class Resources{
    private int cpu;
    private int ram;
	private final int minimalYield = 1;

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

	public double getCalculatePower(){
		return cpu*0.8 + ram*0.2 + minimalYield;
	}
}