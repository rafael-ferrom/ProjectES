<template>
  <v-app-bar
    app
    :height="115"
    color="white"
  >
    <div
      style="width: 100%;"
      class="d-flex flex-column fill-height"
    >
      <div class="d-flex text-caption text--disabled font-weight-regular">
        Joao Brasileiro | ACME LTDA {{ authenticated }}
        <v-spacer></v-spacer>
        {{ $t('toolbar.timeZone') }}: BRT
      </div>

      <div class="my-auto d-flex" style="justify-content: space-between; overflow: auto hidden">
        <div class="d-flex align-center" style="min-height: 64px">
          <v-img
            aspect-ratio="16/9"
            height="80"
            :src="require(`../assets/logo-tech.png`)"
            max-width="150"
            min-width="150"
            contain
            class="ml-10"
          >
          </v-img>
   
          <v-divider inset vertical class="mx-4" style="height: 100%"></v-divider>
  
          <div 
            class="pr-4 mt-2 font-weight-light primary--text"
            style="font-size: 26px !important;"
          >
            Tech Pharmacy
          </div>
        </div>

        <v-menu
          open-on-hover
          bottom
          offset-y
        >
          <v-list>
            <v-list-item link @click="changeLanguage('en')">
              <v-img
                style="height: 32px;"
                content-class="rounded-lg"
                class="rounded-lg mr-2"
                aspect-ratio="16/9"
                max-width="32"
                width="32"
                :src="require(`./../assets/usd_flag.png`)"
              />
              <v-list-item-title>
                EN-US
              </v-list-item-title>
            </v-list-item>
            <v-list-item link @click="changeLanguage('pt')">
              <v-img
                style="height: 32px;"
                content-class="rounded-lg"
                class="rounded-lg mr-2"
                aspect-ratio="16/9"
                max-width="32"
                width="32"
                :src="require(`./../assets/brazil_flag.png`)"
              />
              <v-list-item-title>
                PT-BR
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu
          open-on-hover
          bottom
          offset-y
        >
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              class="my-auto ml-5"
              v-bind="attrs"
              v-on="on"
              icon
            >
              <v-icon>
                mdi-account
              </v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item link @click="logoutClicked()">
              <v-list-item-title>
                {{ $t('instructions.logout') }}
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </div>

    <template v-slot:extension>
      <v-card class="mt-4 d-flex" style="width: 100%; background-color: #ededeb;">
        <div class="d-flex" style="background-color: #ededeb; height: 40px;">
          <template v-for="(menus, index) in menusList">
            <router-link
              :key="'direct-' + index"
              :to="menus.menus[0].to"
              class="d-flex align-center px-4 text-decoration-none hoverable-menu"
              style="background-color: #ededeb;"
              :style="{ ...isActiveMenu(menus.menus), width: 'auto' }"
            >
              <span class="font-weight-bold">{{ menus.title }}</span>
            </router-link>
          </template>
        </div>
      </v-card>
    </template>

    <v-snackbar
      color="attention"
      v-model="logoutError"
    >
      {{ errorMessage }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="attention"
          text
          v-bind="attrs"
          @click="logoutError = false"
        >
          OK
        </v-btn>
      </template>
    </v-snackbar>
  </v-app-bar>
</template>

<script>
import { mapActions, mapState, mapWritableState } from "pinia"
import { useAuthStore } from "./../store/index"

export default {
  components: {
  },
  data () {
    return {
      tab: 0,
      logoutError: false,
      errorMessage: undefined,
      eventSource: undefined,
      GRIDPretty: "",
      GRIDPROPretty: "PRO",
      intervalMinutely: null,
      intervalHourly: null,
      openGroups: {},
      hasFetchedHourlySuccess: false,
      animationDuration: 20,
      menuStates: {},
      activeSubItemIndex: null
    }
  },
  mounted () {
  },
  beforeDestroy () {
  },
  methods: {
    ...mapActions(useAuthStore, [
      "logout",
      "removeSessionLocalStorage",
      "getUserRoleFromLocalStorage"
    ]),
    logoutClicked() {
      this.logout()
      this.$router.push("/")
    },
    isActiveMenu(menuList) {
      const isCurrentRoute = menuList.some(menu => menu.to === this.$route.path)

      if (isCurrentRoute) {
        return { borderBottom: '5px solid #1976D2' } // Usando a cor primária do Vuetify
      }

      return {}
    },
  },
  computed: {
    ...mapWritableState(useAuthStore, [
      "userId"
    ]),
    ...mapState(useAuthStore, [
      "authenticated"
    ]),
    isPtBRLocale() {
      return this.$i18n.locale === "pt"
    },
    menusList() {
      return [
        {
          title: "Início",
          menus: [
            { label: "Início", to: "/tech-pharmacy/home" },
          ]
        },
        {
          title: "Meus Tratamentos",
          menus: [
            { label: "Meus Tratamentos", to: "/tech-pharmacy/active-medications" },
          ]
        },
        // >>>>> ALTERAÇÃO AQUI <<<<<
        {
          title: "Meu Estoque",
          menus: [
            { label: "Meu Estoque", to: "/tech-pharmacy/inventory" },
          ]
        }
      ]
    },
  }
}
</script>

<style scoped>
::v-deep .v-toolbar__extension {
  height: 42px !important;
  padding: 0 !important;
}

.hoverable-menu {
  color: #333;
  transition: background-color 0.3s;
}

.hoverable-menu:hover {
  background-color: #dcdcdc !important;
}

.router-link-active {
  background-color: #d0d0d0 !important;
}
</style>
