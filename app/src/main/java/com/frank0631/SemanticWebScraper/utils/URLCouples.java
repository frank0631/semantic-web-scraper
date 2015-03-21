package com.frank0631.SemanticWebScraper.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by frank on 1/18/15.
 */
public class URLCouples {
    public List<String> ValidURLs;
    public List<String> inValidURLs;

    public URLCouples() {
        ValidURLs = Collections.synchronizedList(new ArrayList<String>());
        inValidURLs = Collections.synchronizedList(new ArrayList<String>());
    }
}