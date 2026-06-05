package com.example.lab21.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21.utils.SensorEnvironment;
import com.example.lab21.utils.UiKit;
import com.example.lab21.views.LineChartView;

public class MotionSensorFragment extends Fragment implements SensorEventListener {

    private static final String ARG_SENSOR_TYPE = "sensor_type";
    private static final String ARG_TITLE = "title";
    private static final String ARG_UNIT = "unit";

    private SensorManager sensorManager;
    private Sensor sensor;

    private TextView valuesView;
    private TextView statusView;
    private LineChartView chartView;

    private int sensorType;
    private String title;
    private String unit;

    private boolean simulationMode = false;
    private float time = 0f;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public static MotionSensorFragment newInstance(int sensorType, String title, String unit) {
        MotionSensorFragment fragment = new MotionSensorFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SENSOR_TYPE, sensorType);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_UNIT, unit);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorType = requireArguments().getInt(ARG_SENSOR_TYPE);
        title = requireArguments().getString(ARG_TITLE);
        unit = requireArguments().getString(ARG_UNIT);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(sensorType);

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout root = UiKit.createScrollableRoot(requireContext(), scrollView);

        root.addView(UiKit.title(requireContext(), title));
        root.addView(UiKit.subtitle(
                requireContext(),
                "Affichage corrigé des axes X, Y, Z et de la norme."
        ));

        valuesView = UiKit.valueChip(requireContext(), "X : --\nY : --\nZ : --\nNorme : --");
        statusView = UiKit.body(requireContext(), "Initialisation...");

        chartView = new LineChartView(requireContext());
        chartView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                UiKit.dp(requireContext(), 330)
        ));

        LinearLayout card = UiKit.createCard(requireContext());
        card.addView(valuesView);
        card.addView(statusView);
        card.addView(chartView);

        root.addView(card);

        return scrollView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SensorEnvironment.isEmulator()) {
            simulationMode = true;
            statusView.setText("Émulateur détecté : simulation activée pour un affichage dynamique.");
            startSimulation();
            return;
        }

        if (sensor != null) {
            simulationMode = false;
            statusView.setText("Capteur réel actif : " + sensor.getName());

            sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        } else {
            simulationMode = true;
            statusView.setText("Capteur absent : simulation activée.");
            startSimulation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (simulationMode) {
            return;
        }

        if (event.values.length < 3) {
            return;
        }

        updateUi(event.values[0], event.values[1], event.values[2]);
    }

    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time++;

                float[] values = SensorEnvironment.simulateMotionValues(sensorType, time);

                updateUi(values[0], values[1], values[2]);

                handler.postDelayed(this, 700);
            }
        }, 400);
    }

    private void updateUi(float x, float y, float z) {
        float magnitude = SensorEnvironment.magnitude(x, y, z);

        valuesView.setText(
                "X : " + String.format("%.2f", x) + " " + unit + "\n"
                        + "Y : " + String.format("%.2f", y) + " " + unit + "\n"
                        + "Z : " + String.format("%.2f", z) + " " + unit + "\n"
                        + "Norme : " + String.format("%.2f", magnitude)
        );

        chartView.addValue(magnitude);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}