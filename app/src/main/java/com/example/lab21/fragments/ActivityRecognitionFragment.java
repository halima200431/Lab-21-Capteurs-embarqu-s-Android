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

import java.util.LinkedList;
import java.util.Queue;

import com.example.lab21.utils.SensorEnvironment;
import com.example.lab21.utils.UiKit;
import com.example.lab21.views.LineChartView;

public class ActivityRecognitionFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView resultView;
    private TextView detailsView;
    private TextView statusView;
    private LineChartView chartView;

    private final float[] gravity = new float[3];
    private final Queue<Float> movementWindow = new LinkedList<>();

    private static final int WINDOW_SIZE = 30;
    private static final float ALPHA = 0.8f;

    private float time = 0f;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout root = UiKit.createScrollableRoot(requireContext(), scrollView);

        root.addView(UiKit.title(requireContext(), "Reconnaissance d’activité"));
        root.addView(UiKit.subtitle(
                requireContext(),
                "Correction : activité simulée sur émulateur, détection réelle sur téléphone."
        ));

        resultView = UiKit.valueChip(requireContext(), "Activité : --");
        statusView = UiKit.body(requireContext(), "Initialisation...");
        detailsView = UiKit.body(requireContext(), "Données : --");

        chartView = new LineChartView(requireContext());
        chartView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                UiKit.dp(requireContext(), 330)
        ));

        LinearLayout card = UiKit.createCard(requireContext());
        card.addView(resultView);
        card.addView(statusView);
        card.addView(detailsView);
        card.addView(chartView);

        root.addView(card);

        return scrollView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SensorEnvironment.isEmulator()) {
            statusView.setText("Émulateur détecté : scénario d’activité simulé.");
            startSimulation();
            return;
        }

        if (accelerometer != null) {
            statusView.setText("Accéléromètre réel actif : " + accelerometer.getName());

            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME
            );
        } else {
            statusView.setText("Accéléromètre absent : simulation activée.");
            startSimulation();
        }
    }

    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time++;

                float movement;
                String activity;

                int phase = ((int) time / 8) % 4;

                if (phase == 0) {
                    movement = 0.15f + (float) Math.sin(time) * 0.05f;
                    activity = "Stable";
                } else if (phase == 1) {
                    movement = 1.8f + Math.abs((float) Math.sin(time)) * 1.2f;
                    activity = "Marche";
                } else if (phase == 2) {
                    movement = 11f + Math.abs((float) Math.sin(time)) * 3f;
                    activity = "Saut / mouvement brusque";
                } else {
                    movement = 0.8f + Math.abs((float) Math.sin(time)) * 0.4f;
                    activity = "Position inclinée";
                }

                resultView.setText("Activité : " + activity);

                detailsView.setText(
                        "Mode : simulation\n"
                                + "Mouvement filtré : " + String.format("%.2f", movement) + "\n"
                                + "Le scénario change automatiquement toutes les quelques secondes."
                );

                chartView.addValue(movement);

                handler.postDelayed(this, 700);
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        float linearX = x - gravity[0];
        float linearY = y - gravity[1];
        float linearZ = z - gravity[2];

        float movement = SensorEnvironment.magnitude(linearX, linearY, linearZ);

        addMovementValue(movement);

        String activity = classifyActivity(x, y, z);

        resultView.setText("Activité : " + activity);

        detailsView.setText(
                "X : " + String.format("%.2f", x) + "\n"
                        + "Y : " + String.format("%.2f", y) + "\n"
                        + "Z : " + String.format("%.2f", z) + "\n\n"
                        + "Mouvement filtré : " + String.format("%.2f", movement)
        );

        chartView.addValue(movement);
    }

    private void addMovementValue(float movement) {
        if (movementWindow.size() >= WINDOW_SIZE) {
            movementWindow.poll();
        }

        movementWindow.add(movement);
    }

    private String classifyActivity(float x, float y, float z) {

        if (movementWindow.size() < WINDOW_SIZE) {
            return "Calibration...";
        }

        float mean = 0f;
        float max = 0f;

        for (float value : movementWindow) {
            mean += value;
            max = Math.max(max, value);
        }

        mean = mean / movementWindow.size();

        float variance = 0f;

        for (float value : movementWindow) {
            variance += (value - mean) * (value - mean);
        }

        variance = variance / movementWindow.size();

        float standardDeviation = (float) Math.sqrt(variance);

        if (max > 10f) {
            return "Saut / mouvement brusque";
        }

        if (standardDeviation > 1.2f) {
            return "Marche";
        }

        if (Math.abs(z) > 8f) {
            return "Téléphone stable à plat";
        }

        if (Math.abs(x) > 7f || Math.abs(y) > 7f) {
            return "Position verticale ou inclinée";
        }

        return "Stable";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}