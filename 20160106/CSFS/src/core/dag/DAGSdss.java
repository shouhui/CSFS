package core.dag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DAGSdss {
	/*
	 * The default constructor for DAGSdss
	 */
	public DAGSdss(){
		
	}
	/*
	 * Generate a SDSS DAG, and saves the results to a file DAGAll.txt
	 */
	public void generateRandomFile(double ccrRatio){
		int nodeCount = 124;
		boolean[][]result = new boolean[nodeCount][nodeCount];

		//	The zero layer
		for(int i = 1;i <= 20;i++){
			result[0][i] = true;
		}
		
		//	The first layer
		for(int i = 1;i <= 20;i++){
			result[i][i+20] = true;
		}

		//	The second layer
		for(int i = 21;i <= 23;i++){
			result[i][41] = true;
			result[i][42] = true;
		}
		
		for(int i = 24;i <= 27;i++){
			result[i][43] = true;
			result[i][44] = true;
		}
		
		result[24][41] = true;
		result[24][42] = true;
		result[25][41] = true;
		result[25][42] = true;

		for(int i = 28;i <= 33;i++){
			result[i][45] = true;
			result[i][46] = true;
		}
		
		result[28][43] = true;
		result[28][44] = true;
		result[29][43] = true;
		result[29][44] = true;
		
		result[32][47] = true;
		result[32][48] = true;
		result[33][47] = true;
		result[33][48] = true;

		for(int i = 34;i <= 37;i++){
			result[i][47] = true;
			result[i][48] = true;
		}
		
		result[36][49] = true;
		result[36][50] = true;
		result[37][49] = true;
		result[37][50] = true;

		for(int i = 38;i <= 40;i++){
			result[i][49] = true;
			result[i][50] = true;
		}
		
		//	The third layer
		for(int i = 51;i <= 74;i++){
			result[41][i] = true;
		}

		for(int i = 51;i <= 74;i++){
			result[41][i] = true;
			result[42][i] = true;
		}
		
		for(int i = 63;i <= 86;i++){
			result[43][i] = true;
			result[44][i] = true;
		}
		
		for(int i = 75;i <= 98;i++){
			result[45][i] = true;
			result[46][i] = true;
		}
		
		for(int i = 87;i <= 110;i++){
			result[47][i] = true;
			result[48][i] = true;
		}
		
		
		for(int i = 99;i <= 122;i++){
			result[49][i] = true;
			result[50][i] = true;
		}
		
		//	The fourth layer
		for(int i = 51;i <= 122;i++){
			result[i][123] = true;
		}
		
		//	Save the results to the file DAGMontage.txt
		writeFile(result,ccrRatio);
		
//		for(int i = 0;i <= nodeCount - 1;i++){
////			System.out.print(i + " ");
//			for(int j = 0;j <= nodeCount - 1;j++){
//				if(result[i][j]){
//					System.out.print("T ");
//				}else{
//					System.out.print("F ");
//				}
////				System.out.print(result[i][j] + " ");
//			}
//			System.out.println();
//		}
	}
	
	public void writeFile(boolean[][]result,double ccrRatio){
		try{
			String fileName = "src/code/DAGAll.txt";
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
//			double ccrTemp = ((int)(Math.random()*10)+1)/10.0;
//			System.out.println("ccrTemp:" + ccrTemp);
			
			for(int i = 0;i <= result.length - 1;i++){
				for(int j = i + 1;j <= result.length - 1;j++){
					if(result[i][j]){
						output.write(i + " " + j + " " + ((int)(Math.random()*90)+10));
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

}
