package temaemiso.jp.weatheralarm;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

public class GifWebView extends WebView {
    public GifWebView(Context context, Weather weather) {
        super(context);

        //呼び出し元のActivityを取得
        Activity act = (Activity)this.getContext();
        //enumの種類によってタイトルを変更
        switch (weather){
            case sunny:
                act.setTitle("晴れ");
                break;
            case cloudiness:
                act.setTitle("曇り");
                break;
            case rain:
                act.setTitle("雨");
                break;
            case snow:
                act.setTitle("雪");
                break;
        }

        //端末内のファイルを読み込む場合は、file:///をつける必要あり
        String path = "file:///android_res/raw/" + weather + ".gif";
        loadUrl(path);
    }
}