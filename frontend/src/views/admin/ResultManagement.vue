
<template>
  <div class="result-management">
    <div class="management-header">
      <h3>成绩管理</h3>
      <el-select v-model="searchExamId" placeholder="选择考试">
        <el-option v-for="e in exams" :key="e.id" :label="e.title" :value="e.id" />
      </el-select>
    </div>
    
    <el-card class="stats-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <p class="stat-value">{{ results.length }}</p>
            <p class="stat-label">参考人数</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <p class="stat-value">{{ avgScore }}</p>
            <p class="stat-label">平均分</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <p class="stat-value">{{ maxScore }}</p>
            <p class="stat-label">最高分</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <p class="stat-value">{{ passRate }}%</p>
            <p class="stat-label">及格率</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <el-table :data="results" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="考生ID" width="100" />
      <el-table-column prop="examId" label="考试ID" width="100" />
      <el-table-column prop="score" label="成绩" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.score >= 60 ? 'success' : 'danger'">
            {{ scope.row.score }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已提交' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="spendTime" label="用时">
        <template #default="scope">
          {{ formatTime(scope.row.spendTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="screenSwitchCount" label="切屏次数" width="120" />
      <el-table-column prop="submitTime" label="提交时间">
        <template #default="scope">
          {{ formatDateTime(scope.row.submitTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showDetailDialog" title="答题详情" width="800px">
      <div v-if="selectedResult">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="考试ID">{{ selectedResult.examId }}</el-descriptions-item>
          <el-descriptions-item label="成绩">{{ selectedResult.score }}</el-descriptions-item>
          <el-descriptions-item label="用时">{{ formatTime(selectedResult.spendTime) }}</el-descriptions-item>
          <el-descriptions-item label="切屏次数">{{ selectedResult.screenSwitchCount }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getExams } from '../../api/exam'
import { getAllAnswersByExam, getAnswerDetail } from '../../api/answer'

const results = ref([])
const exams = ref([])
const searchExamId = ref('')
const showDetailDialog = ref(false)
const selectedResult = ref(null)

const avgScore = computed(() => {
  if (results.value.length === 0) return 0
  const total = results.value.reduce((sum, r) => sum + (r.score || 0), 0)
  return Math.round(total / results.value.length)
})

const maxScore = computed(() => {
  if (results.value.length === 0) return 0
  return Math.max(...results.value.map(r => r.score || 0))
})

const passRate = computed(() => {
  if (results.value.length === 0) return 0
  const passCount = results.value.filter(r => (r.score || 0) >= 60).length
  return Math.round((passCount / results.value.length) * 100)
})

function formatTime(seconds) {
  if (!seconds) return '-'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}分${s}秒`
}

function formatDateTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

function viewDetail(row) {
  selectedResult.value = row
  showDetailDialog.value = true
}

async function loadResults() {
  if (!searchExamId.value) {
    results.value = []
    return
  }
  console.log('DEBUG: loadResults examId:', searchExamId.value)
  const res = await getAllAnswersByExam(searchExamId.value)
  console.log('DEBUG: loadResults result:', res)
  if (res.code === 200) {
    results.value = res.data || []
    console.log('DEBUG: results loaded:', results.value)
  }
}

async function loadExams() {
  try {
    const res = await getExams()
    if (res.code === 200) {
      exams.value = res.data || []
      if (exams.value.length > 0 && !searchExamId.value) {
        searchExamId.value = exams.value[0].id
      }
    }
  } catch (error) {
    console.error('加载考试列表失败:', error)
  }
}

watch(searchExamId, loadResults)

onMounted(async () => {
  await loadExams()
})
</script>

<style scoped>
.result-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #667eea;
  margin: 0;
}

.stat-label {
  color: #909399;
  margin: 0;
}
</style>
