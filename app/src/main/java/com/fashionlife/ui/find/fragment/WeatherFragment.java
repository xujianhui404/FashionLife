package com.fashionlife.ui.find.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashionlife.R;
import com.fashionlife.base.component.BaseFragment;
import com.fashionlife.manager.PermissionMangerHelper;
import com.fashionlife.ui.find.data.WeatherBean;
import com.fashionlife.ui.find.impl.WeatherContract;
import com.fashionlife.ui.find.impl.WeatherPresenter;
import com.fashionlife.util.NetStatusUtil;
import com.fashionlife.util.ToastHelper;
import com.fashionlife.util.Utils;
import com.fashionlife.widget.CircularBeadView;
import com.fashionlife.widget.WeatherView;

import java.util.List;

/**
 * @author: lovexujh
 * @time: 2017/10/22
 * @descripition: 天气
 */

public class WeatherFragment extends BaseFragment<WeatherPresenter> implements WeatherContract.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {


    private WeatherView mWeatherView;
    private TextView mTvCityDistrict;
    private TextView mTvTemperature;
    private TextView mTvWeather;
    private TextView mTvWind;
    private TextView mTvExerciseIndex;
    private TextView mTvDressingIndex;
    private TextView mTvHumidity;
    private TextView mTvAirCondition;
    private TextView mTvPollutionIndex;
    private CircularBeadView mAirConditionLine;
    private ImageView mIvWallpaper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsFirstEnter = true;

    @Override
    protected WeatherPresenter attachPresenter() {
        return new WeatherPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weather;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWeatherView = (WeatherView) view.findViewById(R.id.weather_view);
        mTvCityDistrict = (TextView) view.findViewById(R.id.tv_city_distrct);
        mTvTemperature = (TextView) view.findViewById(R.id.tv_temperature);
        mTvWeather = (TextView) view.findViewById(R.id.tv_weather);
        mTvWind = (TextView) view.findViewById(R.id.tv_wind);
        mTvExerciseIndex = (TextView) view.findViewById(R.id.tv_exerciseIndex);
        mTvDressingIndex = (TextView) view.findViewById(R.id.tv_dressingIndex);
        mTvHumidity = (TextView) view.findViewById(R.id.tv_humidity);
        mTvAirCondition = (TextView) view.findViewById(R.id.tv_air_condition);
        mTvPollutionIndex = (TextView) view.findViewById(R.id.tv_pollutionIndex);
        mAirConditionLine = (CircularBeadView) view.findViewById(R.id.circular_beadview);
        mIvWallpaper = (ImageView) view.findViewById(R.id.iv_bg);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mContainerView;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mTvCityDistrict.setOnClickListener(this);
        handlerDownloadWallpaper();
    }

    private void handlerDownloadWallpaper() {
        if (Utils.hadFilePermissions(getActivity())) {
            mPresenter.downloadWallpaper();
        } else {
            PermissionMangerHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionMangerHelper.PERMISSION_FILE_OK);
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mIsFirstEnter) {
                tryQueryWeather();
                mIsFirstEnter = false;
            }
        } else {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    /**
     * 判断权限后
     */
    private void tryQueryWeather() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean result = PermissionMangerHelper.needRequestLocationPermission(this);
            if (!result) {
                mPresenter.queryWeather();
            }

        } else {
            mPresenter.queryWeather();

        }


    }

    @Override
    public void updateFutureView(final List<WeatherBean.ResultBean.FutureBean> future) {
        if (!Utils.isEmpty(future) && mWeatherView != null) {
            ToastHelper.showDebugToast("更新天气成功");
            mSwipeRefreshLayout.setRefreshing(false);
            mWeatherView.updateView(future);
        }
    }

    @Override
    public void updateCurDayView(WeatherBean.ResultBean resultBean) {
        if (resultBean == null) {
            return;
        }
        mTvTemperature.setText(resultBean.getTemperature().replace("C", ""));
        mTvWeather.setText(resultBean.getWeather());
        mTvWind.setText(resultBean.getWind());
        mTvExerciseIndex.setText(resultBean.getExerciseIndex());
        mTvDressingIndex.setText(resultBean.getDressingIndex());
        mTvHumidity.setText(resultBean.getHumidity());
        mTvAirCondition.setText(resultBean.getAirCondition());
        mTvPollutionIndex.setText(resultBean.getPollutionIndex());
        String pollutionIndex = resultBean.getPollutionIndex().trim();
        int pollutionIndexInt = 0;
        try {
            pollutionIndexInt = Integer.valueOf(pollutionIndex);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAirConditionLine.setColor(pollutionIndexInt);

//        NotificationService.start(getActivity());
    }

    @Override
    public void onError() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateLocationView(String city, String district) {
        if (city.equals(district)) {
            city = "";
        }
        mTvCityDistrict.setText(city + "  " + district);
    }


    @Override
    public void downloadWallpaperSucceed(String imgName) {
        if (mIvWallpaper != null) {
            Glide.with(this).load(imgName).centerCrop().into(mIvWallpaper);
        }
    }

    @Override
    public void checkLocationPermission() {
        PermissionMangerHelper.needRequestLocationPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionMangerHelper.PERMISSION_LOCATION_OK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//授权定位成功
                    mPresenter.queryWeather();
                }
                break;
            case PermissionMangerHelper.PERMISSION_FILE_OK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//读写文件权限
                    mPresenter.downloadWallpaper();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        if(NetStatusUtil.CONNECTION_FAILED == NetStatusUtil.getAPNType(getContext())){
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        mPresenter.queryWeather();
        mPresenter.downloadWallpaper();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_city_distrct:
                if (mPresenter != null) {
                    mPresenter.ShowPickerView(getContext());
                }
                break;
        }
    }
}
