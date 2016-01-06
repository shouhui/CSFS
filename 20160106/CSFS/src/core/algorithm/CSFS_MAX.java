package core.algorithm;

import java.util.List;

import core.dag.DAG;
import core.vm.Vm;
import core.vm.VmComputeCost;

/**
 * Paper: Pietri I, Sakellariou R. Cost-efficient CPU Provisioning for Scientific Workflows on Clouds[J].
 * 
 * Date: Dec16+, 2015
 * @author shouhui
 *
 */
public class CSFS_MAX extends Algorithm {
	
	
	
	
	
	
	
	public CSFS_MAX(double deadline, DAG dag, List<Vm> vmList,
			VmComputeCost vcc, double[][] ccr) {
		super(deadline, dag, vmList, vcc, ccr);
	}

}
