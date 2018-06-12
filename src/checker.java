import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class checker
{
	public static void main ( String args [])
	{
		BufferedReader br1 = null,br2=null,br3=null;
		data r = new data();

		try {
			String s1,s2,s3;
			br1 = new BufferedReader(new FileReader("C:\\Users\\E0302940\\eclipse-workspace\\OD\\src\\nodes_adjacency"));	//the nodes and adjacency file
			br2 = new BufferedReader(new FileReader("C:\\Users\\E0302940\\eclipse-workspace\\OD\\src\\speeds"));	//the speeds file
			br3 = new BufferedReader(new FileReader("C:\\Users\\E0302940\\eclipse-workspace\\OD\\src\\clusters"));	//cluster numbering file

			s1 = br1.readLine();
			String s[]= s1.split(" ");
			int num_nodes = Integer.parseInt(s[0]);
			int num_edges = Integer.parseInt(s[1]);
			
			r.setNum_edges(num_edges);
			r.setNum_nodes(num_nodes);
			
			s2 = br2.readLine();
			s = s2.split(" ");
			int num_intervals = Integer.parseInt(s[0]);
			double end_time = Double.parseDouble(s[1]);
			
			r.setEndTime(end_time);
			r.setNum_intervals(num_intervals);
			r.setStartTime(0);
			
			node nodes[] = new node[num_nodes];
			
			int count=0;
			while ((s1 = br1.readLine()) != null) {
				s3 = br3.readLine();
//				System.out.println("s1: "+s1 + ", s3: "+s3);

				int clusternum = Integer.parseInt(s3);
				
				String[] neighbours_s = s1.split(" ");
				
				nodes[count] = new node();
				
				for(String neighbour: neighbours_s) {
					s2 = br2.readLine();
//					System.out.println("s2: "+s2);
					s = s2.split(" ");
					
					double speed[] = new double[num_intervals];
					
					double dist = Double.parseDouble(s[0]);
					for(int i=0;i<num_intervals;i++) {
						speed[i] = Double.parseDouble(s[i+1]);
					}
					
					edge e = new edge();
					e.setStart(count);
					e.setEnd(Integer.parseInt(neighbour));
					e.setDist(dist);
					e.setSpeed(speed);
					
					nodes[count].addEdge(e);
					nodes[count].setClusterNum(clusternum);
				}
				count++;
			}
			
			r.setNodes(Arrays.asList(nodes));
			
			System.out.println(r.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br1 != null)br1.close();
				if (br2 != null)br2.close();
				if (br3 != null)br3.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	
//	public static void main(String args[]) {
//		List<Integer> in = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
//		
//		for(int i: in) {
//			System.out.println(i);
//		}
//	}
}