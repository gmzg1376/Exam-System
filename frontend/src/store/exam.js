
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useExamStore = defineStore('exam', () => {
  const currentExam = ref(null)
  const questions = ref([])
  const answers = ref({})
  const startTime = ref(null)
  const remainingTime = ref(0)
  const screenSwitchCount = ref(0)
  const questionSpendTime = ref({})

  function setCurrentExam(exam) {
    currentExam.value = exam
    remainingTime.value = exam.duration * 60
  }

  function setQuestions(data) {
    questions.value = data
    answers.value = {}
    questionSpendTime.value = {}
  }

  function setAnswer(questionId, answer) {
    answers.value[questionId] = answer
  }

  function getAnswer(questionId) {
    return answers.value[questionId] || ''
  }

  function setQuestionSpendTime(questionId, time) {
    questionSpendTime.value[questionId] = time
  }

  function decreaseTime() {
    if (remainingTime.value > 0) {
      remainingTime.value--
    }
  }

  function incrementScreenSwitch() {
    screenSwitchCount.value++
  }

  function reset() {
    currentExam.value = null
    questions.value = []
    answers.value = {}
    startTime.value = null
    remainingTime.value = 0
    screenSwitchCount.value = 0
    questionSpendTime.value = {}
  }

  const answeredCount = computed(() => {
    return Object.keys(answers.value).filter(k => answers.value[k]).length
  })

  return {
    currentExam,
    questions,
    answers,
    startTime,
    remainingTime,
    screenSwitchCount,
    questionSpendTime,
    setCurrentExam,
    setQuestions,
    setAnswer,
    getAnswer,
    setQuestionSpendTime,
    decreaseTime,
    incrementScreenSwitch,
    reset,
    answeredCount
  }
})
