package core.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.dag.Cloudlet;
import core.dag.DAG;
import core.scheduler.ComparatorCloudlet;
import core.vm.Vm;
import core.vm.VmComputeCost;

/**
 * Paper : Topcuoglu H, Hariri S, Wu M. Performance-effective and low-complexity 
 * task scheduling for heterogeneous computing[J]. Parallel and Distributed Systems,
 *  IEEE Transactions on, 2002, 13(3): 260-274.
 * Date: Dec16, 2015
 * @author shouhui
 */
public class HEFT extends Algorithm {
	private List<Double[]> computeCost;
	private List<Double[]> exeTime;



	private List<Cloudlet> cloudletList = getDag().getCloudletList();
	private DAG dag = getDag();
	private List<Vm>vmList = getVmList();
	private double[][] ccr = getCcr();
	
	private Map<Integer, Cloudlet> cloudletIdToCloudletMap;
	
	public void start(){
		bindCloudletToVmHEFT();
		Collections.sort(cloudletList, new Comparator<Cloudlet>(){
			public int compare(Cloudlet cl1,Cloudlet cl2){
				return ((Double)cl1.getAst()).compareTo((Double)cl2.getAst());
			}
		});
	}
	
	/*	HEFT algorithm*/
	@SuppressWarnings("unchecked")
	public void bindCloudletToVmHEFT(){
		/*	Set cloudletIdToCloudletMap*/
		cloudletIdToCloudletMap = new HashMap<Integer,Cloudlet>();
		for(int i=0;i<cloudletList.size();i++){
			cloudletIdToCloudletMap.put(i, cloudletList.get(i));
		}

		/*	Compute up rank value of all the cloudlets*/
		computeUpRankValue();
		
		/*	Sort the cloudlet list with up rank value*/
//		ComparatorCloudlet comparator = new ComparatorCloudlet();
		Collections.sort(cloudletList,new ComparatorCloudlet());
		
		//	Reset the state of all the virtual machines
		for(Vm vm:vmList){
			vm.setAvailTime(0);
		}
		//	Assign all the cloudlet to the virtual machines
		for(Cloudlet cl:cloudletList){
			assignInsert(cl);
		}
//		for(Cloudlet cl:cloudletList){
//			int clId = cl.getCloudletId();
//			System.out.println("cloudletId:" + (clId + 1) + "    vmId:" + (cl.getVmId() + 1) + 
//					"    ast:" + exeTime.get(clId)[0] + "    aft:" + exeTime.get(clId)[1]);
//		}
	}	

	/*
	 * Assign the cloudlet
	 */
	private void assignInsert(Cloudlet cl) {
		if(insert(cl)){
			return;
		}
		int minVmId = -1;
		double minValue = Double.MAX_VALUE;
		for(Vm vm:vmList){
			Iterator<Integer> itPre = cl.getPreCloudletList().iterator();
			int vmId = vm.getVmId();
			double maxTem = 0;
			while(itPre.hasNext()){
				int preCloudletIdTem = itPre.next();
				Cloudlet preCl = dag.getCloudletById(preCloudletIdTem);
				double tem = getExeTimeTemp(preCloudletIdTem)[1];
				if(preCl.getVmId() != vmId){
//					System.out.println(preCl.getVmId() + " " + cl.getVmId());
					tem += dag.getDependValue(preCloudletIdTem, cl.getCloudletId())*ccr[preCl.getVmId()][vm.getVmId()];
				}
				maxTem = (maxTem > tem)?maxTem:tem;
			}
			maxTem = (maxTem > vm.getAvailTime())?maxTem:vm.getAvailTime();
			maxTem += computeCost.get(cl.getCloudletId())[vmId];
			if(maxTem < minValue){
				minValue = maxTem;
				minVmId = vmId;
			}
		}
		//	Assign the cloudlet cl to the virtual machine whose id is minVmId
		Double[] exeTime = {minValue - computeCost.get(cl.getCloudletId())[minVmId],minValue};
		setExeTimeTemp(cl.getCloudletId(),exeTime);
		
		cl.setVmId(minVmId);
		vmList.get(minVmId).setAvailTime(minValue);
		Integer id = cl.getCloudletId();
		vmList.get(minVmId).insertToVm(vmList.get(minVmId).getCloudletInVm().size(), id);
	}
	
	/*
	 * Judge whether the cloudlet cl could be insert
	 */
	private boolean insert(Cloudlet cl) {
		for(Vm vm:vmList){
			if(vm.getCloudletInVm().size() < 2){
				continue;
			}
			//	Compute the earliest start time of cloudlet cl
			double preMax = 0;
			Iterator<Integer>itPre = cl.getPreCloudletList().iterator();
			while(itPre.hasNext()){
				int preCloudletId = itPre.next();
				Cloudlet preCl = dag.getCloudletById(preCloudletId);
				double preTem = getExeTimeTemp(preCloudletId)[1];
				if(preCl.getVmId() != vm.getVmId()){
					preTem += dag.getDependValue(preCloudletId, cl.getCloudletId())*ccr[preCl.getVmId()][vm.getVmId()];
				}
				preMax = (preMax > preTem)?preMax:preTem;
			}

			for(int i = 0;i <= vm.getCloudletInVm().size() - 2;i++){
				Cloudlet preCl = dag.getCloudletById(vm.getCloudletInVm().get(i));
				Cloudlet sucCl = dag.getCloudletById(vm.getCloudletInVm().get(i + 1));
				
				double temp = (getExeTimeTemp(preCl.getCloudletId())[1] > preMax)?getExeTimeTemp(preCl.getCloudletId())[1]:preMax;
				if( (getExeTimeTemp(sucCl.getCloudletId())[0] - temp) >= computeCost.get(cl.getCloudletId())[vm.getVmId()] ){
					Double[] exeTime = {temp,temp + computeCost.get(cl.getCloudletId())[vm.getVmId()]};
					setExeTimeTemp(cl.getCloudletId(),exeTime);
					cl.setVmId(vm.getVmId());
					vm.insertToVmSort(cl.getCloudletId(),preCl.getCloudletId());
//					System.out.println("Insert cloudlet:" + cl.getCloudletId() + " to vm:" + vm.getVmId());
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Set the temporary actual start time and actual finish time of all the cloudlets 
	 */
	public void setExeTimeTemp(int cloudletId,Double[]exeTime){
		this.exeTime.set(cloudletId, exeTime);
	}
	
	/*	计算cloudlet的向上排序值*/
	public void computeUpRankValue(){
		for(int i = cloudletList.size()-1;i>=0;i--){
			Cloudlet cl = dag.getCloudletById(i);
			Double[]temp = computeCost.get(i);
			double sum = 0;
			for(Double d:temp){
				sum += d;
			}
			cl.setUpRankValue(sum/temp.length);
			double tem = 0;
			Iterator <Integer>it = cl.getSucCloudletList().iterator();
			while(it.hasNext()){
				int sucCloudletIdTem = it.next();
				Cloudlet sucCl = dag.getCloudletById(sucCloudletIdTem);
				if((sucCl.getUpRankValue()+dag.getDependValue(i, sucCloudletIdTem)*getAveCcr())>tem){
					tem = sucCl.getUpRankValue()+dag.getDependValue(i, sucCloudletIdTem)*getAveCcr();
				}
			}
			tem+=cl.getUpRankValue();
			tem = (int)(tem*1000)/1000.0;
			cl.setUpRankValue(tem);
			
		}
	}
	
	public double getAveCcr(){
		double sumCcr = 0;
		for(int i = 0;i < vmList.size();i++){
			for(int j = i+1;j < vmList.size();j++){
				sumCcr += ccr[i][j];
			}
		}
		return sumCcr/vmList.size();
	}
	
	public void initialize(List<Double[]> computeCostSend){
		exeTime = new ArrayList<Double[]>();
		computeCost = computeCostSend;

		for(int i = 0;i < cloudletList.size();i++){
			Double[]temp = {(double) 0,(double)0};
			exeTime.add(temp);
		}
		
		for(Vm vm:vmList){
			vm.setAvailTime(0);
			vm.setCloudletIdInVm(new ArrayList<Integer>());
		}
		

	}

	/*
	 * Reassign the cloudlets in the select virtual machine
	 */
	public double reAssignCloudlet(List<Cloudlet>cloudletList,List<Double>computeCostTemp){
		for(Vm vm:vmList){
			vm.setAvailTime(0);
		}
		for(Cloudlet cl:cloudletList){
			int cloudletId = cl.getCloudletId();
			double preMax = 0;
			Iterator<Integer>preIt = cl.getPreCloudletList().iterator();
			while(preIt.hasNext()){
				int preCloudletId = preIt.next();
				Cloudlet preCl = dag.getCloudletById(preCloudletId);
				double temp = getExeTimeTemp(preCloudletId)[1];
				if( preCl.getVmId() != cl.getVmId() ){
					temp += dag.getDependValue(preCloudletId, cloudletId)*ccr[preCl.getVmId()][cl.getVmId()];
				}
				preMax = (preMax > temp)?preMax:temp;
			}
			preMax = (preMax > vmList.get(cl.getVmId()).getAvailTime())?preMax:vmList.get(cl.getVmId()).getAvailTime();
			Double[]exeTemp = {preMax,preMax + computeCostTemp.get(cloudletId)};
			setExeTimeTemp(cl.getCloudletId(),exeTemp);
			vmList.get(cl.getVmId()).setAvailTime(preMax + computeCostTemp.get(cloudletId));
		}
		return getExeTimeTemp(cloudletList.size() - 1)[1];
	}


	/*
	 * Get the temporary actual start time and actual finish time of all the cloudlets
	 */
	public Double[] getExeTimeTemp(int cloudletId){
		return this.exeTime.get(cloudletId);
	}
	
	
	public List<Double[]> getExeTime() {
		return exeTime;
	}


	public HEFT(double deadline, DAG dag, List<Vm> vmList, VmComputeCost vcc,
			double[][] ccr) {
		super(deadline, dag, vmList, vcc, ccr);
	}

}
