package com.rtmap.locationdemo;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rtm.common.model.RMLocation;
import com.rtm.common.utils.RMLog;
import com.rtm.location.LocationApp;
import com.rtm.location.utils.RMLocationListener;
import com.rtm.location.utils.RMVersionLocation;

/**
 * 定位整个过程需要三步，全程基本只需拷贝： 1.Libs库(so库是必须的，还有rtmap_lbs_location_v*.
 * jar和rtmap_lbs_common_v*.jar包)；
 * 2.配置(AndroidManifest.xml中智慧图服务请完全拷贝到你应用中，记得替换key哦
 * )；3.代码(LocationApp为定位类，采用单例模式
 * ，调用方法快捷高效，init()、start()、stop()除了这三个，别忘了注册和取消回调哦registerLocationListener
 * ()unRegisterLocationListener())
 * 
 * 更多方法请查看智慧图开发者平台(www.lbs.rtmap.com)定位API
 * 
 * @author dingtao
 *
 */
public class LocationActivity extends Activity implements OnClickListener,
		RMLocationListener {

	private TextView locateView;
	private Button mButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		locateView = (TextView) findViewById(R.id.tv_indoor_locate_info);
		mButton = (Button) findViewById(R.id.btn_start_locate);
		mButton.setOnClickListener(this);

		/**
		 * 设置log级别，默认是错误_LEVEL_ERROR，如果无法定位请打开log，我们会打印在初始化和定位过程中打印一些必要的信息用于核对错误
		 */
		// RMLog.LOG_LEVEL = RMLog.LOG_LEVEL_INFO;

		LocationApp.getInstance().init(getApplicationContext());// 定位服务初始化
		LocationApp.getInstance().registerLocationListener(this);// 注册回调接口
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start_locate:
			if (mButton.getText().equals(getString(R.string.startLocate))) {
				boolean result = LocationApp.getInstance().start();// 开始定位，如果没有key，定位无法启动
				if (result)
					mButton.setText(getString(R.string.stopLocate));
				else
					Toast.makeText(this, "请输入key", Toast.LENGTH_LONG).show();
			} else {
				LocationApp.getInstance().stop(); // 停止定位
				mButton.setText(getString(R.string.startLocate));
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		LocationApp.getInstance().unRegisterLocationListener(this);// 取消回调
		LocationApp.getInstance().stop();// 停止定位
		super.onDestroy();
	}

	/**
	 * 注册回调接口RMLocationListener，重写此方法，你可以在这里处理定位结果 当错误码为0时，说明定位成功
	 */
	@Override
	public void onReceiveLocation(RMLocation location) {
		Log.i("rtmap", location.getErrorInfo());
		locateView.setText("错误码: " + location.getError() + "\n建筑物ID: "
				+ location.getBuildID() + "\n楼层: " + location.getFloorID()
				+ "\nx坐标(米): " + location.getX() + "\ny坐标(米):  "
				+ location.getY() + "\n精度(米):" + location.getAccuracy());
	}
}
