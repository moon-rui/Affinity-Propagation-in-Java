import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class AP {

	int num = 900;
	int iter = 400;
	double similar[][] = new double[num][num];
	double r[][] = new double[num][num];
	double a[][] = new double[num][num];
	double lambda = 0.9;
	int i = 0;
	int j = 0;
	
	//read data file, initialize similar matrix
	void init(String filepath) throws IOException{
		File file = new File(filepath);
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		
		//read file program may different due to the specific file format
		//1. read ToyProblem's file
		/*while((line=br.readLine()) != null){
			String split[] = line.split("\\s+");
			if(i == j){
				j++;
			}	
			similar[i][j++]= Double.parseDouble(split[2]);
			if(j >= num){
				i++;
				j = 0;
			}
		}*/
		
		br.close();
		isr.close();
		fis.close();
		
	}
	
	//calculate the preference
	void preference(){
		//use the median as preference
		int m = 0;
		int n = num * num;
		double median = 0;
		double list[] = new double[n];
		
		//find the median
		for(int i=0; i<num; i++){
			for(int j=0; j<num; j++){
				list[m++]=similar[i][j];
			}
		}
		Arrays.sort(list);
		if(n%2 == 0){
			median = (list[n/2] + list[n/2-1]) / 2;
		}
		else{
			median = list[n/2];
		}
		for(int i=0; i<num; i++){
			similar[i][i] = median;
		}
		
	}
	
	//get preference by the preference file
	void readpreffile(String filepath) throws IOException{
		File file = new File(filepath);
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		int i = 0;
		
		while((line=br.readLine()) != null){
			similar[i][i] = Double.parseDouble(line);
			i++;
		}
		
		br.close();
		isr.close();
		fis.close();
		
	}
	
	void responsi(){
		for(int i=0; i<num; i++){
			for(int k=0; k<num; k++){
				double max = -65535;
				for(int j=0; j<num; j++){
					if(j != k){
						if(max < a[i][j]+similar[i][j]){
							max = a[i][j] + similar[i][j];
						}
					}
				}
				r[i][k] = (1-lambda)*(similar[i][k] - max) + lambda*r[i][k];
			}
		}
	}
	
	void availa(){
		for(int i=0; i<num; i++){
			for(int k=0; k<num; k++){
				if(i == k){
					double sum = 0;
					for(int j=0; j<num; j++){
						if(j != k){
							if(r[j][k] > 0){
								sum += r[j][k];
							}
						}
					}
					a[k][k] = sum;
				}
				else{
					double sum = 0;
					for(int j=0; j<num; j++){
						if(j!=i && j!=k){
							if(r[j][k] > 0){
								sum += r[j][k];
							}
						}
					}
					a[i][k] = (1-lambda)*(r[k][k] + sum) + lambda*a[i][k];
					if(a[i][k] > 0){
						a[i][k] = 0;
					}
				}
			}
		}
	}
	
	void clustering(){
		//iteratively calculate responsibility and availability
		for(int i=0; i<iter; i++){
			responsi();
			availa();
			System.out.println("iter " + i +" complete");
		}
		
		//find the clustering centers
		ArrayList<Integer> exemplar = new ArrayList<Integer>();
		for(int k=0; k<num; k++){
			if(r[k][k]+a[k][k] > 0){
				exemplar.add(k);
			}
		}
		
		//data point assignment
		int idx[] = new int[num];
		for(int i=0; i<num; i++){
			double max = -65535;
			int index = 0;
			for(int j=0; j<exemplar.size(); j++){
				int k = exemplar.get(j);
				if(max < similar[i][k]){
					max = similar[i][k];
					index = k;
				}
			}
			idx[i] = index;
		}
		
		//result output
		for(int i=0; i<num; i++){
			System.out.println(idx[i]+1);
		}
	}

}
