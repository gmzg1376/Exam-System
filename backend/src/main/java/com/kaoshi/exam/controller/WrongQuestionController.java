
package com.kaoshi.exam.controller;

import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.entity.User;
import com.kaoshi.exam.service.WrongQuestionService;
import com.kaoshi.exam.utils.Result;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wrong")
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    public WrongQuestionController(WrongQuestionService wrongQuestionService) {
        this.wrongQuestionService = wrongQuestionService;
    }

    @GetMapping
    public Result<List<Question>> getWrongQuestions(@AuthenticationPrincipal User user) {
        return Result.success(wrongQuestionService.findWrongQuestionsByUserId(user.getId()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeWrongQuestion(@PathVariable Long id) {
        wrongQuestionService.delete(id);
        return Result.success("移除成功", null);
    }

    @DeleteMapping("/clear")
    public Result<Void> clearWrongQuestions(@AuthenticationPrincipal User user) {
        wrongQuestionService.clearByUserId(user.getId());
        return Result.success("清空成功", null);
    }
}
