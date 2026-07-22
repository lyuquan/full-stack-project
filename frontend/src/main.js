import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

// EmptyPage is only a route placeholder.
// The current learning project still renders page blocks inside App.vue, while
// Vue Router is first used to keep the browser address in sync with menu clicks.
const EmptyPage = { template: '<div></div>' }

// router defines the frontend paths that can appear in the browser address bar.
// createWebHistory() means the URL looks like /users instead of /#/users.
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: EmptyPage },
    { path: '/users', component: EmptyPage },
    { path: '/roles', component: EmptyPage },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ]
})

// Create the Vue app, install the router plugin, then mount it to #app.
createApp(App).use(router).mount('#app')
