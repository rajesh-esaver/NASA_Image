package com.notfound.normalapp.pojo;

import com.google.gson.annotations.SerializedName;

public class ApodImage {

    @SerializedName("copyright")
    private String copyright;

    @SerializedName("date")
    private String date;

    @SerializedName("explanation")
    private String explanation;

    @SerializedName("hdurl")
    private String hdurl;

    @SerializedName("media_type")
    private String mediaType;

    @SerializedName("service_version")
    private String serviceVersion;

    @SerializedName("title")
    private String tittle;

    @SerializedName("url")
    private String url;

    public ApodImage(String copyright, String date, String explanation, String hdurl, String mediaType, String serviceVersion, String tittle, String url) {
        this.copyright = copyright;
        this.date = date;
        this.explanation = explanation;
        this.hdurl = hdurl;
        this.mediaType = mediaType;
        this.serviceVersion = serviceVersion;
        this.tittle = tittle;
        this.url = url;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getDate() {
        return date;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getHdurl() {
        return hdurl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getTittle() {
        return tittle;
    }

    public String getUrl() {
        return url;
    }

    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }
}
