package temaemiso.jp.weatheralarm;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements RoopSettingFragment.OnFragmentInteractionListener {

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    HashMap<String,String> data1;
    HashMap<String,String> data2;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);

        ListView listView = (ListView)findViewById(R.id.listView2);

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
                                        AlertDialog.Builder checkDlg = new AlertDialog.Builder(SettingsActivity.this);
                                        checkDlg.setTitle("曜日選択");
                                        checkDlg.setMultiChoiceItems(
                                                new String[]{"日","月","火","水","木","金","土"},
                                                new boolean[]{true,true,true,true,true,true,true},
                                                null
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
    }

    private void createData() {
        data1 = new HashMap<>();
        data1.put("Item","繰り返し");
        data1.put("Value","月");
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
