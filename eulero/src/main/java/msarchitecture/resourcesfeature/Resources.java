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
}