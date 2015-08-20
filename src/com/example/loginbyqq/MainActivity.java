package com.example.loginbyqq;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 *使用腾讯开发平台进行QQ登录并且获取资料
 * @author monster
 * date:2015-08-20
 */

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements OnClickListener{

	private Button btn_LoginQQ;
	private Tencent mTencent;
	public String APP_ID = "APPID";//请将此处替换成您的APPID
	
	private TextView user_nickname;
	private ImageView user_photo;
	
	private IUiListener listener;
	private String nickName , figureurl;

	private static final int SUCCESS = 0;
	private static final int FAILED = 1;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				String nick=(String) msg.obj;
				user_nickname.setText(nick);
				break;
			case FAILED:
				Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext()); //实例化对象
		initView();
	}
	/**
	 * 初始化视图
	 */
	private void initView() {
		btn_LoginQQ=(Button) findViewById(R.id.btn_LoginQQ);
		btn_LoginQQ.setOnClickListener(this);
		user_photo=(ImageView) findViewById(R.id.user_logo);
		user_nickname=(TextView) findViewById(R.id.user_nickname);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_LoginQQ:
				login();
			break;
		}
	}
	

	/**
	 * 登陆
	 */
	private void login() {
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());  //创建实例
		if(!mTencent.isSessionValid()){
			listener=new BaseUiListener(){
				@Override
				public void onComplete(Object object) {
					JSONObject json = (JSONObject) object;
					getUserInfoByVolley(json);
				}
			};
			mTencent.login(this, "all", listener);
			
		}
	}

	/**
	 * 使用Volley解析json数据
	 * @param json
	 */

	protected void getUserInfoByVolley(JSONObject json) {
		try {
			String openid=json.getString("openid");
			String access_token=json.getString("access_token");
			Log.e("TAG--->Openid",openid);
			Log.e("TAG--->Openid",access_token);
			
			String url="http://119.147.19.43/v3/user/get_info?openid="+openid+"&openkey="+access_token+"&pf=qzone&appid=1104812858&format=json&userip=10.0.0.1&sig=C3BGTm24S%2FZJdt1J%2BjfEzRpCLWA%3D"; 
			/**
			 * 使用Volley框架得到json数据
			 * tag : https://github.com/adamrocker/volley
			 */
			
			RequestQueue requestQueue=Volley.newRequestQueue(MainActivity.this); //用于获取一个Volley的请求对象
			 JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, 
					 new Response.Listener<JSONObject>() {
				 			@Override
				 			public void onResponse(JSONObject json) {
				 			/**
				 			 * 解析json数据
				 			 */
				 				try {
									 nickName=json.getString("nickname");  //昵称
									 figureurl=json.getString("figureurl");  //头像的url
									 
									 //TODO 通过异步任务处理图片
									 new NewAsyncTask().execute(figureurl);
									 //
									 
									 mHandler.obtainMessage(SUCCESS, nickName).sendToTarget();
									 
								} catch (JSONException e) {
									e.printStackTrace();
								}
				 			}
						}, 
					new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
								Log.e("error","问题~~");
								mHandler.obtainMessage(FAILED).sendToTarget();
							}
					});
			 requestQueue.add(jsonObjectRequest); //在请求队列中加入当前的请求
			 
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步任务加载图片
	 * @author monster
	 */
	class NewAsyncTask extends AsyncTask<String, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... params) {
			String url=params[0];
			return getBitmapByUrl(url);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			user_photo.setImageBitmap(result);
		}
		
	}

	/**
	 * 通过url 得到bitmap对象
	 * @param urlString
	 * @return
	 */
	public Bitmap getBitmapByUrl(String urlString) {
		Bitmap bitmap;
		InputStream is = null;
		try {
			URL url=new URL(urlString);
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			is=new BufferedInputStream(connection.getInputStream());
			bitmap=BitmapFactory.decodeStream(is);
			connection.disconnect(); //关闭连接
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();  
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

