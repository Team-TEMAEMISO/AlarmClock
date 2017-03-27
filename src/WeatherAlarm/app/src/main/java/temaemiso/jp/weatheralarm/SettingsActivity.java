package temaemiso.jp.weatheralarm;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements RoopSettingFragment.OnFragmentInteractionListener {

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    HashMap<String,String> data1;
    HashMap<String,String> data2;
    SimpleAdapter adapter;
    boolean[] weekday = new boolean[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);

        Button cancel = (Button)findViewById(R.id.cancel);
        Button save = (Button)findViewById(R.id.save);
        ListView listView = (ListView)findViewById(R.id.listView2);
        final TimePicker picker = (TimePicker)findViewById(R.id.timePicker);
        final SharedPreferences pref = getSharedPreferences("settings1",MODE_PRIVATE);

        picker.setHour(pref.getInt("Hour",7));
        picker.setMinute(pref.getInt("Minute",0));
        weekday[0] = pref.getBoolean("Sunday",true);
        weekday[1] = pref.getBoolean("Monday",true);
        weekday[2] = pref.getBoolean("Tuesday",true);
        weekday[3] = pref.getBoolean("Wednesday",true);
        weekday[4] = pref.getBoolean("Thursday",true);
        weekday[5] = pref.getBoolean("Friday",true);
        weekday[6] = pref.getBoolean("Saturday",true);

        createData();

        adapter = new SimpleAdapter(this,list,R.layout.listview_settings,
                new String[]{"Item",
                        "Value"},
                new int[]{R.id.item,
                        R.id.value});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    AlertDialog.Builder listDlg = new AlertDialog.Builder(SettingsActivity.this);
                    listDlg.setTitle("繰り返し");
                    listDlg.setItems(
                            new String[]{"一回のみ","毎日","カスタム"},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == 0){
                                        data1.put("Value","一回のみ");
                                        adapter.notifyDataSetChanged();
                                    }else if(which == 1){
                                        data1.put("Value","毎日");
                                        adapter.notifyDataSetChanged();
                                    }else if(which == 2){
                                        data1.put("Value","カスタム");
                                        adapter.notifyDataSetChanged();
                                        AlertDialog.Builder checkDlg = new AlertDialog.Builder(SettingsActivity.this);
                                        checkDlg.setTitle("曜日選択");
                                        checkDlg.setMultiChoiceItems(
                                                new String[]{"日", "月", "火", "水", "木", "金", "土"},
                                                weekday,
                                                new DialogInterface.OnMultiChoiceClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                        weekday[which] = isChecked;
                                                    }
                                                }
                                        );
                                        checkDlg.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }
                                        );
                                        checkDlg.create().show();
                                    }
                                }
                            }
                    );
                    listDlg.create().show();
                }else if(position == 1){
                    Fragment fragment = new RoopSettingFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.contents, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("Hour",picker.getHour());
                editor.putInt("Minute",picker.getMinute());
                editor.putBoolean("Sunday",weekday[0]);
                editor.putBoolean("Monday",weekday[1]);
                editor.putBoolean("Tuesday",weekday[2]);
                editor.putBoolean("Wednesday",weekday[3]);
                editor.putBoolean("Thursday",weekday[4]);
                editor.putBoolean("Friday",weekday[5]);
                editor.putBoolean("Saturday",weekday[6]);
                editor.commit();
                SettingsActivity.this.finish();
            }
        });
    }

    private void createData() {
        String roop;
        if(weekday[0] == weekday[1] &&
                weekday[0] == weekday[2] &&
                weekday[0] == weekday[3] &&
                weekday[0] == weekday[4] &&
                weekday[0] == weekday[5] &&
                weekday[0] == weekday[6]){
            if(weekday[0]){
                roop = "毎日";
            }else{
                roop = "一回のみ";
            }
        }else{
            roop = "カスタム";
        }
        data1 = new HashMap<>();
        data1.put("Item","繰り返し");
        data1.put("Value",roop);
        list.add(data1);
        data2 = new HashMap<>();
        data2.put("Item","早める時間");
        data2.put("Value","");
        list.add(data2);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
