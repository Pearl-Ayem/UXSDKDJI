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
        void sendInput(LatLng o, LatLng d, Double h);
    }

    public onInputListener mOnInputListener;

    private EditText mHeadingOrigin;
    private EditText mHeadingDest;
    private TextView mHeading, mActionOk, mActionCancel;

    private LatLng origin;
    private LatLng tie_point;
    private Double headingCalc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.heading_fragment, container, false);

        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        mHeadingOrigin = (EditText) view.findViewById(R.id.origin);
        mHeadingDest = (EditText) view.findViewById(R.id.dest);
        mHeading = view.findViewById(R.id.heading);


        if (((MapsActivity) getActivity()).getHeadingOrg()!= null) {
            origin = ((MapsActivity) getActivity()).getHeadingOrg();
        } else {
            origin = null;
        }

        if (((MapsActivity) getActivity()).getHeadingDest() !=null) {
            tie_point = ((MapsActivity) getActivity()).getHeadingDest();
        } else {
            tie_point = null;
        }

        updateHeading();

//        headingCalc = null;


        mHeadingOrigin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_NEXT
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    origin = convertoLatLon(textView.getText().toString());
//                    updateHeading();
                    return true;
                }

                return false;
            }
        });

        mHeadingDest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    tie_point = convertoLatLon(textView.getText().toString());
                    updateHeading();
                    return true;
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
//                updateHeading();
                mOnInputListener.sendInput(origin,tie_point, headingCalc);
                getDialog().dismiss();
            }
        });

        return view;

    }

    private void updateHeading() {
        try {
            double heading = getHeading(origin, tie_point);
            mHeading.setText(" Heading: " + heading);
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    private double getHeading(LatLng o, LatLng dest) {
        double heading = computeHeading(origin,tie_point);
        headingCalc = heading;
        return heading;
    }


    private LatLng convertoLatLon(String input) {
        try {
            String[] latlonString = input.split(",");
            double lat = Double.parseDouble(latlonString[0]);
            double lon = Double.parseDouble(latlonString[1]);
            LatLng latlon = new LatLng(lat, lon);
            return latlon;
        } catch (NullPointerException e) {
            //do nothing
        } catch (NumberFormatException e) {
            //do nothing
        }

        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (onInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
