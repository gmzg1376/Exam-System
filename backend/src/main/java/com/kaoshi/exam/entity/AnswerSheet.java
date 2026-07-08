
package com.kaoshi.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer_sheet")
public class AnswerSheet {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long examId;
    
    private LocalDateTime submitTime;
    
    private Integer score;
    
    private Integer status;
    
    private Long spendTime;
    
    private Integer screenSwitchCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
