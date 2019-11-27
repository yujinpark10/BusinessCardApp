package org.techtown.businesscardapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WhereMeActivity extends AppCompatActivity {

    private Button button;
    private TextView me_result;

    // 시간
    long todayTime = System.currentTimeMillis(); // 현재 시간을 msec 으로 구한다.
    Date todayDate = new Date(todayTime); // 현재 시간을 date 변수에 저장한다.
    SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 시간을 나타낼 포맷을 정한다
    private String today = todayFormat.format(todayDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_me);
        getSupportActionBar().hide();

        button = (Button)findViewById(R.id.button);
        me_result = (TextView)findViewById(R.id.me_result);

        final Geocoder geocoder = new Geocoder(this);

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WhereMeActivity.this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 0);
                } else {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String provider = location.getProvider();
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    double altitude = location.getAltitude();
                    List<Address> list = null;

                    String where = null;

                    try{
                        list = geocoder.getFromLocation(latitude, longitude, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("text", "입출력 오류");
                    }

                    if(list !=null) {
                        where = list.get(0).getAddressLine(0);
                    }

                    me_result.setText("위치 : " + where + "\n" +
                            "시간 : " + today);

//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                }
            }
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            me_result.setText("위치정보 : " + provider + "\n" +
                    "위도 : " + latitude + "\n" +
                    "경도 : " + longitude + "\n" +
                    "고도 : " + altitude + "\n" +
                    "시간 : " + today);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
