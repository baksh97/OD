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
	public static void main ( String arguments [])
	{
//		String[] arguments = {"src\\nodes_adjacency5X5","4", "src\\speeds5X5","src\\nodes_adjacency_new.part.10"};
		String nodes_adjacencyFileName = arguments[0];
		int parts = Integer.parseInt(arguments[1]);
		String speedsFileName = arguments[2];
		String clustersFileName="";
		


		BufferedReader br1 = null,br2=null,br3=null,br4=null;
		data r = new data();

		try {
			String s1,s2,s3;
			br1 = new BufferedReader(new FileReader(nodes_adjacencyFileName));	//the nodes and adjacency file
			br2 = new BufferedReader(new FileReader(speedsFileName));	//the speeds file
			
			
			br4 = new BufferedReader(new FileReader("config.properties"));		//reading the properties file, config file path will be same
			Properties prop = new Properties();
			prop.load(br4);
			main.storeTimeDiff = Integer.parseInt(prop.getProperty("store_time_diff"));
			

			s1 = br1.readLine().trim();
			String s[]= s1.split(" ");
			int num_nodes = Integer.parseInt(s[0]);
			
			r.setNum_nodes(num_nodes);
			
			s2 = br2.readLine().trim();
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
				s1 = s1.trim();
				
				String[] neighbours_s = s1.split(" ");
				
				nodes[count] = new node();
				
				for(String neighbour: neighbours_s) {
					s2 = br2.readLine().trim();
//					if(s2=="" || s2== null)
//						break;
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
//					nodes[count].addNeighbour(Integer.parseInt(neighbour)-1);
				}
				count++;
			}
			
			
			if(arguments.length == 3){
				
				nodes_adjacencyFileName = nodes_adjacencyFileName+"checked";
				check_directed_nodes_file.checkFile(nodes,nodes_adjacencyFileName);
				
				try{
					System.out.println("running gpmetis");
					Process p = Runtime.getRuntime().exec("gpmetis "+nodes_adjacencyFileName+" "+arguments[1]);
					p.waitFor();
					System.out.println("gpmetis done");
				}
				catch(Exception e){
					System.out.println("error in compiling");
					return;
				}
				clustersFileName = nodes_adjacencyFileName+".part."+arguments[1];
			}
			else{
				clustersFileName = arguments[3];
			}
			
			br3 = new BufferedReader(new FileReader(clustersFileName));	//cluster numbering file

			
			
			for(int i=0;i<num_nodes;i++){
				s3 = br3.readLine().trim();
				int clusternum = Integer.parseInt(s3);
				nodes[i].setClusterNum(clusternum);
			}
			
			
			
			r.setNodes(Arrays.asList(nodes));
			
			System.out.println(r.toString());
			
			main.prepareData(r);		//processes the data to make clusters

			//...

			Scanner in = new Scanner(System.in);
			while(true) {
				System.out.println("input query as <nodeID1 nodeID2 time> or input -1 to exit.");
				int n = in.nextInt();
				if(n==-1)
					break;
				else if(n == 1){
					
					List<node> routeNodes = new ArrayList<>();
					
					while(true){
						int n1 = in.nextInt() - 1;
						if(n1 == -2)
							break;
						else{
							routeNodes.add(nodes[n1]);
						}
					}
					
					double time = in.nextDouble();
					
					System.out.println("time for this route: "+ main.getTimeForRoute(routeNodes,time));
				}
				else {
					int n1 = in.nextInt() - 1;
					int n2 = in.nextInt() - 1;
					
					double currentTime = in.nextDouble();
					
					if(n1 > nodes.length-1 || n2 > nodes.length-1 || n1 < 0 || n2 < 0) {
						System.err.println("input error!!");
					}
					else {

						System.out.println("time by my method:");
						long startTime = System.currentTimeMillis();
						System.out.println(main.findTimeBetweenNodesAtTime(nodes[n1], nodes[n2], currentTime)+ " and route is: " + main.getRoutesBetweenNodesAtTime(nodes[n1], nodes[n2], currentTime).toString());
						long endTime = System.currentTimeMillis();
						System.out.println("\tTook "+(endTime - startTime) + " ms");
						
						System.out.println("actual time:");
						startTime = System.currentTimeMillis();
						System.out.println(actual_time.findTime(nodes[n1], nodes[n2], currentTime, r.getNodes()));
						endTime = System.currentTimeMillis();
						System.out.println("\tTook "+(endTime - startTime) + " ms");
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