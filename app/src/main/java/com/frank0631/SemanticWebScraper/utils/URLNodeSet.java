package com.frank0631.SemanticWebScraper.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class URLNodeSet {

    Map<String, URLNode> urlNodes;

    public URLNodeSet() {
        urlNodes = new LinkedHashMap<String, URLNode>();
    }

    public int size() {
        return urlNodes.size();
    }

    public void setURLCouples(URLCouples urls) {
        for (String validURL : urls.ValidURLs) {
            URLNode tmpNode = new URLNode();

            //url_str as key
            tmpNode.url_str = validURL;
            try {
                URL url_tmp = new URL(validURL);
                tmpNode.url = url_tmp;
                tmpNode.hostname = url_tmp.getHost();
                tmpNode.valid = true;
                urlNodes.put(validURL, tmpNode);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    public void setSummeries(Map<String, String> summeryMap) {

        for (Map.Entry<String, String> entry : summeryMap.entrySet())
            if (urlNodes.containsKey(entry.getKey()))
                urlNodes.get(entry.getKey()).summery = entry.getValue();

    }

    public void setKeywords(Map<String, List<String>> keywordDistro) {
        for (Map.Entry<String, List<String>> entry : keywordDistro.entrySet())
            if (urlNodes.containsKey(entry.getKey()))
                urlNodes.get(entry.getKey()).topics = entry.getValue();
    }

    public void setKeywordVector(Map<String, int[]> keywordMatrixMap) {
        for (Map.Entry<String, int[]> entry : keywordMatrixMap.entrySet())
            if (urlNodes.containsKey(entry.getKey()))
                urlNodes.get(entry.getKey()).keyword_vector = entry.getValue();
    }
}
