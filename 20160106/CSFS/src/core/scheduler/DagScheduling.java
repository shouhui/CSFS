package core.scheduler;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import core.dag.Cloudlet;
import core.dag.DAG;
import core.dag.DAGAirsn;
import core.dag.DAGLigo;
import core.dag.DAGMontage;
import core.dag.DAGSdss;
import core.dag.DAGTest;
import core.vm.Vm;
import core.vm.VmComputeCost;

/*
 * Title: DAG scheduling Toolkit
 * Description: DAG scheduling Toolkit using different algorithm
 * Date: Oct3, 2014
 */
public class DagScheduling {
	/** The cloudlet list */
	private static List<Cloudlet>cloudletList;
	
	/** The vm list */
	private static List<Vm>vmList;
	
	/** The voltage and frequency list */
	private static List<Double[]>vfList;
	
	/**	The deadline*/
	private static double deadline;

	/** The dependency of all the cloudlets	*/
	private static Map<String,String>cloudletDependMap;
	
	/**	The dependency value of all the cloudlets */
	private static Map<String,Double>cloudletDependValueMap;
	
	/** The compute cost of the vm for the cloudlet */
	private static Map<Integer,double[]>vmComputeCostMap;
	
	/** The average compute cost of the vm for the cloudlet */
	private static Map<Integer,Double>vmAveComputeCostMap;
	
	private static double ccrRatio = 1;
	
	private static double[][] ccr;
	
	private static double B = 0;

	/**
	 * Create the main() to run.
	 * @param args
	 * @throws Throwable 
	 */
	
	public static void main(String[] args) throws Throwable {
		int times = 100;
//		System.out.println("Starting the main function....");
		

		//	Delete the final result
		try{
			String fileName = "src/code/final.txt";
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
			output.write("");
			output.flush();
			output.close();
		}catch(IOException e){
			e.printStackTrace();
		}		
		
		int []vmNums = {3,5,8};
		for(int m = 1;m <= 4;m++)
		{
			for(int n = 0;n < vmNums.length ;n++)
			{

				try {
					int dagCase = m;
					int vmNum = vmNums[n];
					System.out.println(" DAG:" + dagCase + " vmNum:" + vmNum);
					int i = 0;
					for(;i<=10;i++)
					{
						
						//	Delete the ratio result
						try{
							String fileName = "src/code/result.txt";
							BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
							output.write("");
							output.flush();
							output.close();
						}catch(IOException e){
							e.printStackTrace();
						}
						//	Delete the time result
						try{
							String fileName = "src/code/timeResult.txt";
							BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
							output.write("");
							output.flush();
							output.close();
						}catch(IOException e){
							e.printStackTrace();
						}				
						
						
						
						
						B = 0.1*i;
						System.out.println("B:" + B );
						//	Execute the experiment
						for(int j = 0;j < times;j++){
							metaExe(dagCase,vmNum);
						}
						
						//	Compute the average saved ratio
						computeAveR(times,dagCase,vmNum);
						
						//	Compute the average time
						computeAveTime(times,dagCase,vmNum);
						
						//	Compute the average time of all the three algorithm, GIT, EES, ESFS
						computeAveTimeAll(times,dagCase,vmNum);
					}
					
					
				} catch(Exception e) {
					e.printStackTrace();
					System.out.println("Unwanted errors happen.");
				}
				
				
			}
			
		}
		
	}
	
	private static void computeAveTimeAll(int times, int dagCase, int vmNum) throws IOException {
		String fileName = "src/code/timeResultAll.txt";
		String buffered;
		double[]ratio = new double[3];
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
		System.out.println("The average time is " + "    GTI:" + r0 + "    EES:" + r1 + "    ESFS:" + r2);
		
		fileName = "src/code/data/" + dagCase + '_' + vmNum + "_time_all";
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName,true));
		out.write( B + " " + r0 + " " + r1 + " " + r2);
		out.write('\n');
		out.close();
		
	}


	private static void computeAveTime(int times, int dagCase, int vmNum) throws IOException {
		String fileName = "src/code/timeResult.txt";
		String buffered;
		double[]ratio = new double[2];
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
		System.out.println("The average time is " + "    GTI:" + r0 + "    Individual:" + r1);
		
		fileName = "src/code/data/" + dagCase + '_' + vmNum + "_time";
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName,true));
		out.write( B + " " + r0 + " " + r1);
		out.write('\n');
		out.close();
		
	}

	public static void computeAveR(int times,int dagCase,int vmNum) throws IOException{
		String fileName = "src/code/result.txt";
		String buffered;
		double[]ratio = new double[3];
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
		System.out.println("The average saved ratio is " + "    GTI:" + r0 + "    EES:" + r1 + "    ESFS:" + r2);
		
		fileName = "src/code/data/" + dagCase + '_' + vmNum;
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName,true));
		out.write( B + " " + r0 + " " + r1 + " " + r2 );
		out.write('\n');
		out.close();
	}
	
	
	
	//	Base execute
	public static void metaExe(int dagCase,int vmNum) throws Throwable{
		// 1. Initialize the scheduling parameter.
		Broker broker = new Broker();
		
		//	Create ccr
		ccr = new double[vmNum][vmNum];
		for(int i = 0;i < vmNum;i++){
			for(int j = i+1;j < vmNum;j++ ){
				ccr[i][j] = Math.random() + 0.5;
				ccr[j][i] = ccr[i][j];
			}
		}
		broker.setCcr(ccr);
		
		//	Select whether with individual
		if( (dagCase == 1) || ( (dagCase == 3) && (vmNum == 8) ) ){
			broker.setSelecedIndividul(false);
		}
		
		
		//	Set theta value
		if( vmNum == 3 ){
			broker.setC(1.03);
		}else if( vmNum == 5 ){
			if( dagCase == 1 ){
				broker.setC(1);
			}else if( dagCase == 2 ){
				broker.setC(1.005);
			}else if( dagCase == 3 ){
				broker.setC(1.01);
			}else if( dagCase == 4 ){
				broker.setC(1.02);
			}
		}else if(vmNum == 8){
			broker.setC(1);
		}

		int cloudletNum = 0;
		//	Montage DAG generate
//		int cloudletNum = DAGGeneInitial(broker);		//	CHANGE..................................
		 
		switch( dagCase ){
			//	test DAG,
			case 0:
			{
				cloudletNum = 34;		//	CHANGE..............................................................
				DAG dag = new DAG();
				cloudletList = createCloudlet(cloudletNum);
				dag.setCloudletList(cloudletList);
				
				createCloudletDependSimple();
				dag.setCloudletDependValueMap(cloudletDependValueMap);
				
				// 3. Create broker
				broker.submitDAG(dag);
				break;
			}
			//	Montage DAG
			case 1:
			{
				cloudletNum = 34;
				DAG dag = new DAG();
				cloudletList = createCloudlet(cloudletNum);
				dag.setCloudletList(cloudletList);

				DAGMontage dagAll = new DAGMontage();
				dagAll.generateRandomFile();
				
				createCloudletDepend();
				dag.setCloudletDependValueMap(cloudletDependValueMap);
				
				broker.submitDAG(dag);
				break;
			}
			//	Ligo DAG
			case 2:
			{
				cloudletNum = 77;
				DAG dag = new DAG();
				cloudletList = createCloudlet(cloudletNum);
				dag.setCloudletList(cloudletList);

				DAGLigo dagAll = new DAGLigo();
				dagAll.generateRandomFile(ccrRatio);
				
				createCloudletDepend();
				dag.setCloudletDependValueMap(cloudletDependValueMap);
				
				broker.submitDAG(dag);
				break;
			}
			//	Airsn DAG
			case 3:
			{
				cloudletNum = 53;
				DAG dag = new DAG();
				cloudletList = createCloudlet(cloudletNum);
				dag.setCloudletList(cloudletList);

				DAGAirsn dagAll = new DAGAirsn();
				dagAll.generateRandomFile(ccrRatio);
				
				createCloudletDepend();
				dag.setCloudletDependValueMap(cloudletDependValueMap);
				
				broker.submitDAG(dag);
				break;
			}
			//	Sdss DAG
			case 4:
			{
				cloudletNum = 124;
				DAG dag = new DAG();
				cloudletList = createCloudlet(cloudletNum);
				dag.setCloudletList(cloudletList);

				DAGSdss dagAll = new DAGSdss();
				dagAll.generateRandomFile(ccrRatio);
				
				createCloudletDepend();
				dag.setCloudletDependValueMap(cloudletDependValueMap);
				
				broker.submitDAG(dag);
				break;
			}
			//	HEFT 
			case 5:
			{
				cloudletNum = 10;
				DAG dag = new DAG();
				cloudletList = createCloudlet(cloudletNum);
				dag.setCloudletList(cloudletList);

				DAGTest dagAll = new DAGTest();
				dagAll.generateRandomFile(ccrRatio);
				
				createCloudletDependTest();
				dag.setCloudletDependValueMap(cloudletDependValueMap);
				
				broker.submitDAG(dag);
				break;

				
			}
			default:
			{
				
			}
		}
		// 2. Create cloudlet

		// 4. Create virtual machine
		
		//	vm properties
		vmList = createVmRandom(vmNum);
		broker.submitVm(vmList);
		
		if(  dagCase != 0){
			generateCost(cloudletNum,vmNum);
			createVmComputeCost();
		}else{
			createVmComputeCostSimple();
		}

		VmComputeCost vcc = new VmComputeCost();
		vcc.setVmComputeCostMap(vmComputeCostMap);
		vcc.setAveVmComputeCostMap(vmAveComputeCostMap);
		
		// 6. Start the scheduling
		//		start_new() is the compare without group period.
//		for( int i = 0;i <= 10;i++ )
		{
			broker.setB(B);
			
			
			
			broker.start();	

		}
		// 7. Print the result

	}

	/*
	 * Generate the of all the cloudlets for all the virtual machines
	 */
	public static void generateCost(int cloudletNum,int vmNum){
		try{
			String fileName = "src/code/proCostAll.txt";		//		
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
			
			for(int i = 0;i <= cloudletNum - 1;i++){
				for(int j = 0;j <= vmNum - 1;j++){
//					output.write(Math.random()*100,0,5);
//					Double temp = Math.random()*100;
					Integer temp = (int)((Math.random()*9+1)*(Math.random()*9+1));
					output.write(temp.toString());
					if(j != vmNum - 1){
						output.write(' ');
					}else{
						output.write('\n');
					}
				}
			}
			output.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Create vms
	 */
	private static List<Vm> createVm(int vmNum) throws IOException {
		List<Vm>list = new ArrayList<Vm>();
		int count = 0;
		BufferedReader bf = new BufferedReader(new FileReader("src/code/DV.txt"));
		String buffered ;
		while( ((buffered = bf.readLine()) != null) && (count < vmNum)){
			Vm vm = new Vm(count,0);
			list.add(vm);
			count++;
			vfList = new ArrayList<Double[]>();
			String bufferedArray[] = buffered.split(",");
			for(String s:bufferedArray){
				String strTemp[] = s.split(" ");
				Double douTemp[] = {Double.parseDouble(strTemp[0]),Double.parseDouble(strTemp[1])};
				vfList.add(douTemp);
			}
			vm.setVfList(vfList);
			vm.setMaxVfLevel(vfList.size() - 1);
		}
		bf.close();
		return list;
	}
	private static List<Vm> createVmRandom(int vmNum) throws IOException {
		List<Vm>list = new ArrayList<Vm>();

		int count = 0;
		while( count < vmNum ){
			String buffered ;
			BufferedReader bf = new BufferedReader(new FileReader("src/code/DV.txt"));
			double border = (int)(Math.random() * 5);		//	The number of DVs is 5.
//			System.out.println(border);
			int i = 0;
			while( ((buffered = bf.readLine()) != null) && i < border ){
				i++;
			}
//			System.out.println(buffered);
			

			Vm vm = new Vm(count,0);
			list.add(vm);
			count++;
			vfList = new ArrayList<Double[]>();
			String bufferedArray[] = buffered.split(",");
			for(String s:bufferedArray){
				String strTemp[] = s.split(" ");
				Double douTemp[] = {Double.parseDouble(strTemp[0]),Double.parseDouble(strTemp[1])};
				vfList.add(douTemp);
			}
			vm.setVfList(vfList);
			vm.setMaxVfLevel(vfList.size() - 1);
			bf.close();
		}
		return list;
	}
	
	
	/*
	 * Create cloudlets
	 */
	private static List<Cloudlet>createCloudlet(int cloudletNum){
		List<Cloudlet>list = new ArrayList<Cloudlet>();
		for(int i = 0;i < cloudletNum;i++){
			Cloudlet cl = new Cloudlet(i,-1,0);
			list.add(cl);
		}
		return list;
	}
	/*
	 * Create cloudlet dependency
	 */
	private static void createCloudletDepend() throws Throwable{
		BufferedReader bd = new BufferedReader(new FileReader("src/code/DAGAll.txt"));
		
		String buffered;
		cloudletDependMap = new HashMap<String,String>();
		cloudletDependValueMap = new HashMap<String,Double>();
		
		while((buffered=bd.readLine())!=null){
			String bufferedArray[] = buffered.split(" ");
			cloudletDependMap.put(bufferedArray[0], bufferedArray[1]);
//			System.out.println(cloudletDependMap.get(bufferedArray[0]));
			cloudletDependValueMap.put(bufferedArray[0]+" "+bufferedArray[1], Double.parseDouble(bufferedArray[2]));
//			System.out.println(cloudletDependValueMap.get(bufferedArray[0]+" "+bufferedArray[1]));

		/*	添加任务的前驱，后继*/
			int tem0 = Integer.parseInt(bufferedArray[0]);
			int tem1 = Integer.parseInt(bufferedArray[1]);

			cloudletList.get(tem0).addToSucCloudletList(tem1);
			cloudletList.get(tem1).addToPreCloudletList(tem0);
		}
		bd.close();
	}
	private static void createCloudletDependSimple() throws Throwable{
//		BufferedReader bd = new BufferedReader(new FileReader("src/code/DAG_Simple_N6"));
		BufferedReader bd = new BufferedReader(new FileReader("src/code/data/DAG_Simple_Montage"));
		
		
		String buffered;
		cloudletDependMap = new HashMap<String,String>();
		cloudletDependValueMap = new HashMap<String,Double>();
		
		while((buffered=bd.readLine())!=null){
			String bufferedArray[] = buffered.split(" ");
			cloudletDependMap.put(bufferedArray[0], bufferedArray[1]);
//			System.out.println(cloudletDependMap.get(bufferedArray[0]));
			cloudletDependValueMap.put(bufferedArray[0]+" "+bufferedArray[1], Double.parseDouble(bufferedArray[2]));
//			System.out.println(cloudletDependValueMap.get(bufferedArray[0]+" "+bufferedArray[1]));

		/*	添加任务的前驱，后继*/
			int tem0 = Integer.parseInt(bufferedArray[0]);
			int tem1 = Integer.parseInt(bufferedArray[1]);

			cloudletList.get(tem0).addToSucCloudletList(tem1);
			cloudletList.get(tem1).addToPreCloudletList(tem0);
		}
		bd.close();
	}
	private static void createCloudletDependTest() throws Throwable{
		BufferedReader bd = new BufferedReader(new FileReader("src/code/DAGTest.txt"));
		
		
		String buffered;
		cloudletDependMap = new HashMap<String,String>();
		cloudletDependValueMap = new HashMap<String,Double>();
		
		while((buffered=bd.readLine())!=null){
			String bufferedArray[] = buffered.split(" ");
			cloudletDependMap.put(bufferedArray[0], bufferedArray[1]);
//			System.out.println(cloudletDependMap.get(bufferedArray[0]));
			cloudletDependValueMap.put(bufferedArray[0]+" "+bufferedArray[1], Double.parseDouble(bufferedArray[2]));
//			System.out.println(cloudletDependValueMap.get(bufferedArray[0]+" "+bufferedArray[1]));

		/*	添加任务的前驱，后继*/
			int tem0 = Integer.parseInt(bufferedArray[0]);
			int tem1 = Integer.parseInt(bufferedArray[1]);

			cloudletList.get(tem0).addToSucCloudletList(tem1);
			cloudletList.get(tem1).addToPreCloudletList(tem0);
		}
		bd.close();
	}

	/*
	 * Create virtual machine compute time
	 */
	private static void createVmComputeCost() throws IOException{
		vmComputeCostMap = new HashMap<Integer,double[]>();
		vmAveComputeCostMap = new HashMap<Integer,Double>();
		String buffered;
//		BufferedReader bd = new BufferedReader(new FileReader("src/code/processorCost_4_11"));
		BufferedReader bd = new BufferedReader(new FileReader("src/code/proCostAll.txt"));

		int num = 0;
		while((buffered=bd.readLine())!=null){
			String bufferedArray[] = buffered.split(" ");
			double[] bufferedDouble = new double[bufferedArray.length];
			double sum = 0;
			for(int i=0;i<bufferedArray.length;i++){
				bufferedDouble[i] = Double.parseDouble(bufferedArray[i]);
				sum += bufferedDouble[i];
			}
			vmComputeCostMap.put(num, bufferedDouble);
			vmAveComputeCostMap.put(num, sum/bufferedArray.length);
			num++;
		}
		bd.close();
	}
	private static void createVmComputeCostSimple() throws IOException{
		vmComputeCostMap = new HashMap<Integer,double[]>();
		vmAveComputeCostMap = new HashMap<Integer,Double>();
		String buffered;
//		BufferedReader bd = new BufferedReader(new FileReader("src/code/proCost_Simple_n6"));
		BufferedReader bd = new BufferedReader(new FileReader("src/code/data/proCost_Simple_Montage"));

		int num = 0;
		while((buffered=bd.readLine())!=null){
			String bufferedArray[] = buffered.split(" ");
			double[] bufferedDouble = new double[bufferedArray.length];
			double sum = 0;
			for(int i=0;i<bufferedArray.length;i++){
				bufferedDouble[i] = Double.parseDouble(bufferedArray[i]);
				sum += bufferedDouble[i];
			}
			vmComputeCostMap.put(num, bufferedDouble);
			vmAveComputeCostMap.put(num, sum/bufferedArray.length);
			num++;
		}
		bd.close();
	}


}
