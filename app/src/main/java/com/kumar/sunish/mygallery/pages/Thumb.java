package com.kumar.sunish.mygallery.pages;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.kumar.sunish.mygallery.MediaUtils;
import com.kumar.sunish.mygallery.MyHTTPD;

import java.io.ByteArrayInputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 13/8/15.
 */
public class Thumb implements  DynamicPage {


    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {

        Map<String,String> parms = session.getParms();
        if (parms.get("type") != null && parms.get("id")!=null ) {
            int type = 0;
            long id = 0;

            try{
                type = Integer.parseInt(parms.get("type"));

            }catch (Exception x){}
            try{
                id = Long.parseLong(parms.get("id"));

            }catch (Exception x){}

            MediaUtils utils = new MediaUtils(server);
            byte[] b = utils.createThumb(type,id);
            if(null != b){
                ByteArrayInputStream bs = new ByteArrayInputStream(b);

                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "image/png", bs, b.length);
            }
            else{
                return server.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "File Not Found");
            }

        }
        return server.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "File Not Found");
    }
}
