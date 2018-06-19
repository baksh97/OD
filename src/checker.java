import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.lang.*;

public class checker
{
//	public static void main ( String args [])
//	{
//		System.out.println(Double.POSITIVE_INFINITY-5);
//	}
	
	public static void main ( String args [])
	{
		
		// String args[] = {"src\\nodes_adjacency","3", "src\\speeds"};
		String nodes_adjacencyFileName = args[0];
		int parts = Integer.parseInt(args[1]);
		String speedsFileName = args[2];
		try{
			Process p = Runtime.getRuntime().exec("gpmetis "+nodes_adjacencyFileName+" "+args[1]);
			p.waitFor();
		}
		catch(Exception e){
			System.err.println("error in compiling");
		}

		String clustersFileName = nodes_adjacencyFileName+".part."+args[1];


		BufferedReader br1 = null,br2=null,br3=null,br4=null;
		data r = new data();

		try {
			String s1,s2,s3;
			br1 = new BufferedReader(new FileReader(nodes_adjacencyFileName));	//the nodes and adjacency file
			br2 = new BufferedReader(new FileReader(speedsFileName));	//the speeds file
			br3 = new BufferedReader(new FileReader(clustersFileName));	//cluster numbering file
			
			
			br4 = new BufferedReader(new FileReader("config.properties"));		//reading the properties file, config file path will be same
			Properties prop = new Properties();
			prop.load(br4);
			main.storeTimeDiff = Integer.parseInt(prop.getProperty("store_time_diff"));
			

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
			
			int num_clusters = parts;
			r.setNum_clusters(num_clusters);
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
					e.setEnd(Integer.parseInt(neighbour)-1);
					e.setDist(dist);
					e.setSpeed(speed);
					
					nodes[count].addEdge(e);
					nodes[count].setId(count);
					nodes[count].setClusterNum(clusternum);
				}
				count++;
			}
			
			r.setNodes(Arrays.asList(nodes));
			
			System.out.println(r.toString());
			
			main.prepareData(r);		//processes the data to make clusters

			//...

			Scanner in = new Scanner(System.in);
//			int num = in.nextInt();
			while(true) {
				System.out.println("input query as <nodeID1 nodeID2 time> or input -1 to exit.");
				int n1 = in.nextInt();
				if(n1==-1)
					break;
				else {
					int n2 = in.nextInt();
					double currentTime = in.nextDouble();
					
					if(n1 > nodes.length-1 || n2 > nodes.length-1 || n1 < 0 || n2 < 0) {
						System.err.println("input error!!");
					}
					else {
						System.out.println("time by my method:");
						System.out.println(main.findTimeBetweenNodesAtTime(nodes[n1], nodes[n2], currentTime));
						
						System.out.println("actual time:");
						System.out.println(actual_time.findTime(nodes[n1], nodes[n2], currentTime, r.getNodes()));
					}
				}
			}
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
}