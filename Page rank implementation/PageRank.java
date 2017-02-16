import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;

public class PageRank {

    static class Edge {
        int dest;     // destination airport
        int weight;   // number of routes in this edge
    }

    static class EdgeList {
        int weight;    // total number of edges = sum of second components of list
        ArrayList<Edge> list;
    }
    
    static Double[] P;
    static String airportCodes[];           // index to short code
    static String airportNames[];           // index to airport name
    static HashMap<String,Integer> airportIndices = new HashMap<String, Integer>();  // airport code to index
    static EdgeList[] G;             // G[i] is a list of pairs (j,k) meaning
                                     // "there are k routes from airport i to airport j"
				      // other info??
    
    public static void readAirports() {
      try {	
         String fileName = "airports.txt";
         System.out.println("... opening file "+fileName);
         FileInputStream fstream = new FileInputStream(fileName);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         
         String strLine;
         int index = 0;
         ArrayList<String> codeTemp = new ArrayList<String>();
         ArrayList<String> nameTemp = new ArrayList<String>();
         while ((strLine = br.readLine()) != null) {           
               String[] aLine = strLine.split(",");
               String airportCode = aLine[4];
               String airportName = aLine[1]+" ("+aLine[3]+")";
               if (airportCode.length() > 2) {
                   codeTemp.add(airportCode.split("\"")[1]);
                   nameTemp.add(airportName);
                   index++;
                }
         }

         // TO DO: DUMP STUFF TO airportCodes, airportNames, airportIndices
         airportCodes = new String[codeTemp.size()];
         airportNames = new String[nameTemp.size()];
         G = new EdgeList[codeTemp.size()];
         for (int i = 0; i < index; i++) {
	    airportCodes[i] = codeTemp.get(i);
	    airportNames[i] = nameTemp.get(i);
	    airportIndices.put(codeTemp.get(i), i); 
	    G[i] = new EdgeList();
	    G[i].weight = 0;
	    G[i].list = new ArrayList<Edge>();
         }
         
         System.out.println("... "+index+" airports read");

         in.close();
         
       } catch (Exception e){
		     //Catch exception if any
             System.err.println("Error: " + e.getMessage());
             // return null;
       }
    
    }


   public static void readRoutes() {
            try {
         String fileName = "routes.txt";
         System.out.println("... opening file "+fileName);
         FileInputStream fstream = new FileInputStream(fileName);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         
         String strLine;
         int index = 0;
         ArrayList<String> codeTemp = new ArrayList<String>();
         ArrayList<String> nameTemp = new ArrayList<String>();
         
         while ((strLine = br.readLine()) != null) {           
               String[] aLine = strLine.split(",");
               String airport1 = aLine[2];
               String airport2 = aLine[4];
               if (airportIndices.get(airport1) != null && airportIndices.get(airport2) != null) {
		int indice = airportIndices.get(airport2);
		G[indice].weight++;
		Boolean found = false;
		for(Edge e : G[indice].list){
		      if(e.dest == airportIndices.get(airport1))
		      {
			//something here
			found = true;
			e.weight++;
		      }
		}
		 
		if (!found)
		  {
		      Edge e = new Edge();
		      e.weight = 1;
		      e.dest = airportIndices.get(airport1);
		      G[indice].list.add(e);
		  }
		}	
               index++;
         }

        
         System.out.println("... "+index+" routes read");

         in.close();
         
       } catch (Exception e){
		     //Catch exception if any
             System.err.println("Error: " + e.getMessage());
             // return null;
       }
   }
   
   public static void computePageRanks() {
      int iteraciones = 2000; //stop condition
      double L = 0.9;
      int k = 0;
      int n = airportIndices.size();
      P = new Double[n];
      for(int j = 0; j < n; ++j) P[j] = 1.0/n;
      boolean run = true;
      int l=0;
      while(k < iteraciones)//cambiar por run para stop condition con convergencia
      {
	  run = false;
	  Double[] Q = new Double[n];
	  for(int i = 0; i < n; ++i){
	      double suma = 0;
	      boolean encontrado = false;  
	      for(Edge e : G[i].list){ //a los que no reciben de nadie, ponerle un arco inventado, uno solo que le sume a el para que no sume 0 
		encontrado = true;
		suma += P[e.dest] * (double)e.weight / (double)G[i].weight; 
	      }
	      if(!encontrado){
		suma += 1.0/n;
	      }
	      
	      Q[i] = L * suma + (1-L)/n;
	      if(Math.abs(Q[i] - P[i]) > (double)0.0000000000000000001) run = true;
	      
	  }
	  l++;
	  P = Q;
	  k++;
      }
      System.out.println("Iteraciones: " + l);
   }

   public static void outputPageRanks() {
     double suma = 0;
     for(int i = 0; i < airportIndices.size(); ++i){
	suma += P[i];
     }
     System.out.println("total: " + suma);
     for(int i = 0; i < airportIndices.size(); ++i){
	for(int j = i; j < airportIndices.size(); ++j){
	  double aux;
	  String aux2;
	  if(P[i] < P[j]){
	    aux2 = airportCodes[i];
	    airportCodes[i] = airportCodes[j];
	    airportCodes[j] = aux2;
	    aux = P[i];
	    P[i] = P[j];
	    P[j] = aux;
	  }
	
	}
     }
     for(int i = 0; i < airportIndices.size(); ++i){
	System.out.println(airportCodes[i] + " pagerank: " + P[i] + " peso: " + G[airportIndices.get(airportCodes[i])].weight);
     }
   }

   public static void main(String args[])  {

       readAirports();   // get airport names, codes, and assign indices
       readRoutes();     // read tuples and build graph
       computePageRanks();
       outputPageRanks(); 

    }
    
}
