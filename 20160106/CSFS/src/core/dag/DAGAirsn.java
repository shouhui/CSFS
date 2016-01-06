package core.dag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/*
 * DAGMontage is a class used for generating Montage Directed Acyclic graphs (DAGs)
 */
public class DAGAirsn {
	/*
	 * The default constructor for DAGMontage
	 */
	public DAGAirsn(){
		
	}
	/*
	 * Generate a Montage DAG, and saves the results to a file DAGMontage.txt
	 */
	public void generateRandomFile(double ccrRatio){
		int nodeCount = 53;
		int layerCount = 13;
		boolean[][]result = new boolean[nodeCount][nodeCount];
		int layerEndLine[] = { 0,10,20,23,26,27,28,29,39,40,41,51,52 };
//		for(int a:layerEndLine){
//			System.out.println(a);
//		}
		//	The zero layer
		for(int i = layerEndLine[0] + 1;i <= layerEndLine[1];i++){
			result[0][i] = true;
		}
		//	The first layer
		for(int i = layerEndLine[0] + 1;i <= layerEndLine[1];i++){
			result[i][i + layerEndLine[1] - layerEndLine[0]] = true;
			result[i][i + layerEndLine[7] - layerEndLine[0]] = true;
		}
		for(int i = layerEndLine[2] + 1;i <= layerEndLine[3];i++){
			result[0][i] = true;
		}
		//	The second layer
		int random1 = 3;
		int random2 = 3;
		for(int i = layerEndLine[1] + 1;i <= layerEndLine[1] + random1;i++){
			result[i][layerEndLine[12]] = true;
		}
		for(int i = layerEndLine[1] + random1 + random2 + 1;i <= layerEndLine[2];i++){
			result[i][layerEndLine[12]] = true;
		}
		for(int i = layerEndLine[1] + random1 + 1;i <= layerEndLine[1] + random1 + random2;i++){
			result[i][i + layerEndLine[2] - layerEndLine[1] - random1] = true;
			result[i][i + layerEndLine[3] - layerEndLine[1] - random1] = true;
		}
		//	The third layer
		for(int i = layerEndLine[2] + 1;i <= layerEndLine[3];i++){
			result[i][i + layerEndLine[3] - layerEndLine[2]] = true;
		}
		//	The fourth layer
		for(int i = layerEndLine[3] + 1;i <= layerEndLine[4];i++){
			result[i][layerEndLine[5]] = true;
		}
		//	The fifth layer
		result[layerEndLine[5]][layerEndLine[6]] = true;
		//	The sixth layer
		result[layerEndLine[6]][layerEndLine[7]] = true;
		//	The seventh layer
		for(int i = layerEndLine[7] + 1;i <= layerEndLine[8];i++){
			result[layerEndLine[7]][i] = true;
		}
		//	The eighth layer
		for(int i = layerEndLine[7] + 1;i <= layerEndLine[8];i++){
			result[i][layerEndLine[9]] = true;
			result[i][layerEndLine[10] - layerEndLine[7]] = true;
		}
		//	The ninth
		result[layerEndLine[9]][layerEndLine[10]] = true;
		//	The 10th
		for(int i = layerEndLine[10] + 1;i <= layerEndLine[11];i++){
			result[layerEndLine[10]][i] = true;
		}
		//	The 11th
		for(int i = layerEndLine[10] + 1;i <= layerEndLine[11];i++){
			result[i][layerEndLine[12]] = true;
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
