import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import HomePage from './pages/HomePage.vue'
import RolesPage from './pages/RolesPage.vue'
import UsersPage from './pages/UsersPage.vue'
import './style.css'

// router defines the frontend paths that can appear in the browser address bar.
// createWebHistory() means the URL looks like /users instead of /#/users.
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomePage },
    { path: '/users', component: UsersPage },
    { path: '/roles', component: RolesPage },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ]
})

// Create the Vue app, install the router plugin, then mount it to #app.
createApp(App).use(router).mount('#app')
