package core.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.dag.Cloudlet;
import core.dag.DAG;
import core.vm.Vm;
import core.vm.VmComputeCost;

public class GTI extends Algorithm {
	private boolean debug = true;
	/*	The constant for selecting virtual machine when rescaling by group*/
	double C = 1.01;
	
	private List<Cloudlet> cloudletList = getDag().getCloudletList();
	private DAG dag = getDag();
	private List<Vm> vmList = getVmList();
	private VmComputeCost vcc = getVcc();
	private double[][] ccr = getCcr();
	private double deadline = getDeadline();
	
	/*	The temporary cost of all the cloudlets in the assigned virtual machine after HEFT scheduling*/
	private List<Double>computeCostA = new ArrayList<>();
	/*	The temporary actual start time and actual finish time of all the cloudlets*/
	private List<Double[]>exeTime = new ArrayList<>();

	/*	The current overall energy consumption of all the cloudlets*/
	double curE = 0;
	
	/*	The overall energy consumption of all the cloudlets after HEFT scheduling*/
	double baseE = 0;

	/*	The current overall direct energy consumption of all the cloudlets */
	double curED = 0;
	
	private double priceAfterG = 0;
	


	public void start() {
		// Reset the state of all the virtual machines
		for (Vm vm : vmList) {
			vm.setAvailTime(0);
		}
		
		//	Set computeCostA
		for(int i = 0;i <= cloudletList.size() - 1;i++){
//			computeCostA.set(i, exeTimeBackup.get(i)[1] - exeTimeBackup.get(i)[0]);
			computeCostA.add(dag.getCloudletById(i).getAft() - dag.getCloudletById(i).getAst());
			exeTime.add(new Double []{(double) 0, (double) 0});
		}
		
		if (debug) {
			System.out.println("After the HEFT algorithm.");
			System.out.println("The deadline:" + deadline);
			for (Cloudlet cl : cloudletList) {
				System.out.println("cloudletId:" + cl.getCloudletId()
						+ "    vmId:" + cl.getVmId() + "    ast:" + cl.getAst()
						+ "    aft:" + cl.getAft() + " "
						+ (cl.getAft() - cl.getAst()) + "    levle:"
						+ cl.getLevel());
			}
		}

		computeBasePrice();
		System.out.println("base Price: " + baseE);
		
		// Rescaling by group
		rescaleByG();
		if (debug) {
			System.out.println("After the Group scaling.");
			for (Cloudlet cl : cloudletList) {
				System.out.println("cloudletId:" + cl.getCloudletId()
						+ "    vmId:" + cl.getVmId() + "    ast:" + cl.getAst()
						+ "    aft:" + cl.getAft() + " "
						+ (cl.getAft() - cl.getAst()) + "    levle:"
						+ cl.getLevel());
			}
		}
		setPriceAfterG(curE);
		
		System.out.println("After the Group scaling, Price: " + curE);

		// Rescaling by individual
		rescaleByI();
		if (debug) {
			System.out.println("After the individual scaling.");
			for (Cloudlet cl : cloudletList) {
				System.out.println("cloudletId:" + cl.getCloudletId()
						+ "    vmId:" + cl.getVmId() + "    ast:" + cl.getAst()
						+ "    aft:" + cl.getAft() + " "
						+ (cl.getAft() - cl.getAst()) + "    levle:"
						+ cl.getLevel());
			}
		}
		
		System.out.println("After the individual scaling, Price: " + curE);
	}
	
	/*
	 * Rescaling all the cloudlets by individual
	 */
	public void rescaleByI(){
//		System.out.println("    rescaling all the cloudlets by individual");
		boolean contI = true;
		List<Double>computeCostTemp = new ArrayList<Double>();
		for(int i = 0;i <= cloudletList.size() - 1;i++){
			computeCostTemp.add(computeCostA.get(i));
		}
		
		double maxSavedE = 0;
		while(contI){
			int selectCloudletId = -1;
			maxSavedE = 0;
			
			for(Cloudlet cl:cloudletList){

				int cloudletId = cl.getCloudletId();
				Vm vm = vmList.get(cl.getVmId());
				double cloudletESum = 0;
				double cloudletEDSum = 0;
				double savedETemp = 0;
				double makespanTemp = 0;
				for(int i = 0;i <= cloudletList.size() - 1;i++){
					Double[] temp = {(Double)0.0,(Double)0.0};
					setExeTimeTemp(i, temp);
				}
				for(int i = 0;i <= vmList.size() - 1;i++){
					vmList.get(i).setAvailTime(0);
				}
				//	Reset the temporary cloudlet compute cost for all the cloudlets after scaling in some virtual machine
				for(int i = 0;i <= cloudletList.size() - 1;i++){
					computeCostTemp.set(i, computeCostA.get(i));
				}
				if(cl.getLevel() == vm.getMaxfLevel()){
					continue;
				}else{
//					Double temp = computeCostA.get(cloudletId) * ( vm.getVfListByLevel(cl.getLevel())[1]/vm.getVfListByLevel(cl.getLevel() + 1)[1] );
					Double temp = vcc.getScaledTime(cl, cl.getLevel() + 1, vm);
					computeCostTemp.set(cloudletId, temp);
				}

				//	Reassign the cloudlet cl
				reAssignCloudlet(cloudletList,computeCostTemp);
				
				//	Compute the energy consumption of all the cloudlets
				for(int i = 0;i <= cloudletList.size() - 1;i++){
					Cloudlet curCl = dag.getCloudletById(i);
					Vm curVm = vmList.get(curCl.getVmId());
					int level = curCl.getLevel();
					if( (cloudletId == i)&&( level != curVm.getMaxfLevel() )){
						level += 1;
					}
					double exeTime = getExeTimeTemp(i)[1] - getExeTimeTemp(i)[0];
//					cloudletEDSum += computeCloudletE(curCl,level,exeTime);
					cloudletEDSum += curVm.getUnitPrice(level) * exeTime;
				}
				makespanTemp = getExeTimeTemp(cloudletList.size() - 1)[1];
				double vmETemp = 0;
				for(Vm vmIdle:vmList){
					double workTime = 0;
					for(int cloudletIdInVm:vmIdle.getCloudletInVm()){
						workTime += computeCostTemp.get(cloudletIdInVm);
					}
//					vmETemp += computeIdleE(vmIdle,makespanTemp - workTime);
					
					vmETemp +=  vmIdle.getUnitPrice(vmIdle.getMaxfLevel())*( makespanTemp - workTime );
				}
				cloudletESum = cloudletEDSum + vmETemp;
				savedETemp = curE - cloudletESum;
				
				if( (makespanTemp <= deadline)&&(savedETemp >= 0)&&( savedETemp > maxSavedE ) ){
					selectCloudletId = cloudletId;
					maxSavedE = savedETemp;
				}
				
				System.out.println(cloudletId + " " + savedETemp + " " + cloudletEDSum + " " + vmETemp + " makespan: " + makespanTemp);
			}
			
//			System.out.println("select cloudlet id: " + selectCloudletId);
			
			if( selectCloudletId == -1 ){
				contI = false;
			}else{
//				System.out.println("select id : " + selectCloudletId);
				//	Apply the scaling in the selected cloudlet
				//	Reset the state
				for(Cloudlet cl:cloudletList){
					Double[] temp = {(Double)0.0,(Double)0.0};
					setExeTimeTemp(cl.getCloudletId(),temp);
				}
				for(Vm vm_reset:vmList){
					vm_reset.setAvailTime(0);
				}
				//	Reset the temporary cloudlet compute cost for all the cloudlets after scaling in some virtual machine
				for(Cloudlet cl:cloudletList){
//					computeCostTemp.set(cl.getCloudletId(),computeCostA.get(cl.getCloudletId()));
					computeCostTemp.set(cl.getCloudletId(),cl.getAft() - cl.getAst());
				}
				//	Increase the level for the selected cloudlet, and calculate the makespan and energy saving value after that
				Cloudlet selectCl = dag.getCloudletById(selectCloudletId);
				
//				Double temp = computeCostA.get(selectCloudletId) * ( vmList.get(selectCl.getVmId()).getVfListByLevel(selectCl.getLevel())[1]/vmList.get(selectCl.getVmId()).getVfListByLevel(selectCl.getLevel() + 1)[1] );
//				Double temp = ( selectCl.getAft() - selectCl.getAst() ) * ( vmList.get(selectCl.getVmId()).getVfListByLevel(selectCl.getLevel())[1]/vmList.get(selectCl.getVmId()).getVfListByLevel(selectCl.getLevel() + 1)[1] );
				Double temp = vcc.getScaledTime(selectCl, selectCl.getLevel() + 1, vmList.get(selectCl.getVmId()));
				
				computeCostTemp.set(selectCloudletId, temp);
				if(selectCl.getLevel()!=vmList.get(selectCl.getVmId()).getMaxfLevel()){
					selectCl.setLevel(selectCl.getLevel() + 1);
				}else{
					contI = false;
				}
				reAssignCloudlet(cloudletList,computeCostTemp);
				for(Cloudlet cl:cloudletList){
					double tem = computeCostTemp.get(cl.getCloudletId());
					computeCostA.set(cl.getCloudletId(), tem);
				}
				for(Cloudlet cl:cloudletList){
					cl.setAst(getExeTimeTemp(cl.getCloudletId())[0]);
					cl.setAft(getExeTimeTemp(cl.getCloudletId())[1]);
				}
				//	Compute the energy consumption of all the cloudlets

				curE -= maxSavedE;
			}
		}
	}


	private void computeBasePrice() {
		double curM = cloudletList.get(cloudletList.size() - 1).getAft();
		//	Compute the overall price consumption after HEFT scheduling
		double energyDTemp = 0;
		double energyITemp = 0;
		for(Cloudlet cl:cloudletList){
			int level = cl.getLevel();
			double exeTime = cl.getAft() - cl.getAst();
//			energyDTemp += computeCloudletE(cl,level,exeTime);
			energyDTemp += vmList.get(cl.getVmId()).getUnitPrice(level)*exeTime;
		}
		for(Vm vm:vmList){
			double workTime = 0;
			Iterator<Integer>it = vm.getCloudletInVm().iterator();
			while(it.hasNext()){
				int cloudletId = it.next();
				Cloudlet cl = dag.getCloudletById(cloudletId);
				workTime += ( cl.getAft() - cl.getAst() );
			}
			double idleTime = curM - workTime;
//			energyITemp += computeIdleE(vm,idleTime);
			energyITemp += vm.getUnitPrice(vm.getMaxfLevel())*idleTime;
		}
		curED = energyDTemp;
		curE = energyDTemp + energyITemp;
		baseE = curE;
		
//		System.out.println(curED + " " + curE);
	}

	/*
	 * Rescaling all the cloudlets by group
	 */
	public void rescaleByG() {
		// System.out.println("    rescaling all the cloudlets by group");
		boolean contG = true;
		List<Double> computeCostTemp = new ArrayList<Double>();
		for (int i = 0; i <= cloudletList.size() - 1; i++) {
			computeCostTemp.add(computeCostA.get(i));
			// System.out.println( computeCostA.get(i));
		}
		while (contG) {
			int selectVmId = -1;
			double maxSavedED = Double.NEGATIVE_INFINITY;
			double maxSavedE = 0;

			for (Vm vm : vmList) {
//				System.out.println(vm.getVmId());
				double cloudletESum = 0;
				double cloudletEDSum = 0;
				double savedEDTemp = 0;
				
				double savedETemp = 0;
				double makespanTemp = 0;
				// Reset the state
				for (Cloudlet cl : cloudletList) {
					Double[] temp = { (Double) 0.0, (Double) 0.0 };
					setExeTimeTemp(cl.getCloudletId(), temp);
				}
				for (Vm vm_reset : vmList) {
					vm_reset.setAvailTime(0);
				}

				// Reset the temporary cloudlet compute cost for all the
				// cloudlets after scaling in some virtual machine
				for (Cloudlet cl : cloudletList) {
					computeCostTemp.set(cl.getCloudletId(),
							computeCostA.get(cl.getCloudletId()));
				}

				if (vm.getCloudletInVm().isEmpty()) {
					continue;
				}

				// Tentatively increase the level for all the cloudlets in the
				// vm, and calculate the makespan and energy saving value after
				// that
				for (int cloudletId : vm.getCloudletInVm()) {
					Cloudlet cl = dag.getCloudletById(cloudletId);
					if (cl.getLevel() == vm.getMaxfLevel()) {
						continue;
					}
//					Double temp = computeCostA.get(cloudletId)
//							* (vm.getfByLevel(cl.getLevel()) / 
//									vm.getfByLevel(cl.getLevel() + 1));
					Double temp = vcc.getScaledTime(cl, cl.getLevel() + 1, vm);
					
					computeCostTemp.set(cloudletId, temp);
					// System.out.println( cloudletId + " " + temp + " " +
					// computeCostA.get(cloudletId));
				}

				// If the energy saving value is heavier, and satisfied the
				// deadline, slect the vm
				// Reassign the cloudlets in the select virtual machine
				reAssignCloudlet(cloudletList, computeCostTemp);

//				 for(Cloudlet cl:cloudletList){
//					 int clId = cl.getCloudletId();
//					 System.out.println( cl.getCloudletId() + " " +
//					 exeTime.get(clId)[0] + " " + exeTime.get(clId)[1] );
//				 }

				// Compute the energy consumption of all the cloudlets
				for (Cloudlet cl : cloudletList) {
					int level = cl.getLevel();
					if (vm.getCloudletInVm().contains(cl.getCloudletId())
							&& (cl.getLevel() != vm.getMaxfLevel())) {
						level += 1;
					}
//					double exeTime = getExeTimeTemp(cl.getCloudletId())[1]
//							- getExeTimeTemp(cl.getCloudletId())[0];
//					cloudletEDSum += computeCloudletE(cl, level, exeTime);
					cloudletEDSum += vm.getUnitPrice(level) * vcc.getScaledTime(cl, level, vm);
				}
				savedEDTemp = curED - cloudletEDSum;
				makespanTemp = getExeTimeTemp(cloudletList.size() - 1)[1];
				double vmETemp = 0;
				for (Vm vmIdle : vmList) {
					double workTime = 0;
					for (int cloudletIdInVm : vmIdle.getCloudletInVm()) {
						workTime += computeCostTemp.get(cloudletIdInVm);
					}
					vmETemp += vmIdle.getUnitPrice(vmIdle.getMaxfLevel())*( makespanTemp - workTime );
				}
				cloudletESum = cloudletEDSum + vmETemp;
				
				savedETemp = curE - cloudletESum;

				if ((makespanTemp <= deadline)
						&& (savedETemp > maxSavedE)) {
					// System.out.println(vm.getVmId() + ".............");
					selectVmId = vm.getVmId();
					maxSavedE = savedETemp;
					// System.out.println("......");
				}
				if (debug) {
					System.out.println("vm:" + vm.getVmId()
							+ "    total consumption:" + cloudletESum + "");
				}

			}
			
//			System.out.println("selectvm id:" + selectVmId);

			if (selectVmId == -1) {
				// Quite the loop
				contG = false;
			} else {
				// Apply the scaling in the virtual machine whose id is
				// selectVmId
				// Reset the state
				for (Cloudlet cl : cloudletList) {
					Double[] temp = { (Double) 0.0, (Double) 0.0 };
					setExeTimeTemp(cl.getCloudletId(), temp);
				}
				for (Vm vm_reset : vmList) {
					vm_reset.setAvailTime(0);
				}
				// Reset the temporary cloudlet compute cost for all the
				// cloudlets after scaling in some virtual machine
				for (Cloudlet cl : cloudletList) {
					computeCostTemp.set(cl.getCloudletId(),
							computeCostA.get(cl.getCloudletId()));
				}
				// Increase the level for all the cloudlets in the vm, and
				// calculate the makespan and energy saving value after that
				Vm vm = vmList.get(selectVmId);
				for (int cloudletId : vm.getCloudletInVm()) {
					Cloudlet cl = dag.getCloudletById(cloudletId);
					if (cl.getLevel() == vm.getMaxfLevel()) {
						contG = false;
						break;
					}
//					Double temp = computeCostA.get(cloudletId)
//							* (vm.getfListByLevel(cl.getLevel())[1] / vm
//									.getfListByLevel(cl.getLevel() + 1)[1]);
//					
					Double temp = vcc.getScaledTime(cl, cl.getLevel() + 1, vm);
					computeCostTemp.set(cloudletId, temp);
					cl.setLevel(cl.getLevel() + 1);
				}

				//

				reAssignCloudlet(cloudletList, computeCostTemp);

				for (Cloudlet cl : cloudletList) {
					computeCostA.set(cl.getCloudletId(),
							computeCostTemp.get(cl.getCloudletId()));
				}
				for (Cloudlet cl : cloudletList) {
					cl.setAst(getExeTimeTemp(cl.getCloudletId())[0]);
					cl.setAft(getExeTimeTemp(cl.getCloudletId())[1]);
				}
				
//				if (debug) {
//					System.out.println("After the Group scaling.");
//					for (Cloudlet cl : cloudletList) {
//						System.out.println("cloudletId:" + cl.getCloudletId()
//								+ "    vmId:" + cl.getVmId() + "    ast:" + cl.getAst()
//								+ "    aft:" + cl.getAft() + " "
//								+ (cl.getAft() - cl.getAst()) + "    levle:"
//								+ cl.getLevel());
//					}
//				}

				// Compute the energy consumption of all the cloudlets
				double cloudletESum = 0;
				double cloudletEDSum = 0;
				double makespanTemp = 0;
				for (Cloudlet cl : cloudletList) {
					int level = cl.getLevel();
					// System.out.println(level);
					double exeTime = getExeTimeTemp(cl.getCloudletId())[1]
							- getExeTimeTemp(cl.getCloudletId())[0];
//					cloudletEDSum += computeCloudletE(cl, level, exeTime);
					cloudletEDSum += vmList.get(cl.getVmId()).getUnitPrice(level)*exeTime;
					
				}
				makespanTemp = getExeTimeTemp(cloudletList.size() - 1)[1];
				double vmETemp = 0;
				for (Vm vmIdle : vmList) {
					double workTime = 0;
					for (int cloudletIdInVm : vmIdle.getCloudletInVm()) {
						workTime += computeCostTemp.get(cloudletIdInVm);
					}
//					vmETemp += computeIdleE(vmIdle, makespanTemp - workTime);
					vmETemp += vmIdle.getUnitPrice(vmIdle.getMaxfLevel())*(makespanTemp - workTime);
				}
//				System.out.println("Debug." + cloudletEDSum + " " + vmETemp);
				curE = cloudletEDSum + vmETemp;
				curED = cloudletEDSum;

				if (debug) {
					System.out.println("The selected vmId:" + selectVmId
							+ "    current energy consumption:" + curE);
					for (Cloudlet cl : cloudletList) {
						System.out.println("cloudletId:" + cl.getCloudletId()
								+ "    vmId:" + cl.getVmId() + "    ast:"
								+ cl.getAst() + "    aft:" + cl.getAft() + " "
								+ (cl.getAft() - cl.getAst()) + "    levle:"
								+ cl.getLevel());
					}

				}
			}
		}

	}
	
	/*
	 * Reassign the cloudlets in the select virtual machine
	 */
	public void reAssignCloudlet(List<Cloudlet>cloudletList,List<Double>computeCostTemp){
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
	}
	
	/*
	 * Set the temporary actual start time and actual finish time of all the cloudlets 
	 */
	public void setExeTimeTemp(int cloudletId,Double[]exeTime){
		this.exeTime.set(cloudletId, exeTime);
	}
	/*
	 * Get the temporary actual start time and actual finish time of all the cloudlets
	 */
	public Double[] getExeTimeTemp(int cloudletId){
		return this.exeTime.get(cloudletId);
	}

	public GTI(double deadline, DAG dag, List<Vm> vmList, VmComputeCost vcc,
			double[][] ccr) {
		super(deadline, dag, vmList, vcc, ccr);
	}
	
	public double getCurE() {
		return curE;
	}

	public void setCurE(double curE) {
		this.curE = curE;
	}
	
	public double getBaseE() {
		return baseE;
	}

	public void setBaseE(double baseE) {
		this.baseE = baseE;
	}

	public double getPriceAfterG() {
		return priceAfterG;
	}

	public void setPriceAfterG(double priceAfterG) {
		this.priceAfterG = priceAfterG;
	}
}
