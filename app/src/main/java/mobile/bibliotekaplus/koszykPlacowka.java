package mobile.bibliotekaplus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class koszykPlacowka extends AppCompatActivity implements OnMapReadyCallback {
    private GlobalClass globalClass;

    private static final String TAG = "MapActivity";


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERIMISION_REQUEST_CODE = 1234;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    TextView selectedMarkerTextView;
    TextView selectedMarkerAdres;
    String selectedMarkerName = null;
    String selectedMarkerUlica = null;

    Button realizujButton;

    ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koszyk_placowka);
        globalClass =(GlobalClass) getApplicationContext();
        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.step_view);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Koszyk",1);
        StepBean stepBean1 = new StepBean("Placówka",0);
        StepBean stepBean2 = new StepBean("Kaucja",-1);
        StepBean stepBean3 = new StepBean("Realizacja",-1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);
        setpview5
                .setStepViewTexts(stepsBeanList)//总步骤
                .setTextSize(12)//set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(koszykPlacowka.this, android.R.color.white))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(koszykPlacowka.this, R.color.uncompleted_text_color))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(koszykPlacowka.this, android.R.color.white))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(koszykPlacowka.this, R.color.uncompleted_text_color))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(koszykPlacowka.this, R.drawable.complted))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(koszykPlacowka.this, R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(koszykPlacowka.this, R.drawable.attention));//设置StepsViewIndicator AttentionIcon




        final ProgressBar myProgressBar= findViewById(R.id.myProgressBar);
        SearchView mySearchView=findViewById(R.id.mySearchView);

        selectedMarkerTextView = findViewById(R.id.selectedMarker);
        selectedMarkerAdres = findViewById(R.id.selectedMarkerAdres);

        getLocationPermission();

        realizujButton = findViewById(R.id.btnranking);



    }
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the currentlocation");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "found location");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 10f);

                            findClosestMarker(currentLocation);
                        } else {
                            Log.d(TAG, "null");
                            Toast.makeText(koszykPlacowka.this, "Nie odnaleziono lokalizacji", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: errors:" + e.getMessage());
        }
    }

    private void findClosestMarker(Location currentLocation) {
        final double currentUserLatitude = currentLocation.getLatitude();
        final double currentUserLongitude = currentLocation.getLongitude();


        db.collection("placowki")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double najmniejszaOdleglosc = Double.MAX_VALUE;
                            String najblizszyOddzialName = "";
                            String najblizszyOddzialUlica = "";
                            String najblizszyOddzialId = "";


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geoPoint = document.getGeoPoint("Geopoint");
                                String id = document.getId();
                                String nazwaOddzialu = document.getString("nazwaOddzialu");
                                String adres = document.getString("Adres");
                                LatLng latlngDoc = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());


                                //mMap.addMarker(new MarkerOptions().position(latlngDoc).title(nazwaOddzialu+"\n"+"Adres: "+adres).snippet(id));
                                //Toast.makeText(koszykPlacowka.this, document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();

                                float[] result = new float[1];
                                Location.distanceBetween(currentUserLatitude, currentUserLongitude, geoPoint.getLatitude(), geoPoint.getLongitude(), result);
                                Log.d("Odleglosc", "result: " + " => " + result[0]);
                                if(result[0] < najmniejszaOdleglosc)
                                {
                                    Log.d("Odleglosc", "result: " + " => " + result[0] + " jest najmniejsza");
                                    Log.d("Odleglosc", "nazwaOddzialu: " + " => " + nazwaOddzialu );
                                    Log.d("Odleglosc", "adres: " + " => " + adres );
                                    Log.d("Odleglosc", "id: " + " => " + id );
                                    najmniejszaOdleglosc = result[0];
                                    najblizszyOddzialName = nazwaOddzialu;
                                    najblizszyOddzialUlica = adres;
                                    najblizszyOddzialId = id;
                                }
                            }

                            selectedMarkerTextView.setText("Najbliższa placówka: "+ najblizszyOddzialName +"\n"+"Adres: "+ najblizszyOddzialUlica);
//
                            for (Marker marker : markers) {
                                if(marker.getSnippet().equals(najblizszyOddzialId))
                                {
                                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    selectedMarkerName = marker.getTitle();
                                    selectedMarkerUlica = marker.getSnippet();
                                    globalClass.setPlacowka(selectedMarkerUlica);
                                    marker.showInfoWindow();
                                }
                            }

                        } else {
                            String taskExc = task.getException()+"";
                            //Toast.makeText(koszykPlacowka.this,taskExc , Toast.LENGTH_SHORT).show();

                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void initMap() {
        Log.d(TAG, "onMapReady: map is initialized");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(koszykPlacowka.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "onMapReady: map is getting");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERIMISION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERIMISION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onMapReady: map is called");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERIMISION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;

                            return;
                        }
                    }

                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("placowki");

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        db.collection("placowki")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                GeoPoint geoPoint = document.getGeoPoint("Geopoint");
                                String id = document.getId();
                                String nazwaOddzialu = document.getString("nazwaOddzialu");
                                String adres = document.getString("Adres");
                                LatLng latlngDoc = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                                Marker markerName = mMap.addMarker(new MarkerOptions().position(latlngDoc).title(nazwaOddzialu+"\n"+"Adres: "+adres).snippet(id));
                                //Toast.makeText(koszykPlacowka.this, document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
                                markers.add(markerName);
                            }
                        } else {
                            String taskExc = task.getException()+"";
                            //Toast.makeText(koszykPlacowka.this,taskExc , Toast.LENGTH_SHORT).show();

                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (selectedMarkerName == null) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    selectedMarkerName = marker.getTitle();
                    selectedMarkerUlica = marker.getSnippet();
                    selectedMarkerTextView.setText("Wybrana placówka: "+ selectedMarkerName);
                    //selectedMarkerAdres.setText("ID:"+ selectedMarkerUlica);
                    globalClass.setPlacowka(selectedMarkerUlica);
                    marker.showInfoWindow();
                }
                else
                {
                    if(selectedMarkerName.equals(marker.getTitle()))
                    {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        selectedMarkerName = null;
                        selectedMarkerUlica = null;
                        selectedMarkerTextView.setText("Wybierz placówkę");
                        selectedMarkerAdres.setText("");
                    }
                }
                return true;
            }
        });
    }

    public void OnClickRealizuj(View view) {
        if (selectedMarkerName == null) {
            Toast.makeText(koszykPlacowka.this, "Wybierz placówkę!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, Kaucja.class);
            intent.putExtra("placowka", selectedMarkerName);
            intent.putExtra("adres", selectedMarkerUlica);
            this.startActivity ( intent );
        }

    }
}