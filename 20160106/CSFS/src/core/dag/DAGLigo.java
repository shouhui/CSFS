package core.dag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DAGLigo {
	/*
	 * Generate a LIGO DAG, and saves the results to a file DAGAll.txt
	 */
	public void generateRandomFile(double ccrRatio){
		int nodeCount = 77;
		boolean[][]result = new boolean[nodeCount][nodeCount];
		//	0th
		for(int i = 1;i <= 7;i++){
			result[0][i] = true;
		}
		//	1th
		for(int i = 1;i <= 6;i++){
			result[i][i+7] = true;
		}
		for(int i = 14;i <= 17;i++){
			result[7][i] = true;
		}
		//	2th
		for(int i = 8;i <= 12;i++){
			result[i][i+10] = true;
		}
		result[9][18] = true;
		result[9][59] = true;
		result[9][20] = true;
		
		result[10][18] = true;
		result[10][59] = true;
		result[10][19] = true;
		result[10][41] = true;
		result[10][21] = true;

		result[11][19] = true;
		result[11][20] = true;
		result[11][41] = true;
		result[11][22] = true;

		result[12][21] = true;
		result[12][41] = true;
		result[12][42] = true;
		result[12][24] = true;

		result[13][21] = true;
		result[13][22] = true;
		result[13][24] = true;
		result[13][42] = true;

		result[14][23] = true;
		result[14][25] = true;
		result[14][26] = true;

		for(int i = 15;i <= 17;i++){
			result[i][i+12] = true;
		}
		
		//	3th
		result[18][53] = true;
		result[19][54] = true;
		for(int i = 20;i <= 23;i++){
			result[i][i+10] = true;
		}
		result[23][30] = true;
		result[23][31] = true;
		result[23][32] = true;
		result[24][33] = true;
		
		result[25][34] = true;
		result[26][35] = true;
		for(int i = 27;i <= 29;i++){
			result[i][i+10] = true;
			result[i][i+11] = true;
		}
		result[27][52] = true;
		result[28][69] = true;
		result[28][40] = true;
		result[29][69] = true;
		
		//	4th
		result[30][41] = true;
		result[31][41] = true;
		result[31][42] = true;
		result[32][41] = true;
		result[32][42] = true;
		result[33][42] = true;

		result[34][43] = true;
		result[34][45] = true;
		result[34][58] = true;

		result[35][43] = true;
		result[35][45] = true;
		result[35][58] = true;

		result[36][44] = true;
		result[36][46] = true;
		result[36][47] = true;

		result[37][46] = true;
		result[38][47] = true;
		
		result[39][62] = true;
		result[40][63] = true;
		
		//	5th
		result[41][73] = true;
		result[41][70] = true;
		result[41][55] = true;
		result[41][48] = true;
		result[41][56] = true;

		result[42][76] = true;
		
		result[43][50] = true;
		result[44][50] = true;
		result[44][51] = true;
		result[44][49] = true;
		result[45][51] = true;

		result[46][52] = true;
		result[46][52] = true;

		//	6th
		result[48][65] = true;

		result[49][53] = true;
		result[49][54] = true;
		result[49][57] = true;

		result[50][58] = true;
		result[51][58] = true;

		result[52][76] = true;

		//	7th
		result[53][59] = true;

		result[54][76] = true;

		result[55][64] = true;
		result[56][66] = true;

		result[57][60] = true;
		result[57][62] = true;
		result[57][63] = true;

		result[58][75] = true;
		result[58][61] = true;
		result[58][68] = true;
		result[58][74] = true;

		//	8th
		result[59][76] = true;
		
		for(int i = 64;i <= 67;i++){
			result[60][i] = true;
		}
		
		result[61][71] = true;

		result[62][69] = true;
		result[63][69] = true;

		//	9th
		result[64][70] = true;
		result[65][70] = true;
		result[66][70] = true;

		result[67][71] = true;
		result[67][72] = true;

		result[68][72] = true;

		result[69][76] = true;

		//	10th
		result[70][73] = true;
		result[71][74] = true;
		result[72][74] = true;

		//	11th
		result[73][76] = true;
		
		result[74][75] = true;

		//	12th
		result[75][76] = true;
		
		
		//	Save the results to the file DAGLigo.txt
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
