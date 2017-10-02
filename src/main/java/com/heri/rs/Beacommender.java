package com.heri.rs;

import java.sql.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Servlet implementation class RS_DB_connection
 */
@WebServlet("/RS_DB_connection")
public class Beacommender extends HttpServlet {
	
	 static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  // JDBC Driver name
	   static final String DB_URL = "jdbc:mysql://localhost:3306/upload"; //DB URL in which we are connecting  
  static final String TITLE= "post_title";
	   static final Integer NEIGHBORHOODSIZE = 5;
	   static final Integer MINIMUMRATINGTHRESHOLD = 2;
	   static final String ONOMASYGGRAFEA = "name";
	   static final String USER_ID_NAME = "customer_id";
	   static final String ITEM_ID_NAME = "product_id";
	   static final int NUMOFRECOMMENDATIONS = 3;
	   private int user_id = 0;
	   private int item_id = 0;
	   
	   /**
     * @see HttpServlet#HttpServlet()
     */
   

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

	    PrintWriter out = null; //printerWriter for handling respnses Strings
	    Connection connection = null; //Connection for the DB
	    Statement statement = null; //Statement in which we provide our SQL statement
	    ResultSet rs = null; //It contains the answer from the SQL Query
	   //calculateItemCFCosineSimilarity(connection, statement, out, rs);
	
	
	  }
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("UTF-8");
		 response.setContentType("application/json"); //Response contains JSON
	      response.addHeader("Access-Control-Allow-Origin", "*"); //Allows all connections from various URL's
	    



BufferedReader br = new BufferedReader(new InputStreamReader (request.getInputStream()));

String str = br.readLine();
str=str.trim();

System.out.println(str);

if(str.contains("user_id"))
		{
user_id = Integer.valueOf(str.substring(str.indexOf("user_id =") + 10,str.indexOf(",")));
System.out.println("To user_id einai : "+user_id);
item_id = Integer.valueOf(str.substring(str.lastIndexOf("=") + 2,str.length()));
System.out.println("To item_id einai : "+item_id);
PrintWriter out = null; //printerWriter for handling respnses Strings
		    Connection connection = null; //Connection for the DB
		    Statement statement = null; //Statement in which we provide our SQL statement
		    ResultSet rs = null; //It contains the answer from the SQL Query
		//    String requestPar = request.getParameter("user_id").trim();
		//    int user_id = Integer.valueOf(requestPar);
		
		
		JSONObject jsonReturnedWithBothRS = new JSONObject();
		try {
				jsonReturnedWithBothRS.put("userCF",user_userCollaborativeFilteriing(user_id, out, connection, statement, rs, response));
			jsonReturnedWithBothRS.put("itemCF",item_itemCollaborativeFilteriing(item_id, out, connection, statement, rs, response));
			
		} catch (JSONException e) {
			
		}
		 out = response.getWriter();
		out.print(jsonReturnedWithBothRS);
		  System.out.println(jsonReturnedWithBothRS.toString());
		 out.flush();
		}
else
{
	item_id = Integer.valueOf(str.substring(str.lastIndexOf("=") + 2,str.length()));
	System.out.println("To item_id einai : "+item_id);
	PrintWriter out = null; //printerWriter for handling respnses Strings
			    Connection connection = null; //Connection for the DB
			    Statement statement = null; //Statement in which we provide our SQL statement
			    ResultSet rs = null; //It contains the answer from the SQL Query
			//    String requestPar = request.getParameter("user_id").trim();
			//    int user_id = Integer.valueOf(requestPar);
			
			
			JSONObject jsonReturnedWithBothRS = new JSONObject();
			try {
				jsonReturnedWithBothRS.put("userCF",returnNone(new JSONArray()));
				jsonReturnedWithBothRS.put("itemCF",item_itemCollaborativeFilteriing(item_id, out, connection, statement, rs, response));
				
			} catch (JSONException e) {
				
			}
			 out = response.getWriter();
			out.print(jsonReturnedWithBothRS);
			  System.out.println(jsonReturnedWithBothRS.toString());
			 out.flush();
}

	}

public JSONArray returnNone (JSONArray json)
{
	 json.put("none");
	 
	  
	 
	 return json;
	 
}
public JSONArray user_userCollaborativeFilteriing(int user_id, PrintWriter out, Connection connection, Statement statement, ResultSet rs, HttpServletResponse response) throws IOException
{
	 JSONArray jsonReturned = new JSONArray();
	try {
		
		Map<Integer, Double> recommendedItemCount = new HashMap<Integer, Double>();
		Class.forName(JDBC_DRIVER);

	      connection = DriverManager
	          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
	      statement = connection.createStatement();
		String query="";
		ArrayList<Integer> nearestNeighbors = new ArrayList<>();
	     try{
		nearestNeighbors = findNearestNeighborsPearsonCorrelation(connection, statement, rs, out);
	      //STEP 6 : Check if there is a neighborfood of 1 or 2 and more
	    
		System.out.println("To megethos ths pithanhs geitonias einai "+ nearestNeighbors.size());
	      if (nearestNeighbors.size() >= 2)
	    {
	     query = " ( ";
	    	
	    	for(int counter =0; counter<nearestNeighbors.size() && counter<NEIGHBORHOODSIZE; counter++)
	    	{
	    		
	    		//We get only the first (NeighborfoodSize)
	    		
	    		
	    	query = query + "" + USER_ID_NAME + " = "  + nearestNeighbors.get(counter) + " OR ";
	    	
	    	
	    		
	    	}
	    	
	    	query = query.substring(0, query.length() - 3) + " )";
	    	System.out.println(query);
	    	System.out.println("SELECT "+ITEM_ID_NAME+", COUNT(*) as plithos FROM rs_review WHERE " + query +" AND "+ITEM_ID_NAME+" NOT IN ( SELECT "+ITEM_ID_NAME+" FROM rs_review WHERE " + USER_ID_NAME + " = " + user_id + " ) GROUP BY "+ITEM_ID_NAME+"");
	    	rs = statement.executeQuery("SELECT "+ITEM_ID_NAME+", COUNT(*) as plithos FROM rs_review WHERE " + query +" AND "+ITEM_ID_NAME+" NOT IN ( SELECT "+ITEM_ID_NAME+" FROM rs_review WHERE " + USER_ID_NAME + " = " + user_id + " ) GROUP BY "+ITEM_ID_NAME+"");
	    	
	    	while(rs.next())
	    	{
	    		//STEP 7 : We create an array with all recommendations and their
	    		// neighborhood count (max = NEIGHBORHOOD SIZE)
	  
	    	recommendedItemCount.put(rs.getInt(""+ITEM_ID_NAME+""), rs.getDouble("plithos"));
	    	}
	    }
	      
	    else 
	    	
	    	//If there is only 1 neighbor
	    {
	    	
	    		rs = statement.executeQuery("SELECT "+ITEM_ID_NAME+", COUNT(*) as plithos FROM rs_review WHERE " + USER_ID_NAME + " = " + nearestNeighbors.get(0) + " AND "+ITEM_ID_NAME+" NOT IN ( SELECT "+ITEM_ID_NAME+" FROM rs_review WHERE " + USER_ID_NAME + " = " + user_id + " ) GROUP BY "+ITEM_ID_NAME+"");
		    while(rs.next())
		    {
		    	System.out.println("Plithos = " +rs.getInt("plithos"));
		    	recommendedItemCount.put(rs.getInt(""+ITEM_ID_NAME+""), rs.getDouble("plithos"));
		    }
	    		}
	    
	  // System.out.println(" THa preepei na doume to megethos "+ recommendedItemCount.size());
	   //STEP 8 : We sort descending the recommendations so as to propose first the 
	   // top commonest of the neighborhood
	   recommendedItemCount = SortHashMap.sortByComparator(recommendedItemCount);
	   for(int key : recommendedItemCount.keySet())
	   {
		   System.out.println("To id "+key +" emfanizetai "+ recommendedItemCount.get(key) +" fores");
	   }
	   //We check if there are any valid recommendations 
	   
	   if(recommendedItemCount.size() >= 1)
	     {
	      int count =0; 
	     query="";
		   for (int key : recommendedItemCount.keySet())
		   {
			   //if the item currently viewing is on the recommendation list, remove it
			   if(key == item_id)
			   {}
			   else if(count>NUMOFRECOMMENDATIONS)
			   {break;
			   
			   }
			   else{
			   
	    		  JSONObject json = new JSONObject();
	    		  json.put(""+ITEM_ID_NAME+"", key);
	    		  query=query + " ( "+ key + " , 1 ), ";
	    		  
	    		  jsonReturned.put(json);
	    		  count++;
		   }
		   }
		   query = query.substring(0, query.length() - 2) + " ";
		   statement.executeUpdate("INSERT INTO rs_framework (" + ITEM_ID_NAME+", productRecommendedViewed) VALUES " +query+ " ON DUPLICATE KEY UPDATE  productRecommendedViewed = productRecommendedViewed + 1;");
	    	
	    	
			return jsonReturned;
	    		
		
	}
	   
	   //If there are no valid recs, we return none
	   
	else{
		return returnNone(jsonReturned);
		}
	     }
	     catch (Exception e)
	     {
	    	 e.printStackTrace();
	    	 return returnNone(jsonReturned);
	     }
	} catch (ClassNotFoundException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally {
		if (out != null)
		{
			try {
				out.close();
			}
			catch (Exception e){
			e.printStackTrace();
			}
		}
	    if (rs != null) {
	        try {
	            rs.close();
	        } catch (SQLException e) { /* ignored */}
	    }
	    if (statement != null) {
	        try {
	            statement.close();
	        } catch (SQLException e) { /* ignored */}
	    }
	    if (connection != null) {
	        try {
	            connection.close();
	        } catch (SQLException e) { /* ignored */}
	    }
	}
	return jsonReturned;
}

//We run the item_item CF where user_id becomes product_id 
public JSONArray item_itemCollaborativeFilteriing(int user_id, PrintWriter out, Connection connection, Statement statement, ResultSet rs, HttpServletResponse response) throws IOException
{
	String USER_ID = Beacommender.ITEM_ID_NAME;
	String ITEM_ID = Beacommender.USER_ID_NAME;
	
	JSONArray jsonReturned = new JSONArray();
	try {
		
		
		Class.forName(JDBC_DRIVER);

	      connection = DriverManager
	          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
	      statement = connection.createStatement();
	      ArrayList<Integer> nearestNeighbors = new ArrayList<>();
	     
	      
	     try{
	    	 nearestNeighbors=calculateItemCFSimrankSimilarity(connection, statement, out, rs, ITEM_ID_NAME, USER_ID_NAME, item_id, this.user_id);
	    			 //findNearestNeighborsJaccardSimulation(connection, statement, rs, out, USER_ID, ITEM_ID,this.item_id, this.user_id);
	      //STEP 6 : Check if there is a neighborfood of 1 or 2 and more
	      System.out.println("To megethos ths pithanhs geitonias einai "+ nearestNeighbors.size());
	      if (nearestNeighbors.size() > 0)
	    {
	    	  String query="";
	    	  for(int counter = 0;counter<nearestNeighbors.size() && counter<=NUMOFRECOMMENDATIONS; counter++)
	    	  {
	    		  
				   				  
		    		  JSONObject json = new JSONObject();
		    		  json.put(""+USER_ID+"", nearestNeighbors.get(counter));
		    		  jsonReturned.put(json);
		    		  query=query + " ( "+ nearestNeighbors.get(counter) + " , 1 ), ";
	    	  }
	    	
	    	  query = query.substring(0, query.length() - 2) + " ";
	    	   statement.executeUpdate("INSERT INTO rs_framework (" + ITEM_ID_NAME+", productRecommendedViewed) VALUES " +query+ " ON DUPLICATE KEY UPDATE  productRecommendedViewed = productRecommendedViewed + 1;");
	    	
	    	
	    }
	    else 
	    	
	    	
	    {
	    	return returnNone(jsonReturned);
	    
	    }
	      System.out.println(jsonReturned);
	      return jsonReturned;
			
	      
	     
	      
	      
	      
	      
	     
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	finally {
		if (out != null)
		{
			try {
				out.close();
			}
			catch (Exception e){
			e.printStackTrace();
			}
		}
	    if (rs != null) {
	        try {
	            rs.close();
	        } catch (SQLException e) { /* ignored */}
	    }
	    if (statement != null) {
	        try {
	            statement.close();
	        } catch (SQLException e) { /* ignored */}
	    }
	    if (connection != null) {
	        try {
	            connection.close();
	        } catch (SQLException e) { /* ignored */}
	    }
	}
	
	return jsonReturned;
}

public double calculatePearsonSimilarity(double[] v1, double[] v2 )
{
PearsonsCorrelation pc = new PearsonsCorrelation();
double v3 = pc.correlation(v1, v2);
return v3;
	
}
private ArrayList<Integer> findNearestNeighborsPearsonCorrelation(Connection connection, Statement statement, ResultSet rs, PrintWriter out) {
	try {
		 TreeMap<Integer, Double> itemsPearsonsCorrelation = new TreeMap<Integer, Double>();	
		Map<Integer, Double> neighborfoodSimilarity = new HashMap<Integer, Double>();
		
		Class.forName(JDBC_DRIVER);

	      connection = DriverManager
	          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
	      statement = connection.createStatement();

	     
	      
	      /* Step 1:
	       * Calculating current user's objects that has rated positevely >3
	       */
	      rs = statement.executeQuery("SELECT DISTINCT "+ITEM_ID_NAME+", rating from rs_review where "+USER_ID_NAME+" = "+user_id+" GROUP BY " +ITEM_ID_NAME);
	     
	      while (rs.next()) 
	    	  /*
	    	   * Creating an array for holding items preferences
	    	   */
	      {
	  itemsPearsonsCorrelation.put(rs.getInt(ITEM_ID_NAME), (double) rs.getInt("rating"));
	//  System.out.println (" O xrhsths protimaei to " +rs.getInt(ITEM_ID) + " me rating : " + (double) rs.getInt("rating"));
	      }
	      if(itemsPearsonsCorrelation.size()<=1)
	      {return null;}
	      else
	      {
	      String query = " AND ( ";
	      for (Integer key : itemsPearsonsCorrelation.keySet())
	      {
	    	  query= query + " rs_review."+ITEM_ID_NAME+" = " + key +" OR";
	   	  
	      }
	      query=query.substring(0, query.length()-3) + ")";
	      rs = statement.executeQuery("SELECT DISTINCT * FROM rs_review WHERE " + USER_ID_NAME + " <> " + user_id + " " + query + "AND " + USER_ID_NAME + " in (SELECT " + USER_ID_NAME + " from rs_review where " + USER_ID_NAME + " <> " + user_id +" and "+ITEM_ID_NAME+" not in (SELECT "+ITEM_ID_NAME+" from rs_review where " + USER_ID_NAME + " = " + user_id +")) GROUP BY "+USER_ID_NAME+" ");
	      System.out.println("SELECT DISTINCT * FROM rs_review WHERE " + USER_ID_NAME + " <> " + user_id + " " + query + " AND " + USER_ID_NAME + " in (SELECT " + USER_ID_NAME + " from rs_review where " + USER_ID_NAME + " <> " + user_id +" and "+ITEM_ID_NAME+" not in (SELECT "+ITEM_ID_NAME+" from rs_review where " + USER_ID_NAME + " = " + user_id +")) ");
	      while(rs.next())
	      {
	    	  
//	    	  query = "AND ( ";
//	    	  for (Integer key : itemsPearsonsCorrelation.keySet())
//		      {
//		    	query = query + ITEM_ID + " = " + key + " OR ";
//		    	 tmTest.put(key, 0.00);
//		      }
	    	  ResultSet rs1 = null;
	    	  Statement statement1 = null;
	    	  statement1 = connection.createStatement();
	    	 
	    	  rs1 = statement1.executeQuery("Select DISTINCT "+ITEM_ID_NAME+" FROM rs_review WHERE "+USER_ID_NAME+" = "+ user_id + " AND "+ ITEM_ID_NAME+" in (SELECT "+ITEM_ID_NAME+" from rs_review where "+USER_ID_NAME+" = "+rs.getInt(USER_ID_NAME)+" ) GROUP BY "+ITEM_ID_NAME);
	    	
	    	  query = " ( ";//We store here the id's of corrated items to run the next query to database for ratings
	    	  int arraySize=0;//We use this counter to count the size of corrated items array
	    	  while (rs1.next())
	    	  {
	    	  query = query + ITEM_ID_NAME+ " = "+ rs1.getInt(ITEM_ID_NAME)+ " OR ";
	    	  arraySize++;
	    	  }
	    	  if(arraySize>1)
	    	  {
	    	  query=query.substring(0, query.length()-3)+ " ) ";
	    	
	    	  //This query gets the ratings from the corrated items for the current user
	    	  rs1 = statement1.executeQuery("Select DISTINCT rating, "+ITEM_ID_NAME+" FROM rs_review WHERE "+ query+ " AND "+ USER_ID_NAME+ " = "+ user_id );
	    	 
	    	  double[] v1 = new double[arraySize];//We store here the ratings of current user for corrated items
		    	
	    	  double[] v2 = new double[arraySize];//we store here the ratings of similar user
	    	 int counter = 0;
	    	  while(rs1.next())
	    	  {
	    		  v1[counter] = (double) rs1.getInt("rating");
	    		  counter++;
	    	  }
	    	  
	    	
	    	//This query gets the ratings from the corrated items for the similar user
	    	  rs1 = statement1.executeQuery("Select DISTINCT rating, "+ITEM_ID_NAME+" FROM rs_review WHERE "+ query+ " AND "+ USER_ID_NAME+ " = "+ rs.getInt(USER_ID_NAME) );
	    	  counter =0;
	    	  while(rs1.next())
	    	  {
	    		  v2[counter] = (double) rs1.getInt("rating");
	    		  counter++;
	    	  }
	    	  
	    	 
	    	
	    	 Double similarity =calculatePearsonSimilarity(v1, v2);
	    	 
	    	 if(!similarity.isNaN())
	    	  neighborfoodSimilarity.put(rs.getInt(USER_ID_NAME), calculatePearsonSimilarity(v1, v2));
	    	  }
	      }
	      for (int key : neighborfoodSimilarity.keySet() )
	      {
	    	  System.out.println("H Omoiothta me to xrhsth "+key+ " einai "+neighborfoodSimilarity.get(key)); 
	      }
	      ArrayList<Integer> neighborfoodCutomers = new ArrayList<Integer>(); 
	      
	     neighborfoodSimilarity = SortHashMap.sortByComparator(neighborfoodSimilarity);
	     
	     for (int key : neighborfoodSimilarity.keySet() )
	      {
	    	  System.out.println("H Omoiothta me to xrhsth "+key+ " einai "+neighborfoodSimilarity.get(key)); 
	      }
	      int i =0;
	      for(Iterator<Entry<Integer, Double>> it = neighborfoodSimilarity.entrySet().iterator(); it.hasNext() && i<NEIGHBORHOODSIZE;) {
	    	  Entry<Integer, Double> neighbor = it.next();
	    	 
	    	 neighborfoodCutomers.add(neighbor.getKey());
	    	  i++;
	    	}
	      for(int x=0; x<neighborfoodCutomers.size();x++)
	    	  System.out.println("Sth geitonia yparxoun oi :"+neighborfoodCutomers.get(x));
		return neighborfoodCutomers;}
	} catch (ClassNotFoundException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	finally {
		
	}
	return null;
	
}

private ArrayList<Integer> findNearestNeighborsJaccardSimulation(Connection connection, Statement statement, ResultSet rs, PrintWriter out, String user_id_name, String item_id_name, int user_id_value, int item_id_value)
{
	
		try {
			ArrayList<Integer > itemsPreferred = new ArrayList<Integer>();
			Map<Integer, Double> personSimilarity = new HashMap<Integer, Double>();
			ArrayList<Integer> nearestNeighbors = new ArrayList<>();
			Class.forName(JDBC_DRIVER);

		      connection = DriverManager
		          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
		      statement = connection.createStatement();

		     
		     
		      /* Step 1:
		       * Calculating current user's objects that has rated positevely >3
		       */
		      rs = statement.executeQuery("SELECT DISTINCT * FROM rs_review WHERE rating > 3 and " + user_id_name + " = " + user_id_value + " GROUP BY "+item_id_name+"" );
		      

		     
		    

		      while (rs.next()) 
		    	  /*
		    	   * Creating an array for holding items preferences
		    	   */
		      {
		    	  System.out.println("To "+user_id_value+" protimatai apo tous xrhstes :"+ rs.getInt(item_id_name));
		  itemsPreferred.add(rs.getInt(item_id_name));
		  
		      }
			    
		  /*
		   * If the current user has no preferred items, return none, else continue to Step 2
		   */
		      
		     if(itemsPreferred.size() >= MINIMUMRATINGTHRESHOLD)
		      {
		    	 String query =" AND (";
		    	
		  
		   for (int i=0; i<itemsPreferred.size();i++)
			   query= query + " rs_review."+item_id_name+" = " + itemsPreferred.get(i) +" OR";
		   query=query.substring(0, query.length()-3) + ")";
		   System.out.println(query); 
		   /*
		    * Step 2
		    * Checking similar users that have at least one item preferred as 
		    * current user and at least one item different from current user's
		    * itemset (So he has a potential recommendation)
		    */
		  
		      rs = statement.executeQuery("SELECT * FROM rs_review WHERE rating > 3 AND " + user_id_name + " <> " + user_id_value + " " + query + "AND " + user_id_name + " in (SELECT " + user_id_name + " from rs_review where " + user_id_name + " <> " + user_id_value +" and "+item_id_name+" not in (SELECT "+item_id_name+" from rs_review where " + user_id_name + " = " + user_id_value +")) GROUP BY "+user_id_name );
		      System.out.println("SELECT * FROM rs_review WHERE rating > 3 AND " + user_id_name + " <> " + user_id_value + " " + query + "AND " + user_id_name + " in (SELECT " + user_id_name + " from rs_review where " + user_id_name + " <> " + user_id_value +" and "+item_id_name+" not in (SELECT "+item_id_name+" from rs_review where " + user_id_name + " = " + user_id_value +")) GROUP BY "+user_id_name );
		     
		     
		      while(rs.next())
		      {
		    	  /*
		    	   * Creating hash map for person-similarity matrix
		    	   */
		    	 
		    	personSimilarity.put(rs.getInt("" + user_id_name + ""), 0.00);  
		    	System.out.println(" Omoios xrhsths :" +rs.getInt("" + user_id_name + ""));
		   
		      }
		  	System.out.println(" Plithos omoiwn xrhstwn: " + personSimilarity.size());   
		
		  	//STEP 3 : Similarity Calculation
		      
		      for (Integer key : personSimilarity.keySet())
		      {
		    	  System.out.println("to kleidi einai " + key);	
		    	  Double samePreferences = 0.00;
		    	  Double totalPreferred = 0.00;
		    	  Double similarity = 0.00;
		    	  
		    	  //Getting same preferences from DB
		    	  
		    	  rs = statement.executeQuery("SELECT COUNT(*) as samePreferences FROM rs_review WHERE rating > 3 AND " + user_id_name + " = "+ key + " and  "+item_id_name+" IN (SELECT "+item_id_name+" from rs_review WHERE " + user_id_name + " = "+ user_id_value+" )");
		    	 
		    	if(rs.next())
		    	{
		    	 samePreferences= (double) (rs.getInt("samePreferences"));
		    	}
		    	 System.out.println("Idies protimhseis " +samePreferences + " me ton xrhsth " + key);
		    	 
		    	 //Getting all pair's total preferences, so as to count next SAME/TOTAL -> Jaccard
		    	 
		    	 query = "SELECT DISTINCT "+item_id_name+" FROM rs_review WHERE " + user_id_name + " = " + user_id_value + " OR " + user_id_name + " = " + key;
		    	
		    	 rs = statement.executeQuery(query);
		 
		    	 while(rs.next())
		    	 {
		    		 //Check the various preferences of the pair
		    		 System.out.println("Stis synolikes protimhseis anhkei to " +rs.getInt(""+item_id_name+""));
		    totalPreferred ++;
		    	 }
		    similarity = samePreferences/totalPreferred;
		    System.out.println("Omoiothta me to xrhsth " + key + " : "+similarity);
		   if(similarity == 1.0)
			   personSimilarity.put(key, 0.0);
		   else
		    personSimilarity.put(key, similarity);
		    
		
		      }
		      
		  //STEP 4 : Sort to a descending order so as to be able to get the most similar
		      personSimilarity = SortHashMap.sortByComparator(personSimilarity);
		      for (Integer key : personSimilarity.keySet())
		      {
		    	  
		      System.out.println("Pinakas omoiothtwn \n"+ key +" me omoiothta : " + personSimilarity.get(key));
		      }
		  //STEP 5 :Remove the persons who dont offer to the neighborhood
		     
		      int counter = 0;
		      for (Integer key : personSimilarity.keySet())
		      {
		    	if(counter >= NEIGHBORHOODSIZE )
		    	{
		    		break;
		    	}
		    	else
		    	{
		    		  nearestNeighbors.add(key);
		    		  counter++;
		    	}	  
		      
		      }
		   return nearestNeighbors;
		      
		      
		   //If there are no valid recs, we return none
		
		      }
		      else
		      {
		    	 return nearestNeighbors;
		      }
		      
		      
		      
		      
		     
		    } catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    	out.println("Driver Error");
		    } catch (SQLException e) {
		    	e.printStackTrace();
		      System.out.println("SQLException: " + e.getMessage());
		    } 
		finally {
			
		    
		}
		return null;	
}


public ArrayList<Integer> calculateItemCFCosineSimilarity(Connection connection, Statement statement, PrintWriter out, ResultSet rs,String user_id_name,String item_id_name,int user_id_value,int item_id_value)
{
//	try {
//		ArrayList<Integer > totalCustomersRate = new ArrayList<Integer>();
//		TreeMap<Integer, TreeMap<Integer, Double>> itemCustomerRatings = new TreeMap<Integer, TreeMap<Integer, Double>>();
//		ArrayList<Integer> nearestNeighbors = new ArrayList<>();
//		Class.forName(JDBC_DRIVER);
//
//	      connection = DriverManager
//	          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
//	      statement = connection.createStatement();
//	      statement.executeUpdate("TRUNCATE table rs_itemCosineSimilarity");
//	     
//	     
//	      /* Step 1:
//	       * Calculating current user's objects that has rated positevely >3
//	       */
//	      rs = statement.executeQuery("SELECT DISTINCT customer_id from rs_review");
//	      
//
//	     
//	    
//
//	      while (rs.next()) 
//	    	  /*
//	    	   * Creating an array for holding items preferences
//	    	   */
//	      {
//	  totalCustomersRate.add(rs.getInt(USER_ID_NAME));
//	  System.out.println (" Ston pinaka rs review yparxei o xrhsths " +rs.getInt(USER_ID_NAME));
//	  
//	      }
//		    
//	  /*
//	   * If there are no customers who have rated items in database, break
//	   */
//	      
//	     if(totalCustomersRate.size() > 0)
//	      {
//	    	 
//	   /*
//	    * Step 2
//	    * Get all items who have been rated at least once 
//	    */
//	  
//	      rs = statement.executeQuery("SELECT DISTINCT "+ITEM_ID_NAME+" FROM rs_review"  );
//	
//	     
//	     
//	      while(rs.next())
//	      {
//	    	  /*
//	    	   * Creating hash map for getting ratings from all customers for every item
//	    	   */
//	    	 TreeMap<Integer, Double> ratingsFromCustomers = new TreeMap<Integer, Double>();
//	    	itemCustomerRatings.put(rs.getInt("" + ITEM_ID_NAME + ""), ratingsFromCustomers );
//	    	// We create a treemap for holding ratings from each customer, first assigning 0.00 to all	
//	    	for(int i =0; i<totalCustomersRate.size();i++)
//	    		{
//	    			ratingsFromCustomers.put(totalCustomersRate.get(i), 0.00);
//	    		}
//	    			  ResultSet rs1 = null;
//	    	    	  Statement statement1 = null;
//	    	    	  statement1 = connection.createStatement();
//	    	    	  //We get all ratings, customers pairs and update the values to the treemap
//	    			rs1 = statement1.executeQuery("select "+USER_ID_NAME+", rating from rs_review where product_id = "+rs.getInt(ITEM_ID_NAME)+" GROUP BY customer_id");
//	    			while(rs1.next())
//	    			{
//	    				ratingsFromCustomers.put(rs1.getInt(USER_ID_NAME), (double) rs1.getInt("rating"));
//	    				 System.out.println (" To antikeimeno " +rs.getInt(ITEM_ID_NAME) + " to protimaei o "+rs1.getInt(USER_ID_NAME) + " me rating : "+(double) rs1.getInt("rating"));
//	    			}
//	      }
//	      String query = "";
//	      // We create for each pair of items vectors to be compared using Cosine SImilarity
//	      for (Integer key : itemCustomerRatings.keySet())
//	      {
//	    	
//	    	  ArrayList<Double> v1 = new ArrayList<>();
//	    	 
//	    	  for (Integer key1 : itemCustomerRatings.get(key).keySet())
//	    	  {
//	    		  v1.add(itemCustomerRatings.get(key).get(key1));
//	    		  
//	    		 
////	    		  try{
////	    			  nextKey = itemCustomerRatings.higherKey(key);
////	    		  v2.add(itemCustomerRatings.get(itemCustomerRatings.higherKey(key)).get(key1));
////	    	  }
////	    		 catch (Exception e)
////	    		  {
////	    			 nextKey = itemCustomerRatings.firstKey();
////	    			 v2.add(itemCustomerRatings.get(itemCustomerRatings.firstKey()).get(key1));
////	    		  }
//	    	  }
//	    	  for (Integer key1 : itemCustomerRatings.keySet())
//	    	  {
//	    		  ArrayList<Double> v2 = new ArrayList<>();
//	    		  for (Integer key2 : itemCustomerRatings.get(key1).keySet())
//		    	  {
//	    			  
//	    			  System.out.println("To antikeimeno "+ key1+ " protimate apo to xrhsth "+key2+" me rating "+(itemCustomerRatings.get(key1).get(key2)));
//		    		  v2.add(itemCustomerRatings.get(key1).get(key2));
//		    		  
//		    			 
//		    		  }
//	    		  System.out.println("H omoiothta tou antikeimenoy " +key + " me to antiekimeno "+key1 +" einai "+cosineSimilarity(v1, v2));
//			    			 query=query + "(NULL, "+key+", "+key1+", "+ cosineSimilarity(v1,v2)+ "), "; 
//			    		 
//			    	  
//		    	  }
//	    	  }
//	    	  
//	    	 
//	    	 
//	    	  query = query.substring(0, query.length() - 2);
//	    	  
//	    	  
//	    	  statement.executeUpdate("INSERT INTO `upload`.`rs_itemCosineSimilarity` (`itemCosineSimilarity_ID`, `product_ID`, `similarProduct_ID`, `similarity`) VALUES "+query);
//	      }
//	    
//	
//	 
//	   
//	      
//	      
//	   //If there are no valid recs, we return none
//	
//	      
//	      
//	      
//	      
//	      
//	      
//	     
//	    } catch (ClassNotFoundException e) {
//	    e.printStackTrace();
//	    	out.println("Driver Error");
//	    } catch (SQLException e) {
//	    	e.printStackTrace();
//	      System.out.println("SQLException: " + e.getMessage());
//	    } 
//	finally {
//		
//	    
//	}
	 ArrayList<Integer> neighborfoodCutomers = new ArrayList<Integer>();
	try {
		 TreeMap<Integer, Double> itemsPearsonsCorrelation = new TreeMap<Integer, Double>();	
		Map<Integer, Double> neighborfoodSimilarity = new HashMap<Integer, Double>();
		
		Class.forName(JDBC_DRIVER);


	      connection = DriverManager
	          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
	      statement = connection.createStatement();

	     
	      
	      /* Step 1:
	       * Calculating current user's objects that has rated positevely >3
	       */
	      rs = statement.executeQuery("SELECT DISTINCT "+item_id_name+", rating from rs_review where "+user_id_name+" = "+user_id_value+" AND rating > 3 GROUP BY " +item_id_name);
	     
	      while (rs.next()) 
	    	  /*
	    	   * Creating an array for holding items preferences
	    	   */
	      {
	  itemsPearsonsCorrelation.put(rs.getInt(item_id_name), (double) rs.getInt("rating"));
	//  System.out.println (" O xrhsths protimaei to " +rs.getInt(ITEM_ID) + " me rating : " + (double) rs.getInt("rating"));
	      }
	      if(itemsPearsonsCorrelation.size()<MINIMUMRATINGTHRESHOLD)
	      {return neighborfoodCutomers;}
	      else
	      {
	      String query = " AND ( ";
	      for (Integer key : itemsPearsonsCorrelation.keySet())
	      {
	    	  query= query + " rs_review."+item_id_name+" = " + key +" OR";
	   	  
	      }
	      query=query.substring(0, query.length()-3) + ")";
	      rs = statement.executeQuery("SELECT DISTINCT * FROM rs_review WHERE " + user_id_name + " <> " + user_id_value + " " + query + "AND " + user_id_name + " in (SELECT " + user_id_name + " from rs_review where " + user_id_name + " <> " + user_id_value +" and "+item_id_name+" not in (SELECT "+item_id_name+" from rs_review where " + user_id_name + " = " + user_id_value +")) GROUP BY "+user_id_name+" ");
	      System.out.println("SELECT DISTINCT * FROM rs_review WHERE " + user_id_name + " <> " + user_id_value + " " + query + " AND " + user_id_name + " in (SELECT " + user_id_name + " from rs_review where " + user_id_name + " <> " + user_id_value +" and "+item_id_name+" not in (SELECT "+item_id_name+" from rs_review where " + user_id_name + " = " + user_id_value +")) ");
	      while(rs.next())
	      {
	    	  
//	    	  query = "AND ( ";
//	    	  for (Integer key : itemsPearsonsCorrelation.keySet())
//		      {
//		    	query = query + ITEM_ID + " = " + key + " OR ";
//		    	 tmTest.put(key, 0.00);
//		      }
	    	  ResultSet rs1 = null;
	    	  Statement statement1 = null;
	    	  statement1 = connection.createStatement();
	    	 
	    	  rs1 = statement1.executeQuery("Select DISTINCT "+item_id_name+" FROM rs_review WHERE "+user_id_name+" = "+ user_id_value + " AND "+ item_id_name+" in (SELECT "+item_id_name+" from rs_review where "+user_id_name+" = "+rs.getInt(user_id_name)+" ) GROUP BY "+item_id_name);
	    	
	    	  query = " ( ";//We store here the id's of corrated items to run the next query to database for ratings
	    	  int arraySize=0;//We use this counter to count the size of corrated items array
	    	  while (rs1.next())
	    	  {
	    	  query = query + item_id_name+ " = "+ rs1.getInt(item_id_name)+ " OR ";
	    	  arraySize++;
	    	  }
	    	  if(arraySize>1)
	    	  {
	    	  query=query.substring(0, query.length()-3)+ " ) ";
	    	
	    	  //This query gets the ratings from the corrated items for the current user
	    	  rs1 = statement1.executeQuery("Select DISTINCT rating, "+item_id_name+" FROM rs_review WHERE "+ query+ " AND "+ user_id_name+ " = "+ user_id_value + " GROUP BY "+item_id_name );
	    	 System.out.println(arraySize);
	    	  double[] v1 = new double[arraySize];//We store here the ratings of current user for corrated items
		    	
	    	  double[] v2 = new double[arraySize];//we store here the ratings of similar user
	    	 int counter = 0;
	    	  while(rs1.next())
	    	  {
	    		  v1[counter] = (double) rs1.getInt("rating");
	    		  counter++;
	    	  }
	    	  
	    	
	    	//This query gets the ratings from the corrated items for the similar user
	    	  rs1 = statement1.executeQuery("Select DISTINCT rating, "+item_id_name+" FROM rs_review WHERE "+ query+ " AND "+ user_id_name+ " = "+ rs.getInt(user_id_name)+ " GROUP BY "+item_id_name );
	    	  counter =0;
	    	  while(rs1.next())
	    	  {
	    		  v2[counter] = (double) rs1.getInt("rating");
	    		  counter++;
	    	  }
	    	  
	    	 
	    	
	    	 Double similarity =cosineSimilarity(v1, v2);
	    	 
	    	 if(!similarity.isNaN())
	    	  neighborfoodSimilarity.put(rs.getInt(user_id_name), similarity);
	    	  }
	      }
	     
	      
	      
	     neighborfoodSimilarity = SortHashMap.sortByComparator(neighborfoodSimilarity);
	     
	     for (int key : neighborfoodSimilarity.keySet() )
	      {
	    	  System.out.println("H Omoiothta me to xrhsth "+key+ " einai "+neighborfoodSimilarity.get(key)); 
	      }
	      int i =0;
	      for(Iterator<Entry<Integer, Double>> it = neighborfoodSimilarity.entrySet().iterator(); it.hasNext() && i<NEIGHBORHOODSIZE;) {
	    	  Entry<Integer, Double> neighbor = it.next();
	    	 
	    	 neighborfoodCutomers.add(neighbor.getKey());
	    	  i++;
	    	}
	      for(int x=0; x<neighborfoodCutomers.size();x++)
	    	  System.out.println("Sth geitonia yparxoun oi :"+neighborfoodCutomers.get(x));
		return neighborfoodCutomers;}
	} catch (ClassNotFoundException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	finally {
		return neighborfoodCutomers;	
	}
		
}

public ArrayList<Integer> calculateItemCFSimrankSimilarity(Connection connection, Statement statement, PrintWriter out, ResultSet rs,String user_id_name,String item_id_name,int user_id_value,int item_id_value)
{
	
	
	
	 ArrayList<Integer> neighborfoodCutomers = new ArrayList<Integer>();
	try {
		 TreeMap<Integer, Double> itemsPearsonsCorrelation = new TreeMap<Integer, Double>();	
		Map<Integer, Double> neighborfoodSimilarity = new HashMap<Integer, Double>();
		
		Class.forName(JDBC_DRIVER);


	      connection = DriverManager
	          .getConnection(DB_URL,"root",""); //we provide URL, username and password for connection to DB
	      statement = connection.createStatement();

	     
	      
	      
	      rs = statement.executeQuery("SELECT DISTINCT "+item_id_name+", rating from rs_review where "+user_id_name+" = "+user_id_value+" AND rating > 4 GROUP BY " +item_id_name);
		     
	      while (rs.next()) 
	    	  /*
	    	   * Creating an array for holding items preferences
	    	   */
	      {
	  itemsPearsonsCorrelation.put(rs.getInt(item_id_name), (double) rs.getInt("rating"));
	//  System.out.println (" O xrhsths protimaei to " +rs.getInt(ITEM_ID) + " me rating : " + (double) rs.getInt("rating"));
	      }
	      if(itemsPearsonsCorrelation.size()<MINIMUMRATINGTHRESHOLD)
	      {return neighborfoodCutomers;}
	      else
	      {
	      
	    
	     
	      
	      String query = " AND ( ";
	      for (Integer key : itemsPearsonsCorrelation.keySet())
	      {
	    	  query= query + " rs_review."+item_id_name+" = " + key +" OR";
	   	  
	      }
	      query=query.substring(0, query.length()-3) + ")";
	      System.out.println("SELECT DISTINCT * FROM rs_review WHERE rating > 4 "  + query );
	      
	      rs = statement.executeQuery("SELECT DISTINCT * FROM rs_review WHERE rating > 4 "  + query );
	   
	      WebGraph wg = new WebGraph();
	      while(rs.next())
	      {
	    	  wg.addLink("user_id "+rs.getInt(item_id_name), "item_id "+rs.getInt(user_id_name), 1.00);
	    	  wg.addLink("item_id "+rs.getInt(user_id_name), "user_id "+rs.getInt(item_id_name), 1.00);
//	    	  query = "AND ( ";
//	    	  for (Integer key : itemsPearsonsCorrelation.keySet())
//		      {
//		    	query = query + ITEM_ID + " = " + key + " OR ";
//		    	 tmTest.put(key, 0.00);
//		      }
	      }
	      
	    	  double[][] matrixData = new double[wg.numNodes()][wg.numNodes()];
	    	  for (int i=1; i<=wg.numNodes();i++)
	  		{
	  			
	  			for(Object outLinksLink1: wg.outLinks(i).keySet())
	  			{
	  				int y = (int) outLinksLink1;
	  				matrixData[i-1][y-1]= 1.00/wg.inLinks(y).size() ;
	  			}
	  		}
	    	  //We define the item_id which we need to calculate the similarity measures
	    	  int itemIDIdentifier = 0;
	    	  
	    	  for(int i =1; i<=wg.numNodes();i++)
	    	  {
	    		  if(wg.IdentifyerToURL(i).equals("item_id "+item_id))
	    		  
	    			  itemIDIdentifier=i;
	    	  }
	    		  	
	    	  
	    	  
		      calculateSimrankMatrices( matrixData, wg, itemIDIdentifier, neighborfoodSimilarity);
		      
		      
	    	
	    	  
	      }
	     
	      if(neighborfoodSimilarity.containsKey(item_id))
	      neighborfoodSimilarity.remove(item_id);
	     neighborfoodSimilarity = SortHashMap.sortByComparator(neighborfoodSimilarity);
	     
	     for (int key : neighborfoodSimilarity.keySet() )
	      {
	    	  System.out.println("H Omoiothta me to xrhsth "+key+ " einai "+neighborfoodSimilarity.get(key)); 
	      }
	      int i =0;
	      for(Iterator<Entry<Integer, Double>> it = neighborfoodSimilarity.entrySet().iterator(); it.hasNext() && i<NEIGHBORHOODSIZE;) {
	    	  Entry<Integer, Double> neighbor = it.next();
	    	 
	    	 neighborfoodCutomers.add(neighbor.getKey());
	    	  i++;
	    	}
	      for(int x=0; x<neighborfoodCutomers.size();x++)
	    	  System.out.println("Sth geitonia yparxoun oi :"+neighborfoodCutomers.get(x));
		return neighborfoodCutomers;
	} catch (ClassNotFoundException | SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	finally {
		return neighborfoodCutomers;	
	}
		
}
public static void calculateSimrankMatrices(double[][] matrixData,WebGraph wg, int itemIdentifier, Map<Integer, Double> neighborfoodSimilarity)
{
	
	
	RealMatrix I = MatrixUtils.createRealIdentityMatrix(wg.numNodes()*wg.numNodes());
	
RealMatrix I2 = MatrixUtils.createRealIdentityMatrix(wg.numNodes());
	RealMatrix W;
	//We create the W matrix which is the transition probability matrix
	
	W=MatrixUtils.createRealMatrix(matrixData);
	//System.out.println(W.getColumnDimension());
	
	//We calculate the kronecker product W' x W'
	KroneckerProduct kp=new KroneckerProduct(W.transpose(), W.transpose());
	//Calculate inverseOf[I-c(W' x W')]
RealMatrix pinverse = new LUDecomposition(I.subtract(kp.calculateProduct().scalarMultiply(0.8))).getSolver().getInverse();
RealMatrix S = pinverse.scalarMultiply(0.2).multiply(kp.vectorization(I2));		
	//System.out.println(S);
S=kp.deVectorization(pinverse.scalarMultiply(0.2).multiply(kp.vectorization(I2)));		
	System.out.println("To identifier einai " +(itemIdentifier-1));
	int URLToIdentifier=0;
	
	for(int i=0;i<S.getRow(itemIdentifier-1).length;i++)

	{
		URLToIdentifier = Integer.valueOf(wg.IdentifyerToURL(i+1).substring(8));
		neighborfoodSimilarity.put(URLToIdentifier, S.getRow(itemIdentifier-1)[i]);
		
	}
	
	System.out.println(S);
	
}

public double cosineSimilarity(double[] v1, double[] v2) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < v1.length; i++) {
        dotProduct += v1[i] * v2[i];
     //   System.out.println (dotProduct);
        normA += Math.pow(v1[i], 2);
        normB += Math.pow(v2[i], 2);
    }   
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}
public static class SortHashMap {
	public static Map<Integer, Double> sortByComparator(Map<Integer, Double> personSimilarity) {
		 
		// Convert Map to List
		List<Map.Entry<Integer, Double>> list = 
			new LinkedList<Map.Entry<Integer, Double>>(personSimilarity.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1,
                                           Map.Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}

public class WebGraph {

	/** A Map storing relationships from numeric identifiers to URLs, usefull for storing Web graphs */
	private Map IdentifyerToURL;
	
	/** A Map storing relationships from URLs to numeric identifiers, usefull for storing Web graphs */
	private Map URLToIdentifyer;

	/**
	 *  A Map storing InLinks. For each identifyer (the key), another Map is stored,
	 *  containing for each inlink an associated "connection weight"
	 */
	private Map InLinks;
	
	/**
	 *  A Map storing OutLinks. For each identifyer (the key), another Map is stored,
	 *  containing for each inlink an associated "connection weight"
	 */
	private Map OutLinks;

	/** The number of nodes in the graph */
	private int nodeCount;

	/**
	 * Constructor for WebGraph
     *
	 */
	public WebGraph () {
		IdentifyerToURL = new HashMap();
		URLToIdentifyer = new HashMap();
		InLinks = new HashMap();
		OutLinks = new HashMap();
		nodeCount = 0;
	}

	/**
	 * Constructor for WebGraph, reading data from a text file. Each line of the file
	 * contains an association in the form: 
	 *
	 *    http://url1.com -> http://url2.com 1.0
	 * 
	 * Stating that "http://url1.com" contains an outlink to "http://url2.com", with 
	 * an associated connection strength of 1.0
	 *
	 * @param aux The name of the file
	 * @throws IOException An error occured while reading the file
	 * @throws FileNotFoundException An error occured while reading the file
	 */
	public WebGraph (File file) throws IOException, FileNotFoundException {
		this();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line=reader.readLine())!=null) {
			int index1 = line.indexOf("->");
			if(index1==-1) addLink(line.trim()); else {
				String url1 = line.substring(0,index1).trim();
				String url2 = line.substring(index1+2).trim();
				Double strength = new Double(1.0);
				index1 = url2.indexOf(" ");
				if(index1!=-1) try {
					strength = new Double(url2.substring(index1+1).trim());
					url2 = url2.substring(0,index1).trim(); 
				} catch ( Exception e ) {}
				addLink (url1,url2,strength);
			}
		}
	}
	
	/**
	 * Returns the identifyer associated with a given URL
	 * 
	 * @param URL The URL
	 * @return The identifyer associated with the given URL
	 */
	public Integer URLToIdentifyer ( String URL ) {
		String host;
		String name;
		int index = 0 , index2 = 0;
		if(URL.startsWith("http://")) index = 7; 
		else if(URL.startsWith("ftp://")) index = 6;
		index2 = URL.substring(index).indexOf("/");
		if(index2!=-1) {
			name = URL.substring(index+index2+1);
			host = URL.substring(0,index+index2);
		} else {
			host = URL;
			name = "";
		}
		Map map = (Map)(URLToIdentifyer.get(host));
		if(map==null) return null;
		return (Integer)(map.get(name));
	}

	/**
	 * Returns the URL associated with a given identifyer
	 * 
	 * @param id The identifyer
	 * @return The URL associated with the given identifyer
	 */
	public String IdentifyerToURL ( Integer id ) {
		return (String)(IdentifyerToURL.get(id));
	}
	
	/**
	 *  Adds a node to the graph
	 * 
	 * @param link The URL associated with the added node
	 */
	public void addLink (String link) {
		Integer id = URLToIdentifyer(link);
		if(id==null) {
			id = new Integer(++nodeCount);
			String host;
			String name;
			int index = 0 , index2 = 0;
			if(link.startsWith("http://")) index = 7; 
			else if(link.startsWith("ftp://")) index = 6;
			index2 = link.substring(index).indexOf("/");
			if(index2!=-1) {
				name = link.substring(index+index2+1);
				host = link.substring(0,index+index2);
			} else {
				host = link;
				name = "";
			}
			Map map = (Map)(URLToIdentifyer.get(host));
			if(map==null) map = new HashMap();
			map.put(name,id);
			URLToIdentifyer.put(link,map);
			IdentifyerToURL.put(id,link);
			InLinks.put(id,new HashMap());
			OutLinks.put(id,new HashMap());
		}
	}

	/**
	 * Adds an association between two given nodes in the graph. If the 
	 * corresponding nodes do not exists, this method creates them. If the
	 * connection already exists, the strength value is updated.
	 * 
	 * @param fromLink The URL for the source node in the graph 
	 * @param fromLink The URL for the target node in the graph
	 * @param fromLink The strength to associate with the connection
	 * @return The strength associated with the connection
	 */
	public Double addLink (String fromLink, String toLink, Double weight) {
		addLink(fromLink);
		addLink(toLink);
		Integer id1 = URLToIdentifyer(fromLink);
		Integer id2 = URLToIdentifyer(toLink); 
		return addLink(id1,id2,weight);
	}

	/**
	 * Adds an association between two given nodes in the graph. If the 
	 * corresponding nodes do not exists, this method creates them. If the
	 * connection already exists, the strength value is updated.
	 * 
	 * @param fromLink The identifyer for the source node in the graph 
	 * @param fromLink The identifyer for the target node in the graph
	 * @param fromLink The strength to associate with the connection
	 * @return The strength associated with the connection
	 */
	 Double addLink ( Integer fromLink, Integer toLink, Double weight ) {
		Double aux;
		Map map1 = (Map)(InLinks.get(toLink));
		Map map2 = (Map)(OutLinks.get(fromLink));
		aux = (Double)(map1.get(fromLink));
		if(aux==null) map1.put(fromLink,weight); 
		else if(aux.doubleValue()<weight.doubleValue()) map1.put(fromLink,weight); 
		else weight = new Double(aux.doubleValue());
		aux = (Double)(map2.get(toLink));
		if(aux==null) map2.put(toLink,weight);
		else if(aux.doubleValue()<weight.doubleValue()) map2.put(toLink,weight);
		else {
			weight = new Double(aux.doubleValue());
			map1.put(fromLink,weight);
		}
		InLinks.put(toLink,map1);
		OutLinks.put(fromLink,map2);
		return weight;

	}

	/**
	 * Returns a Map of the nodes that connect to a given
	 * node in the graph. Each mapping contains the identifyer for a node
	 * and the associated connection strength.  
	 * 
	 * @param URL The URL for the node in the graph 
	 * @return A Map of the nodes that connect to the given node in the graph.
	 */
	public Map inLinks ( String URL ) {
		Integer id = URLToIdentifyer(URL);
		return inLinks(id);
	}
	
	/**
	 * Returns a Map of the nodes that connect to a given
	 * node in the graph. Each mapping contains the identifyer for a node
	 * and the associated connection strength.  
	 * 
	 * @param link The identifyer for the node in the graph 
	 * @return A Map of the nodes that connect to the given node in the graph.
	 */
	public Map inLinks ( Integer link ) {
		if(link==null) return new HashMap();
		Map aux = (Map)(InLinks.get(link));
		return (aux == null) ? new HashMap() : aux;
	}

	/**
	 * Returns a Map of the nodes that are connected from a given
	 * node in the graph. Each mapping contains the identifyer for a node
	 * and the associated connection strength.  
	 * 
	 * @param URL The URL for the node in the graph 
	 * @return A Map of the nodes that are connected from the given node in the graph.
	 */
	public Map outLinks ( String URL ) {
		Integer id = URLToIdentifyer(URL);
		return outLinks(id);
	}

	/**
	 * Returns a Map of the nodes that are connected from a given
	 * node in the graph. Each mapping contains the identifyer for a node
	 * and the associated connection strength.  
	 * 
	 * @param link The URL for the node in the graph 
	 * @return A Map of the nodes that are connected from the given node in the graph.
	 */
	public Map outLinks ( Integer link ) {
		if(link==null) return new HashMap();
		Map aux = (Map)(OutLinks.get(link));
		return (aux == null) ? new HashMap() : aux;
	}
	
	/**
	 * Returns the connection strength between two nodes, assuming there is a
	 * connection from the first to the second. If no connection exists, a link
	 * strength of zero is returned.
	 * 
	 * @param fromLink The source link
	 * @param toLink  The target link
	 * @return The strenght for the connection between fromLink and toLink ( fromLink -> toLink )
	 * @see inLink
	 */
	public Double inLink ( String fromLink, String toLink ) {
		Integer id1 = URLToIdentifyer(fromLink);
		Integer id2 = URLToIdentifyer(toLink); 
		return inLink(id1,id2);
	}
	
	/**
	 * Returns the connection strength between two nodes, assuming there is a
	 * connection from the first to the second. If no connection exists, a link
	 * strength of zero is returned.
	 * 
	 * @param fromLink The source link
	 * @param toLink  The target link
	 * @return The strenght for the connection between fromLink and toLink ( fromLink -> toLink )
	 * @see outLink
	 */
	public Double outLink ( String fromLink, String toLink ) {
		Integer id1 = URLToIdentifyer(fromLink);
		Integer id2 = URLToIdentifyer(toLink); 
		return outLink(id1,id2);
	}

	/**
	 * Returns the connection strength between two nodes, assuming there is a
	 * connection from the first to the second. If no connection exists, a link
	 * strength of zero is returned.
	 * 
	 * @param fromLink An identifyer for the source link
	 * @param toLink  An identifyer for the target link
	 * @return The strenght for the connection between fromLink and toLink ( fromLink -> toLink )
	 * @see outLink
	 */
	public Double inLink ( Integer fromLink, Integer toLink ) {	
		Map aux = inLinks(toLink);
		if(aux==null) return new Double(0);
		Double weight = (Double)(aux.get(fromLink));
		return (weight == null) ? new Double(0) : weight;
	}
	
    /**
     * Returns the connection strength between two nodes, assuming there is a
     * connection from the first to the second. If no connection exists, a link
     * strength of zero is returned.
     * 
     * @param fromLink An identifyer for the source link
     * @param toLink  An identifyer for the target link
     * @return The strenght for the connection between fromLink and toLink ( fromLink -> toLink )
     * @see inLink
     */
	public Double outLink ( Integer fromLink, Integer toLink ) {	
		Map aux = outLinks(fromLink);
		if(aux==null) return new Double(0);
		Double weight = (Double)(aux.get(toLink));
		return (weight == null) ? new Double(0) : weight;
	}

	/**
	 * Transforms a bi-directional graph to an uni-directional equivalent. The connection
	 * strenght between two nodes A and B that are inter-connected in the bi-directional
	 * graph is transformed into MAX(weight_inlink(A,B),weight_outlink(A,B))
	 */
	public void transformUnidirectional () {
		Iterator it = OutLinks.keySet().iterator();
		while (it.hasNext()) {
			Integer link1 = (Integer)(it.next());
			Map auxMap = (Map)(OutLinks.get(link1));
			Iterator it2 = auxMap.keySet().iterator();
			while (it2.hasNext()) {
				Integer link2 = (Integer)(it.next());
				Double weight = (Double)(auxMap.get(link2));
				addLink(link2,link1,weight);
			}
		}
	}

	/**
	 * Remove nodes which correspond to an internal link. In a Web Graph, internal
	 * links are those made to pages that are situated on the same host.
	 */
	public void removeInternalLinks () {
		int index1;
		Iterator it = OutLinks.keySet().iterator();
		while (it.hasNext()) {
			Integer link1 = (Integer)(it.next());
			Map auxMap = (Map)(OutLinks.get(link1));
			Iterator it2 = auxMap.keySet().iterator();
			if(it2.hasNext()) {
				String URL1 = (String)(IdentifyerToURL.get(link1));
				index1 = URL1.indexOf("://");
				if(index1!=-1) URL1=URL1.substring(index1+3);
				index1 = URL1.indexOf("/");
				if(index1!=-1) URL1=URL1.substring(0,index1);
				while (it2.hasNext()) {
					Integer link2 = (Integer)(it.next());
					String URL2 = (String)(IdentifyerToURL.get(link2));
					index1 = URL2.indexOf("://");
					if(index1!=-1) URL2=URL1.substring(index1+3);
					index1 = URL2.indexOf("/");
					if(index1!=-1) URL2=URL1.substring(0,index1);
					if(URL1.equals(URL2)) {
						auxMap.remove(link2);
						OutLinks.put(link1,auxMap);
						auxMap = (Map)(InLinks.get(link2));
						auxMap.remove(link1);
						InLinks.put(link2,auxMap);
					}
				}
			}
		}
	}
	
	/**
	 *  Remove nodes which correspond to nepotistic links. In a Web Graph, nepotistic
	 *  links are tipically those made to pages that are situated on the same host, correspondig
	 *  to links made for hypertext navigational purposes rather than semantic similarity.
	 *
	 *  See the paper "<a href="http://www.cse.lehigh.edu/~brian/pubs/2000/aaaiws/">Recognizing Nepotistic Links on the Web</a>" by Brian Davison, presented at the 
	 *  AAAI-2000 Workshop on Artificial Intelligence for Web Search, Austin, TX, July 30, and published in Artificial Intelligence for Web Search,
	 *  Technical Report WS-00-01, pp. 23-28, AAAI Press.
	 * 
	 */
	public void removeNepotistic() {
		removeInternalLinks();
	}

	/**
	 * Remove nodes which correspond to stop URLs. 
	 * 
	 * @param stopURLs An array of Strings with the Stop URLs
	 */
	public void removeStopLinks(String stopURLs[]) {
		HashMap aux = new HashMap();
		for (int i=0; i<stopURLs.length; i++) aux.put(stopURLs[i],null);
		removeStopLinks(aux);
	}
	
	/**
	 * Remove nodes which correspond to stop URLs. In a Web Graph, stop URLs
	 * correspond to very frequent pages. A link from/to such an URLs usually does not
	 * imply semantic similarity.
	 * 
	 * @param stopURLs A Map where keys are the Stop URLs
	 */
	public void removeStopLinks(Map stopURLs) {
		int index1;
		Iterator it = OutLinks.keySet().iterator();
		while (it.hasNext()) {
			Integer link1 = (Integer)(it.next());
			String URL1 = (String)(IdentifyerToURL.get(link1));
			index1 = URL1.indexOf("://");
			if(index1!=-1) URL1=URL1.substring(index1+3);
			index1 = URL1.indexOf("/");
			if(index1!=-1) URL1=URL1.substring(0,index1);
			if(stopURLs.containsKey(URL1)) {
				OutLinks.put(link1,new HashMap());
				InLinks.put(link1,new HashMap());
			}
		}
	}

	/**
	 * Returns the number of nodes in the graph
	 * 
	 * @return The number of nodes in the graph
	 */
	public int numNodes() {
		return nodeCount;
	}

}
}



