package temaemiso.jp.weatheralarm;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends Activity {

    ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            ListView listView = (ListView)findViewById(R.id.listView);

            list.clear();
            createData();

            listView.setAdapter(new SwitchAdapter(this,list,R.layout.listview_alarm,
                    new String[]{"Timer",
                            "SunnyView",
                            "SunnyNumber",
                            "CloudyView",
                            "CloudyNumber",
                            "RainyView",
                            "RainyNumber",
                            "SnowyView",
                            "SnowyNumber",
                            "Switch"},
                    new int[]{R.id.Timer,
                            R.id.SunnyView,
                            R.id.SunnyNumber,
                            R.id.CloudyView,
                            R.id.CloudyNumber,
                            R.id.RainyView,
                            R.id.RainyNumber,
                            R.id.SnowyView,
                            R.id.SnowyNumber,
                            R.id.switch1}
                    ,R.id.switch1));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        ListView listView = (ListView)findViewById(R.id.listView);

        createData();

        listView.setAdapter(new SwitchAdapter(this,list,R.layout.listview_alarm,
                new String[]{"Timer",
                        "SunnyView",
                        "SunnyNumber",
                        "CloudyView",
                        "CloudyNumber",
                        "RainyView",
                        "RainyNumber",
                        "SnowyView",
                        "SnowyNumber",
                        "Switch"},
                new int[]{R.id.Timer,
                        R.id.SunnyView,
                        R.id.SunnyNumber,
                        R.id.CloudyView,
                        R.id.CloudyNumber,
                        R.id.RainyView,
                        R.id.RainyNumber,
                        R.id.SnowyView,
                        R.id.SnowyNumber,
                        R.id.switch1}
                ,R.id.switch1));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view instanceof CompoundButton){
                    SharedPreferences pref = getSharedPreferences("settings" + String.valueOf(position + 1),MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("Alarm",((CompoundButton) view).isChecked());
                    editor.commit();
                    if(((CompoundButton) view).isChecked()) {
                        setAlarm(position + 1);
                    }
                }else {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    intent.putExtra("Number",String.valueOf(position + 1));
                    startActivityForResult(intent, 1);
                }
            }
        });

//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(MainActivity.this,switch1.getTag().toString(),Toast.LENGTH_LONG);
//            }
//        });
    }

    private void setAlarm(int i) {

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        // PendingIntentが同じ物の場合は上書きされてしまうので requestCode で区別する
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        // アラームをセットする
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);

        //this.startActivity(new Intent(getApplicationContext(), AlarmNortificationActivity.class));
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeZone(tz);
        calendar1.setTimeInMillis(System.currentTimeMillis());
        calendar1.add(Calendar.SECOND, 10);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), 10000, pending);

        SharedPreferences pref = getSharedPreferences("settings" + String.valueOf(i), MODE_PRIVATE);
        int setHour = pref.getInt("Hour", 0);
        int setMinute = pref.getInt("Minute", 0);
        if (pref.getBoolean("Sunday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week - 1;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }

        if (pref.getBoolean("Monday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week - 2;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }

        if (pref.getBoolean("Tuesday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week - 3;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }

        if (pref.getBoolean("Wednesday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week - 4;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }

        if (pref.getBoolean("Thursday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week - 5;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }

        if (pref.getBoolean("Friday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week - 6;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }

        if (pref.getBoolean("Saturday", true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int targetday = week;
            if (targetday <= -1 ||
                    (targetday == 0 && setHour < hour) ||
                    (targetday == 0 && setHour == hour && setMinute <= minute)) targetday += 7;

            calendar.setTimeZone(tz);
            calendar.add(Calendar.DAY_OF_MONTH, targetday);
            calendar.set(Calendar.HOUR_OF_DAY, setHour);
            calendar.set(Calendar.MINUTE, setMinute);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);

            // トーストで設定されたことをを表示
            Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
        }
    }

    private void createData() {
        for(int n=1;n<=5;n++){
            SharedPreferences pref = getSharedPreferences("settings" + String.valueOf(n),MODE_PRIVATE);
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("Timer",String.valueOf(pref.getInt("Hour",7)) + ":" + String.format("%02d", pref.getInt("Minute",0)));
            data.put("SunnyView",R.mipmap.sun);
            data.put("SunnyNumber",pref.getInt("Sunny",0));
            data.put("CloudyView",R.mipmap.cloud);
            data.put("CloudyNumber",pref.getInt("Cloudy",0));
            data.put("RainyView",R.mipmap.rain);
            data.put("RainyNumber",pref.getInt("Rainy",0));
            data.put("SnowyView",R.mipmap.snow);
            data.put("SnowyNumber",pref.getInt("Snowy",0));
            data.put("Switch",pref.getBoolean("Alarm",false));
            list.add(data);
        }
    }



//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        switch1 = (Switch)this.findViewById(R.id.switch1);
//        if(switch1.isChecked()){
//
//            ToggleButton sunday = (ToggleButton)this.findViewById(R.id.sunday);
//            ToggleButton monday = (ToggleButton)this.findViewById(R.id.monday);
//            ToggleButton tuesday = (ToggleButton)this.findViewById(R.id.tuesday);
//            ToggleButton wednesday = (ToggleButton)this.findViewById(R.id.wednesday);
//            ToggleButton thursday = (ToggleButton)this.findViewById(R.id.thursday);
//            ToggleButton friday = (ToggleButton)this.findViewById(R.id.friday);
//            ToggleButton saturday = (ToggleButton)this.findViewById(R.id.saturday);
//
//            Intent intent = new Intent(getApplicationContext(), WeatherService.class);
//            intent.putExtra("intentId", 1);
//            // PendingIntentが同じ物の場合は上書きされてしまうので requestCode で区別する
//            PendingIntent pending = PendingIntent.getService(getApplicationContext(), bid1, intent, 0);
//
//            // アラームをセットする
//            AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
//
//            if(sunday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week - 1;
//                if(targetday <= 0) targetday += 7;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//
//            if(monday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week - 2;
//                if(targetday <= 0) targetday += 7;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//
//            if(tuesday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week - 3;
//                if(targetday <= 0) targetday += 7;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//
//            if(wednesday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week - 4;
//                if(targetday <= 0) targetday += 7;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//
//            if(thursday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week - 5;
//                if(targetday <= 0) targetday += 7;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//
//            if(friday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week - 6;
//                if(targetday <= 0) targetday += 7;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//
//            if(saturday.isChecked()){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());
//                int week = calendar.get(Calendar.DAY_OF_WEEK);
//
//                int targetday = week;
//
//                calendar.add(Calendar.DAY_OF_MONTH, targetday);
//                calendar.set(Calendar.HOUR_OF_DAY,0);
//                calendar.set(Calendar.MINUTE,0);
//                calendar.set(Calendar.SECOND,0);
//
//                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), /*1000 * 60 * 60 * 24 * 7*/10000, pending);
//
//                // トーストで設定されたことをを表示
//                Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(calendar.getTime()), Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}