
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')

  function setUser(data) {
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
  }

  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  function isAdmin() {
    return user.value && user.value.role === 'ADMIN'
  }

  function isStudent() {
    return user.value && user.value.role === 'STUDENT'
  }

  return {
    user,
    token,
    setUser,
    setToken,
    logout,
    isAdmin,
    isStudent
  }
})
