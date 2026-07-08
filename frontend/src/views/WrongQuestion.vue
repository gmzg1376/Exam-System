
<template>
  <div class="wrong-container">
    <el-header>
      <div class="header-left">
        <h2>错题本</h2>
      </div>
      <div class="header-right">
        <el-button type="danger" @click="clearAll">清空错题本</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </div>
    </el-header>
    
    <el-main>
      <el-card v-if="wrongQuestions.length === 0" class="empty-card">
        <div class="empty-content">
          <el-icon class="empty-icon"><CheckCircle /></el-icon>
          <p>暂无错题，继续保持！</p>
        </div>
      </el-card>
      
      <div v-else class="wrong-list">
        <div v-for="question in wrongQuestions" :key="question.id" class="wrong-item">
          <el-card>
            <div class="item-header">
              <el-tag :type="getQuestionTypeTag(question.type)">
                {{ getQuestionType(question.type) }}
              </el-tag>
              <span class="knowledge-point">{{ question.knowledgePoint }}</span>
            </div>
            
            <div class="item-content">
              <p>{{ question.content }}</p>
            </div>
            
            <div class="item-options" v-if="question.type !== 3">
              <div v-for="(opt, idx) in getOptions(question.options)" :key="idx" class="option-row">
                <span class="option-label">{{ opt.label }}</span>
                <span>{{ opt.value }}</span>
                <el-tag v-if="question.answer === opt.label" type="success">正确答案</el-tag>
              </div>
            </div>
            
            <div class="item-analysis" v-if="question.analysis">
              <h4>解析：</h4>
              <p>{{ question.analysis }}</p>
            </div>
            
            <div class="item-footer">
              <span class="wrong-count">错误次数：{{ getWrongCount(question.id) }}</span>
              <el-button size="small" @click="removeQuestion(question.id)">移除</el-button>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getWrongQuestions, removeWrongQuestion, clearWrongQuestions } from '../api/wrong'

const wrongQuestions = ref([])
const wrongCounts = ref({})

function getQuestionType(type) {
  const types = { 1: '单选题', 2: '多选题', 3: '填空题', 4: '编程题' }
  return types[type] || '未知'
}

function getQuestionTypeTag(type) {
  const types = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger' }
  return types[type] || 'info'
}

function getOptions(optionsStr) {
  try {
    return JSON.parse(optionsStr)
  } catch {
    return []
  }
}

function getWrongCount(questionId) {
  return wrongCounts.value[questionId] || 1
}

async function removeQuestion(id) {
  const res = await removeWrongQuestion(id)
  if (res.code === 200) {
    wrongQuestions.value = wrongQuestions.value.filter(q => q.id !== id)
  }
}

async function clearAll() {
  if (confirm('确定要清空错题本吗？')) {
    const res = await clearWrongQuestions()
    if (res.code === 200) {
      wrongQuestions.value = []
    }
  }
}

onMounted(async () => {
  const res = await getWrongQuestions()
  if (res.code === 200) {
    wrongQuestions.value = res.data
  }
})
</script>

<style scoped>
.wrong-container {
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

.empty-card {
  text-align: center;
}

.empty-content {
  padding: 40px;
}

.empty-icon {
  font-size: 64px;
  color: #67c23a;
  margin-bottom: 20px;
}

.wrong-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.wrong-item {
  max-width: 800px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.knowledge-point {
  color: #909399;
}

.item-content p {
  font-size: 16px;
  line-height: 1.8;
}

.item-options {
  margin: 20px 0;
}

.option-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
}

.item-analysis {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
}

.item-analysis h4 {
  margin: 0 0 10px;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

.wrong-count {
  color: #f56c6c;
}
</style>
