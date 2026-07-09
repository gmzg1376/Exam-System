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
<<<<<<< HEAD
}
=======
}
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
