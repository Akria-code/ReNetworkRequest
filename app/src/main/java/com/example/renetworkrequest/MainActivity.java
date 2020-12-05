package com.example.renetworkrequest;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.button);
        mTextView=findViewById(R.id.textView);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                sendRequestWithOkHttp();
                break;
        }

    }

    private void sendRequestWithOkHttp() {
        Log.e("MY","okhttp");
        try {
            final URL link = new URL("http://192.168.2.93/get_data.xml");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(link)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Log.e("MY",responseData);
                        parseXMLWithPull(responseData);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("MY",e.getMessage());
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("MY",e.getMessage());
        }
    }
    private void parseXMLWithPull(String xmlData) {
        /**
         * 1.这里首先要获取到一个XmlPullParserFactory的实例，并借助这个实例得到XmlPullParser对象
         * 2.然后调用XmlPullParser的setInput()方法将服务器返回的XML数据设置进去就可以开始解析了。
         * 3.通过getEventType()可以得到当前的解析事件
         * 4.如果当前的解析事件不等于XmlPullParser.END_DOCUMENT，说明解析工作还没完成，调用next()方法后可以获取下一个解析事件。
         */
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            String i="";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    // 开始解析某个节点
                    case XmlPullParser.START_TAG: {
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)) {
                            version = xmlPullParser.nextText();
                        }
                        i=i+id+name+version;
                        break;
                    }
                    // 完成解析某个节点
                    case XmlPullParser.END_TAG: {
                        if ("app".equals(nodeName)) {

                            final String finalId = "\nid is"+id;
                            final String finalName = "\nname is"+name;
                            final String finalVersion ="\nversion is"+ version;
                            final String finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(finalI);
                                }
                            });
                            Log.d("MainActivity", "id is " + id);
                            Log.d("MainActivity", "name is " + name);
                            Log.d("MainActivity", "version is " + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}