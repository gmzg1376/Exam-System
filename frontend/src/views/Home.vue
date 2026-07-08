<template>
  <div class="home-container">
    <el-header>
      <div class="header-left">
        <h2>在线考试系统</h2>
      </div>
      <div class="header-right">
          <div class="user-info">
            <el-button 
              type="primary" 
              size="large" 
              @click="goToProfile" 
              icon="User"
              class="profile-btn"
            >
              个人信息
            </el-button>
            <el-button 
              type="danger" 
              size="large" 
              @click="handleLogout" 
              icon="SwitchButton"
              class="logout-btn"
            >
              退出登录
            </el-button>
          </div>
        </div>
    </el-header>
    
    <el-main>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card 
            class="stat-card" 
            :class="{ active: filterType === 'available' }"
            @click="filterExams('available')"
          >
            <div class="stat-icon exam-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ availableCount }}</p>
              <p class="stat-label">可用考试</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card 
            class="stat-card" 
            :class="{ active: filterType === 'completed' }"
            @click="filterExams('completed')"
          >
            <div class="stat-icon answer-icon">
              <el-icon><EditPen /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ completedCount }}</p>
              <p class="stat-label">已完成</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card 
            class="stat-card" 
            @click="goToWrongQuestions"
          >
            <div class="stat-icon wrong-icon">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ wrongCount }}</p>
              <p class="stat-label">错题本</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon score-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ avgScore }}</p>
              <p class="stat-label">平均分</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <div class="filter-tabs">
        <el-button 
          type="primary" 
          :plain="filterType !== 'all'"
          @click="filterExams('all')"
        >
          全部
        </el-button>
        <el-button 
          type="primary" 
          :plain="filterType !== 'available'"
          @click="filterExams('available')"
        >
          可用考试
        </el-button>
        <el-button 
          type="primary" 
          :plain="filterType !== 'completed'"
          @click="filterExams('completed')"
        >
          已完成
        </el-button>
      </div>
      
      <el-card class="exam-list-card">
        <template #header>
          <h3>考试列表</h3>
        </template>
        <el-table :data="filteredExams" border>
          <el-table-column prop="title" label="考试名称" />
          <el-table-column prop="duration" label="时长(分钟)" />
          <el-table-column prop="startTime" label="开始时间" />
          <el-table-column prop="endTime" label="结束时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <template v-if="hasCompleted(scope.row.id)">
                <el-button type="success" size="small" @click="viewResult(scope.row.id)">
                  查看结果
                </el-button>
              </template>
              <template v-else>
                <el-button type="primary" size="small" @click="goToExam(scope.row)">
                  进入考试
                </el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="filteredExams.length === 0" class="empty-tip">
          <el-empty :description="getEmptyDescription()" />
        </div>
      </el-card>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getAvailableExams, getExamById } from '../api/exam'
import { getAnswerHistory, getAnswerByExam } from '../api/answer'
import { getWrongQuestions } from '../api/wrong'

const router = useRouter()
const userStore = useUserStore()

const exams = ref([])
const answers = ref([])
const wrongQuestions = ref([])
const filterType = ref('all')

const completedExamIds = computed(() => new Set(answers.value.map(a => a.examId)))

const availableCount = computed(() => {
  return exams.value.filter(e => !completedExamIds.value.has(e.id)).length
})

const completedCount = computed(() => answers.value.length)
const wrongCount = computed(() => wrongQuestions.value.length)
const avgScore = computed(() => {
  if (answers.value.length === 0) return 0
  const total = answers.value.reduce((sum, a) => sum + (a.score || 0), 0)
  return Math.round(total / answers.value.length)
})

const filteredExams = computed(() => {
  if (filterType.value === 'all') {
    return exams.value
  } else if (filterType.value === 'available') {
    return exams.value.filter(e => !completedExamIds.value.has(e.id))
  } else if (filterType.value === 'completed') {
    return exams.value.filter(e => completedExamIds.value.has(e.id))
  }
  return exams.value
})

function getEmptyDescription() {
  if (filterType.value === 'available') return '暂无可用考试'
  if (filterType.value === 'completed') return '暂无已完成考试'
  return '暂无考试'
}

function filterExams(type) {
  filterType.value = type
}

function getStatusType(status) {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

function goToExam(exam) {
  router.push(`/exam/${exam.id}/rules`)
}

function goToProfile() {
  router.push('/profile')
}

function goToWrongQuestions() {
  router.push('/wrong')
}

function hasCompleted(examId) {
  return completedExamIds.value.has(examId)
}

async function viewResult(examId) {
  const res = await getAnswerByExam(examId)
  if (res.code === 200 && res.data) {
    router.push(`/result/${res.data.id}`)
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

onMounted(async () => {
  const [examRes, answerRes, wrongRes] = await Promise.all([
    getAvailableExams(),
    getAnswerHistory(),
    getWrongQuestions()
  ])
  if (examRes.code === 200) exams.value = examRes.data
  if (answerRes.code === 200) answers.value = answerRes.data
  if (wrongRes.code === 200) wrongQuestions.value = wrongRes.data
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-left h2 {
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.profile-btn {
  background: #409eff !important;
  border-color: #409eff !important;
  color: white !important;
  font-weight: bold;
  padding: 10px 24px;
  font-size: 14px;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.profile-btn:hover {
  background: #66b1ff !important;
  border-color: #66b1ff !important;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.logout-btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: bold;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 108, 108, 0.4);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-card.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.exam-icon {
  background: #ecf5ff;
  color: #409eff;
}

.answer-icon {
  background: #f0f9eb;
  color: #67c23a;
}

.wrong-icon {
  background: #fef0f0;
  color: #f56c6c;
}

.score-icon {
  background: #fdf6ec;
  color: #e6a23c;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.stat-label {
  color: #909399;
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.exam-list-card {
  margin-top: 20px;
}

.empty-tip {
  padding: 40px;
}
</style>