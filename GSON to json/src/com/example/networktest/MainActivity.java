package com.example.networktest;

import java.io.InputStream;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity implements OnClickListener {

	public static final int SHOW_RESPONSE = 0;
	
	private Button 		sendRequest;
	private TextView 	responseText;
	
	private Handler 	handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj;
				// 在这里进行UI操作，将结果显示到界面上
				responseText.setText(response);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sendRequest 	= (Button) 		findViewById(R.id.send_request);
		responseText 	= (TextView) 	findViewById(R.id.response_text);
		
		sendRequest.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send_request) {
			sendRequestWithHttpClient();
		}
	}

	private void sendRequestWithHttpClient() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				String json = getFromAssets("get_data.json");
				// GSON解析
				parseJSONWithGSON(json.toString());

				Message message = new Message();
				message.what = SHOW_RESPONSE;
				message.obj = json.toString();
				handler.sendMessage(message);
				
			}
		}).start();
	}
	
	// 从assets 文件夹中获取文件并读取数据
	public String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 使用gson解析
	private void parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();
		List<AppInfoModel> appList = gson.fromJson(jsonData, new TypeToken<List<AppInfoModel>>() {}.getType());
		
		for (AppInfoModel app : appList) {
			Log.d("andli", "id is " 		+ app.getId());
			Log.d("andli", "name is " 		+ app.getName());
			Log.d("andli", "version is " 	+ app.getVersion());
		}
	}

}
