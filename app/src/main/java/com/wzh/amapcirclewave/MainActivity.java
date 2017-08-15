package com.wzh.amapcirclewave;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

public class MainActivity extends FragmentActivity implements AMap.OnMapLoadedListener, AMap.OnMapClickListener, LocationSource, AMapLocationListener {
    private AMap aMap;
    private MapView mapView;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mListener;

    private Marker locMarker;
    private Circle ac;
    private Circle c,d;

    public static final int zoomLevel = 19 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationStyle(new MyLocationStyle().myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
    }

    public void click(View view){
        int id =view.getId() ;
        if (id==R.id.location){
            startlocation();
        }else if (id==R.id.zoom_in){
            changeCamera(CameraUpdateFactory.zoomIn(), null);
        }else if (id==R.id.zoom_out){
            changeCamera(CameraUpdateFactory.zoomOut(), null);
        }
    }
    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        aMap.animateCamera(update, 300, callback);
//        aMap.moveCamera(update); //无动画的时候调用
    }
    private Marker addMarker(LatLng point) {
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.icon_bus_position);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        Marker marker = aMap.addMarker(new MarkerOptions().position(point).icon(des)
                .anchor(0.5f, 0.5f));
        return marker;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        deactivate();
    }

    @Override
    public void onMapLoaded() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        startlocation();
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    /**
     * 开始定位。
     */
    private void startlocation() {

        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置为单次定位
            mLocationOption.setOnceLocation(true);
            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        } else {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                LatLng mylocation = new LatLng(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude());
                changeCamera(CameraUpdateFactory.newLatLngZoom(mylocation,zoomLevel),null);
                if (locMarker==null){
                    addLocationMarker(mylocation);
                }else {
                    locMarker.setPosition(mylocation);
                }
                if (ac!=null){
                    ac.setCenter(mylocation);
                }
                if (c!=null){
                    c.setCenter(mylocation);
                }
                if (d!=null){
                    d.setCenter(mylocation);
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 添加坐标点，这里可以添加任意坐标点位置
     * @param mylocation
     */
    private void addLocationMarker(LatLng mylocation) {
        float accuracy = (float) ((mylocation.longitude/mylocation.latitude )*20);
        if (locMarker == null) {
            locMarker = addMarker(mylocation);
            if (ac==null){
                ac = aMap.addCircle(new CircleOptions().center(mylocation)
                        .fillColor(Color.argb(0, 98 ,189, 255)).radius(accuracy)
                        .strokeColor(Color.argb(0, 98, 198, 255)).strokeWidth(0));
            }
            if (c==null){
                c = aMap.addCircle(new CircleOptions().center(mylocation)
                        .fillColor(Color.argb(0, 98, 198, 255))
                        .radius(accuracy).strokeColor(Color.argb(0,98, 198, 255))
                        .strokeWidth(0));
            }
            if (d==null){
                d = aMap.addCircle(new CircleOptions().center(mylocation)
                        .fillColor(Color.argb(0, 98, 198, 255))
                        .radius(accuracy).strokeColor(Color.argb(0,98, 198, 255))
                        .strokeWidth(0));
            }
        } else {
            locMarker.setPosition(mylocation);
            ac.setCenter(mylocation);
            ac.setRadius(accuracy);
            c.setCenter(mylocation);
            c.setRadius(accuracy);
            d.setCenter(mylocation);
            d.setRadius(accuracy);
        }
        handle.postDelayed(rb,0);
        handle1.postDelayed(rb1,800);
        handle2.postDelayed(rb2,1600);
    }

    /**
     * 位置波纹扩散动画
     * @param ac
     */
    private void Scalecircle1(final Circle ac) {
        ValueAnimator vm = ValueAnimator.ofFloat(0,(float)ac.getRadius());
        vm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curent = (float) animation.getAnimatedValue();
                ac.setRadius(curent);
                aMap.invalidate();
            }
        });
        ValueAnimator vm1 = ValueAnimator.ofInt(160,0);
        vm1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                ac.setFillColor(Color.argb(color, 98, 198, 255));
                aMap.invalidate();
            }
        });
        vm.setRepeatCount(Integer.MAX_VALUE);
        vm.setRepeatMode(ValueAnimator.RESTART);
        vm1.setRepeatCount(Integer.MAX_VALUE);
        vm1.setRepeatMode(ValueAnimator.RESTART);
        AnimatorSet set = new AnimatorSet();
        set.play(vm).with(vm1);
        set.setDuration(2500);
        set.setInterpolator(interpolator1);
        set.start();
    }
    private final Interpolator interpolator1 = new LinearInterpolator();
    Runnable rb = new Runnable() {
        @Override
        public void run() {
            Scalecircle1(ac);
        }
    };
    Handler handle =new Handler();

    Runnable rb1 = new Runnable() {
        @Override
        public void run() {
            Scalecircle1(c);
        }
    };
    Handler handle1 =new Handler();

    Runnable rb2 = new Runnable() {
        @Override
        public void run() {
            Scalecircle1(d);
        }
    };
    Handler handle2 =new Handler();
}