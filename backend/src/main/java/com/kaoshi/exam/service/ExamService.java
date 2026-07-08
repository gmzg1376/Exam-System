package com.kaoshi.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kaoshi.exam.dto.ExamDTO;
import com.kaoshi.exam.entity.Exam;
import com.kaoshi.exam.mapper.ExamMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService {

    private final ExamMapper examMapper;

    public ExamService(ExamMapper examMapper) {
        this.examMapper = examMapper;
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