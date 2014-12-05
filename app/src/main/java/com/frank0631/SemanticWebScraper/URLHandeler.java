package com.frank0631.SemanticWebScraper;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

//Scrapes list of URLs
public class URLHandeler {

   public void URLsFromBookmarks(String bookmarks_filename,String urlout_filename, boolean inHREF) {

      ArrayList<String> url_array = new ArrayList<String>();
      File bookmarks_file = new File(bookmarks_filename);

      try {
         Scanner scanner = new Scanner(bookmarks_file);
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Pattern p;
            if(inHREF)
               p = Pattern.compile("HREF=\"(.*?)\"");
            else
               p = Pattern.compile("http(.*)");
            Matcher m = p.matcher(line);
            while (m.find()){
               if(inHREF)
                  url_array.add(m.group(1));
               else
                  url_array.add("http"+m.group(1));
            }
            scanner.close();
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      try {
         PrintWriter extracted_urls = new PrintWriter(urlout_filename, "US-ASCII");
         for(String url:url_array)
            extracted_urls.println(url);
         extracted_urls.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public ArrayList<String> readURLrows(String readfilename){

      File readfile = new File(readfilename);
      ArrayList<String> URLs = new ArrayList<String>();

      if(readfile.exists())
         try{
            BufferedReader br = new BufferedReader(new FileReader(readfilename));
            for(String line; (line = br.readLine()) != null; ) {

               String url_str= line;

               if (!url_str.toLowerCase().startsWith("http://") &&
                       !url_str.toLowerCase().startsWith("https://") )
                  url_str = "http://" + url_str;

               if(!URLs.contains(url_str))
                  URLs.add(url_str);
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }

      return URLs;
   }

}