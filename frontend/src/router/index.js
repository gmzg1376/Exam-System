
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/exam/:id',
    name: 'Exam',
    component: () => import('../views/Exam.vue'),
    meta: { requiresAuth: true, studentOnly: true }
  },
  {
    path: '/exam/:id/rules',
    name: 'ExamRules',
    component: () => import('../views/ExamRules.vue'),
    meta: { requiresAuth: true, studentOnly: true }
  },
  {
    path: '/result/:sheetId',
    name: 'Result',
    component: () => import('../views/Result.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wrong',
    name: 'WrongQuestion',
    component: () => import('../views/WrongQuestion.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminHome.vue'),
    meta: { requiresAuth: true, adminOnly: true },
    children: [
      {
        path: 'exams',
        name: 'AdminExams',
        component: () => import('../views/admin/ExamManagement.vue')
      },
      {
        path: 'questions',
        name: 'AdminQuestions',
        component: () => import('../views/admin/QuestionManagement.vue')
      },
      {
        path: 'results',
        name: 'AdminResults',
        component: () => import('../views/admin/ResultManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  if (to.meta.adminOnly && (!user || user.role !== 'ADMIN')) {
    next('/')
    return
  }

<<<<<<< HEAD
  if (to.meta.studentOnly && (!user || user.role !== 'STUDENT')) {
=======
  if (to.meta.studentOnly && (!user || (user.role !== 'STUDENT' && user.role !== 'ADMIN'))) {
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
    next('/')
    return
  }

  next()
})

export default router
