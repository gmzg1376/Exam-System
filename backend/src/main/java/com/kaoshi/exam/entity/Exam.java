
package com.kaoshi.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private Integer duration;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    private Boolean randomQuestion;
    
    private Boolean randomOption;
    
    private String description;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
