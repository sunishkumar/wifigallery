package com.kumar.sunish.mygallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    Intent serverService = null;



    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Server.
     */
    // TODO: Rename and change types and number of parameters
    public static ServerFragment newInstance(String param1) {
        ServerFragment fragment = new ServerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    public ServerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view
                = inflater.inflate(R.layout.fragment_server, container, false);

        Button startServerbt = (Button) view.findViewById(R.id.startServer);
        startServerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });

        Button stopServerBt = (Button) view.findViewById(R.id.stopServer);
        stopServerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopServer();
            }
        });

        serverService = new Intent(getActivity().getBaseContext(), HttpdService.class) ;

        return view;
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

    public void stopServer() {

        getActivity().stopService(serverService);
        ((TextView) getView().findViewById(R.id.clientUrl)).setText("");
        ( (ImageView) getView().findViewById(R.id.barCodeImage)) .setImageBitmap(null);
    }

    public void startServer() {

        getActivity().startService(serverService);

        displayUrl(false);



    }

    public void displayUrl(boolean checkStatus) {

        if(checkStatus==false || AppData.getInstance().isServerRunning()==true){
            ((TextView) getView().findViewById(R.id.clientUrl)).setText("http://" + ServerUtil.getIpAddress() + ":" + MyHTTPD.PORT);


            try {
                Bitmap bm = ServerUtil.encodeAsBitmap("http://" + ServerUtil.getIpAddress() + ":" + MyHTTPD.PORT, BarcodeFormat.QR_CODE, 150, 150);
                if(bm != null) {
                    ( (ImageView) getView().findViewById(R.id.barCodeImage)) .setImageBitmap(bm);
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }


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


}
