
<template>
  <div class="result-container">
    <el-card class="result-card">
      <div class="score-section">
        <div class="score-circle" :class="getScoreClass(sheet?.score)">
          <span class="score-value">{{ sheet?.score || 0 }}</span>
          <span class="score-label">分</span>
        </div>
        <p class="score-title">考试成绩</p>
        <div class="score-stats">
          <span class="stat-item">
            <el-icon><Check /></el-icon>
            {{ correctCount }} 题正确
          </span>
          <span class="stat-item">
            <el-icon><Close /></el-icon>
            {{ wrongCount }} 题错误
          </span>
          <span class="stat-item">
            <el-icon><Clock /></el-icon>
            {{ formatTime(sheet?.spendTime) }}
          </span>
        </div>
      </div>
      
      <div class="info-section">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <el-icon class="info-icon"><Document /></el-icon>
              <div class="info-content">
                <span class="info-label">考试名称</span>
                <span class="info-value">{{ exam?.title }}</span>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <el-icon class="info-icon"><Clock /></el-icon>
              <div class="info-content">
                <span class="info-label">用时</span>
                <span class="info-value">{{ formatTime(sheet?.spendTime) }}</span>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <el-icon class="info-icon"><Monitor /></el-icon>
              <div class="info-content">
                <span class="info-label">切屏次数</span>
                <span class="info-value" :class="{ warning: sheet?.screenSwitchCount > 5 }">
                  {{ sheet?.screenSwitchCount || 0 }}
                </span>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <el-icon class="info-icon"><Calendar /></el-icon>
              <div class="info-content">
                <span class="info-label">提交时间</span>
                <span class="info-value">{{ formatDateTime(sheet?.submitTime) }}</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <div class="analysis-section">
        <div class="section-header">
          <h3>答题详情</h3>
          <el-button-group>
            <el-button 
              :type="filterType === 'all' ? 'primary' : ''" 
              size="small"
              @click="filterType = 'all'"
            >全部</el-button>
            <el-button 
              :type="filterType === 'wrong' ? 'danger' : ''" 
              size="small"
              @click="filterType = 'wrong'"
            >错题</el-button>
            <el-button 
              :type="filterType === 'correct' ? 'success' : ''" 
              size="small"
              @click="filterType = 'correct'"
            >正确</el-button>
          </el-button-group>
        </div>
        
        <div 
          v-for="(detail, index) in filteredDetails" 
          :key="detail.id" 
          class="answer-detail"
          :class="{ correct: detail.isCorrect, wrong: !detail.isCorrect }"
        >
          <div class="detail-header">
            <div class="detail-index">
              <el-icon :type="detail.isCorrect ? 'CheckCircle' : 'CloseCircle'" />
              <span>第 {{ getOriginalIndex(detail) + 1 }} 题</span>
            </div>
            <el-tag :type="detail.isCorrect ? 'success' : 'danger'" size="small">
              {{ detail.isCorrect ? '正确' : '错误' }}
            </el-tag>
            <span class="detail-score">
              {{ getQuestionScore(detail.questionId) }}分
            </span>
          </div>
          
          <div class="detail-content">
            <div class="question-type">
              <el-tag size="mini" :type="getQuestionTypeTag(getQuestionType(detail.questionId))">
                {{ getQuestionTypeName(getQuestionType(detail.questionId)) }}
              </el-tag>
            </div>
            <p class="question-text">{{ getQuestionContent(detail.questionId) }}</p>
          </div>
          
          <div class="answer-section">
            <div class="answer-row">
              <span class="answer-label">
                <el-icon><EditPen /></el-icon>
                你的答案：
              </span>
              <span class="user-answer" :class="{ wrong: !detail.isCorrect }">
                {{ detail.userAnswer || '未作答' }}
              </span>
            </div>
            <div class="answer-row" v-if="!detail.isCorrect">
              <span class="answer-label">
                <el-icon><CircleCheck /></el-icon>
                正确答案：
              </span>
              <span class="correct-answer">
                {{ getQuestionAnswer(detail.questionId) }}
              </span>
            </div>
          </div>
        </div>
        
        <div v-if="filteredDetails.length === 0" class="empty-state">
          <el-empty description="暂无符合条件的答题记录" />
        </div>
      </div>
      
      <div class="action-section">
        <el-button type="primary" size="large" @click="$router.push('/')">
          <el-icon><Home /></el-icon>
          返回首页
        </el-button>
        <el-button type="warning" size="large" @click="$router.push('/wrong')">
          <el-icon><Book /></el-icon>
          查看错题本
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAnswerDetail } from '../api/answer'
import { getExamById, getExamQuestions } from '../api/exam'

const route = useRoute()

const sheet = ref(null)
const details = ref([])
const exam = ref(null)
const questions = ref([])
const filterType = ref('all')

const correctCount = computed(() => details.value.filter(d => d.isCorrect).length)
const wrongCount = computed(() => details.value.filter(d => !d.isCorrect).length)

const filteredDetails = computed(() => {
  if (filterType.value === 'all') return details.value
  if (filterType.value === 'correct') return details.value.filter(d => d.isCorrect)
  if (filterType.value === 'wrong') return details.value.filter(d => !d.isCorrect)
  return details.value
})

function formatTime(seconds) {
  if (!seconds) return '0分钟'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}分${s}秒`
}

function formatDateTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

function getScoreClass(score) {
  if (!score) return 'score-default'
  if (score >= 90) return 'score-excellent'
  if (score >= 70) return 'score-good'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

function getQuestionContent(questionId) {
  const q = questions.value.find(item => item.id === questionId)
  return q?.content || ''
}

function getQuestionAnswer(questionId) {
  const q = questions.value.find(item => item.id === questionId)
  return q?.answer || ''
}

function getQuestionType(questionId) {
  const q = questions.value.find(item => item.id === questionId)
  return q?.type || 0
}

function getQuestionTypeName(type) {
  const types = { 1: '单选题', 2: '多选题', 3: '填空题', 4: '编程题' }
  return types[type] || '未知'
}

function getQuestionTypeTag(type) {
  const types = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger' }
  return types[type] || 'info'
}

function getQuestionScore(questionId) {
  const q = questions.value.find(item => item.id === questionId)
  return q?.score || 0
}

function getOriginalIndex(detail) {
  return details.value.findIndex(d => d.id === detail.id)
}

onMounted(async () => {
  try {
    const detailRes = await getAnswerDetail(route.params.sheetId)
    
    if (detailRes.code === 200 && detailRes.data) {
      sheet.value = detailRes.data.sheet
      details.value = detailRes.data.details || []
      
      const sheetData = detailRes.data.sheet
      const examId = sheetData?.examId || sheetData?.exam_id
      
      if (examId) {
        const examRes = await getExamById(examId)
        if (examRes.code === 200 && examRes.data) {
          exam.value = examRes.data
          const questionRes = await getExamQuestions(examRes.data.id)
          if (questionRes.code === 200 && questionRes.data) {
            questions.value = questionRes.data
          }
        }
      }
    }
  } catch (error) {
    console.error('获取结果失败:', error)
  }
})
</script>

<style scoped>
.result-container {
  min-height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  background: #f5f7fa;
  padding: 40px;
}

.result-card {
  width: 900px;
}

.score-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 0;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
}

.score-circle {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.score-default {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.score-excellent {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.score-good {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.score-pass {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.score-fail {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
}

.score-value {
  font-size: 56px;
  font-weight: bold;
  color: white;
}

.score-label {
  font-size: 24px;
  color: white;
  margin-left: 8px;
}

.score-title {
  font-size: 20px;
  color: #303133;
  margin-bottom: 20px;
}

.score-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #606266;
}

.stat-item .el-icon {
  font-size: 20px;
}

.info-section {
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 10px;
}

.info-icon {
  font-size: 24px;
  color: #409eff;
}

.info-content {
  flex: 1;
}

.info-label {
  display: block;
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.info-value {
  font-weight: bold;
  font-size: 16px;
}

.info-value.warning {
  color: #f56c6c;
}

.analysis-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  color: #303133;
}

.answer-detail {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.answer-detail.correct {
  border-color: #e8f5e9;
  background: #fafdfb;
}

.answer-detail.wrong {
  border-color: #fdecea;
  background: #fefaf9;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-index {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
}

.detail-index .el-icon {
  font-size: 20px;
}

.answer-detail.correct .detail-index {
  color: #67c23a;
}

.answer-detail.wrong .detail-index {
  color: #f56c6c;
}

.detail-score {
  font-size: 14px;
  color: #909399;
}

.detail-content {
  margin-bottom: 16px;
}

.question-type {
  margin-bottom: 12px;
}

.question-text {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  margin: 0;
}

.answer-section {
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.answer-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.answer-row:last-child {
  margin-bottom: 0;
}

.answer-label {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-answer {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}

.user-answer.wrong {
  color: #f56c6c;
}

.correct-answer {
  font-size: 15px;
  font-weight: bold;
  color: #67c23a;
}

.empty-state {
  padding: 40px;
  text-align: center;
}

.action-section {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.action-section .el-button {
  padding: 12px 32px;
  font-size: 15px;
  font-weight: bold;
  border-radius: 10px;
}
</style>
