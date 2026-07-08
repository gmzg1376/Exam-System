
<template>
  <div class="exam-management">
    <div class="management-header">
      <h3>考试管理</h3>
      <el-button type="primary" @click="openCreateDialog">创建考试</el-button>
    </div>
    
    <el-table :data="exams" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="考试名称" />
      <el-table-column prop="duration" label="时长(分钟)" width="120" />
      <el-table-column prop="startTime" label="开始时间">
        <template #default="scope">
          {{ formatTime(scope.row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="endTime" label="结束时间">
        <template #default="scope">
          {{ formatTime(scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="randomQuestion" label="随机出题" width="100">
        <template #default="scope">
          <el-switch :value="scope.row.randomQuestion" disabled />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" @click="editExam(scope.row)">编辑</el-button>
          <el-button size="small" type="success" v-if="scope.row.status === 0" @click="handleStartExam(scope.row)">开始</el-button>
          <el-button size="small" type="warning" v-if="scope.row.status === 1" @click="handleEndExam(scope.row)">结束</el-button>
          <el-button size="small" type="danger" @click="handleDeleteExam(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showCreateDialog" :title="editingId ? '编辑考试' : '创建考试'" width="600px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="考试名称" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="考试时长(分钟)" prop="duration">
          <el-input v-model="form.duration" type="number" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" />
        </el-form-item>
        <el-form-item label="随机出题">
          <el-switch v-model="form.randomQuestion" />
        </el-form-item>
        <el-form-item label="随机选项">
          <el-switch v-model="form.randomOption" />
        </el-form-item>
        <el-form-item label="考试描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExams, createExam, updateExam, deleteExam as deleteExamApi, startExam as startExamApi, endExam as endExamApi } from '../../api/exam'

const exams = ref([])
const showCreateDialog = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const form = reactive({
  title: '',
  duration: 60,
  startTime: '',
  endTime: '',
  randomQuestion: false,
  randomOption: false,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

function getStatusType(status) {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

function formatDateTime(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

function buildFormData() {
  return {
    title: form.title,
    duration: Number(form.duration),
    startTime: form.startTime ? formatDateTime(form.startTime) : '',
    endTime: form.endTime ? formatDateTime(form.endTime) : '',
    randomQuestion: form.randomQuestion,
    randomOption: form.randomOption,
    description: form.description
  }
}

function resetForm() {
  editingId.value = null
  form.title = ''
  form.duration = 60
  form.startTime = ''
  form.endTime = ''
  form.randomQuestion = false
  form.randomOption = false
  form.description = ''
  formRef.value?.resetFields()
}

function openCreateDialog() {
  resetForm()
  showCreateDialog.value = true
}

async function handleSubmit() {
  if (!await formRef.value.validate()) return

  try {
    const formData = buildFormData()
    const res = editingId.value
      ? await updateExam(editingId.value, formData)
      : await createExam(formData)
    if (res.code === 200) {
      ElMessage.success(editingId.value ? '更新成功' : '创建成功')
      showCreateDialog.value = false
      loadExams()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败，请稍后重试')
  }
}

function editExam(row) {
  editingId.value = row.id
  form.title = row.title
  form.duration = row.duration
  form.startTime = row.startTime ? new Date(row.startTime) : ''
  form.endTime = row.endTime ? new Date(row.endTime) : ''
  form.randomQuestion = !!row.randomQuestion
  form.randomOption = !!row.randomOption
  form.description = row.description || ''
  showCreateDialog.value = true
}

async function handleStartExam(row) {
  try {
    const res = await startExamApi(row.id)
    if (res.code === 200) {
      ElMessage.success('考试已开始')
      loadExams()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败，请稍后重试')
  }
}

async function handleEndExam(row) {
  try {
    const res = await endExamApi(row.id)
    if (res.code === 200) {
      ElMessage.success('考试已结束')
      loadExams()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败，请稍后重试')
  }
}

async function handleDeleteExam(row) {
  try {
    await ElMessageBox.confirm(`确定删除考试「${row.title}」吗？`, '提示', { type: 'warning' })
    const res = await deleteExamApi(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadExams()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e?.message || '删除失败，请稍后重试')
    }
  }
}

async function loadExams() {
  const res = await getExams()
  if (res.code === 200) {
    exams.value = res.data
  }
}

onMounted(loadExams)
</script>

<style scoped>
.exam-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
