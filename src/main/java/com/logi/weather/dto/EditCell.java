package com.logi.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditCell {
  private String editDate;
  private int dataType;
  private float value;
  private String editTime;
}
