package com.logi.weather.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.logi.weather.dto.Weather;
import com.logi.weather.service.WeatherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logi")
public class WeatherController {
  
  WeatherService weatherService;

  @Autowired
  public WeatherController(WeatherService weatherService) {
    this.weatherService = weatherService;
  }
  
  @Scheduled(cron = "3 10 * * * ?")
  @RequestMapping(path = "/weather", method = RequestMethod.GET)
  public Object getWeather() throws UnsupportedEncodingException {
    Object response = weatherService.getWeather();
    return response;
  }

  @RequestMapping("/getAll")
  public List<Weather> query() throws Exception{
    return weatherService.getAll();
  }

  @RequestMapping(value = "/weatherData", method = RequestMethod.GET)
  public Weather weatherData(HttpServletRequest request) throws Exception {
    String chooseData = request.getParameter("data");
    
    Weather returnData = weatherService.chooseData(chooseData);
    
    return returnData;
  }

  @RequestMapping(value = "/update")
  public String cellEdit(HttpServletRequest request) throws Exception {
    String editDate = request.getParameter("rowDate");
    String cellName = request.getParameter("cellName");
    String value = request.getParameter("value");
    String iRow = request.getParameter("iRow");

    weatherService.editCell(editDate, cellName, value, iRow);

    return "success";
  }
}
