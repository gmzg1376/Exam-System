
package com.kaoshi.exam.dto;

import lombok.Data;

@Data
public class QuestionDTO {

    private Long examId;
    
    private Integer type;
    
    private String content;
    
    private String options;
    
    private Integer score;
    
    private String answer;
    
    private String knowledgePoint;
    
    private String analysis;
}
