package core.algorithm;

import java.util.List;

import core.dag.DAG;
import core.vm.Vm;
import core.vm.VmComputeCost;

/**
 * The abstract class for all the scheduling algorithm.
 * Received:	deadline, dag, virtual machine list, vm compute cost and ccr, etc.
 * Result:	Generate the scheduling result, including every cloudlet's actual start
 * time, actual finish time, executing frequency level, the vm number in which to be
 * executed and so on. The result is stored in dag's cloudlet.
 * @author shouhui
 *
 */
public abstract class Algorithm {
	/** Simulation's deadline */
    private double deadline;
    /** All simulation's DAGs */
    private DAG dag;
    /** Simulation's virtual machines list*/
	private List<Vm> vmList;
    /** Every cloudlet's compute cost */
	private VmComputeCost vcc;
	/** Simulation's ccr */
	private double[][] ccr;
    
    public Algorithm(double deadline, DAG dag, List<Vm> vmList,
			VmComputeCost vcc, double[][] ccr) {
		super();
		this.deadline = deadline;
		this.dag = dag;
		this.vmList = vmList;
		this.vcc = vcc;
		this.ccr = ccr;
	}    
    /**
     * The getters and setters.
     * @return
     */
	public double getDeadline() {
		return deadline;
	}
	public void setDeadline(double deadline) {
		this.deadline = deadline;
	}
	public DAG getDag() {
		return dag;
	}
	public void setDag(DAG dag) {
		this.dag = dag;
	}
	public List<Vm> getVmList() {
		return vmList;
	}
	public void setVmList(List<Vm> vmList) {
		this.vmList = vmList;
	}
	public VmComputeCost getVcc() {
		return vcc;
	}
	public void setVcc(VmComputeCost vcc) {
		this.vcc = vcc;
	}
	public double[][] getCcr() {
		return ccr;
	}
	public void setCcr(double[][] ccr) {
		this.ccr = ccr;
	}

}
