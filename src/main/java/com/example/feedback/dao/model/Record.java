package com.example.feedback.dao.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户反馈记录
 */
@Entity
@Table(name = "record")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Record implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 提出问题的反馈人,只有一个
     */
    @Column(nullable = false)
    private String commiter;

    /**
     * 跟进处理的人
     * 可以有多个，以英文','分隔
     */
    @Column(nullable = false)
    private String handlers;

    /**
     * 当前问题的状态
     * 待解决 -> 正在解决 -> 已解决 -> 待改进 -> 正在改进 -> 已改进
     * 用户反馈的处理流程，需要首先为用户解决问题，其次再是去思考要如何改进，才能避免再次出现这种问题.
     */
    @Column(nullable = false)
    private String status;

    /**
     * 标签，可以有多个，以英文','分隔
     * 对此记录添加标签，便于进行分类分析
     */
    @Column(nullable = false)
    private String labels;

    /**
     * 问题描述
     * 如需记录多人时，可以以 人名:描述的方式进行记录。
     * 可以存储 <a href="">xxx</> 这样的超链接文本
     */
    @Column(nullable = false)
    private String desc;

    /**
     * 解决方案
     * 如多人合作解决，以 人名 : xx 的方式记录解决方案，勿要贪了别人的功劳
     */
    @Column(nullable = false)
    private String solution;

    /**
     * 记录反馈的时间，目前仅精确到日期
     */
    @Column(nullable = false)
    private Long feedBackTime;

    /**
     * 记录来源
     * 目前的记录来源：report_feedback，报表反馈；chat_feedback，群反馈
     */
    @Column(nullable = false)
    private String source;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Long createTime;

    @LastModifiedDate
    @Column(nullable = false)
    private Long updateTime;
}