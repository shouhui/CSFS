package core.vm;
import java.util.Map;

import core.dag.Cloudlet;

public class VmComputeCost {

	/*	各个任务在各个vm上执行代价*/
	private Map<Integer,double[]>vmComputeCostMap;
	
	/*	各个任务在各个vm上平均执行代价*/
	private Map<Integer,Double>vmAveComputeCostMap;
	
	/*	设置vmComputeCostMap*/
	public void setVmComputeCostMap(Map<Integer, double[]> cch){
		this.vmComputeCostMap = cch;
	}
	
	public Map<Integer, double[]> getVmComputeCostMap(){
		return vmComputeCostMap;
	}
	
	/*	获得computeCost*/
	public double getVmComputeCost(int cloudletId,int vmId){
		return vmComputeCostMap.get(cloudletId)[vmId];
	}
	
	/*	设置vmAveComputeCostMap*/
	public void setAveVmComputeCostMap(Map<Integer, Double> acch){
		this.vmAveComputeCostMap = acch;
	}
	
	public Map<Integer, Double>getAveVmComputeCostMap(){
		return vmAveComputeCostMap;
	}
	
	/*	获取vmAveComputeCost*/
	public double getVmAveComputeCost(int cloudletId){
		return vmAveComputeCostMap.get(cloudletId);
	}
	
	public double getScaledTime(Cloudlet cl, int level, Vm vm){
		double maxTime = vmComputeCostMap.get(cl.getCloudletId())[cl.getVmId()];
		return ( cl.getBeta()*( vm.getfByLevel(0)/vm.getfByLevel(level) - 1 ) + 1 )*maxTime;
	}
	
	
}
