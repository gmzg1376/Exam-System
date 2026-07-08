package com.kaoshi.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.entity.WrongQuestion;
import com.kaoshi.exam.mapper.WrongQuestionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WrongQuestionService {

    private final WrongQuestionMapper wrongQuestionMapper;
    private final QuestionService questionService;

    public WrongQuestionService(WrongQuestionMapper wrongQuestionMapper, QuestionService questionService) {
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.questionService = questionService;
    }

    public List<WrongQuestion> findByUserId(Long userId) {
        return wrongQuestionMapper.selectList(new QueryWrapper<WrongQuestion>().eq("user_id", userId));
    }

    public List<Question> findWrongQuestionsByUserId(Long userId) {
        List<WrongQuestion> wrongQuestions = findByUserId(userId);
        List<Question> questions = new ArrayList<>();
        for (WrongQuestion wq : wrongQuestions) {
            Question question = questionService.findById(wq.getQuestionId());
            if (question != null) {
                questions.add(question);
            }
        }
        return questions;
    }

    public void delete(Long id) {
        wrongQuestionMapper.deleteById(id);
    }

    public void clearByUserId(Long userId) {
        List<WrongQuestion> wrongQuestions = findByUserId(userId);
        for (WrongQuestion wq : wrongQuestions) {
            wrongQuestionMapper.deleteById(wq.getId());
        }
    }
}