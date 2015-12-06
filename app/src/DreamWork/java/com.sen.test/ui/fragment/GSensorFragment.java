package com.sen.test.ui.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sen.test.R;

import java.util.List;

/**
 * Created by Senny on 2015/11/30.
 */
public class GSensorFragment extends Fragment {

    private SensorManager sensorManager;
    private TextView textView;
    private ScrollView scrollView;
    private SensorEventListener sensorEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gsensor, null);
        textView = (TextView) view.findViewById(R.id.fragment_gsensor_result);
        scrollView = (ScrollView) view.findViewById(R.id.fragment_gsensor_result_scroll);
        sensorManager = (SensorManager) container.getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
        if (sensors != null && sensors.size() > 0) {
            sensorEventListener = new SensorEventListener() {

//                boolean isBlnStart;
                float lastValue;
                boolean isBlnRotating;
                final float DEFAULT_OFFSET = 0.3f;
                final float MAX_ROTATE_VALUE = -6.0f;

                @Override
                public void onSensorChanged(SensorEvent event) {

                    if (!isBlnRotating && DEFAULT_OFFSET > event.values[2] && lastValue > 0.0f) {
                        isBlnRotating = true;
                        textView.setText("");
                    }
                    if (isBlnRotating) {
                        float offset = event.values[2] - lastValue;
                        textView.setText(textView.getText() + "\n" + offset);
                        scrollView.scrollTo(0, textView.getHeight());
                        if (0 > offset) {
//                            lastValue = event.values[2];
                        } else {
                            isBlnRotating = false;
                        }

                    }
                    if (isBlnRotating) {
                        if (MAX_ROTATE_VALUE > lastValue) {
                            textView.setText(textView.getText() + "\n Go go go!");
                            scrollView.scrollTo(0, textView.getHeight());
                            isBlnRotating = false;
                        }
                    }
                    lastValue = event.values[2];
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
//                    textView.setText(textView.getEditableText() + "\n" + sensor.getPower()+ " " + accuracy);
                }
            };
            sensorManager.registerListener(sensorEventListener, sensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
        super.onDestroyView();
    }
}
