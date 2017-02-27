package temaemiso.jp.weatheralarm;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by y-kawase on 2017/02/23.
 */

public class WeatherThread extends AsyncTask<URL,String,InputStream> {
    WeatherService service = null;

    public WeatherThread(WeatherService service){
        this.service = service;
    }

    @Override
    protected InputStream doInBackground(URL... urls) {
        try {
            return urls[0].openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(InputStream is) {
        service.getWeather(is);
    }
}
