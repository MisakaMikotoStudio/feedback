package com.example.feedback.dao.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户反馈记录标签
 */
@Entity
@Table(name = "label")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Label implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 标签，不能为空
     */
    @Column(nullable = false)
    private String label;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Long createTime;

    @LastModifiedDate
    @Column(nullable = false)
    private Long updateTime;
}
