package temaemiso.jp.weatheralarm;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class AlarmNortificationActivity extends Activity {
    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // スクリーンロックを解除する
        // 権限が必要
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //gif表示用レイアウト
        LinearLayout target = (LinearLayout)findViewById(R.id.ImgContainer);
        //アプリ終了用ボタン
        Button button = (Button)findViewById(R.id.button);

        GifWebView gif = new GifWebView(this, Weather.snow);
        //gifをインサート
        target.addView(gif);

        //クリックイベント
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmNortificationActivity.super.finishAndRemoveTask();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // 音を鳴らす
        if (mp == null)
            // resのrawディレクトリにtest.mp3を置いてある
            mp = MediaPlayer.create(this, R.raw.alarm1);
        mp.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAndRelaese();
    }

    private void stopAndRelaese() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //alarmNowText = (TextView) findViewById(R.id.alarm_now_time);
        //handler.sendEmptyMessage(WHAT);
        // mam.stopAlarm();
    }
}