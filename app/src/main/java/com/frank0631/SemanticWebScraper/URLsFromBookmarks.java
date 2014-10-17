package com.frank0631.SemanticWebScraper;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class URLsFromBookmarks {

   public static void main(String[] args) {
   
      boolean inHREF = false;
      boolean plainText = true;
      String bookmarks_name = "bookmarks_frank_mega";
      
      File file;
      if(plainText)
         file = new File(bookmarks_name+".txt");
      else
         file = new File(bookmarks_name+".html");
   
      try {
      
         PrintWriter extracted_urls = new PrintWriter(bookmarks_name+"_url_out.txt", "US-ASCII");
         Scanner scanner = new Scanner(file);
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();   
            
            Pattern p;
            if(inHREF)
               p = Pattern.compile("HREF=\"(.*?)\"");
            else
               p = Pattern.compile("http(.*)");            
            
            Matcher m = p.matcher(line);
            if (m.find())  
               for (int i = 1; i <= m.groupCount(); i++) {
                  if(inHREF)
                     extracted_urls.println(m.group(1));  
                  else
                     extracted_urls.println("http"+m.group(1));  
               }
               
         }
         scanner.close();
         extracted_urls.close();
      } 
      catch (Exception e) {
         e.printStackTrace();
      }
   
   }
}