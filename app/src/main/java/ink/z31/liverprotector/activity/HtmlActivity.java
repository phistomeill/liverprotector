package ink.z31.liverprotector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import ink.z31.liverprotector.MainActivity;
import ink.z31.liverprotector.R;
import ink.z31.liverprotector.html.MapPathHtml;
import ink.z31.liverprotector.html.TaskAddHtml;
import ink.z31.liverprotector.html.TaskManagerHtml;
import ink.z31.liverprotector.interfaces.HttpFinishCallBack;

public class HtmlActivity extends AppCompatActivity {
    private WebView webview;
    public static final int HTML_MAP = 0;
    public static final int HTML_TASK = 1;
    public static final int HTML_TASK_MANAGER = 2;

    public static final int REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        // 对web进行初始设置
        webview = findViewById(R.id.webView);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setDomStorageEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());

        Intent intent = getIntent();
        switch (intent.getIntExtra("type", HTML_MAP)) {
            case HTML_MAP:
                // 用于添加配置
                String name = intent.getStringExtra("name");
                webview.addJavascriptInterface(new MapPathHtml(name, webview, new HttpFinishCallBack() {
                    @Override
                    public void onFinish(Bundle bundle) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onCancel(Bundle bundle) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }), "android");
                break;
            case HTML_TASK:
                // 用于管理配置
                webview.addJavascriptInterface(new TaskAddHtml(webview, new HttpFinishCallBack() {
                    @Override
                    public void onFinish(Bundle bundle) {
                        setResult(MainActivity.TASK_CHANGE);
                        finish();
                    }

                    @Override
                    public void onCancel(Bundle bundle) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }), "android");
                break;
            case HTML_TASK_MANAGER:
                // 用于管理任务
                webview.addJavascriptInterface(new TaskManagerHtml(webview, new HttpFinishCallBack() {
                    @Override
                    public void onFinish(Bundle bundle) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onCancel(Bundle bundle) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }), "android");
                break;
        }


    }
}
