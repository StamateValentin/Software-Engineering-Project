package com.bfourclass.smartbooking.external_api.instance.covid_news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Item {
    public String kind;
    public String title;
    public String htmlTitle;
    public String link;
    public String displayLink;
    public String snippet;
    public String htmlSnippet;
    public String cacheId;
    public String formattedUrl;
    public String htmlFormattedUrl;

    @JsonIgnoreProperties
    public Object pagemap;
}
