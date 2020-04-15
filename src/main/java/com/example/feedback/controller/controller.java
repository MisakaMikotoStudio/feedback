package com.example.feedback.controller;

import com.example.feedback.dao.LabelRepository;
import com.example.feedback.dao.RecordRepository;
import com.example.feedback.dao.model.Label;
import com.example.feedback.dao.model.Record;
import com.example.feedback.model.BaseResponse;
import com.example.feedback.model.RecordCountDto;
import com.example.feedback.model.RecordDto;
import com.example.feedback.util.UpdateTool;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = "用户反馈记录的数据接口")
@RestController
@RequestMapping("/")
public class controller {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private LabelRepository labelRepository;

    @GetMapping("/data/query")
    @ApiOperation("根据来源，获取所有的用户反馈记录")
    @ApiImplicitParam(name = "source", value = "记录来源:report_feedback，报表反馈；chat_feedback，群反馈",
            required = false, dataType = "String", paramType = "query")
    public BaseResponse getAllRecords(@RequestParam String source) {
        List<RecordDto> dtos = recordRepository.findAll().stream()
                .filter(record -> record.getSource().equals(source))
                .map(RecordDto::new).collect(Collectors.toList());
        dtos.sort(((o1, o2) -> o2.getFeedBackTime().compareTo(o1.getFeedBackTime())));
        return BaseResponse.success(dtos);
    }

    @PostMapping("/data/modify")
    @ApiOperation("更新记录，id必须传递")
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

    @PostMapping("/data/add")
    @ApiOperation("添加记录，id不能传递")
    public BaseResponse addRecord(@RequestBody RecordDto dto) {
        if (!Objects.isNull(dto.getId())) {
            throw new RuntimeException("添加记录时，不能传递id");
        }
        recordRepository.save(dto.toRecord());
        return BaseResponse.success();
    }

    @GetMapping("/count/status")
    @ApiOperation("状态统计数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "startTime",
                    value = "开始时间,毫秒级时间戳。如果不传递，代表不做限制",
                    required = false,
                    dataType = "Long",
                    paramType = "query"
            ),
            @ApiImplicitParam(
                    name = "endTime",
                    value = "开始时间,毫秒级时间戳。如果不传递，代表不做限制",
                    required = false,
                    dataType = "Long",
                    paramType = "query"
            )
    })
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

    @GetMapping("/count/labels")
    @ApiOperation("标签统计数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "startTime",
                    value = "开始时间,毫秒级时间戳。如果不传递，代表不做限制",
                    required = false,
                    dataType = "Long",
                    paramType = "query"
            ),
            @ApiImplicitParam(
                    name = "endTime",
                    value = "开始时间,毫秒级时间戳。如果不传递，代表不做限制",
                    required = false,
                    dataType = "Long",
                    paramType = "query"
            )
    })
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

    @PostMapping("/label/add")
    @ApiOperation("添加标签")
    @ApiImplicitParam(name = "label", value = "标签，不能为空，不能重复", required = false, dataType = "String", paramType = "query")
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

    @GetMapping("/label/getAll")
    @ApiOperation("获取所有标签")
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
        // 因为前端传递的时间是 2020-04-02 00:00:00 这种，所以要加一天才能查到最后一天的记录
        Long finalEndTime = endTime + 24 * 3600 * 1000;
        return recordRepository.findAll().stream()
                .filter(record -> record.getFeedBackTime() > finalStartTime && record.getFeedBackTime() < finalEndTime)
                .collect(Collectors.toList());
    }
}
