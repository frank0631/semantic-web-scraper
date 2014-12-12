package com.frank0631.SemanticWebScraper;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

//Scrapes list of URLs
public class URLHandeler {

   int reachTimeoutSec = 3;
   int readTimeoutSec = 6;
   ArrayList<String> urlSchemas = new ArrayList<String>();

   public URLHandeler(){
      urlSchemas.add("http://");
      urlSchemas.add("https://");
      urlSchemas.add("http://www.");
      urlSchemas.add("https://www.");
   }

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
      String url_str = null;
      if(readfile.exists())
         try{
            BufferedReader br = new BufferedReader(new FileReader(readfilename));
            for(String line; (line = br.readLine()) != null; ) {
               url_str= valid(line);
               if(url_str!=null && !URLs.contains(url_str)) {
                  URLs.add(url_str);
                  System.out.println("added to URL list: "+url_str);
               }else
                  System.out.println("URL not valid: "+line);
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }

      return URLs;
   }

   public String valid(String url_str){

      if (ping(url_str))
         return url_str;

      for(String prefix : urlSchemas)
         if (ping(prefix+url_str))
            return prefix+url_str;

      return null;
   }

   public boolean ping(String url_str){

      //false if malformed URL
      try {
         URL url = new URL(url_str);
         return ping(url);
      }
      catch (IOException ex){
         return false;
      }
   }

   public boolean ping(URL url){

      //ping url, false if not responding or no connection
      try {
         final HttpURLConnection urlping = (HttpURLConnection) url.openConnection();
         urlping.setConnectTimeout(1000 * reachTimeoutSec);
         urlping.setRequestMethod("GET");
         urlping.setReadTimeout(1000 * readTimeoutSec);
         urlping.setInstanceFollowRedirects(false);
         urlping.connect();
         urlping.disconnect();
         if (urlping.getResponseCode() == HttpURLConnection.HTTP_OK)
            return true;
         return false;
      }catch (IOException ex){
         return false;
      }
   }

}