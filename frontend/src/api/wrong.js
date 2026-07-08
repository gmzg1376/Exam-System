
import request from '../utils/request'

export function getWrongQuestions() {
  return request({
    url: '/wrong',
    method: 'get'
  })
}

export function removeWrongQuestion(id) {
  return request({
    url: `/wrong/${id}`,
    method: 'delete'
  })
}

export function clearWrongQuestions() {
  return request({
    url: '/wrong/clear',
    method: 'delete'
  })
}
