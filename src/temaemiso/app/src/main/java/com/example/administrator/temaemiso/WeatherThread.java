package com.example.administrator.temaemiso;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Administrator on 2017/02/23.
 */

public class WeatherThread extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        String requestURL = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=43.067885&lon=141.355539&mode=json&cnt=14&appid=30c5fec2b9fa64434eb3a41338af61c5";
        URL url;
        InputStream is;
        BufferedReader reader;
        StringBuilder sb;
        String line;

        try {
//            天気情報を取得します。
            url = new URL(requestURL);
            is = url.openConnection().getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            sb = new StringBuilder();
            while (null != (line = reader.readLine())) {
                sb.append(line);
            }
            String data = sb.toString();
            JSONObject jsonObject = new JSONObject(data);
            JSONArray root = jsonObject.getJSONArray("list");
            JSONObject jsonObject2 = root.getJSONObject(0);
            jsonObject.get("id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
