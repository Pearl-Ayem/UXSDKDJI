package com.dji.uxsdkdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.maps.android.SphericalUtil.computeHeading;

/**
 * Created by Pearl on 2018-07-26.
 */

public class Heading extends DialogFragment {
    private static final String TAG = "Heading Fragment";

    public interface onInputListener {
        void sendInput(LatLng o, LatLng d);
    }

    public onInputListener mOnInputListener;

    private EditText mHeadingOrigin;
    private EditText mHeadingDest;
    private TextView mHeading, mActionOk, mActionCancel;

    private String origin;
    private String tie_point;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heading_fragment, container, false);

        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        mHeadingOrigin = view.findViewById(R.id.origin);
        mHeadingDest = view.findViewById(R.id.dest);
        mHeading = view.findViewById(R.id.heading);
        origin = null;
        tie_point = null;


        mHeadingOrigin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_BACK
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    origin = textView.getText().toString();
                    updateHeading();
                }
                return false;
            }
        });

        mHeadingDest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_BACK
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    tie_point = textView.getText().toString();
                    updateHeading();
                }
                return false;
            }
        });

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });


        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");
//                setMarkers();

                mOnInputListener.sendInput(convertoLatLon(origin), convertoLatLon(tie_point));
                getDialog().dismiss();
            }
        });

        return view;

    }

    private void updateHeading() {
        try {
            double heading = getHeading(origin, tie_point);
            mHeading.setText(" Heading: " + getHeading(origin, tie_point));
        } catch (NullPointerException e) {
            //do nothing
        }
        catch (NumberFormatException n){
            //do nothing
        }
    }

//    private void setMarkers() {
//        if (!origin.isEmpty() && !tie_point.isEmpty()) {
//            MarkerOptions originMarkerOptions = new MarkerOptions().position(convertoLatLon(origin)).title("Origin")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//            ((MapsActivity) getActivity()).originMarker = ((MapsActivity) getActivity()).mMap.addMarker(originMarkerOptions);
//
//
//            MarkerOptions destMarkerOptions = new MarkerOptions().position(convertoLatLon(tie_point)).title("Tie-Point");
////                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            ((MapsActivity) getActivity()).destMarker = ((MapsActivity) getActivity()).mMap.addMarker(destMarkerOptions);
//        } else {
//            //do nothing
//        }
//
//    }

    private double getHeading(String origin, String dest) {
        return computeHeading(convertoLatLon(origin), convertoLatLon(dest));
    }


    private LatLng convertoLatLon(String input) {
       try{
           String[] latlonString = input.split(",");
           double lat = Double.parseDouble(latlonString[0]);
           double lon = Double.parseDouble(latlonString[1]);
           LatLng latlon = new LatLng(lat, lon);
           return latlon;
       } catch (NullPointerException e){
           //do nothing
       }

       catch (NumberFormatException e){
           //do nothing
       }

       return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            try {
                mOnInputListener = (onInputListener)getActivity();
            } catch (ClassCastException e) {
                Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
