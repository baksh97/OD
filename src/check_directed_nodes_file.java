import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.IntStream;

class check_directed_nodes_file {
	
	static void checkFile(node[] nodes,String nodesfileName){
		int num_nodes = nodes.length;
		
		int[] counted_neighbours = new int[num_nodes];
		int[] actual_neighbours = new int[num_nodes];
		
		for(int i=0;i<num_nodes;i++){
			actual_neighbours[i] = nodes[i].getEdges().size();
			for(edge e: nodes[i].getEdges()){
				int otherEnd = e.getEnd();
				if(!nodes[otherEnd].isNeighbour(i))
					counted_neighbours[otherEnd]++;
				counted_neighbours[i]++;
			}
		}
		
		PrintWriter pw;
		try {
			pw = new PrintWriter(nodesfileName);
			pw.write(Integer.toString(num_nodes)+" "+Integer.toString(IntStream.of(counted_neighbours).sum()/2)+"\n");
			
			for(int i=0;i<num_nodes;i++){
				for(edge e: nodes[i].getEdges()){
					int n = e.getEnd();
					pw.write(Integer.toString(n+1)+" ");
				}
				for(int j=0;j<counted_neighbours[i] - actual_neighbours[i];j++){
					pw.write(Integer.toString(i+1)+" ");
				}
				pw.write("\n");
			}
			
			pw.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
