
import request from '../utils/request'

export function submitAnswer(data) {
  return request({
    url: '/answer/submit',
    method: 'post',
    data
  })
}

export function getAnswerHistory() {
  return request({
    url: '/answer/history',
    method: 'get'
  })
}

export function getAnswerByExam(examId) {
  return request({
    url: `/answer/exam/${examId}`,
    method: 'get'
  })
}

export function getAnswerDetail(sheetId) {
  return request({
    url: `/answer/sheet/${sheetId}`,
    method: 'get'
  })
}

export function getAllAnswersByExam(examId) {
  return request({
    url: `/answer/exam/all/${examId}`,
    method: 'get'
  })
}
