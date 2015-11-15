package com.kumar.sunish.mygallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText urlText = null;
    private TextView statusText =null;
    private ProgressBar progressBar=null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ClientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientFragment newInstance(String param1) {
        ClientFragment fragment = new ClientFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    public ClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client, container, false);

        urlText = (EditText) view.findViewById(R.id.clientUrl);

        statusText = (TextView) view.findViewById(R.id.statusText);
        statusText.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        WebView myWebView = (WebView) view.findViewById(R.id.webView);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        myWebView.addJavascriptInterface(this, "Android");

        myWebView.getSettings().setUserAgentString(
                myWebView.getSettings().getUserAgentString()
                        + " "
                        + "SunishGallery777"
        );

        Button goBt = (Button) view.findViewById(R.id.buttonGo);
        goBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl();
            }
        });

        Button scanBt = (Button) view.findViewById(R.id.buttonScan);
        scanBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*  Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);*/
                new IntentIntegrator(getActivity()).initiateScan();
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Toast.makeText(getView().getContext(), "" +resultCode,
                Toast.LENGTH_LONG).show();

        if (requestCode == 0) {
            if (resultCode == -1) {
                urlText.setText(intent.getStringExtra("SCAN_RESULT"));
                loadUrl();
               // String contents = intent.getStringExtra("SCAN_RESULT");
               // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
            } else if (resultCode == 0) {
                // Handle cancel
            }
        }
    }

    private void loadUrl() {
        String url = urlText.getText().toString();
        AppData.getInstance().setUrl(url);
        ((WebView) getView().findViewById(R.id.webView)).loadUrl(url);
    }

    public void setUrl(String url){
        urlText.setText(url);
        loadUrl();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @JavascriptInterface
    public void downloadFiles(final String type, final String[] ids, final String[] files) {
        statusText.post(new Runnable() {
            public void run() {
                statusText.setVisibility(View.VISIBLE);

            }
        });

        progressBar.post(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.VISIBLE);

            }
        });


        final File folder = new File(Environment.getExternalStorageDirectory() + "/MyGalleryFiles");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyGalleryFiles")));
        //getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyGalleryFiles")));

        final String serverPath = urlText.getText().toString();

        if (success) {

            new Thread()
            {
                public void run() {


                    for (int j=0;j<ids.length;j++) {
                        String file = files[j];
                        setProgress(0);


                        final String statusMsg = ""+(j+1) + "/" + files.length + " [" + file + "]";
                        setStatusMsg(statusMsg);


                        try {
                            URL url = new URL(serverPath+"/pages/Gallery?type="+type +"&id="+ids[j]);
                            URLConnection connection = url.openConnection();
                            connection.connect();
                            // this will be useful so that you can show a typical 0-100% progress bar
                            int fileLength = connection.getContentLength();
                            InputStream input = url.openConnection().getInputStream();
                            FileOutputStream output = new FileOutputStream(folder.getPath()+"/"+file);
                            byte data[] = new byte[1024];
                            long total = 0;
                            int count;
                            while ((count = input.read(data)) != -1) {
                                total += count;
                                // publishing the progress....
                               // Bundle resultData = new Bundle();

                              //  resultData.putInt("progress" ,(int) (total * 100 / fileLength));
                               // receiver.send(UPDATE_PROGRESS, resultData);
                                setProgress((int) (total * 100 / fileLength));
                                output.write(data, 0, count);
                            }

                            output.flush();
                            output.close();
                            input.close();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            //Toast.makeText(getActivity(), "Error: "+e.getMessage(),
                            //        Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                           // Toast.makeText(getActivity(), "Error: " + e.getMessage(),
                           //         Toast.LENGTH_LONG).show();
                        }
                    }
                   /* Toast.makeText(getActivity(), "Download Process Completed",
                            Toast.LENGTH_LONG).show();*/



                    statusText.post(new Runnable() {
                        public void run() {
                            statusText.setText("Download Process Completed");
                            statusText.setVisibility(View.INVISIBLE);
                        }
                    });

                    progressBar.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(0);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }.start();

        } else {
            Toast.makeText(getActivity(), "Unable to create folder",
                    Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void viewImage(String id,String allIds){

        AppData.getInstance().setAllImgIds(allIds);

        Intent intent = new Intent(this.getActivity(), ImageActivity.class);
        intent.putExtra("id", id);
        getActivity().startActivity(intent);


    }

    @JavascriptInterface
    public void viewVideo(String id,String allIds){

        AppData.getInstance().setAllImgIds(allIds);

        Intent intent = new Intent(this.getActivity(), VideoActivity.class);
        intent.putExtra("id", id);
        getActivity().startActivity(intent);


    }

    private void setStatusMsg(final String statusMsg) {
        statusText.post(new Runnable() {
            public void run() {
                statusText.setText(statusMsg);
            }
        });
    }

    private void setProgress(final int progress) {
        progressBar.post(new Runnable() {
            public void run() {
                progressBar.setProgress(progress);
            }
        });
    }

    public WebView getWebView() {
        return ((WebView) getView().findViewById(R.id.webView));
    }
}
