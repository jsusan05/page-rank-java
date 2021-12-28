//===========================================================================//
// Filename: MinHash.java
// Description: Computes Jaccards Similarity of two files
//				Computes Min-Hash Matrix of document collection
// Author: jsusan@iastate.edu for COMS-535X
//===========================================================================//

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class JaccardSimilarity {			 
	private int numTerms;
	private int[] allTermsM;
	private String[] links1;
	private String[] links2;
	
	/**
	 * Constructor
	 * @param folder the name of a folder containing our document collection for which we wish to construct MinHash matrix
	 * @param numPermutations the number of permutations to be used in creating the MinHash matrix
	 */
	JaccardSimilarity(String[] links1, String[] links2)
	{
		this.links1=links1;
		this.links2=links2;
		
		//Prepare the term vector
		collectTerms();		
	}

	/**
	 * Creates an array with the term ids of document
	 * @param fileName Filename
	 * @return term vector
	 */
	private int[] createDocVector(String[] wikiLinks) {	    	    
	    Set<Integer> docTerms = new HashSet<Integer>();	
	  //For every pair of file in the folder	    
	  	for(int d=0; d<wikiLinks.length; ++d) {
	  		int index = Arrays.binarySearch(allTermsM, wikiLinks[d].hashCode());
			if(index>=0) 
			{
				docTerms.add(index);
			}			     
	  	}
		int[] docVector = new int[docTerms.size()];
		int i = 0;
		for (Integer val : docTerms) docVector[i++] = val.intValue();
		return docVector;				
	}			
		
	/**
	 * Get names of two files (in the document collection) and returns the exact Jaccard Similarity
	 * @param file1 Name of file in document collection
	 * @param file2 Name of file in document collection
	 * @return The exact Jaccard Similarity of the files
	 */
	float exactJaccard()
	{
		int[] docVector1 = createDocVector(links1);
		int[] docVector2 = createDocVector(links2);
		Arrays.sort(docVector1);
		Arrays.sort(docVector2);
		int intSize=0;
		for(int i=0; i<docVector1.length; ++i){
			if (Arrays.binarySearch(docVector2,docVector1[i])>=0) {
				intSize++;
			}
		}
		float exactJac= (float)intSize/(float)(docVector1.length+docVector2.length-intSize);
		return exactJac;
		
	}
		
	/**
	 * Returns the number of terms in the document collection
	 * @return number of terms
	 */
	int numTerms()
	{
		return numTerms;		
	}
	
	/**
	 * Compute the terms and assign an integer to it
	 */
	private void collectTerms()
	{					
		ArrayList<String> allLinks = new ArrayList<String>(links1.length + links2.length);
	    Collections.addAll(allLinks, links1);
	    Collections.addAll(allLinks, links2);	    
	    
		int i=0;
		//Populate doc names		
		Set<Integer> UnionAllTerms = new HashSet<Integer>();		
	
		//For every pair of file in the folder	    
		for(int d=0; d<allLinks.size(); ++d) {
			UnionAllTerms.add(allLinks.get(d).hashCode());			      
		}
		
		//Get Numterms
		numTerms = UnionAllTerms.size();		
		
		//Arrange terms according to order of hashcode
		allTermsM = new int[numTerms];
		i = 0;
		for (Integer val : UnionAllTerms) allTermsM[i++] = val;
		Arrays.sort(allTermsM);
	}
	
}
