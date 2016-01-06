package core.dag;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;


/*
 * DAGMontage is a class used for generating Montage Directed Acyclic graphs (DAGs)
 */
public class DAGTest {
	/*
	 * The default constructor for DAGMontage
	 */
	public DAGTest(){
		
	}
	/*
	 * Generate a Montage DAG, and saves the results to a file DAGMontage.txt
	 */
	public void generateRandomFile(double ccrRatio){
		int nodeCount = 10;
		boolean[][]result = new boolean[nodeCount][nodeCount];
		//	The zero layer
		result[0][1] = true;
		result[0][2] = true;
		result[0][3] = true;
		result[0][4] = true;
		result[0][5] = true;
		
		result[1][7] = true;
		result[1][8] = true;
		
		result[2][6] = true;
		
		result[3][7] = true;
		result[3][8] = true;
		
		result[4][8] = true;
		
		result[5][7] = true;
		
		result[6][9] = true;
		
		result[7][9] = true;
		
		result[8][9] = true;
		
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
			String fileName = "src/code/DAGTest.txt";
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName));
			
			for(int i = 0;i <= result.length - 1;i++){
				for(int j = i + 1;j <= result.length - 1;j++){
					if(result[i][j]){
						output.write(i + " " + j + " " + ( (int)(Math.random()*100) + 1 ));
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
