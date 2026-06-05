package com.example.lab21.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lab21.utils.SensorEnvironment;
import com.example.lab21.utils.UiKit;

public class StepCounterFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private TextView stepsView;
    private TextView statusView;

    private float initialSteps = -1;
    private int simulatedSessionSteps = 0;
    private int simulatedTotalSteps = 1240;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            startRealSensor();
                        } else {
                            statusView.setText("Permission refusée. Simulation activée pour la démonstration.");
                            startSimulation();
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout root = UiKit.createScrollableRoot(requireContext(), scrollView);

        root.addView(UiKit.title(requireContext(), "Compteur de pas"));
        root.addView(UiKit.subtitle(
                requireContext(),
                "Correction : simulation automatique sur émulateur, vrai compteur sur téléphone."
        ));

        stepsView = UiKit.valueChip(requireContext(), "Pas : --");
        statusView = UiKit.body(requireContext(), "Initialisation...");

        LinearLayout card = UiKit.createCard(requireContext());
        card.addView(stepsView);
        card.addView(statusView);

        root.addView(card);

        return scrollView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SensorEnvironment.isEmulator()) {
            statusView.setText("Émulateur détecté : compteur de pas simulé.");
            startSimulation();
            return;
        }

        if (stepCounterSensor == null) {
            statusView.setText("Capteur de pas absent. Simulation activée.");
            startSimulation();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
        ) != PackageManager.PERMISSION_GRANTED) {

            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            startRealSensor();
        }
    }

    private void startRealSensor() {
        statusView.setText("Capteur réel actif : " + stepCounterSensor.getName());

        sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simulatedSessionSteps += 1;
                simulatedTotalSteps += 1;

                stepsView.setText(
                        "Depuis redémarrage : " + simulatedTotalSteps
                                + "\nSession : " + simulatedSessionSteps
                );

                handler.postDelayed(this, 1200);
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
        float totalStepsSinceBoot = event.values[0];

        if (initialSteps < 0) {
            initialSteps = totalStepsSinceBoot;
        }

        int sessionSteps = (int) (totalStepsSinceBoot - initialSteps);

        stepsView.setText(
                "Depuis redémarrage : " + (int) totalStepsSinceBoot
                        + "\nSession : " + sessionSteps
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}