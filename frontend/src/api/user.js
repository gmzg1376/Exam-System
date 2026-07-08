import request from '../utils/request'

export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/change-password',
    method: 'post',
    data
  })
}