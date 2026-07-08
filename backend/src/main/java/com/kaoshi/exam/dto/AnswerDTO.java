
package com.kaoshi.exam.dto;

import lombok.Data;

@Data
public class AnswerDTO {

    private Long questionId;
    
    private String userAnswer;
    
    private Long spendTime;
}
