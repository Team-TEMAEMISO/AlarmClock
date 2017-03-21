package temaemiso.jp.weatheralarm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by y-kawase on 2017/02/22.
 */

public class WeatherService extends Service implements LocationListener {

    LocationManager mLocationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String requestURL = "http://api.openweathermap.org/data/2.5/find?lat=" + latitude + "&lon=" + longitude + "&cnt=1&APPID=30c5fec2b9fa64434eb3a41338af61c5";

        try {
            URL url = new URL(requestURL);

            WeatherThread weatherThread = new WeatherThread(this);

            weatherThread.execute(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getWeather(InputStream is){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();

            String line;

            while (null != (line = reader.readLine())) {

                sb.append(line);

            }
            String data = sb.toString();

            JSONObject jsonObject = new JSONObject(data);
            String weather = jsonObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon");
            Log.v("VERBOSE", weather);

            SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);

            int minute;
            if(weather.equals("01n")){
                Log.v("VERBOSE","晴れ");
                minute = pref.getInt("Sunny",0);
            }else if(weather.equals("02n") || weather.equals("03n") || weather.equals("04n") || weather.equals("50n")){
                Log.v("VERBOSE","曇り");
                minute = pref.getInt("Cloudy",0);
            }else if(weather.equals("13n")){
                Log.v("VERBOSE","雪");
                minute = pref.getInt("Snowy",0);
            }else{
                Log.v("VERBOSE","雨");
                minute = pref.getInt("Rainy",0);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // 5秒後に設定
            calendar.add(Calendar.SECOND, 5);

            Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
            intent.putExtra("intentId", 1);
            // PendingIntentが同じ物の場合は上書きされてしまうので requestCode で区別する
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 2, intent, 0);

            // アラームをセットする
            AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            // 約10秒で 繰り返し
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationManager.removeUpdates(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
