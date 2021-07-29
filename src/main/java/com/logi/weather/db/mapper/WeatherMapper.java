package com.logi.weather.db.mapper;

import java.util.List;

import com.logi.weather.dto.EditCell;
import com.logi.weather.dto.MergeWeather;
import com.logi.weather.dto.Weather;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WeatherMapper {
  public List<Weather> getAll() throws Exception;
  public int mergeWeather(MergeWeather mergeWeather) throws Exception;
  public void cellEdit(EditCell editCell) throws Exception;
}
