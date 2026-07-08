
package com.kaoshi.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long examId;
    
    private Integer type;
    
    private String content;
    
    private String options;
    
    private Integer score;
    
    private String answer;
    
    private String knowledgePoint;
    
    private String analysis;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
