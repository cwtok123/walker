package com.example.walker;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class OutdoorActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	// Used to store the last screen title. For use in
	// {@link #restoreActionBar()}.
	private CharSequence mTitle;
	// 百度地图相关
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode = null;
	private BitmapDescriptor mCurrentMarker;
	
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	
	private MyLocationData locData;
	private float locDirection = 0;
	
	private boolean isFirstLoc = true;
	//private CurrentMaker mCurrentMarker
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_outdoor);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 设置定位模式为普通
		mCurrentMode = LocationMode.NORMAL;
		// 设置指针类型为默认箭头
		mCurrentMarker = null;
		// 应用定位模式与指针类型的设置
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		
	    // 开启定位图层  
	    mBaiduMap.setMyLocationEnabled(true);
	    // 定位初始化
	    mLocationClient = new LocationClient(getApplicationContext());	//声明LocationClient类
	    mLocationClient.registerLocationListener(myListener);	//注册监听函数
	    LocationClientOption option = new LocationClientOption();
	    	option.setOpenGps(true);
	    	option.setCoorType("bd09ll");
	    	option.setScanSpan(1000);
	    	option.setNeedDeviceDirect(true);
	    mLocationClient.setLocOption(option);
	    
	 // 传感器管理器  
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 注册传感器(Sensor.TYPE_ORIENTATION(方向传感器);SENSOR_DELAY_FASTEST(0毫秒延迟);  
        // SENSOR_DELAY_GAME(20,000毫秒延迟)、SENSOR_DELAY_UI(60,000毫秒延迟))  
        sm.registerListener(new SensorEventListener() {
                // 用于传感器监听中，设置灵敏程度
                int mIncrement;

                @Override
                public void onSensorChanged(SensorEvent event) {
                        // 方向传感器
                        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                                // x表示手机指向的方位，0表示北,90表示东，180表示南，270表示西
                                float x = event.values[SensorManager.DATA_X];
                                // Log.e("x",x+"");
                                mIncrement++;
                                if (mIncrement == 10) {
                                        // 修改定位图标方向
                                        locDirection = x;
                                        //  重新设置当前位置数据
                                        // mBaiduMap.setMyLocationData(locData);
                                        //myLocationOverlay.setData(locData);
                                        //mMapView.refresh();

                                        mIncrement = 0;
                                        Log.i("direction", ""+locDirection);
                                }
                        }
                }

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}

        }, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                        SensorManager.SENSOR_DELAY_NORMAL);
	    
	    mLocationClient.start();

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.outdoor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// TODO Auto-generated method stub
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			sb.append("\ndirection : ");
			sb.append(location.getDirection());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			Log.i ("BDmap", sb.toString());
			
			locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(locDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
		public void onREceivePoi(BDLocation poiLocation) {
			
		}
	}
	@Override
	protected void onDestroy() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		super.onPause();
	}

}
