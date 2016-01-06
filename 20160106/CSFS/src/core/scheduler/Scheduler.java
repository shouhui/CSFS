package core.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.dag.Cloudlet;
import core.dag.DAG;
import core.dag.DAGMontage;
import core.vm.Vm;
import core.vm.VmComputeCost;

/*
 * Title: DAG scheduling Toolkit
 * Description: DAG scheduling Toolkit using different algorithm
 * Date: Dec14+, 2015
 */

public class Scheduler {
	private static DAG dag;
	private static List<Vm> vmList;
	private static VmComputeCost vcc;
	private static double[][] ccr;
	
	private static double beta = 0;
	
	private static double aveCommunicationCost = 0;

	public static void main(String[] args) {
		resetFiles();
		
		int vmNum = 3;
		int dagCase = 1;
		
		int times = 40;
		for(int i = 0; i < times; i++){
			metaExe(vmNum, dagCase);
		}
		
		try {
			computeAveResult(times, dagCase, vmNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void resetFiles() {
		//	Delete the previous final result
		try{
			String fileName = "dataout/result.txt";
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
			output.write("");
			output.flush();
			output.close();
		}catch(IOException e){
			e.printStackTrace();
		}		
		
	}

	@SuppressWarnings("resource")
	public static void computeAveResult(int times,int dagCase,int vmNum) throws IOException{
		String fileName = "dataout/result.txt";
		String buffered;
		double[]ratio = new double[4];
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		while((buffered = br.readLine()) != null){
//			System.out.println(buffered);
			String bufferedStr[] = buffered.split(" ");
			for(int i = 0;i <= ratio.length - 1;i++){
				ratio[i] += Double.parseDouble(bufferedStr[i]);
			}
		}
		double r0 = ratio[0]/times;
		double r1 = ratio[1]/times;
		double r2 = ratio[2]/times;
		double r3 = ratio[3]/times;
		
		fileName = "dataout/" + dagCase + '_' + vmNum;
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName,true));
		out.write( r0 + " " + r1 + " " + r2 + " " + r3 );
		out.write('\n');
		out.close();
	}
	
	private static void metaExe(int vmNum, int dagCase) {
		//	1. Create the scheluler.
		Scheduler scheduler = new Scheduler();

		//	2-6, initialization
		scheduler.init(dagCase, vmNum);
		
		//	7.	Submit dag, vmList, vcc, and ccr to broker.
		Broker broker = new Broker();
		broker.setDag(dag);
		broker.setVmList(vmList);
		broker.setVcc(vcc);
		broker.setCcr(ccr);
		
		broker.start();		
	}

	/**
	 * 
	 * @param dagCase
	 * @param vmNum
	 */
	private void init(int dagCase, int vmNum) {
		// 2. Create dag contains cloudlets.
		createDag(dag, dagCase);
		// 3. Create vms
		int freqMin = 1000;
		int freqMax = 3000;
		int freqStep = 100;
		createVm(vmList, vmNum, freqMin, freqMax, freqStep);

//		for( int i = 0; i < vmList.size(); i++ ){
//			System.out.println(vmList.get(i).getfList());
//			System.out.println(vmList.get(i).getMaxfLevel());
//		}
		
		//	4.	Create cloudlets compute cost on every vm.
		generateCost(dag.getCloudletList().size(), vmNum);
		try {
			createVmComputeCost(vcc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//	5.	Create ccr
		ccr = new double[vmNum][vmNum];

		for(int i = 0;i < vmNum;i++){
			for(int j = i+1;j < vmNum;j++ ){
				ccr[i][j] = Math.random() + 0.5;
				ccr[j][i] = ccr[i][j];
			}
		}

//		HEFT_TEST
//		for(int i = 0;i < vmNum;i++){
//			for(int j = i+1;j < vmNum;j++ ){
//				ccr[i][j] = 1;
//				ccr[j][i] = ccr[i][j];
//			}
//		}
		
//		HEFT_TEST
		
	}
	
	/**
	 * Generate the of all the cloudlets for all the virtual machines
	 * @param cloudletNum
	 * @param vmNum
	 */
	public void generateCost(int cloudletNum,int vmNum){
		try{
			String fileName = "dataout/proCostAll.txt";		//		
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
			
			for(int i = 0;i <= cloudletNum - 1;i++){
				for(int j = 0;j <= vmNum - 1;j++){
//					output.write(Math.random()*100,0,5);
//					Double temp = Math.random()*100;
//					Integer temp = (int)((Math.random()*9+1)*(Math.random()*9+1));
					Integer temp = (int)(aveCommunicationCost/( (Math.random()*0.9 + 0.1) ) );
					output.write(temp.toString());
					if(j != vmNum - 1){
						output.write(' ');
					}else{
						output.write('\n');
					}
				}
			}
			output.flush();
			output.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Create virtual machine compute time
	 * @param vcc
	 * @throws IOException
	 */
	private void createVmComputeCost(VmComputeCost vcc) throws IOException{
		vcc.setVmComputeCostMap(new HashMap<Integer,double[]>());
		vcc.setAveVmComputeCostMap(new HashMap<Integer,Double>());
		String buffered;

		//		**********	HEFT	TEST***************		
		BufferedReader bd = new BufferedReader(new FileReader("dataout/proCostAll.txt"));
//		BufferedReader bd = new BufferedReader(new FileReader("datain/HEFT_PROCESS_TEST.txt"));
		//		**********	HEFT	TEST***************		

		int num = 0;
		while((buffered=bd.readLine())!=null){
			String bufferedArray[] = buffered.split(" ");
			double[] bufferedDouble = new double[bufferedArray.length];
			double sum = 0;
			for(int i=0;i<bufferedArray.length;i++){
				bufferedDouble[i] = Double.parseDouble(bufferedArray[i]);
				sum += bufferedDouble[i];
			}
			vcc.getVmComputeCostMap().put(num, bufferedDouble);
			vcc.getAveVmComputeCostMap().put(num, sum/bufferedArray.length);
			num++;
		}
		bd.close();
	}
	
	/**
	 * 
	 * @param vmList
	 * @param vmNum
	 * @param freqMin
	 * @param freqMax
	 * @param freqStep
	 */
	private void createVm(List<Vm> vmList, int vmNum, int freqMin, int freqMax, int freqStep) {
		int count = 0;
		while( count < vmNum ){
			Vm vm = new Vm(count,0);
			vmList.add(vm);
			count++;
			vm.setfList(new ArrayList<Double>());;
			for( int i = freqMax; i >= freqMin; i-=freqStep){
				vm.getfList().add((double) i);
			}
			vm.setMaxfLevel(vm.getfList().size() - 1);
		}
	}

	/**
	 * dagCase:		1.	Montage.	
	 * @param dag
	 * @param dagCase
	 * 
	 */
	private void createDag(DAG dag, int dagCase) {
		int cloudletNum = 0;
		DAGMontage dagAll = null;
		switch (dagCase) {
			// HEFT test DAG,
			case 0:
				beta = 1;
				cloudletNum = 10;
				break;
			//	Montage
			case 1:
				beta = 0.36;
//				beta = 1;
				cloudletNum = 34;
				dagAll = new DAGMontage();
				break;
			default:
				
		}
		//	Create cloudlets
		dag.setCloudletList(createCloudlet(cloudletNum, beta));
		//	Generate the dependency with communicate value, and store the result in "dataout/DAGAll.txt".

		dagAll.generateRandomFile();

		aveCommunicationCost = dagAll.getAveCommunicateCost();
		//	Create the cloudlets dependency.
		try {
			createCloudletDepend(dag);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	/**
	 * Create cloudlets
	 * @param cloudletNum
	 * @return
	 */
	private List<Cloudlet>createCloudlet(int cloudletNum, double beta){
		List<Cloudlet>list = new ArrayList<Cloudlet>();
		for(int i = 0;i < cloudletNum;i++){
			Cloudlet cl = new Cloudlet(i,-1,0, beta);
			list.add(cl);
		}
		return list;
	}
	
	/**
	 * Create cloudlet dependency
	 * @param dag 
	 * @throws Throwable
	 */
	private void createCloudletDepend(DAG dag) throws Throwable{
		BufferedReader bd = new BufferedReader(new FileReader("dataout/DAGAll.txt"));
//		BufferedReader bd = new BufferedReader(new FileReader("datain/HEFT_DAG_TEST.txt"));

		
		String buffered;
		dag.setCloudletDependValueMap( new HashMap<String,Double>() );
		
		while( (buffered=bd.readLine())!=null ){
			String bufferedArray[] = buffered.split(" ");
//			System.out.println(cloudletDependMap.get(bufferedArray[0]));
			dag.getCloudletDependValueMap().put(bufferedArray[0]+" "+bufferedArray[1], Double.parseDouble(bufferedArray[2]));
//			System.out.println(cloudletDependValueMap.get(bufferedArray[0]+" "+bufferedArray[1]));
			//	添加任务的前驱，后继
			int tem0 = Integer.parseInt(bufferedArray[0]);
			int tem1 = Integer.parseInt(bufferedArray[1]);
			dag.getCloudletList().get(tem0).addToSucCloudletList(tem1);
			dag.getCloudletList().get(tem1).addToPreCloudletList(tem0);
		}
		bd.close();
	}
	
	public Scheduler() {
		dag = new DAG();
		vmList = new ArrayList<Vm>();
		vcc = new VmComputeCost();
	}
}
