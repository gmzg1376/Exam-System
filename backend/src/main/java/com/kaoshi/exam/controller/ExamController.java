
package com.kaoshi.exam.controller;

import com.kaoshi.exam.dto.ExamDTO;
import com.kaoshi.exam.entity.Exam;
import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.service.ExamService;
import com.kaoshi.exam.service.QuestionService;
import com.kaoshi.exam.utils.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamService examService;
    private final QuestionService questionService;

    public ExamController(ExamService examService, QuestionService questionService) {
        this.examService = examService;
        this.questionService = questionService;
    }

    @GetMapping
    public Result<List<Exam>> getAllExams() {
        return Result.success(examService.findAll());
    }

    @GetMapping("/available")
    public Result<List<Exam>> getAvailableExams() {
        return Result.success(examService.findAvailable());
    }

    @GetMapping("/{id}")
    public Result<Exam> getExamById(@PathVariable Long id) {
        return Result.success(examService.findById(id));
    }

    @GetMapping("/{id}/questions")
    public Result<List<Question>> getExamQuestions(@PathVariable Long id) {
        Exam exam = examService.findById(id);
        List<Question> questions = questionService.findByExamIdWithRandom(id, exam.getRandomQuestion());
        return Result.success(questions);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Exam> createExam(@RequestBody ExamDTO dto) {
        return Result.success("创建成功", examService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Exam> updateExam(@PathVariable Long id, @RequestBody ExamDTO dto) {
        return Result.success("更新成功", examService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteExam(@PathVariable Long id) {
        examService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> startExam(@PathVariable Long id) {
        examService.start(id);
        return Result.success("考试已开始", null);
    }

    @PostMapping("/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> endExam(@PathVariable Long id) {
        examService.end(id);
        return Result.success("考试已结束", null);
    }
}
