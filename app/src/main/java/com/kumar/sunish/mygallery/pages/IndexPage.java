package com.kumar.sunish.mygallery.pages;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

import com.kumar.sunish.mygallery.MediaUtils;
import com.kumar.sunish.mygallery.MyHTTPD;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 2/8/15.
 */
public class IndexPage implements DynamicPage {
    private MyHTTPD server ;

    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {
        this.server = server;
        Map<String, String> parms = session.getParms();


        String msg =  "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
                "<meta charset=\"utf-8\">\n" +
                "<meta name=\"description\" content=\"description\">\n" +
                " \n" +
                "<title>My Gallery</title>\n" +
                "<link rel=\"stylesheet\" href=\"/css/mygrid.css\">\n" +
                "</head><body  style='margin:0;padding:0;'>" +
                "<div id=\"wrapper\">" +

                "\n" +
                "\n";

        if(null == parms || parms.size()<=0){
              msg +=  createAlbumList();
        }


        msg += "</div>" +
                "<script src=\"/js/jquery-1.10.1.min.js\"></script> \n" +
                "<script src=\"/js/jquery.lazyload.min.js\"></script> \n" +
                "<script src=\"/js/mygallery.js\"></script> \n" +
                "<script>\n" +
                "jQuery(function($){\n" +
                "\n" +
                "\t$(\"img.lazy\").lazyload({\n" +
                "        effect: 'fadeIn',\n" +
                "\t\teffectspeed: 1000,\n" +
                "\t\tthreshold: 400\n" +
                "    });\n" +
                "\t\n" +
                "});\n" +
                "</script>" +
                "</body></html>\n";


        return server.newFixedLengthResponse(msg );
    }

        private String createAlbumList() {

                MediaUtils utils = new MediaUtils(server);

                String msg = "<div class=\"row \"> \n" +
                        "      <h5>Images</h5>\n" +
                         "    </div><div class=\"masonry row \">";

                ArrayList<Map<String, String>> albums = utils.getGroups(MediaUtils.IMAGE);

                for(Map<String,String> album : albums){

                    msg += "<div class=\"item \"><a href=\"pages/MediaImages?id=" +album.get("ID")  + "\"><img class=\"lazy\" src=\"/blank_img.png\" data-original=\"/pages/Thumb?type=" + MediaUtils.IMAGE + "&id=" + album.get("_ID") + "\" ></a>\n" +
                            "      " +album.get("NAME")  + "\n" +

                            "    </div>";

                }



            msg +=  "</div>  <div class=\"row \">\n" +
                    "      <h5>Videos</h5>\n" +
                    "    </div><div class=\"masonry row \">";


            albums = utils.getGroups(MediaUtils.VIDEO);

            for(Map<String,String> album : albums){

                msg += "<div class=\"item \"><a href=\"pages/MediaVideos?id=" +album.get("ID")  + "\"><img class=\"lazy\" src=\"/blank_img.png\" data-original=\"/pages/Thumb?type=" + MediaUtils.VIDEO + "&id=" + album.get("_ID") + "\" ></a>\n" +
                        "      " +album.get("NAME")  + "\n" +

                        "    </div>";

            }


            msg +="</div>";
                return msg;
        }






}
