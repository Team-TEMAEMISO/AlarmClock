package temaemiso.jp.weatheralarm;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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
                MainActivity2.super.finishAndRemoveTask();
            }
        });
    }
}