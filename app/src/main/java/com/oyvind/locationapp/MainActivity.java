package com.oyvind.locationapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    SQLiteDatabase myDatabase;
    LocationManager locationManager;
    String provider;//lagrer navnet på provideren her, enten om det er GPS eller nettverk
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //context = telefon/tablet/emulator
        provider = locationManager.getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(provider); //husk permissions i manifest.xml

        if(location != null){
            Log.i("Location info", "Location achieved");
        }else{
            Log.i("Location info", "No location :(");
        }

        //lagt til nå
        try{
            myDatabase = this.openOrCreateDatabase("storedLocations", MODE_PRIVATE, null);
            //myDatabase.execSQL("DROP TABLE locations");
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS locations (latitude VARCHAR, longitude VARCHAR)");
            myDatabase.execSQL("INSERT INTO locations (latitude, longitude) VALUES ('1','1')");
            Cursor c = myDatabase.rawQuery("SELECT * FROM locations", null);
            Log.i("DB", "Database created succesfully");
            int latIndex = c.getColumnIndex("latitude");
            int lngIndex = c.getColumnIndex("longitude");

            c.moveToFirst();

            while(c != null){
                result += "Lat:" + c.getString(latIndex) + "Lng: " +  c.getString(latIndex) + "  ";
                //Log.i("Latitude", c.getString(latIndex));
                //Log.i("Longitude", c.getString(lngIndex));
                Log.i("result: ", result);
                c.moveToNext();
            }


        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);//gps, oppdateringsfrekvens, avstand i meter, context
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);//kjører ikke oppdateringer på GPS hvis jeg lukker appen.
    }

    @Override
    public void onLocationChanged(Location location) {//hvis location endrer på seg
        Double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.i("Location Info: Lat", String.valueOf(lat));
        Log.i("Location Info: Lng", String.valueOf(lng));

        EditText latitude = (EditText) findViewById(R.id.lat);
        EditText longitude = (EditText) findViewById(R.id.lng);

        latitude.setText(String.valueOf(lat));
        longitude.setText(String.valueOf(lng));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) { //GPS på

    }

    @Override
    public void onProviderDisabled(String provider) {//GPS av

    }

    public void getLocation(View view){
        Location location = locationManager.getLastKnownLocation(provider);
        //Toast toast = Toast.makeText(getApplicationContext(), "skjiit", Toast.LENGTH_LONG).show();
        onLocationChanged(location); //logger da lng og lat
    }

    public void saveLocation(View view){
        EditText lat = (EditText) findViewById(R.id.lat);
        EditText lng = (EditText) findViewById(R.id.lng);
        TextView res = (TextView) findViewById(R.id.result);

        try{
            myDatabase.execSQL("INSERT INTO locations (latitude, longitude) VALUES ('"+String.valueOf(lat)+"','"+String.valueOf(lng) + "')");
            Log.i("DB:", "yes!");
            //myDatabase.execSQL("INSERT INTO locations (latitude, longitude) VALUES ('312','21321')");
            /*
            Cursor c = myDatabase.rawQuery("SELECT * FROM locations", null);
            int latIndex = c.getColumnIndex("latitude");
            int lngIndex = c.getColumnIndex("longitude");

            c.moveToFirst();

            while(c != null){
                //result += "Lat: " + c.getString(latIndex) + "Lng: " +  c.getString(latIndex) + "  ";
                Log.i("Latitude", c.getString(latIndex));
                Log.i("Longitude", c.getString(lngIndex));
                //Log.i("result: ", result);
                c.moveToNext();
                //res.setText("Latitude"+ c.getString(latIndex)+ "  " + "Longitude"+ c.getString(lngIndex)); virker ikke
            }*/
        }catch(Exception e){
            e.printStackTrace();
            Log.i("DB", "failed to save to DB");
        }

    }
}
