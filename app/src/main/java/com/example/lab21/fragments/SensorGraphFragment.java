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

public class SensorGraphFragment extends Fragment implements SensorEventListener {

    public static final String MODE_FIRST_VALUE = "FIRST_VALUE";
    public static final String MODE_MAGNITUDE = "MAGNITUDE";

    private static final String ARG_SENSOR_TYPE = "sensor_type";
    private static final String ARG_TITLE = "title";
    private static final String ARG_UNIT = "unit";
    private static final String ARG_MODE = "mode";

    private SensorManager sensorManager;
    private Sensor sensor;

    private TextView valueView;
    private TextView statusView;
    private LineChartView chartView;

    private int sensorType;
    private String title;
    private String unit;
    private String mode;

    private boolean simulationMode = false;
    private float time = 0f;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public static SensorGraphFragment newInstance(
            int sensorType,
            String title,
            String unit,
            String mode) {

        SensorGraphFragment fragment = new SensorGraphFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SENSOR_TYPE, sensorType);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_UNIT, unit);
        args.putString(ARG_MODE, mode);

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
        mode = requireArguments().getString(ARG_MODE);

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(sensorType);

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout root = UiKit.createScrollableRoot(requireContext(), scrollView);

        root.addView(UiKit.title(requireContext(), title));
        root.addView(UiKit.subtitle(
                requireContext(),
                "Mode corrigé : données réelles sur téléphone, simulation propre sur émulateur."
        ));

        valueView = UiKit.valueChip(requireContext(), "Valeur : --");
        statusView = UiKit.body(requireContext(), "Initialisation du capteur...");

        chartView = new LineChartView(requireContext());
        chartView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                UiKit.dp(requireContext(), 330)
        ));

        LinearLayout card = UiKit.createCard(requireContext());
        card.addView(valueView);
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
            statusView.setText("Émulateur détecté : simulation activée pour éviter les valeurs Goldfish incorrectes.");
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

        float value = readValue(event.values);

        if (!SensorEnvironment.isValidScalarValue(sensorType, value)) {
            simulationMode = true;
            sensorManager.unregisterListener(this);
            statusView.setText("Valeur invalide détectée : passage automatique en simulation.");
            startSimulation();
            return;
        }

        updateUi(value);
    }

    private float readValue(float[] values) {
        if (MODE_MAGNITUDE.equals(mode) && values.length >= 3) {
            return SensorEnvironment.magnitude(values[0], values[1], values[2]);
        }

        return values[0];
    }

    private void updateUi(float value) {
        valueView.setText("Valeur : " + String.format("%.2f", value) + " " + unit);
        chartView.addValue(value);
    }

    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time++;

                float value = SensorEnvironment.simulateScalarValue(sensorType, time);

                updateUi(value);

                handler.postDelayed(this, 800);
            }
        }, 400);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}