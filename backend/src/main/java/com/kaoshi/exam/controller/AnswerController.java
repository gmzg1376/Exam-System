
package com.kaoshi.exam.controller;

import com.kaoshi.exam.dto.SubmitDTO;
import com.kaoshi.exam.entity.AnswerDetail;
import com.kaoshi.exam.entity.AnswerSheet;
import com.kaoshi.exam.entity.User;
import com.kaoshi.exam.service.AnswerService;
import com.kaoshi.exam.utils.Result;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/submit")
    public Result<AnswerSheet> submitAnswer(@AuthenticationPrincipal User user, @RequestBody SubmitDTO dto) {
        AnswerSheet sheet = answerService.submit(user.getId(), dto);
        return Result.success("提交成功", sheet);
    }

    @GetMapping("/history")
    public Result<List<AnswerSheet>> getAnswerHistory(@AuthenticationPrincipal User user) {
        return Result.success(answerService.findByUserId(user.getId()));
    }

    @GetMapping("/exam/{examId}")
    public Result<AnswerSheet> getAnswerByExam(@AuthenticationPrincipal User user, @PathVariable Long examId) {
        return Result.success(answerService.findByUserIdAndExamId(user.getId(), examId));
    }

    @GetMapping("/sheet/{sheetId}")
    public Result<Map<String, Object>> getAnswerDetail(@PathVariable Long sheetId) {
        AnswerSheet sheet = answerService.findAnswerSheetById(sheetId);
        List<AnswerDetail> details = answerService.findBySheetDetails(sheetId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("sheet", sheet);
        data.put("details", details);
        
        return Result.success(data);
    }

    @GetMapping("/exam/all/{examId}")
    public Result<List<AnswerSheet>> getAllAnswersByExam(@PathVariable Long examId) {
        return Result.success(answerService.findByExamId(examId));
    }
}
