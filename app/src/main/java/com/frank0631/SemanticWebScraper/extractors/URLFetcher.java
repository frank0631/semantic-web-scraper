package com.frank0631.SemanticWebScraper.extractors;

import com.frank0631.SemanticWebScraper.utils.URLCouples;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by frank on 1/18/15.
 */
public class URLFetcher implements Runnable {

    public static int reachTimeoutSec = 3;
    public static int readTimeoutSec = 6;
    public static ArrayList<String> urlSchemas;
    static {
        urlSchemas = new ArrayList<String>();
        urlSchemas.add("http://");
        urlSchemas.add("http://www.");
        urlSchemas.add("https://");
        urlSchemas.add("https://www.");
    }

    public URLCouples urlCouples;
    public String line;

    public URLFetcher(URLCouples urlCouples, String line){
        this.urlCouples=urlCouples;
        this.line=line;
    }

    @Override
    public void run() {
        fetch();
    }

    private void fetch() {
        try {
            String url_str= valid(line);
            if(url_str!=null && !urlCouples.ValidURLs.contains(url_str)) {
                urlCouples.ValidURLs.add(url_str);
                System.out.println("added to URL list: "+url_str);
            }else {
                urlCouples.inValidURLs.add(line);
                System.out.println("URL not valid: " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String valid(String url_str){

        if (ping(url_str))
            return url_str;

        for (String prefix : urlSchemas)
            if (ping(prefix + url_str))
                return prefix + url_str;

        return null;
    }

    static public boolean ping(String url_str){

        //false if malformed URL
        try {
            URL url = new URL(url_str);
            return ping(url);
        }
        catch (IOException ex){
            return false;
        }
    }

    static public boolean ping(URL url){

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
        }catch (Exception ex){
            return false;
        }
    }

}