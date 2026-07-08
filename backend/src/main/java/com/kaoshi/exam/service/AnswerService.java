package com.kaoshi.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaoshi.exam.dto.AnswerDTO;
import com.kaoshi.exam.dto.SubmitDTO;
import com.kaoshi.exam.entity.AnswerDetail;
import com.kaoshi.exam.entity.AnswerSheet;
import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.entity.WrongQuestion;
import com.kaoshi.exam.mapper.AnswerDetailMapper;
import com.kaoshi.exam.mapper.AnswerSheetMapper;
import com.kaoshi.exam.mapper.WrongQuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerService {

    private final AnswerSheetMapper answerSheetMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final QuestionService questionService;
    private final WrongQuestionMapper wrongQuestionMapper;

    public AnswerService(AnswerSheetMapper answerSheetMapper, AnswerDetailMapper answerDetailMapper,
                         QuestionService questionService, WrongQuestionMapper wrongQuestionMapper) {
        this.answerSheetMapper = answerSheetMapper;
        this.answerDetailMapper = answerDetailMapper;
        this.questionService = questionService;
        this.wrongQuestionMapper = wrongQuestionMapper;
    }

    @Transactional
    public AnswerSheet submit(Long userId, SubmitDTO dto) {
        System.out.println("DEBUG: submit called with examId: " + dto.getExamId() + ", answers count: " + dto.getAnswers().size());
        
        AnswerSheet sheet = new AnswerSheet();
        sheet.setUserId(userId);
        sheet.setExamId(dto.getExamId());
        sheet.setStatus(1);
        sheet.setScreenSwitchCount(dto.getScreenSwitchCount());
        sheet.setSpendTime(dto.getTotalSpendTime());
        sheet.setSubmitTime(LocalDateTime.now());
        sheet.setCreateTime(LocalDateTime.now());
        sheet.setUpdateTime(LocalDateTime.now());
        answerSheetMapper.insert(sheet);

        int totalScore = 0;
        for (AnswerDTO answer : dto.getAnswers()) {
            System.out.println("DEBUG: Processing questionId: " + answer.getQuestionId() + ", userAnswer: '" + answer.getUserAnswer() + "'");
            
            Question question = questionService.findById(answer.getQuestionId());
            System.out.println("DEBUG: Found question: " + question.getId() + ", type: " + question.getType() + ", answer: '" + question.getAnswer() + "'");
            
            AnswerDetail detail = new AnswerDetail();
            detail.setSheetId(sheet.getId());
            detail.setQuestionId(answer.getQuestionId());
            detail.setUserAnswer(answer.getUserAnswer());
            detail.setSpendTime(answer.getSpendTime());
            
            boolean isCorrect = isAnswerCorrect(question, answer.getUserAnswer());
            System.out.println("DEBUG: isCorrect: " + isCorrect + ", score: " + question.getScore());
            
            detail.setIsCorrect(isCorrect);
            detail.setCreateTime(LocalDateTime.now());
            answerDetailMapper.insert(detail);
            
            if (isCorrect) {
                totalScore += question.getScore();
            } else {
                updateWrongQuestion(userId, answer.getQuestionId());
            }
        }
        
        System.out.println("DEBUG: Total score: " + totalScore);
        sheet.setScore(totalScore);
        answerSheetMapper.updateById(sheet);
        
        return answerSheetMapper.selectById(sheet.getId());
    }

    private boolean isAnswerCorrect(Question question, String userAnswer) {
        if (question.getAnswer() == null || userAnswer == null) {
            return false;
        }
        
        String correctAnswer = question.getAnswer().trim();
        String submittedAnswer = userAnswer.trim();
        
        if (question.getType() == 3 || question.getType() == 4) {
            return correctAnswer.equalsIgnoreCase(submittedAnswer);
        }
        
        try {
            cn.hutool.json.JSONArray optionsArray = cn.hutool.json.JSONUtil.parseArray(question.getOptions());
            java.util.Map<String, String> labelToValue = new java.util.HashMap<>();
            
            for (int i = 0; i < optionsArray.size(); i++) {
                cn.hutool.json.JSONObject opt = optionsArray.getJSONObject(i);
                String label = opt.getStr("label");
                String value = opt.getStr("value");
                if (label != null && value != null) {
                    labelToValue.put(label, value.trim());
                }
            }
            
            java.util.Set<String> correctValues = new java.util.HashSet<>();
            for (String label : correctAnswer.split(",")) {
                String trimmed = label.trim();
                if (!trimmed.isEmpty()) {
                    String value = labelToValue.get(trimmed);
                    if (value != null) {
                        correctValues.add(value);
                    }
                }
            }
            
            java.util.Set<String> submittedValues = new java.util.HashSet<>();
            for (String label : submittedAnswer.split(",")) {
                String trimmed = label.trim();
                if (!trimmed.isEmpty()) {
                    String value = labelToValue.get(trimmed);
                    if (value != null) {
                        submittedValues.add(value);
                    }
                }
            }
            
            return correctValues.equals(submittedValues);
        } catch (Exception e) {
            return correctAnswer.equalsIgnoreCase(submittedAnswer);
        }
    }

    private void updateWrongQuestion(Long userId, Long questionId) {
        WrongQuestion wrongQuestion = wrongQuestionMapper.selectOne(
            new QueryWrapper<WrongQuestion>()
                .eq("user_id", userId)
                .eq("question_id", questionId)
        );
        if (wrongQuestion == null) {
            wrongQuestion = new WrongQuestion();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setLastWrongTime(LocalDateTime.now());
            wrongQuestion.setCreateTime(LocalDateTime.now());
            wrongQuestion.setUpdateTime(LocalDateTime.now());
            wrongQuestionMapper.insert(wrongQuestion);
        } else {
            wrongQuestion.setWrongCount(wrongQuestion.getWrongCount() + 1);
            wrongQuestion.setLastWrongTime(LocalDateTime.now());
            wrongQuestion.setUpdateTime(LocalDateTime.now());
            wrongQuestionMapper.updateById(wrongQuestion);
        }
    }

    public AnswerSheet findByUserIdAndExamId(Long userId, Long examId) {
        return answerSheetMapper.selectOne(
            new QueryWrapper<AnswerSheet>()
                .eq("user_id", userId)
                .eq("exam_id", examId)
        );
    }

    public List<AnswerSheet> findByUserId(Long userId) {
        return answerSheetMapper.selectList(new QueryWrapper<AnswerSheet>().eq("user_id", userId));
    }

    public List<AnswerSheet> findByExamId(Long examId) {
        return answerSheetMapper.selectList(new QueryWrapper<AnswerSheet>().eq("exam_id", examId));
    }

    public List<AnswerDetail> findBySheetId(Long sheetId) {
        return answerDetailMapper.selectList(new QueryWrapper<AnswerDetail>().eq("sheet_id", sheetId));
    }

    public AnswerSheet findAnswerSheetById(Long sheetId) {
        return answerSheetMapper.selectById(sheetId);
    }

    public List<AnswerDetail> findBySheetDetails(Long sheetId) {
        return answerDetailMapper.selectList(new QueryWrapper<AnswerDetail>().eq("sheet_id", sheetId));
    }
}