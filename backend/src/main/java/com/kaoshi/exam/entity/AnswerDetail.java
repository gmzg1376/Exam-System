
package com.kaoshi.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer_detail")
public class AnswerDetail {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long sheetId;
    
    private Long questionId;
    
    private String userAnswer;
    
    private Boolean isCorrect;
    
    private Long spendTime;
    
    private LocalDateTime createTime;
}
