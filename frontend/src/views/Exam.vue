
<template>
  <div class="exam-container">
    <div class="countdown-bar" :class="{ flashing: remainingTime <= 10 }">
      <div class="countdown-content">
        <div class="exam-title">{{ examStore.currentExam?.title }}</div>
        <div class="time-display">
          <el-icon class="time-icon"><Clock /></el-icon>
          <span class="time-text">{{ formatTime(remainingTime) }}</span>
        </div>
        <div class="progress-info">
          {{ examStore.answeredCount }} / {{ examStore.questions.length }}
        </div>
      </div>
    </div>
    
    <div class="progress-bar-container">
      <div class="progress-bar" :style="{ width: progressPercent + '%' }"></div>
    </div>
    
    <el-container class="exam-body">
      <el-aside class="question-nav">
        <div class="nav-header">
          <span>题目导航</span>
        </div>
        <div class="nav-list">
          <div 
            v-for="(q, index) in examStore.questions" 
            :key="q.id"
            class="nav-item"
            :class="{ active: currentIndex === index, answered: examStore.getAnswer(q.id) }"
            @click="switchQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
      </el-aside>
      
      <el-main class="question-area">
        <div v-if="currentQuestion" class="question-card">
          <div class="question-header">
            <span class="question-number">第 {{ currentIndex + 1 }} 题</span>
            <span class="question-score">{{ currentQuestion.score }}分</span>
          </div>
          
          <div class="question-content">
            <p>{{ currentQuestion.content }}</p>
          </div>
          
          <div class="options-list" v-if="currentQuestion.type !== 3">
            <div 
              v-for="(opt, idx) in currentOptions" 
              :key="idx"
              class="option-item"
              :class="{ selected: isSelected(idx) }"
              @click="selectOption(idx)"
            >
              <span class="option-label">{{ opt.label }}</span>
              <span class="option-content">{{ opt.value }}</span>
            </div>
          </div>
          
          <div class="answer-input" v-else>
            <el-input 
              type="textarea" 
              v-model="currentAnswer" 
              placeholder="请输入答案"
              :rows="6"
              @change="saveAnswer"
            />
          </div>
        </div>
        
        <div class="question-footer">
          <el-button 
            class="nav-btn prev-btn" 
            :disabled="currentIndex === 0"
            @click="prevQuestion"
          >
            <el-icon><ArrowLeft /></el-icon>
            上一题
          </el-button>
          <el-button 
            class="submit-btn" 
            @click="showSubmitConfirm"
          >
            <el-icon><CircleClose /></el-icon>
            提交试卷
          </el-button>
          <el-button 
            class="nav-btn next-btn" 
            :disabled="currentIndex === examStore.questions.length - 1"
            @click="nextQuestion"
          >
            下一题
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </el-main>
    </el-container>
    
    <el-dialog 
      v-model="showConfirmDialog" 
      title="确认提交" 
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="confirm-content">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <p>确认提交试卷？</p>
        <p class="warning-text">提交后将无法修改答案！</p>
        <div v-if="unansweredCount > 0" class="unanswered-warning">
          <p>还有 {{ unansweredCount }} 道题未作答</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="showConfirmDialog = false">继续答题</el-button>
        <el-button type="danger" @click="handleSubmit">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useExamStore } from '../store/exam';
import { getExamById, getExamQuestions } from '../api/exam';
import { submitAnswer, getAnswerByExam } from '../api/answer';
import { CircleClose, Warning, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
const route = useRoute();
const router = useRouter();
const examStore = useExamStore();
const currentIndex = ref(0);
const currentAnswer = ref('');
const questionStartTime = ref(Date.now());
const showConfirmDialog = ref(false);
const shuffledOptionsMap = ref(new Map());
let timer = null;
let saveTimer = null;
const remainingTime = computed(() => examStore.remainingTime);
const progressPercent = computed(() => {
  const total = examStore.questions.length;
  const answered = examStore.answeredCount;
  return total > 0 ? (answered / total) * 100 : 0;
});
const unansweredCount = computed(() => {
  return examStore.questions.length - examStore.answeredCount;
});
const currentQuestion = computed(() => examStore.questions[currentIndex.value]);
const currentOptions = computed(() => {
 if (!currentQuestion.value || !currentQuestion.value.options)
 return [];
 const questionId = currentQuestion.value.id;
 if (shuffledOptionsMap.value.has(questionId)) {
 return shuffledOptionsMap.value.get(questionId);
 }
 const options = JSON.parse(currentQuestion.value.options);
 if (examStore.currentExam?.randomOption) {
 const shuffled = shuffleArray(options);
 const labels = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];
 shuffled.forEach((opt, idx) => {
 opt.label = labels[idx];
 });
 shuffledOptionsMap.value.set(questionId, shuffled);
 return shuffled;
 }
 shuffledOptionsMap.value.set(questionId, options);
 return options;
});
function shuffleArray(array) {
 const arr = [...array];
 for (let i = arr.length - 1; i > 0; i--) {
 const j = Math.floor(Math.random() * (i + 1));
 [arr[i], arr[j]] = [arr[j], arr[i]];
 }
 return arr;
}
function formatTime(seconds) {
 const h = Math.floor(seconds / 3600);
 const m = Math.floor((seconds % 3600) / 60);
 const s = seconds % 60;
 return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
}
function getQuestionType(type) {
 const types = { 1: '单选题', 2: '多选题', 3: '填空题', 4: '编程题' };
 return types[type] || '未知';
}
function getQuestionTypeTag(type) {
 const types = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger' };
 return types[type] || 'info';
}
function isSelected(idx) {
 const answer = examStore.getAnswer(currentQuestion.value?.id);
 if (!answer)
 return false;
 const optionLabels = currentOptions.value.map(o => o.label);
 const selectedLabels = answer.split(',');
 return selectedLabels.includes(optionLabels[idx]);
}
function selectOption(idx) {
 const question = currentQuestion.value;
 const optionLabel = currentOptions.value[idx].label;
 let current = examStore.getAnswer(question.id) || '';
 if (question.type === 1) {
 examStore.setAnswer(question.id, optionLabel);
 }
 else if (question.type === 2) {
 const labels = current ? current.split(',') : [];
 if (labels.includes(optionLabel)) {
 labels.splice(labels.indexOf(optionLabel), 1);
 }
 else {
 labels.push(optionLabel);
 }
 examStore.setAnswer(question.id, labels.join(','));
 }
}
watch(currentAnswer, () => {
 if (currentQuestion.value?.type === 3) {
 examStore.setAnswer(currentQuestion.value.id, currentAnswer.value);
 }
});
function saveAnswer() {
 if (currentQuestion.value?.type === 3) {
 examStore.setAnswer(currentQuestion.value.id, currentAnswer.value);
 }
}
function switchQuestion(index) {
 const prevQuestion = examStore.questions[currentIndex.value];
 if (prevQuestion) {
 const spendTime = Math.floor((Date.now() - questionStartTime.value) / 1000);
 examStore.setQuestionSpendTime(prevQuestion.id, spendTime);
 }
 currentIndex.value = index;
 currentAnswer.value = examStore.getAnswer(currentQuestion.value?.id) || '';
 questionStartTime.value = Date.now();
}
function prevQuestion() {
 if (currentIndex.value > 0) {
 switchQuestion(currentIndex.value - 1);
 }
}
function nextQuestion() {
 if (currentIndex.value < examStore.questions.length - 1) {
 switchQuestion(currentIndex.value + 1);
 }
}
function playSound(type) {
  const audioContext = new (window.AudioContext || window.webkitAudioContext)();
  const oscillator = audioContext.createOscillator();
  const gainNode = audioContext.createGain();
  
  oscillator.connect(gainNode);
  gainNode.connect(audioContext.destination);
  
  if (type === 'warning') {
    oscillator.frequency.value = 440;
    oscillator.type = 'sine';
    gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);
    oscillator.start();
    oscillator.stop(audioContext.currentTime + 0.3);
  } else if (type === 'submit') {
    oscillator.frequency.value = 600;
    oscillator.type = 'square';
    gainNode.gain.setValueAtTime(0.2, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);
    oscillator.start();
    oscillator.stop(audioContext.currentTime + 0.5);
  }
}
async function handleSubmit() {
 playSound('submit');
 const answers = examStore.questions.map(q => ({
 questionId: q.id,
 userAnswer: examStore.getAnswer(q.id) || '',
 spendTime: examStore.questionSpendTime[q.id] || 0
 }));
 const data = {
 examId: parseInt(route.params.id),
 answers,
 screenSwitchCount: examStore.screenSwitchCount,
 totalSpendTime: Math.max(0, (examStore.currentExam?.duration || 60) * 60 - remainingTime.value)
 };
 const res = await submitAnswer(data);
 if (res.code === 200 && res.data) {
 console.log('DEBUG: submit success, sheet:', res.data);
 examStore.reset();
 router.push(`/result/${res.data.id}`);
 }
}
function showSubmitConfirm() {
 showConfirmDialog.value = true;
}
watch(remainingTime, (newVal) => {
 if (newVal <= 10 && newVal > 0) {
 playSound('warning');
 }
});
function handleVisibilityChange() {
 if (document.hidden) {
 examStore.incrementScreenSwitch();
 if (examStore.screenSwitchCount > 5) {
 alert('切屏次数过多，请注意考试纪律');
 }
 }
}
onMounted(async () => {
 const answerRes = await getAnswerByExam(route.params.id);
 if (answerRes.code === 200 && answerRes.data) {
 router.push(`/result/${answerRes.data.id}`);
 return;
 }
 
 const [examRes, questionRes] = await Promise.all([
 getExamById(route.params.id),
 getExamQuestions(route.params.id)
 ]);
 if (examRes.code === 200) {
 examStore.setCurrentExam(examRes.data);
 }
 if (questionRes.code === 200) {
 examStore.setQuestions(questionRes.data);
 }
 timer = setInterval(() => {
 examStore.decreaseTime();
 if (examStore.remainingTime === 0) {
 handleSubmit();
 }
 }, 1000);
 saveTimer = setInterval(() => {
 console.log('自动保存中...');
 }, 30000);
 document.addEventListener('visibilitychange', handleVisibilityChange);
});
onUnmounted(() => {
 if (timer)
 clearInterval(timer);
 if (saveTimer)
 clearInterval(saveTimer);
 document.removeEventListener('visibilitychange', handleVisibilityChange);
});
</script>

<style scoped>
.exam-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  color: #303133;
}

.countdown-bar {
  background: #ffffff;
  padding: 15px 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  border-bottom: 2px solid #d32f2f;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.countdown-bar.flashing {
  animation: flash 0.5s ease-in-out infinite;
}

@keyframes flash {
  0%, 100% { background: #ffffff; }
  50% { background: #fff5f5; }
}

.countdown-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.exam-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-icon {
  font-size: 24px;
  color: #d32f2f;
}

.time-text {
  font-size: 36px;
  font-weight: bold;
  color: #d32f2f;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}

.progress-info {
  font-size: 16px;
  color: #606266;
}

.progress-bar-container {
  position: fixed;
  top: 62px;
  left: 0;
  right: 0;
  height: 4px;
  background: #e4e7ed;
  z-index: 999;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #d32f2f 0%, #ff5252 50%, #d32f2f 100%);
  transition: width 0.5s ease;
  box-shadow: 0 0 10px rgba(211, 47, 47, 0.5);
}

.exam-body {
  flex: 1;
  display: flex;
  margin-top: 66px;
}

.question-nav {
  width: 180px;
  background: #ffffff;
  padding: 20px;
  border-right: 1px solid #e4e7ed;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
}

.nav-header {
  margin-bottom: 20px;
  font-size: 14px;
  color: #909399;
  font-weight: bold;
}

.nav-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.nav-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #606266;
}

.nav-item:hover {
  background: #ecf5ff;
  border-color: #d32f2f;
}

.nav-item.active {
  background: #d32f2f;
  color: white;
  border-color: #d32f2f;
  box-shadow: 0 0 10px rgba(211, 47, 47, 0.4);
}

.nav-item.answered {
  background: #67c23a;
  color: white;
  border-color: #67c23a;
}

.question-area {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
}

.question-card {
  flex: 1;
  background: #ffffff;
  border-radius: 12px;
  padding: 40px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  min-height: 500px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.question-number {
  font-size: 16px;
  color: #909399;
}

.question-score {
  font-size: 16px;
  color: #d32f2f;
  font-weight: bold;
}

.question-content {
  margin-bottom: 40px;
}

.question-content p {
  font-size: 20px;
  line-height: 1.8;
  color: #303133;
  font-weight: 600;
  font-family: 'Microsoft YaHei', sans-serif;
  margin: 0;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 18px 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: #ffffff;
}

.option-item:hover {
  border-color: #d32f2f;
  background: #fff5f5;
}

.option-item.selected {
  border-color: #d32f2f;
  background: rgba(211, 47, 47, 0.08);
}

.option-label {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 50%;
  margin-right: 15px;
  font-weight: bold;
  font-size: 16px;
  color: #606266;
  flex-shrink: 0;
}

.option-item.selected .option-label {
  background: #d32f2f;
  color: white;
  border-color: #d32f2f;
}

.option-content {
  font-size: 16px;
  color: #303133;
  line-height: 1.6;
}

.option-item.selected .option-content {
  color: #d32f2f;
  font-weight: 500;
}

.answer-input {
  margin-top: 20px;
}

.answer-input .el-textarea__inner {
  background: #ffffff;
  border: 2px solid #e4e7ed;
  color: #303133;
  font-size: 16px;
  border-radius: 8px;
}

.answer-input .el-textarea__inner:hover {
  border-color: #d32f2f;
}

.answer-input .el-textarea__inner:focus {
  border-color: #d32f2f;
  box-shadow: 0 0 10px rgba(211, 47, 47, 0.2);
}

.question-footer {
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-btn {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
}

.prev-btn {
  background: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
}

.prev-btn:hover:not(:disabled) {
  background: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.next-btn {
  background: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
}

.next-btn:hover:not(:disabled) {
  background: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.submit-btn {
  background: linear-gradient(135deg, #d32f2f 0%, #b71c1c 100%);
  color: white;
  border: none;
  padding: 12px 36px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 8px;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(211, 47, 47, 0.3);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #e53935 0%, #c62828 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(211, 47, 47, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

.confirm-content {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 48px;
  color: #d32f2f;
  margin-bottom: 20px;
}

.confirm-content p {
  font-size: 18px;
  color: #303133;
  margin: 10px 0;
}

.warning-text {
  color: #d32f2f !important;
  font-weight: bold;
}

.unanswered-warning {
  background: rgba(211, 47, 47, 0.08);
  border: 1px solid #feb2b2;
  border-radius: 8px;
  padding: 15px;
  margin-top: 20px;
}

.unanswered-warning p {
  color: #d32f2f !important;
  font-size: 16px !important;
}

:deep(.el-dialog) {
  background: #ffffff !important;
  border: 1px solid #e4e7ed !important;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e4e7ed !important;
}

:deep(.el-dialog__title) {
  color: #303133 !important;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #e4e7ed !important;
}

:deep(.el-dialog__body) {
  color: #303133 !important;
}
</style>
