import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


public class PageRank {			
	class PageNode {		
		String pageLink;
		int inDegree;
		int outDegree;
		double pageRank;
		
		
		PageNode(String pageLink)
		{
			this.pageLink = pageLink;
			outDegree=0;
			inDegree=0;
		}		
		
		void incrementOutDegree()
		{
			outDegree++;
		}
		
		void incrementInDegree()
		{
			inDegree++;
		}
		
		int inDegree()
		{
			return inDegree;
		}
		
		int outDegree()
		{
			return outDegree;
		}
		
		double pageRank()
		{
			return pageRank;
		}
		
		void setPageRank(double pr)
		{
			pageRank = pr;
		}
		
		String name()
		{
			return pageLink;
		}
	}
	
	ArrayList<ArrayList<Integer>> outAdjList;
	ArrayList<PageNode> vPages; 
	ArrayList<Integer> vertexLookup; 
	double[] PRVector;
	String graphFilename;
	int numVertices;
	int numEdges;
	double approx_e;
	private final double BETA = 0.85;
	
	/**
	 * Constructor
	 * @param grapFilename Name of a fle that contains the edges of the graph. 
	 * You may assume that the first line of this graph lists the number of vertices, 
	 * and every line (except frst) lists one edge. You may assume that each vertex is
	 * a wiki url represented as string.
	 * @param d
	 */
	PageRank(String graphFilename, double approx_e)
	{
		this.graphFilename = graphFilename;
		this.approx_e=approx_e;
		
		//create adjacency list
		createOutAdjacencyList();		
	}
	
	/**
	 * Creates the adjacency list of graph
	 */
	private void createOutAdjacencyList() {			
		Scanner sc;
		try {			
			sc = new Scanner(new File(graphFilename));
			if(sc.hasNext())
			{
				numVertices=sc.nextInt();
				vPages = new ArrayList<PageNode>();
				vertexLookup = new ArrayList<Integer>();	
				outAdjList=new ArrayList<ArrayList<Integer>>();				
				for(int i=0; i<numVertices; ++i) {
					outAdjList.add(new ArrayList<Integer>());
				}
			}
			//For every word in the file
			while(sc.hasNextLine())
			{			
				String line = sc.nextLine(); 
				if(line.contains(" ")) {
					String [] lineToks = line.split(" ");
					String node1 = lineToks[0];
					String node2 = lineToks[1];
					numEdges++;
					if(!vertexLookup.contains(node1.hashCode()))
					{
						PageNode node = new PageNode(node1);
						node.incrementOutDegree();
						vPages.add(node);
						vertexLookup.add(node1.hashCode());												
					}
					else
					{
						int nodePos = vertexLookup.indexOf(node1.hashCode());
						vPages.get(nodePos).incrementOutDegree();						
					}
					
					if(!vertexLookup.contains(node2.hashCode()))
					{
						PageNode node = new PageNode(node2);
						node.incrementInDegree();
						vPages.add(node);
						vertexLookup.add(node2.hashCode());						
					}
					else
					{
						int nodePos = vertexLookup.indexOf(node2.hashCode());
						vPages.get(nodePos).incrementInDegree();						
					}
					int posVertex = vertexLookup.indexOf(node1.hashCode());
					outAdjList.get(posVertex).add(node2.hashCode());
				}
			}	
			sc.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for(int i=0; i<vPages.size();++i) {
			PageNode v = vPages.get(i);
			//System.out.println(v.name()+"|"+v.inDegree()+"|"+v.outDegree());
		}
		System.out.println("Graph |V|="+numVertices+" |E|="+numEdges);
	}

	/**
	 * Gets name of vertex of the graph as parameter and returns
	 * its page rank
	 * @param vertex name of vertex
	 * @return page rank
	 */
	double pageRankOf(String vertex)
	{
		int nodeIndex = vertexLookup.indexOf(vertex.hashCode());
		return PRVector[nodeIndex];
		
	}
	
	/**
	 * The page rank algorithm	 
	 * @return page rank
	 */
	double[] pageRankVector()
	{
		//Initialize P_0
		double[] P = new double[numVertices];
		double[] P_prev ;
		int steps=0;
		final double initVal = (double)1/(double)numVertices;
		for(int i=0; i<numVertices; ++i)
		{
			P[i]=initVal;
		}
		do{
			steps++;
			P_prev = P;
			P = computePR(P_prev);			
		} while(!Converged(P_prev,P,approx_e));
		PRVector = P;
		System.out.println("PR algorithm steps:"+steps);
		return P;
	}
	
	/**
	 * Computes new page rank vector with previous one
	 * @param p_prev The page rank vector from step n
	 * @return The page rank vector for step n+1
	 */
	private double[] computePR(double[] P_prev) {
		double[] P = new double[numVertices];
		final double initVal = (double)(1-BETA)/(double)numVertices;
		for(int i=0; i<numVertices; ++i)
		{
			P[i]=initVal;
		}
		
		for(int i=0; i<numVertices; ++i)
		{
			//inAdjList
			PageNode node = vPages.get(i);
			int nodeIndex = vertexLookup.indexOf(node.name().hashCode());
			ArrayList<Integer> outgoingLinks = outAdjList.get(nodeIndex);
			if(outgoingLinks.size()>0) {
				int outDeg = node.outDegree();
				if(outgoingLinks.size()!=node.outDegree())
					System.out.println("ERROR");
				for (int c=0; c<outgoingLinks.size();++c)
				{
					int q = vertexLookup.indexOf(outgoingLinks.get(c));
					P[q]=P[q]+BETA*P_prev[nodeIndex]/outDeg;
				}
			}
			else
			{
				for (int q=0; q<numVertices;++q)
				{					
					P[q]=P[q]+BETA*P_prev[nodeIndex]/numVertices;
				}				
			}
		}
		
		return P;
	}

	/**
	 * Check if the vectors converged or not by checking if L1-norm is <= epsilon
	 * @param p1 Page rank vector from step n
	 * @param p2 Page rank vector from step n+1
	 * @param approx epsilon
	 * @return True if converged else False
	 */
	private boolean Converged(double[] p1, double[] p2, double approx) {
		double nAns = 0.0;
		for(int i=0;i<numVertices;++i) {
			nAns = nAns+Math.abs(p2[i]-p1[i]);
		}
		return nAns<=approx;
	}

	/**
	 * Gets name of vertex of the graph as parameter and returns
	 * its outdegree
	 * @param vertex name of vertex
	 * @return outdegree
	 */
	int outDegreeOf(String vertex)
	{
		int nodePos = vertexLookup.indexOf(vertex.hashCode());
		return vPages.get(nodePos).outDegree;	
	}
	/**
	 * Gets name of vertex of the graph as parameter and returns
	 * its indegree
	 * @param vertex name of vertex
	 * @return indegree
	 */
	int inDegreeOf(String vertex)
	{
		int nodePos = vertexLookup.indexOf(vertex.hashCode());
		return vPages.get(nodePos).inDegree;		
	}
	
	/**
	 * Number of edges of graph
	 * @return edges
	 */
	int numEdges()
	{
		return numEdges;		
	}
	
	/**
	 * Gets an integer k as parameter and returns an array (of 
	 * strings) of pages with top k page ranks
	 * @param K integer k
	 * @return pages with top k page ranks
	 */
	String[] topKPageRank(int K)
	{
		double PR[] = pageRankVector();
		
		for(int i=0;i<numVertices-1;++i)
		{
			vPages.get(i).setPageRank(PR[i]);
		}
		ArrayList<PageNode> prPages = new ArrayList<PageNode>(vPages);
		for(int i=0;i<numVertices-1;++i)
		{
			for(int j=i+1;j<numVertices;++j){
				if(prPages.get(i).pageRank()<prPages.get(j).pageRank()) {
					PageNode temp = prPages.get(j);
					prPages.set(j, prPages.get(i));
					prPages.set(i, temp);
				}
			}
		}
		String[] output = new String[K];
		for(int i=0; i<K; ++i)
		{
			output[i] = prPages.get(i).name();
		}
		
		return output;				
	}
	
	/**
	 * A method named topKInDegree that gets an integer k as parameter and returns an array (of 
	 * strings) of pages with top k in degree
	 * @param K integer k
	 * @return pages with top k in degree
	 */
	String[] topKInDegree(int K)
	{
		ArrayList<PageNode> inDegPages = new ArrayList<PageNode>(vPages);
		for(int i=0;i<numVertices-1;++i)
		{
			for(int j=i+1;j<numVertices;++j){
				if(inDegPages.get(i).inDegree<inDegPages.get(j).inDegree) {
					PageNode temp = inDegPages.get(j);
					inDegPages.set(j, inDegPages.get(i));
					inDegPages.set(i, temp);
				}
			}
		}
		String[] output = new String[K];
		for(int i=0; i<K; ++i)
		{
			output[i] = inDegPages.get(i).name();
		}
		
		return output;	
		
	}
	
	/**
	 * A method named topKInDegree that gets an integer k as parameter and returns an array (of 
	 * strings) of pages with top k out degree
	 * @param K integer k
	 * @return pages with top k out degree
	 */
	String[] topKOutDegree(int K)
	{
		ArrayList<PageNode> outDegPages = new ArrayList<PageNode>(vPages);
		for(int i=0;i<numVertices-1;++i)
		{
			for(int j=i+1;j<numVertices;++j){
				if(outDegPages.get(i).outDegree<outDegPages.get(j).outDegree) {
					PageNode temp = outDegPages.get(j);
					outDegPages.set(j, outDegPages.get(i));
					outDegPages.set(i, temp);
				}
			}
		}
		String[] output = new String[K];
		for(int i=0; i<K; ++i)
		{
			output[i] = outDegPages.get(i).name();
		}
		
		return output;		
	}

}
