package com.heri.rs;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.MathUtils;


public class stergios {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebGraph wg = new WebGraph();
	
		
		wg.addLink("C", "m1", 1.00);

		wg.addLink("C", "m2", 1.00);

		wg.addLink("C", "m3", 1.00);

		wg.addLink("m1", "p1", 1.00);

		wg.addLink("m2", "p1", 1.00);

		wg.addLink("m2", "p3", 1.00);

		wg.addLink("m2", "p4", 1.00);

		wg.addLink("m3", "p4", 1.00);
		
		wg.addLink("p3", "p4", 1.00);
		
		wg.addLink("p4", "p2", 1.00);
		
	//	wg.addLink("p2", "p1", 1.00);
		
		//wg.addLink("m2", "p2", 1.00);
		
		double[][] matrixData = new double[wg.numNodes()][wg.numNodes()];
		
		
//		matrixData[0][1]=1.00;
//		
//		matrixData[0][2]=0.50;
//		matrixData[1][3]=1.00;
//		matrixData[3][0]=1.00;
//		matrixData[2][4]=1.00;
//		matrixData[4][2]=0.50;
		
		
		for (int i=1; i<=wg.numNodes();i++)
		{
			System.out.println(wg.outLinks(i));
			for(Object outLinksLink1: wg.outLinks(i).keySet())
			{
				int y = (int) outLinksLink1;
				matrixData[i-1][y-1]= 1.00/wg.inLinks(y).size() ;
			}
		}
			
		calculateSimrankMatrices( matrixData, wg);
		
		
		for(int i =1; i<=wg.numNodes();i++)
			System.out.println(i+" : " +wg.IdentifyerToURL(i));
		Map map2 = wg.inLinks(new Integer(1));
		Iterator itr = map2.entrySet().iterator();
		while(itr.hasNext())
			System.out.println(itr.next().toString());
		
	SimRank sm = new SimRank(wg);
	sm.simrankScores();
	//sm.calculateOutLinks();
	
	
	//System.out.println(sm.simRank("M", "m1"));
		//for(int i=0; i<wg.numNodes();i++)
			//System.out.println(sm.simRank(link));
		
	}
	public static void calculateSimrankMatrices(double[][] matrixData,WebGraph wg)
	{RealMatrix I = MatrixUtils.createRealIdentityMatrix(wg.numNodes()*wg.numNodes());
	RealMatrix I2 = MatrixUtils.createRealIdentityMatrix(wg.numNodes());
		RealMatrix W;
		//We create the W matrix which is the transition probability matrix
		W=MatrixUtils.createRealMatrix(matrixData);
		System.out.println(W);
		//We calculate the kronecker product W' x W'
		KroneckerProduct kp=new KroneckerProduct(W.transpose(), W.transpose());
		//Calculate inverseOf[I-c(W' x W')]
RealMatrix pinverse = new LUDecomposition(I.subtract(kp.calculateProduct().scalarMultiply(0.8))).getSolver().getInverse();
RealMatrix S = pinverse.scalarMultiply(0.2).multiply(kp.vectorization(I2));		
		System.out.println(S);
S=kp.deVectorization(pinverse.scalarMultiply(0.2).multiply(kp.vectorization(I2)).scalarMultiply(5.0));		
		System.out.println(S);
		
	}
}

