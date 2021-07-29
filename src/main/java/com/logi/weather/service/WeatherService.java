package com.logi.weather.service;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.logi.weather.db.mapper.WeatherMapper;
import com.logi.weather.dto.EditCell;
import com.logi.weather.dto.MergeWeather;
import com.logi.weather.dto.Weather;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

  WeatherMapper weatherMapper;

  @Autowired
  public WeatherService(WeatherMapper weatherMapper) {
    this.weatherMapper = weatherMapper;
  }

  public Object getWeather() {
    String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    String serviceKey = "76kMp0IHMpUXu8AIlkcGADdXJfr8yQD33ZB3eaU4fTSH9RGA7enNg4W/B3FYs1JddWWpjVQewV4SYLgnnpTJDQ==";

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

    String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd"));
    String nowTime = LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())
        .format(DateTimeFormatter.ofPattern("HHmm"));
    int nowTimeTemp = Integer.parseInt(nowTime);
    
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat Format = new SimpleDateFormat("YYYYMMdd");
    if(210 <= nowTimeTemp && nowTimeTemp < 510) {
      nowTimeTemp = 200;
    } else if(510 <= nowTimeTemp && nowTimeTemp < 810) {
      nowTimeTemp = 500;
    } else if(810 <= nowTimeTemp && nowTimeTemp < 1110) {
      nowTimeTemp = 800;
    } else if(1110 <= nowTimeTemp && nowTimeTemp < 1410) {
      nowTimeTemp = 1100;
    } else if(1410 <= nowTimeTemp && nowTimeTemp < 1710) {
      nowTimeTemp = 1400;
    } else if(1710 <= nowTimeTemp && nowTimeTemp < 2010) {
      nowTimeTemp = 1700;
    } else if(2010 <= nowTimeTemp && nowTimeTemp < 2310) {
      nowTimeTemp = 2000;
    } else if(2310 <= nowTimeTemp && nowTimeTemp < 2359) {
      nowTimeTemp = 2300;
    } else if(0 <= nowTimeTemp && nowTimeTemp < 210) {
      nowTimeTemp = 2300;
      cal.add(Calendar.DATE, -1);
      nowDate = Format.format(cal.getTime());
    }
    
    if (nowTimeTemp < 1000) {
      nowTime = "0" + String.valueOf(nowTimeTemp);
    } else {
      nowTime = String.valueOf(nowTimeTemp);
    }

    UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).queryParam("serviceKey", serviceKey)
        .queryParam("numOfRows", 1000).queryParam("pageNo", 1).queryParam("dataType", "JSON")
        .queryParam("base_date", nowDate).queryParam("base_time", nowTime).queryParam("nx", 63).queryParam("ny", 124)
        .build(false);

    String responseObject = restTemplate.getForObject(uri.toUriString(), String.class);

    String response = new org.json.JSONObject(responseObject).getJSONObject("response").getJSONObject("body")
        .getJSONObject("items").toString();

    try {
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
      JSONArray weatherArray = (JSONArray) jsonObject.get("item");

      for (int i = 0; i < weatherArray.size(); i++) {
        JSONObject item = (JSONObject) weatherArray.get(i);
        
        String baseDate = item.get("fcstDate").toString();
        String baseTime = item.get("fcstTime").toString();
        String baseTimeTemp = "T" + baseTime.substring(0,2);
        String categoryTemp = item.get("category").toString();
        
        int category;
        if(categoryTemp.equals("TMP")) category = 1;
        else if(categoryTemp.equals("REH")) category = 2;
        else category = 0;
        
        if ((category==1) || (category==2)) {
          float value = Float.valueOf(item.get("fcstValue").toString());

          mergeWeather(baseDate, category, baseTimeTemp, value);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return response;
  }

  public List<Weather> getAll() throws Exception {
    return weatherMapper.getAll();
  }

  public int mergeWeather(String baseDate, int category, String baseTimeTemp, float value) throws Exception {    
    MergeWeather mergeWeather = new MergeWeather();
    mergeWeather.setInputdatetime(baseDate);
    mergeWeather.setDataType(category);
    mergeWeather.setBaseTime(baseTimeTemp);
    mergeWeather.setValue(value);
    int result = weatherMapper.mergeWeather(mergeWeather);

    return result;
  }

  public Weather chooseData(String dataOption) throws Exception{
    List<Weather> list = weatherMapper.getAll();
    Weather returnData = null;

    JSONParser jsonParser = new JSONParser();
    JSONObject dataOptioObject = (JSONObject) jsonParser.parse(dataOption);
    String chooseDataTypeTemp = dataOptioObject.get("dataType").toString();
    int chooseDataType = 0;
    if(chooseDataTypeTemp.equals("온도")) {
      chooseDataType = 1;
    } else if(chooseDataTypeTemp.equals("습도")) {
      chooseDataType = 2;
    }
    String chooseDate = dataOptioObject.get("selectDate").toString();

    for(int i=0 ; i<list.size() ; i++) {
      String dataType = String.valueOf(list.get(i).getDataType());
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String inputDateTime = simpleDateFormat.format(list.get(i).getInputdatetime());

      if(String.valueOf(chooseDataType).equals(dataType) && chooseDate.equals(inputDateTime)) {
        // System.out.println(inputDateTime + " " + dataType);
        returnData = list.get(i);
      }
    }

    return returnData;
  }

  public void editCell(String editDate, String dataType, String value, String iRow) throws Exception {
    int dataTypeTemp;
    if(dataType.equals("온도")) {
      dataTypeTemp = 1;
    } else {
      dataTypeTemp = 2;
    }
    float valueTemp = Float.parseFloat(value+".0000");
    int timeTemp = Integer.parseInt(iRow)-1;
    String editTime = "";

    if(timeTemp < 10) {
      editTime = "T0"+String.valueOf(timeTemp);
    } else {
      editTime = "T"+String.valueOf(timeTemp);
    }

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;

    try {
      date = dateFormat.parse(editDate);
    } catch(ParseException e) {
      e.printStackTrace();
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, 1);
    String editDateTemp = dateFormat.format(cal.getTime());
    

    System.out.println(editDateTemp + " " + dataTypeTemp + " " + valueTemp + " " + editTime);

    EditCell editCell = new EditCell(editDate, dataTypeTemp, valueTemp, editTime);

    weatherMapper.cellEdit(editCell);
  }

}
