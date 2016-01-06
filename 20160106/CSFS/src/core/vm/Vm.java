package core.vm;
import java.util.List;
import java.util.ArrayList;

public class Vm {
	/** The id */
	private int id;
	
	/** Vm avail time */
	private double availTime;
	
	/*	The cloudlet id assigned to the virtual machine */
	private List<Integer>cloudletIdInVm;
	
	/** The vm's frequency */
	private List<Double>fList;
	
	/*	The maximize level of frequency*/
	private int maxfLevel;
	
	//	Different price model
	private double[] cMin = {9.24*10e-6, 9.24*10e-6, 2.78*10e-6};
	private double[] cDif = {3.33*10e-6, 4.44*10e-6, 1.2*10e-5};
	private int modelLevel = 0;
	
	public double getUnitPrice(int level){
		double C = 0;
		switch(modelLevel){
			//	Superlinear Model
			case 0:
				double temp = (1 + (getfByLevel(level) - getfByLevel(getMaxfLevel()))/getfByLevel(getMaxfLevel()));
				C = cMin[1] + cDif[1]*( temp * Math.log(temp) );
				break;
			//	Linear Model
			case 1:
				C = cMin[0] + cDif[0]*( (getfByLevel(level) - getfByLevel(getMaxfLevel()))/getfByLevel(getMaxfLevel()) );
				break;

		}
		return C;
	}
	
	
	
	/*
	 * @param id unique id of the vm
	 * @param availTime the avail time of the vm
	 * 
	 */
	public Vm(int id,double availTime){
		this.id = id;
		this.availTime = 0;
		setCloudletIdInVm(null);
		cloudletIdInVm = new ArrayList<Integer>();
	}
	
	/*
	 * Set the avail time
	 * 
	 * @param availTime the new avail time
	 */
	public void setAvailTime(double availTime){
		this.availTime = availTime;
	}
	
	/*
	 * Get the avail time
	 * 
	 */
	public double getAvailTime(){
		return this.availTime;
	}
	
	/*
	 * Set the voltage and frequency list
	 */
	public void setfList(List<Double>fList){
		this.fList = fList;
	}
	
	public List<Double> getfList(){
		return this.fList;
	}

	/*
	 * Get the frequency list by level
	 */
	public Double getfByLevel(int level){
		return this.fList.get(level);
	}
	
	/*
	 * Get the virtual machine id
	 */
	public int getVmId(){
		return this.id;
	}
	
	/*
	 * Set the cloudlet id list in the virtual machine
	 */
	public void setCloudletIdInVm(List<Integer>cloudletInVm){
		this.cloudletIdInVm = cloudletInVm;
	}

	/*
	 * Get the cloudlet id list in the virtual machine
	 */
	public List<Integer>getCloudletInVm(){
		return this.cloudletIdInVm;
	}
	/*
	 * Insert cloudlet id to the list
	 */
	public void insertToVm(int index,Integer cloudletId){
		this.cloudletIdInVm.add(index, cloudletId);
	}
	public void insertToVmSort(Integer cloudletId,Integer preCloudletId){
		for(int i = cloudletIdInVm.size() - 1;;i--){
			if(cloudletIdInVm.get(i) == preCloudletId){
				this.cloudletIdInVm.add(i+1, cloudletId);
				break;
			}
		}
	}
	/*
	 * Set the maximize frequency level for the virtual machine
	 */
	public void setMaxfLevel(int maxfLevel){
		this.maxfLevel = maxfLevel;
	}
	/*
	 * Get the maximize frequency level for the virtual machine
	 */
	public int getMaxfLevel(){
		return this.maxfLevel;
	}
}
