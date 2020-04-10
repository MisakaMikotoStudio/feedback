package com.example.feedback.model;

import com.example.feedback.dao.model.Record;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class RecordDto implements Serializable {

    private Integer id;

    /**
     * 提出问题的反馈人,只有一个
     */
    private String commiter;

    /**
     * 跟进处理的人
     * 可以有多个，以英文','分隔
     */
    private List<String> handlers;

    /**
     * 当前问题的状态
     * 待解决 -> 正在解决 -> 已解决 -> 待改进 -> 正在改进 -> 已改进
     * 用户反馈的处理流程，需要首先为用户解决问题，其次再是去思考要如何改进，才能避免再次出现这种问题.
     */
    private String status;

    /**
     * 标签，可以有多个，以英文','分隔
     * 对此记录添加标签，便于进行分类分析
     */
    private List<String> labels;

    /**
     * 问题描述
     * 如需记录多人时，可以以 人名:描述的方式进行记录。
     * 可以存储 <a href="">xxx</> 这样的超链接文本
     */
    private String desc;

    /**
     * 解决方案
     * 如多人合作解决，以 人名 : xx 的方式记录解决方案，勿要贪了别人的功劳
     */
    private String solution;

    /**
     * 记录反馈的时间，目前仅精确到日期
     */
    private Long feedBackTime;

    /**
     * 记录来源
     * 目前的记录来源：report_feedback，报表反馈；chat_feedback，群反馈
     */
    private String source;

    public RecordDto(Record record) {
        this.id = record.getId();
        this.commiter = record.getCommiter();
        this.handlers = Lists.newArrayList(record.getHandlers().split(","));
        this.status = record.getStatus();
        this.labels = Lists.newArrayList(record.getLabels().split(","));
        this.desc = record.getDesc();
        this.solution = record.getSolution();
        this.feedBackTime = record.getFeedBackTime();
        this.source = record.getSource();
    }

    public Record toRecord() {
        Record record = new Record();
        record.setId(this.getId());
        record.setCommiter(this.getCommiter());
        record.setStatus(this.getStatus());
        record.setDesc(this.getDesc());
        record.setSolution(this.getSolution());
        record.setFeedBackTime(this.getFeedBackTime());
        record.setSource(this.getSource());
        if (!CollectionUtils.isEmpty(this.getHandlers())) {
            record.setHandlers(String.join(",", this.getHandlers()));
        }
        if (!CollectionUtils.isEmpty(this.getLabels())) {
            record.setLabels(String.join(",", this.getLabels()));
        }
        return record;
    }
}
