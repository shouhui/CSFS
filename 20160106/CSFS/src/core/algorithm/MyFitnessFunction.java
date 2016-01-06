package core.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import core.dag.Cloudlet;
import core.dag.DAG;
import core.vm.Vm;
import core.vm.VmComputeCost;
import net.sourceforge.jswarm_pso.FitnessFunction;

/**
 * Sample Fitness function
 * 		f( x1 , x2 ) = 1 - Sqrt( ( x1 - 1/2 )^2 + ( x2 - 1/2 )^2 )
 * 
 * @author Pablo Cingolani <pcingola@users.sourceforge.net>
 */
public class MyFitnessFunction extends FitnessFunction {
	//-------------------------------------------------------------------------
	// Methods
	//-------------------------------------------------------------------------
	private DAG dag;
	private List<Vm> vmList;
	private VmComputeCost vcc;
	private double[][] ccr;
	private double deadline;
	private HEFT heft;

	/**
	 * Evaluates a particles at a given position
	 * @param position : Particle's position
	 * @return Fitness function for a particle
	 */
	public double evaluate(double position[]) {
		List<Cloudlet>cloudletList = dag.getCloudletList();
		List<Double[]> computeCostSend = new ArrayList<Double[]>();

		//	Set the temporary cost of all the cloudlets for vms*/
		for(int i = 0;i < cloudletList.size();i++){
			Cloudlet cl = dag.getCloudletById(i);
			Double[]costTemp = new Double[vmList.size()];
			for(Vm vm:vmList){
				int vmId = vm.getVmId();
				costTemp[vmId] = vcc.getScaledTime(cl, (int)position[i], vm);
			}
			computeCostSend.add(i,costTemp);
		}
		heft.initialize(computeCostSend);
		heft.start();

		if( heft.getExeTimeTemp(cloudletList.size() - 1)[1] > deadline ){
			return Double.MAX_VALUE;
		}

		return computePrice(cloudletList, heft.getExeTime(), position);
	}

	
	private double computePrice(List<Cloudlet> cloudletList, List<Double[]> exeTime, double[] position) {
//		double curM = cloudletList.get(cloudletList.size() - 1).getAft();
		double curM = exeTime.get(cloudletList.size() - 1)[1];
		//	Compute the overall price consumption after HEFT scheduling
		double energyDTemp = 0;
		double energyITemp = 0;
		for(Cloudlet cl:cloudletList){
			double tempTime = exeTime.get(cl.getCloudletId())[1] - exeTime.get(cl.getCloudletId())[0];
//			energyDTemp += computeCloudletE(cl,level,exeTime);
			energyDTemp += vmList.get(cl.getVmId()).getUnitPrice((int)position[cl.getCloudletId()])*tempTime;
		}
		for(Vm vm:vmList){
			double workTime = 0;
			Iterator<Integer>it = vm.getCloudletInVm().iterator();
			while(it.hasNext()){
				int cloudletId = it.next();
				Cloudlet cl = dag.getCloudletById(cloudletId);
				workTime += ( exeTime.get(cl.getCloudletId())[1] - exeTime.get(cl.getCloudletId())[0] );
			}
			double idleTime = curM - workTime;
//			energyITemp += computeIdleE(vm,idleTime);
			energyITemp += vm.getUnitPrice(vm.getMaxfLevel())*idleTime;
		}
		return energyDTemp + energyITemp;
	}


	public MyFitnessFunction(DAG dag, List<Vm> vmList, VmComputeCost vcc,
			double[][] ccr, double deadline, HEFT heft) {
		super();
		this.dag = dag;
		this.vmList = vmList;
		this.vcc = vcc;
		this.ccr = ccr;
		this.deadline = deadline;
		this.heft = heft;
	}
}
