package core.algorithm;

import java.util.List;

import net.sourceforge.jswarm_pso.Swarm;
import net.sourceforge.jswarm_pso.example_2.SwarmShow2D;
import core.dag.DAG;
import core.vm.Vm;
import core.vm.VmComputeCost;

public class PSO extends Algorithm {
	
	private DAG dag = getDag();
	private List<Vm>vmList = getVmList();
	private double[][] ccr = getCcr();	
	private VmComputeCost vcc = getVcc();
	private double deadline = getDeadline();
	
	private double psoPrice = 0;


	public PSO(double deadline, DAG dag, List<Vm> vmList, VmComputeCost vcc,
			double[][] ccr) {
		super(deadline, dag, vmList, vcc, ccr);
	}
	
	//-------------------------------------------------------------------------
	// Main
	//-------------------------------------------------------------------------
	public void start() {
		System.out.println("Begin: pso \n");

		HEFT heft = new HEFT(deadline, dag, vmList, vcc, ccr);
		// Create a swarm (using 'MyParticle' as sample particle and 'MyFitnessFunction' as fitness function)
		MyParticle.setDimention(dag.getCloudletList().size());
		MyFitnessFunction fitnessFunction = new MyFitnessFunction(dag, vmList, vcc, ccr, deadline, heft);
		fitnessFunction.setMaximize(false);
		Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, new MyParticle(), fitnessFunction);

		// Use neighborhood
//		Neighborhood neigh = new Neighborhood1D(Swarm.DEFAULT_NUMBER_OF_PARTICLES / 5, true);
//		swarm.setNeighborhood(neigh);
//		swarm.setNeighborhoodIncrement(0.9);

		// Set position (and velocity) constraints. I.e.: where to look for solutions
		swarm.setInertia(0.95);
		swarm.setMaxPosition(20.9999);
		swarm.setMinPosition(0);
		swarm.setMaxMinVelocity(2.1);

		int numberOfIterations = 40;
		boolean showGraphics = false;

		if (showGraphics) {
			int displayEvery = numberOfIterations / 100 + 1;
			SwarmShow2D ss2d = new SwarmShow2D(swarm, numberOfIterations, displayEvery, true);
			ss2d.run();
		} else {
			// Optimize (and time it)
			for (int i = 0; i < numberOfIterations; i++)
				swarm.evolve();
		}

		// Print results
		System.out.println(swarm.toStringStats());
		System.out.println("End: pso");
		setPsoPrice(swarm.getBestFitness());
	}
	

	public double getPsoPrice() {
		return psoPrice;
	}

	public void setPsoPrice(double psoPrice) {
		this.psoPrice = psoPrice;
	}

}