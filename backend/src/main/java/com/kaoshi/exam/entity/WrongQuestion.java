
package com.kaoshi.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wrong_question")
public class WrongQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long questionId;
    
    private Integer wrongCount;
    
    private LocalDateTime lastWrongTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
