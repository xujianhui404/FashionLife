package fashionlife.com.ui.find.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import fashionlife.com.R;
import fashionlife.com.base.component.BaseFragment;
import fashionlife.com.ui.find.data.WeatherBean;
import fashionlife.com.ui.find.impl.WeatherContract;
import fashionlife.com.ui.find.impl.WeatherPresenter;
import fashionlife.com.util.ToastHelper;
import fashionlife.com.util.Utils;
import fashionlife.com.widget.CircularBeadView;
import fashionlife.com.widget.WeatherView;

/**
 * @author: lovexujh
 * @time: 2017/10/22
 * @descripition: 天气
 */

public class WeatherFragment extends BaseFragment<WeatherPresenter> implements WeatherContract.View {


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
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.queryWeather();
        }
    }

    @Override
    public void updateFutureView(final List<WeatherBean.ResultBean.FutureBean> future) {
        if (!Utils.isEmpty(future) && mWeatherView != null) {
            ToastHelper.showToast("更新天气成功");
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
    }

    @Override
    public void onError() {

    }

    @Override
    public void updateLocationView(String city, String district) {
        mTvCityDistrict.setText(city + "  " + district);
    }
}
