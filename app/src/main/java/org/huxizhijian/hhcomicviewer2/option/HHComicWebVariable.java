package org.huxizhijian.hhcomicviewer2.option;

import android.content.Context;
import android.content.SharedPreferences;

import org.huxizhijian.hhcomicviewer2.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * 网址变量
 * Created by wei on 2017/1/6.
 */

public class HHComicWebVariable {

    private Context mContext;

    private String csite;
    private String pre;
    private String chsite;
    private String behind;
    private String encodeKey;
    private String picServer;
    private String searchUrl;

    public HHComicWebVariable() {
    }

    public HHComicWebVariable(String csite, String pre, String chsite, String behind,
                              String encodeKey, String picServer, String searchUrl) {
        this.csite = csite;
        this.pre = pre;
        this.chsite = chsite;
        this.behind = behind;
        this.encodeKey = encodeKey;
        this.picServer = picServer;
        this.searchUrl = searchUrl;
    }

    public HHComicWebVariable(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        csite = sharedPreferences.getString("csite", Constants.HHCOMIC_URL);
        pre = sharedPreferences.getString("pre", Constants.HHCOMIC_PRE_ID);
        chsite = sharedPreferences.getString("chsite", Constants.COMIC_VOL_PAGE);
        behind = sharedPreferences.getString("behind", Constants.COMIC_VOL_BEHIND_ID);
        encodeKey = sharedPreferences.getString("encode_key", Constants.ENCODE_KEY);
        picServer = sharedPreferences.getString("pic_server", Constants.PIC_SERVICE_URL);
        searchUrl = sharedPreferences.getString("search_url", Constants.SEARCH_URL);
    }

    public void updatePreferences() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("csite", csite);
        editor.putString("pre", pre);
        editor.putString("chsite", chsite);
        editor.putString("behind", behind);
        editor.putString("encode_key", encodeKey);
        editor.putString("pic_server", picServer);
        editor.putString("search_url", searchUrl);
        editor.apply();
    }

    public void setCsite(String csite) {
        this.csite = csite;
    }

    public String getCsite() {
        return this.csite;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPre() {
        return this.pre;
    }

    public void setChsite(String chsite) {
        this.chsite = chsite;
    }

    public String getChsite() {
        return this.chsite;
    }

    public void setBehind(String behind) {
        this.behind = behind;
    }

    public String getBehind() {
        return this.behind;
    }

    public void setEncodeKey(String encodeKey) {
        this.encodeKey = encodeKey;
    }

    public String getEncodeKey() {
        return this.encodeKey;
    }

    public void setPicServer(String picServer) {
        this.picServer = picServer;
    }

    public String getPicServer() {
        return this.picServer;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public String getSearchUrl() {
        return this.searchUrl;
    }
}
