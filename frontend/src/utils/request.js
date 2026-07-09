
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return res
  },
  error => {
<<<<<<< HEAD
=======
    const res = error.response?.data
    if (res && typeof res === 'object' && res.message) {
      return Promise.reject(res)
    }
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
    return Promise.reject(error)
  }
)

export default request
