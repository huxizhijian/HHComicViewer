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

    public void updatePrefrences() {
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

    public String getCsite() {
        return csite;
    }

    public void setCsite(String csite) {
        this.csite = csite;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getChsite() {
        return chsite;
    }

    public void setChsite(String chsite) {
        this.chsite = chsite;
    }

    public String getBehind() {
        return behind;
    }

    public void setBehind(String behind) {
        this.behind = behind;
    }

    public String getEncodeKey() {
        return encodeKey;
    }

    public void setEncodeKey(String encodeKey) {
        this.encodeKey = encodeKey;
    }

    public String getPicServer() {
        return picServer;
    }

    public void setPicServer(String picServer) {
        this.picServer = picServer;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }
}
