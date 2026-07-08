
package com.kaoshi.exam.controller;

import com.kaoshi.exam.dto.QuestionDTO;
import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.service.QuestionService;
import com.kaoshi.exam.utils.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/exam/{examId}")
    public Result<List<Question>> getQuestionsByExam(@PathVariable Long examId) {
        return Result.success(questionService.findByExamId(examId));
    }

    @GetMapping("/{id}")
    public Result<Question> getQuestionById(@PathVariable Long id) {
        return Result.success(questionService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Question> createQuestion(@RequestBody QuestionDTO dto) {
        return Result.success("创建成功", questionService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Question> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        return Result.success("更新成功", questionService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/knowledge/{point}")
    public Result<List<Question>> getQuestionsByKnowledgePoint(@PathVariable String point) {
        return Result.success(questionService.findByKnowledgePoint(point));
    }
}
