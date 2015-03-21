package com.frank0631.SemanticWebScraper.utils;

import java.net.URL;
import java.util.List;

/**
 * Created by frank0631 on 2/21/15.
 */
public class URLNode {

    URL url;
    String hostname;
    String title;
    String url_str;
    List<String> topics;
    String summery;
    boolean valid;
    int[] keyword_vector;

}
