
import request from '../utils/request'

export function getExams() {
  return request({
    url: '/exam',
    method: 'get'
  })
}

export function getAvailableExams() {
  return request({
    url: '/exam/available',
    method: 'get'
  })
}

export function getExamById(id) {
  return request({
    url: `/exam/${id}`,
    method: 'get'
  })
}

export function getExamQuestions(id) {
  return request({
    url: `/exam/${id}/questions`,
    method: 'get'
  })
}

export function createExam(data) {
  return request({
    url: '/exam',
    method: 'post',
    data
  })
}

export function updateExam(id, data) {
  return request({
    url: `/exam/${id}`,
    method: 'put',
    data
  })
}

export function deleteExam(id) {
  return request({
    url: `/exam/${id}`,
    method: 'delete'
  })
}

export function startExam(id) {
  return request({
    url: `/exam/${id}/start`,
    method: 'post'
  })
}

export function endExam(id) {
  return request({
    url: `/exam/${id}/end`,
    method: 'post'
  })
}
