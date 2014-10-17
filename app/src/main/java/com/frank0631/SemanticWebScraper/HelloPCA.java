package com.frank0631.SemanticWebScraper;

public class HelloPCA {

   public static void main(String[] args) {
    
      PrincipleComponentAnalysis pcsTest = new PrincipleComponentAnalysis();
      double [ ] [ ] pc =     {
         {-0.0971,-0.0178,0.0636 },
         {1.3591,1.5820,-1.5266 },
         {0.0149,-0.0178,0.0636 },
         {1.1351,1.0487,-0.8905 },
         {-0.0971,-0.0178,0.0636 },
         {-0.0971,-0.0178,0.0636 },
         {-0.9932,-0.8177,-0.8905 },
         {-0.9932,-0.8177,1.0178 },
         {-1.6653,-1.8842,1.9719 },
         {-1.2173,-1.3509,1.6539 },
         {2.0312,1.8486,-1.5266 },
         {0.6870,0.5155,-0.2544 },
         {-0.0971,-0.0178,0.0636 },
         {0.0149,-0.0178,0.0636 },
         {0.0149,-0.0178,0.0636}};
      System.out.println(pc.length);
      
      //Print matrix 
      for(int i=0;i<pc.length;i++){
         for(int j=0;j<pc[i].length;j++)
            System.out.print(String.format("%f8", pc[i][j])+"\t");
         System.out.println();
      }
      System.out.println();   
      
       
      pcsTest.setup( 15 , 3 );
      for(int i=0;i<pc.length;i++)
         pcsTest.addSample(pc[i]);
      
      pcsTest.computeBasis( 2); 
      for(int i=0;i<2;i++){
         double[] vec = pcsTest.getBasisVector(i);
         for(int j=0;j<vec.length;j++)
            System.out.print(String.format("%f8", vec[j])+"\t");
         System.out.println();
      }
      
      
      
      System.out.println("Hello, World");
   }

}
