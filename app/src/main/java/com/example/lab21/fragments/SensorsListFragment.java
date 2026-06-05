package com.example.lab21.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import com.example.lab21.utils.SensorFormatter;
import com.example.lab21.utils.UiKit;

public class SensorsListFragment extends Fragment {

    private SensorManager sensorManager;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout root = UiKit.createScrollableRoot(requireContext(), scrollView);

        root.addView(UiKit.title(requireContext(), "Catalogue des capteurs"));
        root.addView(UiKit.subtitle(
                requireContext(),
                "Cette page affiche les capteurs disponibles avec leurs caractéristiques techniques."
        ));

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        displaySensors(root);

        return scrollView;
    }

    private void displaySensors(LinearLayout root) {
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if (sensors.isEmpty()) {
            root.addView(UiKit.body(requireContext(), "Aucun capteur détecté sur cet appareil."));
            return;
        }

        for (Sensor sensor : sensors) {
            LinearLayout card = UiKit.createCard(requireContext());

            card.addView(UiKit.title(requireContext(), sensor.getName()));
            card.addView(UiKit.subtitle(requireContext(), "Type Android : " + sensor.getStringType()));
            card.addView(UiKit.body(requireContext(), SensorFormatter.format(sensor)));

            root.addView(card);
        }
    }
}