package com.example.feedback.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class RecordCountDto implements Serializable {
    private Integer count;
    private Map<String, Integer> data;
}
