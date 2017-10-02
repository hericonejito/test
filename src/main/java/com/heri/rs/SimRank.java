package com.heri.rs;

import java.util.*;

import javax.swing.text.html.InlineView;

/**
 *  SimRank is an iterative PageRank-like method for computing similarity.
 *  It goes beyond direct cocitation for computing similarity much as PageRank
 *  goes beyond direct linking for computing importance.
 * 
 * @author Bruno Martins
 */
public class SimRank {
	static final double dampFactor = 0.80;
	static final double lamda = 0.50;
	WebGraph graph;
	Map<Integer, HashMap<Integer, Double>> pRankScores = new HashMap<Integer, HashMap<Integer,Double>>();
	public SimRank(WebGraph graph)
	{
		this.graph = graph;
	}
	
public void simrankScores()
{
Map<Integer, HashMap<Integer, Double>> similarityScores = new HashMap<Integer, HashMap<Integer,Double>>();
Map<Integer, HashMap<Integer, Double>> oldSimilarityScores = new HashMap<Integer, HashMap<Integer,Double>>();
int counter = 1;
	
	boolean converge = false;
while(!converge)
{

	for (int i =1; i<= graph.numNodes();i++)
	{
		HashMap <Integer, Double> scores = new HashMap<Integer, Double>();
		
		for (int y=1; y<i;y++)
		{
			
			System.out.println ("twra exetazoume tous komvous "+i+" kai "+y);
			scores.put(y, calculatePrank(i, y, counter));
			
		}
	similarityScores.put(i, scores);		
	}
	for ( int key: similarityScores.keySet())
		System.out.println(similarityScores.get(key));
	if(oldSimilarityScores.equals(similarityScores) || counter == 8)
	{
		converge = true;
	}
	else
	{
		oldSimilarityScores.putAll(similarityScores);
		counter ++;
	}
	System.out.println("O arithmos twn iterations einai "+counter);
}

}


public Double calculateSimrank(int link1, int link2, int counter)
{
	if(counter ==0 || link1 == link2)
	{
		System.out.println("Eimaste ston ypologismo");
		return calculateSimrankScore(link1, link2);
	}
	else if (graph.inLinks(link1).size() == 0 || graph.inLinks(link2).size() == 0)
		return 0.00;
	else
	{
		counter--;
		double value = dampFactor/(graph.inLinks(link1).size() * graph.inLinks(link2).size());
		double sum = 0.00;
		for ( Object inLinksLink1: graph.inLinks(link1).keySet())
		{
		
			for (Object inLinksLink2: graph.inLinks(link2).keySet())
			{
				
				int inlink1 = (int) inLinksLink1;
				//System.out.println("Komvos "+inlink1);
				int inlink2 = (int) inLinksLink2;
				//System.out.println("Komvos "+inlink2);
				sum = sum + calculatePrank(inlink1,inlink2, counter);
			}
		}
		return sum*value;
	}
}

public Double calculatePrank(int link1, int link2, int counter)
{
	double sum = 0.00;
	// first we calculate for the inLinks
	if(counter ==0 || link1 == link2)
		//If we are at the last iteration or if the two nodes are the same
	{
		//System.out.println("Eimaste ston ypologismo");
		sum= calculateSimrankScore(link1, link2);
	}
	//If there is no inLink
	else if (graph.inLinks(link1).size() == 0 || graph.inLinks(link2).size() == 0)
		sum= 0.00;
	else
	{
		
		//First we reduce the counter
		counter--;
		//Next we calculate the first portion of the equation
		double value = dampFactor/(graph.inLinks(link1).size() * graph.inLinks(link2).size());
		//Next we calculate the total similarity
		for ( Object inLinksLink1: graph.inLinks(link1).keySet())
		{
		
			for (Object inLinksLink2: graph.inLinks(link2).keySet())
			{
				
				int inlink1 = (int) inLinksLink1;
				//System.out.println("Komvos "+inlink1);
				int inlink2 = (int) inLinksLink2;
				//System.out.println("Komvos "+inlink2);
				sum = sum + calculatePrank(inlink1,inlink2, counter);
			}
		}
		sum= sum*value;
	}
	
	//Step 2: We calculate ranking for Outlinks
	double sumOutLinks = 0.00;
	if(counter ==0 ||link1 == link2)
	{
		//System.out.println("Eimaste ston ypologismo");
		sumOutLinks= calculateSimrankScore(link1, link2);
	}
	else if (graph.outLinks(link1).size() == 0 || graph.outLinks(link2).size() == 0)
		sumOutLinks= 0.00;
	else
	{
		counter--;
		double value = dampFactor/(graph.outLinks(link1).size() * graph.outLinks(link2).size());
		
		for ( Object outLinksLink1: graph.outLinks(link1).keySet())
		{
		
			for (Object outLinksLink2: graph.outLinks(link2).keySet())
			{
				
				int outlinkA = (int) outLinksLink1;
				//System.out.println("Komvos "+inlink1);
				int outlinkB = (int) outLinksLink2;
				//System.out.println("Komvos "+inlink2);
				sumOutLinks = sumOutLinks + calculatePrank(outlinkA,outlinkB, counter);
			}
		}
		sumOutLinks= sumOutLinks*value;
	}
	return lamda*sum+(1-lamda)*sumOutLinks;
}

private Double calculateSimrankScore(int link1, int link2) {
	if(link1 == link2)
		return 1.00;
	else
		return 0.00;
}


}