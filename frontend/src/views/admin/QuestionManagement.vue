
<template>
  <div class="question-management">
    <div class="management-header">
      <h3>题库管理</h3>
      <el-select v-model="searchExamId" placeholder="选择考试">
        <el-option v-for="e in exams" :key="e.id" :label="e.title" :value="e.id" />
      </el-select>
      <el-button type="primary" @click="showCreateDialog = true">添加题目</el-button>
    </div>
    
    <el-table :data="questions" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="type" label="题型" width="100">
        <template #default="scope">
          <el-tag :type="getQuestionTypeTag(scope.row.type)">
            {{ getQuestionType(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="题目内容" min-width="300">
        <template #default="scope">
          {{ scope.row.content.slice(0, 50) }}{{ scope.row.content.length > 50 ? '...' : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="80" />
      <el-table-column prop="knowledgePoint" label="知识点" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="editQuestion(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDeleteQuestion(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showCreateDialog" title="添加题目" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="所属考试" prop="examId">
          <el-select v-model="form.examId">
            <el-option v-for="e in exams" :key="e.id" :label="e.title" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型" prop="type">
          <el-select v-model="form.type">
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="填空题" :value="3" />
            <el-option label="编程题" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="选项" v-if="form.type !== 3">
          <div v-for="(opt, idx) in form.options" :key="idx" class="option-input">
            <el-input v-model="opt.label" style="width: 60px" />
            <el-input v-model="opt.value" />
            <el-button size="small" @click="form.options.push({ label: '', value: '' })">+</el-button>
          </div>
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input v-model="form.answer" />
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input v-model="form.score" type="number" />
        </el-form-item>
        <el-form-item label="知识点" prop="knowledgePoint">
          <el-input v-model="form.knowledgePoint" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getQuestionsByExam, createQuestion, updateQuestion, deleteQuestion as deleteQuestionApi } from '../../api/question'
import { getExams } from '../../api/exam'

const questions = ref([])
const exams = ref([])
const searchExamId = ref('')
const showCreateDialog = ref(false)
const formRef = ref(null)

const form = reactive({
  examId: '',
  type: 1,
  content: '',
  options: [{ label: 'A', value: '' }, { label: 'B', value: '' }, { label: 'C', value: '' }, { label: 'D', value: '' }],
  answer: '',
  score: 10,
  knowledgePoint: '',
  analysis: ''
})

const rules = {
  examId: [{ required: true, message: '请选择所属考试', trigger: 'change' }],
  type: [{ required: true, message: '请选择题型', trigger: 'change' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  answer: [{ required: true, message: '请输入答案', trigger: 'blur' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }]
}

function getQuestionType(type) {
  const types = { 1: '单选题', 2: '多选题', 3: '填空题', 4: '编程题' }
  return types[type] || '未知'
}

function getQuestionTypeTag(type) {
  const types = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger' }
  return types[type] || 'info'
}

async function handleCreate() {
  if (!await formRef.value.validate()) return
  
  const data = { ...form }
  data.options = JSON.stringify(form.options)
  
  const res = await createQuestion(data)
  if (res.code === 200) {
    showCreateDialog.value = false
    loadQuestions()
  }
}

function editQuestion(row) {
  Object.assign(form, row)
  if (row.options) {
    form.options = JSON.parse(row.options)
  }
  showCreateDialog.value = true
}

async function handleDeleteQuestion(row) {
  if (confirm('确定删除该题目吗？')) {
    const res = await deleteQuestionApi(row.id)
    if (res.code === 200) {
      loadQuestions()
    }
  }
}

async function loadQuestions() {
  if (!searchExamId.value) {
    questions.value = []
    return
  }
  const res = await getQuestionsByExam(searchExamId.value)
  if (res.code === 200) {
    questions.value = res.data
  }
}

async function loadExams() {
  const res = await getExams()
  if (res.code === 200) {
    exams.value = res.data
  }
}

watch(searchExamId, loadQuestions)

onMounted(async () => {
  await loadExams()
})
</script>

<style scoped>
.question-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;
}

.option-input {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
</style>
