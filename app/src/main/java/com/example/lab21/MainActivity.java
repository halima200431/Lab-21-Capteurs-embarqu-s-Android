package com.example.lab21;

import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import com.example.lab21.fragments.ActivityRecognitionFragment;
import com.example.lab21.fragments.CompassFragment;
import com.example.lab21.fragments.MotionSensorFragment;
import com.example.lab21.fragments.SensorGraphFragment;
import com.example.lab21.fragments.SensorsListFragment;
import com.example.lab21.fragments.StepCounterFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Important :
         * Si ton template Android Studio a ajouté EdgeToEdge.enable(this),
         * supprime cette ligne. Sinon la toolbar peut passer sous la barre d’état.
         */

        getWindow().setStatusBarColor(Color.parseColor("#5A189A"));

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Création du bouton hamburger qui ouvre le menu latéral.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Sécurité : même si le toggle ne capte pas le clic, on force l’ouverture du menu.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Activation des clics sur les éléments du menu.
        navigationView.setNavigationItemSelectedListener(this);

        // Fragment affiché au démarrage.
        if (savedInstanceState == null) {
            openFragment(new SensorsListFragment());
            navigationView.setCheckedItem(R.id.nav_sensors);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Tous les capteurs");
            }
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_sensors) {
            toolbar.setTitle("Tous les capteurs");
            openFragment(new SensorsListFragment());

        } else if (id == R.id.nav_temperature) {
            toolbar.setTitle("Température");
            openFragment(SensorGraphFragment.newInstance(
                    Sensor.TYPE_AMBIENT_TEMPERATURE,
                    "Température ambiante",
                    "°C",
                    SensorGraphFragment.MODE_FIRST_VALUE
            ));

        } else if (id == R.id.nav_humidity) {
            toolbar.setTitle("Humidité");
            openFragment(SensorGraphFragment.newInstance(
                    Sensor.TYPE_RELATIVE_HUMIDITY,
                    "Humidité relative",
                    "%",
                    SensorGraphFragment.MODE_FIRST_VALUE
            ));

        } else if (id == R.id.nav_proximity) {
            toolbar.setTitle("Proximité");
            openFragment(SensorGraphFragment.newInstance(
                    Sensor.TYPE_PROXIMITY,
                    "Détection de proximité",
                    "cm",
                    SensorGraphFragment.MODE_FIRST_VALUE
            ));

        } else if (id == R.id.nav_magnetic) {
            toolbar.setTitle("Champ magnétique");
            openFragment(SensorGraphFragment.newInstance(
                    Sensor.TYPE_MAGNETIC_FIELD,
                    "Champ magnétique",
                    "µT",
                    SensorGraphFragment.MODE_MAGNITUDE
            ));

        } else if (id == R.id.nav_accelerometer) {
            toolbar.setTitle("Accéléromètre");
            openFragment(MotionSensorFragment.newInstance(
                    Sensor.TYPE_ACCELEROMETER,
                    "Accéléromètre",
                    "m/s²"
            ));

        } else if (id == R.id.nav_gravity) {
            toolbar.setTitle("Gravité");
            openFragment(MotionSensorFragment.newInstance(
                    Sensor.TYPE_GRAVITY,
                    "Capteur de gravité",
                    "m/s²"
            ));

        } else if (id == R.id.nav_gyroscope) {
            toolbar.setTitle("Gyroscope");
            openFragment(MotionSensorFragment.newInstance(
                    Sensor.TYPE_GYROSCOPE,
                    "Gyroscope",
                    "rad/s"
            ));

        } else if (id == R.id.nav_steps) {
            toolbar.setTitle("Compteur de pas");
            openFragment(new StepCounterFragment());

        } else if (id == R.id.nav_compass) {
            toolbar.setTitle("Boussole");
            openFragment(new CompassFragment());

        } else if (id == R.id.nav_activity) {
            toolbar.setTitle("Reconnaissance d’activité");
            openFragment(new ActivityRecognitionFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Si le drawer est ouvert, retour le ferme seulement.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}