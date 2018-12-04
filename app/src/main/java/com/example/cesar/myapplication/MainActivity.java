package com.example.cesar.myapplication;

//Lista de Imports utilizados

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private Button acao;
    private GoogleMap mMap;
    TextView txtLatitude, txtLongitude;

// Solicita Permissão para GPS
    private void Permissoes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            configurarServico();
        }
    }
// Configura o serviço de localização
    public void configurarServico() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
// Atualiza localização em tempo real
    @SuppressLint("SetTextI18n")
    public void atualizar(Location location) {
        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();

        txtLatitude.setText(latPoint.toString());
        txtLongitude.setText(lngPoint.toString());

    }
// Verificar conexão com a internet
    public void verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
            enviaServidor();
        } else {
            conectado = false;
            enviaSMS();
        }
    }

//Envia por SMS
    public void enviaSMS() {
        //String numero = numeroTel();
        String latitude = txtLatitude.getText().toString();
        String longitude = txtLongitude.getText().toString();

        //SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(numero, null, latitude + longitude, null, null);

        Toast.makeText(getApplicationContext(), "SMS com Latitude: " + latitude + " e longitude: " + longitude + " enviado com sucesso!", Toast.LENGTH_SHORT).show();
    }
//Envia pelo Servidor
    public void enviaServidor() {
        String latitude = txtLatitude.getText().toString();
        String longitude = txtLongitude.getText().toString();
        Toast.makeText(getApplicationContext(), "Latitude: " + latitude + " e longitude: " + longitude + " enviado com sucesso!", Toast.LENGTH_SHORT).show();
    }

// Configura o mapa
        @Override
        public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng latlng = new LatLng(-30, -51);
        final Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title("Posição atual"));
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    marker.setPosition(latlng);
                    float zoomLevel = 16.0f; //Vai até 21
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } catch (SecurityException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
// Verifica confirmação de permissão
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configurarServico();
                } else {
                    Toast.makeText(this, "Não vai funcionar!!!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
//Botão Power (clique simples)
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {

            event.startTracking(); // Necessário para identificar cliques longos
            Toast.makeText(getApplicationContext(), "Solicitação enviada!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//Botão Power (clique longo)
        @Override
        public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_POWER) {

            Toast.makeText(getApplicationContext(), "Solicitação enviada!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

// Criador que atribui ações ao botão
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        Permissoes();

        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        acao = (Button) findViewById(R.id.acao);
        acao.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                verificaConexao();
            }
        });
    }
}