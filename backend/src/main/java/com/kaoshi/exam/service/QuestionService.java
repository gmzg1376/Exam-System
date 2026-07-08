package com.kaoshi.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaoshi.exam.dto.QuestionDTO;
import com.kaoshi.exam.entity.AnswerDetail;
import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.entity.WrongQuestion;
import com.kaoshi.exam.exception.BusinessException;
import com.kaoshi.exam.mapper.AnswerDetailMapper;
import com.kaoshi.exam.mapper.QuestionMapper;
import com.kaoshi.exam.mapper.WrongQuestionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionMapper questionMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final WrongQuestionMapper wrongQuestionMapper;

    public QuestionService(QuestionMapper questionMapper,
                           AnswerDetailMapper answerDetailMapper,
                           WrongQuestionMapper wrongQuestionMapper) {
        this.questionMapper = questionMapper;
        this.answerDetailMapper = answerDetailMapper;
        this.wrongQuestionMapper = wrongQuestionMapper;
    }

    public List<Question> findByExamId(Long examId) {
        return questionMapper.selectList(new QueryWrapper<Question>().eq("exam_id", examId));
    }

    public List<Question> findByExamIdWithRandom(Long examId, boolean randomQuestion) {
        List<Question> questions = findByExamId(examId);
        if (randomQuestion) {
            Collections.shuffle(questions);
        }
        return questions;
    }

    public Question findById(Long id) {
        return questionMapper.selectById(id);
    }

    public Question create(QuestionDTO dto) {
        Question question = new Question();
        question.setExamId(dto.getExamId());
        question.setType(dto.getType());
        question.setContent(dto.getContent());
        question.setOptions(dto.getOptions());
        question.setScore(dto.getScore());
        question.setAnswer(dto.getAnswer());
        question.setKnowledgePoint(dto.getKnowledgePoint());
        question.setAnalysis(dto.getAnalysis());
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.insert(question);
        return question;
    }

    public Question update(Long id, QuestionDTO dto) {
        Question question = findById(id);
        question.setType(dto.getType());
        question.setContent(dto.getContent());
        question.setOptions(dto.getOptions());
        question.setScore(dto.getScore());
        question.setAnswer(dto.getAnswer());
        question.setKnowledgePoint(dto.getKnowledgePoint());
        question.setAnalysis(dto.getAnalysis());
        question.setUpdateTime(LocalDateTime.now());
        questionMapper.updateById(question);
        return question;
    }

    public void delete(Long id) {
        if (findById(id) == null) {
            throw new BusinessException(404, "题目不存在");
        }
        Long answerCount = answerDetailMapper.selectCount(
                new QueryWrapper<AnswerDetail>().eq("question_id", id));
        if (answerCount > 0) {
            throw new BusinessException("该题目已有答题记录，无法删除");
        }
        wrongQuestionMapper.delete(new QueryWrapper<WrongQuestion>().eq("question_id", id));
        questionMapper.deleteById(id);
    }

    public List<Question> findByKnowledgePoint(String knowledgePoint) {
        return questionMapper.selectList(new QueryWrapper<Question>().eq("knowledge_point", knowledgePoint));
    }
}