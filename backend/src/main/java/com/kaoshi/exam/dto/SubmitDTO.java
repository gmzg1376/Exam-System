
package com.kaoshi.exam.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitDTO {

    private Long examId;
    
    private List<AnswerDTO> answers;
    
    private Integer screenSwitchCount;
    
    private Long totalSpendTime;
}
