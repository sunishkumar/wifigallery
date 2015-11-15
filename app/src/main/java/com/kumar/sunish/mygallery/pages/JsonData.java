package com.kumar.sunish.mygallery.pages;

import com.kumar.sunish.mygallery.MediaUtils;
import com.kumar.sunish.mygallery.MyHTTPD;

import org.json.JSONArray;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 18/8/15.
 */
public class JsonData  implements DynamicPage {
    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {

        MediaUtils utils = new MediaUtils(server);

        Map<String,String> parms = session.getParms();
        if("IMAGEALBUM".equals(parms.get("op"))){

            JSONArray jsonArray = new JSONArray(utils.getGroups(MediaUtils.IMAGE));
            return server.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonArray.toString());
        }
        else if("VIDEOALBUM".equals(parms.get("op"))){

            JSONArray jsonArray = new JSONArray(utils.getGroups(MediaUtils.VIDEO));
            return server.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonArray.toString());
        }
        else if("LISTFORALBUM".equals(parms.get("op"))){
            int type = 0;


            try{
                type = Integer.parseInt(parms.get("type"));

            }catch (Exception x){}



            JSONArray jsonArray = new JSONArray(utils.getListForGroup(type, parms.get("id")));
            return server.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonArray.toString());
        }

        return server.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "File Not Found");
    }
}
