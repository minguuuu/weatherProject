package com.logi.weather.dto;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
  private int sid;
  private Date inputdatetime;
  private int dataType;
  private float T00;  private float T01;  private float T02;
  private float T03;  private float T04;  private float T05;
  private float T06;  private float T07;  private float T08;
  private float T09;  private float T10;  private float T11;
  private float T12;  private float T13;  private float T14;
  private float T15;  private float T16;  private float T17;
  private float T18;  private float T19;  private float T20;
  private float T21;  private float T22;  private float T23;
}
