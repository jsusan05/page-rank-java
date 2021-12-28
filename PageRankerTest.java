//=============================================================================
// Name: PageRankerTest.java
// Description: Test class for page ranking algorithm
// Author: jsusan@iastate.edu for COMS-535X
//=============================================================================

public class PageRankerTest {
	/**
	 * Output pages with highest page rank, highest in-degree and highest
	 * out-degree. Compute the following sets: Top 100 pages as per page rank, 
	 * top 100 pages as per in-degree and top 100 pages as per out degree. 
	 * For each pair of the sets, compute Jaccard Similarity
	 * @param graphFile
	 * @param approx_e
	 * @param K
	 */
	static void test_1(String graphFile, double approx_e, int K)
	{
		System.out.println("---PAGE RANK on "+graphFile+" approx_e="+approx_e);
		PageRank pr = new PageRank(graphFile, approx_e);		
		String[] topKPagesInDeg = pr.topKInDegree(K);				
		//==============================================================//
		System.out.println("---TOP-K INDEGREE PAGES------");
		System.out.println("//PAGES:");
		for(int i=0;i<topKPagesInDeg.length;++i)
		{
			System.out.println(topKPagesInDeg[i]);
			break;
		}
		
		//==============================================================//
		String[] topKPagesOutDeg = pr.topKOutDegree(K);
		System.out.println("---TOP-K OUTDEGREE PAGES------");		
		System.out.println("//PAGES:");
		for(int i=0;i<topKPagesOutDeg.length;++i)
		{
			System.out.println(topKPagesOutDeg[i]);
			break;
		}
		
		//==============================================================//
		String[] topKPagesRank = pr.topKPageRank(K);	
		System.out.println("---TOP-K PAGERANK PAGES------");	
		System.out.println("//PAGES:");
		for(int i=0;i<topKPagesRank.length;++i)
		{
			System.out.println(topKPagesRank[i]);
			break;
		}

		//Compute Jaccard Similarity
		JaccardSimilarity js = new JaccardSimilarity(topKPagesInDeg,topKPagesOutDeg);
		System.out.println("JacSim(In_K, Out_K)="+js.exactJaccard());
		
	    js = new JaccardSimilarity(topKPagesInDeg,topKPagesRank);
		System.out.println("JacSim(In_K, PR_K)="+js.exactJaccard());
		
		js = new JaccardSimilarity(topKPagesOutDeg,topKPagesRank);
		System.out.println("JacSim(Out_K, PR_K)="+js.exactJaccard());
	}
}
