package com.kaoshi.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaoshi.exam.dto.ExamDTO;
<<<<<<< HEAD
import com.kaoshi.exam.entity.Exam;
=======
import com.kaoshi.exam.entity.AnswerSheet;
import com.kaoshi.exam.entity.Exam;
import com.kaoshi.exam.entity.Question;
import com.kaoshi.exam.exception.BusinessException;
import com.kaoshi.exam.mapper.AnswerSheetMapper;
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
import com.kaoshi.exam.mapper.ExamMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService {

    private final ExamMapper examMapper;
<<<<<<< HEAD

    public ExamService(ExamMapper examMapper) {
        this.examMapper = examMapper;
=======
    private final AnswerSheetMapper answerSheetMapper;
    private final QuestionService questionService;

    public ExamService(ExamMapper examMapper,
                       AnswerSheetMapper answerSheetMapper,
                       QuestionService questionService) {
        this.examMapper = examMapper;
        this.answerSheetMapper = answerSheetMapper;
        this.questionService = questionService;
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
    }

    public List<Exam> findAll() {
        return examMapper.selectList(null);
    }

    public List<Exam> findAvailable() {
        return examMapper.selectList(new QueryWrapper<Exam>().eq("status", 1));
    }

    public Exam findById(Long id) {
        return examMapper.selectById(id);
    }

    public Exam create(ExamDTO dto) {
        Exam exam = new Exam();
        exam.setTitle(dto.getTitle());
        exam.setDuration(dto.getDuration());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setRandomQuestion(dto.getRandomQuestion());
        exam.setRandomOption(dto.getRandomOption());
        exam.setDescription(dto.getDescription());
        exam.setStatus(0);
        exam.setCreateTime(LocalDateTime.now());
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.insert(exam);
        return exam;
    }

    public Exam update(Long id, ExamDTO dto) {
        Exam exam = findById(id);
<<<<<<< HEAD
=======
        if (exam == null) {
            throw new BusinessException(404, "考试不存在");
        }
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
        exam.setTitle(dto.getTitle());
        exam.setDuration(dto.getDuration());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setRandomQuestion(dto.getRandomQuestion());
        exam.setRandomOption(dto.getRandomOption());
        exam.setDescription(dto.getDescription());
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);
        return exam;
    }

    public void delete(Long id) {
<<<<<<< HEAD
=======
        Exam exam = findById(id);
        if (exam == null) {
            throw new BusinessException(404, "考试不存在");
        }
        Long sheetCount = answerSheetMapper.selectCount(
                new QueryWrapper<AnswerSheet>().eq("exam_id", id));
        if (sheetCount > 0) {
            throw new BusinessException("该考试已有答题记录，无法删除");
        }
        List<Question> questions = questionService.findByExamId(id);
        for (Question question : questions) {
            questionService.delete(question.getId());
        }
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
        examMapper.deleteById(id);
    }

    public void start(Long id) {
        Exam exam = findById(id);
        exam.setStatus(1);
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);
    }

    public void end(Long id) {
        Exam exam = findById(id);
        exam.setStatus(2);
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);
    }
}