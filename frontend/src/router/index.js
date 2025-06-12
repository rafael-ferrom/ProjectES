// router.js
import VueRouter from "vue-router";
import AuthenticatedLayout from "./../layouts/AuthenticatedLayout.vue";
import LandingView from './../views/LandingView.vue';
import LoginView from "./../views/LoginView.vue";
import HomeView from "./../views/HomeView.vue";
import DosageConfigurationView from "./../views/DosageConfigurationView.vue";
import ActiveMedicationsView from "./../views/ActiveMedicationsView.vue";
import RegisterView from "./../views/RegisterView.vue";
import NotFoundView from './../views/NotFoundView.vue';
import { useAuthStore } from "./../store/index";

// const auth = Number(process.env.VUE_APP_AUTH);

const routes = [
  {
    path: "/",
    name: "landing",
    component: LandingView,
    meta: {
      public: true
    },
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: {
      public: true
    },
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: {
      public: true
    },
  },
  {
    path: "/tech-pharmacy",
    component: AuthenticatedLayout,
    meta: {
      public: false
    },
    redirect: "/tech-pharmacy/home",
    children: [
      {
        path: "home",
        name: "home",
        component: HomeView,
      },
      // The old 'medication-detail' route is removed.
      {
        path: "configure-medication/:id",
        name: "configure-medication",
        component: DosageConfigurationView,
        props: true
      },
      {
        path: "active-medications",
        name: "active-medications",
        component: ActiveMedicationsView,
      },
    ]
  },
  {
    path: '*',
    name: 'NotFound',
    component: NotFoundView
  }
]

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
})

function handleRedirect (to, next, isPublicRoute, userId) {
  if (!isPublicRoute && !userId) {
    return next("/login")
  }
  if (userId && isPublicRoute) {
    return next("/tech-pharmacy/home")
  }
  return next()
}

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  useAuthStore().getAuthenticated()
  const userId = authStore.userId
  const isPublicRoute = to.matched.some(record => record.meta.public)

  return handleRedirect(to, next, isPublicRoute, userId)
})

export default router;