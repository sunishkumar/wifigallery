package com.kumar.sunish.mygallery.pages;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.kumar.sunish.mygallery.MediaUtils;
import com.kumar.sunish.mygallery.MyHTTPD;

import java.util.ArrayList;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by sunish on 13/8/15.
 */
public class MediaImages implements DynamicPage {


    @Override
    public NanoHTTPD.Response createResponse(MyHTTPD server, NanoHTTPD.IHTTPSession session) {

        try {
            boolean isApp = false;
            if (session.getHeaders().get("user-agent").contains("SunishGallery777")) {
                isApp = true;
            }

            String msg = "<!doctype html>\n" +
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
                    " <div id=\"header\">\n" +
                    "    <h5>My Gallery</h5>\n" +
                    "  </div>" +
                    "<div class=\"masonry row \">\n" +
                    "\n";


            Map<String, String> parms = session.getParms();
            String id = parms.get("id");

            MediaUtils util = new MediaUtils(server);

            ArrayList<Map<String, String>> list = util.getListForGroup(MediaUtils.IMAGE, id);

            for (Map<String, String> image : list) {

                if (isApp) {
                    msg += "<div class=\"item \"><a href=\"javascript:viewImage('" + image.get("ID") + "');\"><img class=\"lazy\" src=\"/blank_img.png\" data-original=\"/pages/Thumb?type=" + MediaUtils.IMAGE + "&id=" + image.get("ID") + "\" ></a>\n";
                } else {
                    msg += "<div class=\"item \"><a href=\"/pages/Gallery?type="+MediaUtils.IMAGE+"&id="+ image.get("ID")+"\"  target=\"_newview\"><img class=\"lazy\" src=\"/blank_img.png\" data-original=\"/pages/Thumb?type=" + MediaUtils.IMAGE + "&id=" + image.get("ID") + "\" ></a>\n";
                }
                msg += "      " + image.get("DATE") + "\n" +
                        "      " + image.get("SIZE") + "\n";
                if (isApp) {
                    msg += "<input type=\"checkbox\" name=\"resource\" value=\"" + image.get("ID") + "\" display-name=\"" + image.get("NAME") + "\">";
                }
                msg += "    </div>";


            }


            msg += "</div></div>";

            if (isApp) {
                msg += "<a href=\"javascript:download('" + MediaUtils.IMAGE + "');\"><img src=\"/arrow-download-icon.png\" id=\"fixedbutton\"></a>";
            }
            msg += "<script src=\"/js/jquery-1.10.1.min.js\"></script> \n" +
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

            return server.newFixedLengthResponse(msg);

        } catch (Exception x) {
            return server.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "File Not Found");
        }

    }
}
