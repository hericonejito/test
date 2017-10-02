package com.heri.rs;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;




public class KroneckerProduct {
	RealMatrix sp;
	RealMatrix qr;
	
	public KroneckerProduct(RealMatrix m1, RealMatrix m2)
	{
		sp=m1;//We get the matrixes we want to calculate;
		
		qr=m2;
	}
	
	public RealMatrix calculateProduct()
	{
		RealMatrix m3;
		double [][] matrixData = new double[sp.getRowDimension()*qr.getRowDimension()] [sp.getColumnDimension()*qr.getColumnDimension()];
		for (int i=0;i<sp.getRowDimension()*qr.getRowDimension();i++)
			for(int y=0;y<sp.getColumnDimension()*qr.getColumnDimension();y++)
			{
				//The kronecker product results in multiplying each element of the first matrix with the second matrix
				matrixData[i][y]=sp.getEntry(i/sp.getRowDimension(), y/sp.getColumnDimension())*qr.getEntry(i%qr.getRowDimension(), y%qr.getColumnDimension());
			}
		
		
		m3 = MatrixUtils.createRealMatrix(matrixData);
		
		return m3;
	}
	public RealMatrix vectorization(RealMatrix matrixToVectorise)
	//Function for vectorising a matrix
	{
		double [][] vectorData = new double[matrixToVectorise.getColumnDimension()*matrixToVectorise.getRowDimension()][1];
		int counter =0;
		for(int i =0; i<matrixToVectorise.getRowDimension();i++)
			for(int y=0;y<matrixToVectorise.getColumnDimension();y++)
			{
				vectorData[counter][0] = matrixToVectorise.getEntry(i, y);
			counter++;
			}
		RealMatrix identityVector = MatrixUtils.createRealMatrix(vectorData);
		return identityVector;
	}
	
	public RealMatrix deVectorization(RealMatrix matrixToDevectorise )
	//Function for devectorising a matrix
	{
		RealMatrix returnedMatrix;
		double matrixSize = matrixToDevectorise.getColumnDimension()*matrixToDevectorise.getRowDimension();
		matrixSize = Math.sqrt(matrixSize);
		double [][] matrixData = new double [(int) matrixSize][(int) matrixSize];
		for(int i =0; i<matrixToDevectorise.getColumnDimension()*matrixToDevectorise.getRowDimension();i++)
			
		{
			matrixData[(int) (i/matrixSize)][(int) (i%matrixSize)]=matrixToDevectorise.getEntry(i, 0);
		}
		returnedMatrix = MatrixUtils.createRealMatrix(matrixData);
		return returnedMatrix;
	}
}
