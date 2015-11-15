package com.kumar.sunish.mygallery.pages;

import com.kumar.sunish.mygallery.MyHTTPD;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 2/8/15.
 */
public interface DynamicPage {

    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session);


}
