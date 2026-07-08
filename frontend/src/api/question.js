
import request from '../utils/request'

export function getQuestionsByExam(examId) {
  return request({
    url: `/question/exam/${examId}`,
    method: 'get'
  })
}

export function getQuestionById(id) {
  return request({
    url: `/question/${id}`,
    method: 'get'
  })
}

export function createQuestion(data) {
  return request({
    url: '/question',
    method: 'post',
    data
  })
}

export function updateQuestion(id, data) {
  return request({
    url: `/question/${id}`,
    method: 'put',
    data
  })
}

export function deleteQuestion(id) {
  return request({
    url: `/question/${id}`,
    method: 'delete'
  })
}
