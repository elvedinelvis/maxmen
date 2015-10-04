package maxmen.maxmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //private Marker markers[]; //Array with markers for future use
    private Marker m1;
    BroadcastReceiver receiver;
    Intent serviceIntent;

    //Bestämmer storleken på "ramen" på kartan man får scrolla runt på
    final private double maxLat = 57.722533, minLat = 57.678455,
            maxLng = 12.009791, minLng = 11.911886;

    //Bestämmer max/min zoom-nivå
    final private float maxZoomLvl = 18, minZoomLvl = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Double lat = Double.parseDouble(intent.getStringExtra("latitude"));
                Double lng = Double.parseDouble(intent.getStringExtra("longitude"));

                m1.setPosition(new LatLng(lat,lng));
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        serviceIntent = new Intent(getApplicationContext(),
                UpdaterService.class);
        startService(serviceIntent);

        registerReceiver(receiver, new IntentFilter(
                UpdaterService.BROADCAST_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopService(serviceIntent);
        unregisterReceiver(receiver);

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //ADD COMMENT HERE
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(outsideFrame(
                                mMap.getCameraPosition().target.latitude,
                                mMap.getCameraPosition().target.longitude),
                        zoomLimit(mMap.getCameraPosition().zoom)));
            }
        });

        //Sets the camera-view on start and adds bus marker(s)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(57.696994, 11.9865), 12));
        m1 = mMap.addMarker(new MarkerOptions().position(new LatLng(57.696994, 11.9865)).title("SimulatedBus"));
    }

    /**
     * Sätter begränsningar på zoom.
     * @param zoom  Nuvarande zoom-storlek
     * @return Ny zoom-storlek
     */
    private float zoomLimit(float zoom) {
        float newZoom = zoom;
        if (zoom > maxZoomLvl)  newZoom = maxZoomLvl-1; //-1 Enbart för "snyggheten"
        if (zoom < minZoomLvl)  newZoom = minZoomLvl;
        return newZoom;
    }

    /**
     * Ser till så att man inte kan ha kartan på andra ställen än begränsningsramen.
     * @param currentLat    Nuvarande latitud på kartan
     * @param currentLng    Nuvarande longitud på kartan
     * @return En LatLng med nya lat/lng-värden som är inom begränsningsramen.
     */
    private LatLng outsideFrame(double currentLat, double currentLng) {
        double newLat = currentLat;
        double newLng = currentLng;
        boolean outsideFrame = true;
        if (outsideFrame) {
            if (currentLat > maxLat)    newLat = maxLat - 0.0005; //0.0005 avrundar positionen
            if (currentLat < minLat)    newLat = minLat + 0.0005;
            if (currentLng > maxLng)    newLng = maxLng - 0.0005;
            if (currentLng < minLng)    newLng = minLng + 0.0005;
            if ((currentLat < maxLat) && (currentLat > minLat) && (currentLng < maxLng) && (currentLng > minLng))
                outsideFrame = false;
        }
        return (new LatLng(newLat, newLng));
    }

}
