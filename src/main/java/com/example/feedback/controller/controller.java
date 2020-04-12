package com.example.feedback.controller;

import com.example.feedback.dao.LabelRepository;
import com.example.feedback.dao.RecordRepository;
import com.example.feedback.dao.model.Label;
import com.example.feedback.dao.model.Record;
import com.example.feedback.model.BaseResponse;
import com.example.feedback.model.RecordCountDto;
import com.example.feedback.model.RecordDto;
import com.example.feedback.util.UpdateTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class controller {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private LabelRepository labelRepository;

    /**
     * 查询所有的支持记录
     * @return
     */
    @GetMapping("/data/query")
    public BaseResponse getAllRecords(@RequestParam String source) {
        List<RecordDto> dtos = recordRepository.findAll().stream()
                .filter(record -> record.getSource().equals(source))
                .map(RecordDto::new).collect(Collectors.toList());
        return BaseResponse.success(dtos);
    }

    /**
     * 更新记录
     */
    @PostMapping("/data/modify")
    public BaseResponse updateRecord(@RequestBody RecordDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new RuntimeException("更新记录时，id必须传递");
        }

        Record targetRecord = dto.toRecord();
        Record originRecord = recordRepository.getOne(targetRecord.getId());
        UpdateTool.copyNullProperties(originRecord, targetRecord);
        recordRepository.save(targetRecord);
        return BaseResponse.success();
    }

    /**
     * 添加记录
     */
    @PostMapping("/data/add")
    public BaseResponse addRecord(@RequestBody RecordDto dto) {
        if (!Objects.isNull(dto.getId())) {
            throw new RuntimeException("添加记录时，不能传递id");
        }
        recordRepository.save(dto.toRecord());
        return BaseResponse.success();
    }

    /**
     * 状态统计数据接口
     */
    @GetMapping("/count/status")
    public BaseResponse StatusCount(@RequestParam(required = false) Long startTime,
                                    @RequestParam(required = false) Long endTime) {
        List<Record> records = getRecords(startTime, endTime);
        Map<String, Integer> count = new HashMap<>();
        for (Record record : records) {
            count.put(record.getStatus(), count.getOrDefault(record.getStatus(), 0) + 1);
        }

        RecordCountDto dto = new RecordCountDto();
        dto.setCount(records.size());
        dto.setData(count);
        return BaseResponse.success(dto);
    }

    /**
     * 标签统计接口
     * @return
     */
    @GetMapping("/count/labels")
    public BaseResponse labelsCount(@RequestParam(required = false) Long startTime,
                                    @RequestParam(required = false) Long endTime) {
        List<Record> records = getRecords(startTime, endTime);
        Map<String, Integer> count = new HashMap<>();
        for (Record record : records) {
            for (String label : record.getLabels().split(",")) {
                count.put(label, count.getOrDefault(label, 0) + 1);
            }
        }

        RecordCountDto dto = new RecordCountDto();
        dto.setCount(records.size());
        dto.setData(count);
        return BaseResponse.success(dto);
    }

    /**
     * 添加标签
     * @param label 标签，不能为空，不能重复
     * @return
     */
    @PostMapping("/label/add")
    public BaseResponse addLabel(@RequestParam String label) {
        if (StringUtils.isBlank(label)) {
            throw new RuntimeException("标签不能为空:" + label + ";");
        }
        boolean duplicated = labelRepository.findAll().stream().anyMatch(label::equals);
        if (duplicated) {
            throw new RuntimeException("标签已存在:" + label + ";");
        }

        Label record = new Label();
        record.setLabel(label);
        labelRepository.save(record);
        return BaseResponse.success();
    }

    /**
     * 获取所有的标签
     * @return
     */
    @GetMapping("/label/getAll")
    public BaseResponse getallLabels() {
        List<String> labels = labelRepository.findAll().stream()
                .map(Label::getLabel).collect(Collectors.toList());
        return BaseResponse.success(labels);
    }

    private List<Record> getRecords(Long startTime, Long endTime) {
        if (startTime == null) {
            startTime = -1L;
        }
        if (endTime == null) {
            endTime = System.currentTimeMillis();
        }
        Long finalStartTime = startTime;
        Long finalEndTime = endTime;
        return recordRepository.findAll().stream()
                .filter(record -> record.getFeedBackTime() > finalStartTime && record.getFeedBackTime() < finalEndTime)
                .collect(Collectors.toList());
    }
}
