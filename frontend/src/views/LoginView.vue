<template>
  <div class="d-flex fill-height">
    <v-app-bar
      app
      :height="65"
      color="white"
    >
      <v-img
        style="height: 50px;"
        aspect-ratio="16/9"
        :src="require(`./../assets/logo-tech.png`)"
        max-width="148"
        contain
        class="my-auto"
      >
      </v-img>
      <!-- <div class="my-auto d-flex">
      </div> -->
    </v-app-bar>

    <v-main 
      class="d-flex justify-center align-center content py-0"
    >
      <v-card
        width="450"
        height="515"
        class="pa-6 mx-auto"
      >
        <v-card-title class="text-h4">
          {{ $t('login.title') }}
        </v-card-title>
        <v-card-text>
          {{ $t('login.description.descriptionOne') }}
          {{ $t('login.description.descriptionTwo') }}
          {{ $t('login.description.descriptionThree') }}

          <v-form
            v-model="formValid"
            ref="form"
            class="d-flex flex-column mt-6"
          >
            <span class="font-weight-bold">
              {{ $t('login.corporateEmail') }}
            </span>

            <v-text-field
              v-model="username"
              dense
              :background-color="'white'"
              :placeholder="$t('login.corporateEmail')"
              outlined
              required
              class="pb-4 pt-2"
            >
              <!-- prepend-inner-icon="mdi-monitor-multiple" -->
              <template v-slot:prepend-inner>
                <v-icon 
                  small
                  color="#a5a5a5"
                  class="pa-1"
                >
                  mdi-monitor-multiple
                </v-icon>
              </template>
            </v-text-field>

            <div class="d-flex font-weight-bold">
              <span>
                {{ $t('login.password') }}
              </span>
              <v-spacer></v-spacer>
              <span
                class="primary--text"
                style="cursor: pointer;"
              >
              <!--Forgot Password-->
              </span>
            </div>

            <v-text-field
              v-model="password"
              dense
              :background-color="'white'"
              :placeholder="$t('login.password')"
              outlined
              required
              class="pb-4 pt-2"
              :type="showPassword ? 'text' : 'password'"
              hint=""
              :persistent-hint="true"
              @keyup.enter="loginUser()"
            >
              <!-- prepend-inner-icon="mdi-key" -->
              <template v-slot:prepend-inner>
                <v-icon 
                  small
                  color="#a5a5a5"
                  class="pa-1"
                >
                  mdi-key
                </v-icon>
              </template>

              <!-- :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'" -->
              <template v-slot:append>
                <v-icon 
                  small
                  class="pa-1"
                  color="#a5a5a5"
                  @click="showPassword = !showPassword"
                >
                  {{ showPassword ? 'mdi-eye' : 'mdi-eye-off' }}
                </v-icon>
              </template>
            </v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-btn
            color="primary"
            elevation="0"
            tile
            @click="redirect('/register')"
          >
            <span class="text-capitalize">
              {{ $t('instructions.sign-in') }}
            </span>
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            elevation="0"
            tile
            :disabled="!formValid"
            @click="loginUser()"
          >
            <!-- :href="'/console/home'" -->
            <span class="text-capitalize">
              {{ $t('instructions.next') }}
            </span>
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-main>

    <v-snackbar
      color="attention"
      v-model="loginError"
    >
      {{ errorMessage }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="attention"
          text
          v-bind="attrs"
          @click="loginError = false"
        >
          OK
        </v-btn>
      </template>
    </v-snackbar>

    <v-footer 
      app
      padless
      height="32" 
      color="white"
      class="d-flex text-caption px-4"
    >
      <div>
        © 2023-2024 Tech Pharmacy All rights reserved.
      </div>

      <v-menu
        open-on-hover
        bottom
        offset-y
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            v-bind="attrs"
            v-on="on"
            plain
          >
            <v-img
              v-if="isPtBRLocale"
              style="height: 32px;"
              content-class="rounded-lg"
              class="rounded-lg mr-2"
              aspect-ratio="16/9"
              max-width="32"
              width="32"
              :src="require(`./../assets/brazil_flag.png`)"
            />
            <v-img
              v-else
              style="height: 32px;"
              content-class="rounded-lg"
              class="rounded-lg mr-2"
              aspect-ratio="16/9"
              max-width="32"
              width="32"
              :src="require(`./../assets/usd_flag.png`)"
            />
            {{ $t('extras.language') }}
            </v-btn>
        </template>

        <v-list style="background-color: white;">
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
              <span style="color: #000" >EN-US</span>
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
              <span style="color: #000" >PT-BR</span>
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>

      <v-spacer></v-spacer>

      <!--
      <div class="footer-menu"> Contact Us </div>
      <v-divider vertical inset class="mx-6"></v-divider>
      <div class="footer-menu"> Terms of Service </div>
      <v-divider vertical inset class="mx-6"></v-divider>
      <div class="footer-menu"> Trademarks </div>
      <v-divider vertical inset class="mx-6"></v-divider>
      <div class="footer-menu"> Privacy Policy </div>
      -->
    </v-footer>
  </div>
</template>

<script>
import { mapState, mapWritableState, mapActions } from "pinia";
import { useAuthStore } from "./../store/index";

export default {
  data () {
    return {
      formValid: undefined,
      username: undefined,
      password: undefined,
      showPassword: false,
      loadingLogin: false,
      loginError: false,
      errorMessage: undefined
    }
  },
  methods: {
    ...mapActions(useAuthStore, [
      "login"
    ]),
    changeLanguage(lang) {
      this.$i18n.locale = lang
      localStorage.setItem('userLanguage', lang)
    },
    redirect (url) {
      this.$router.push(url)
    },
    loginUser () {
      // this.$refs.form.validate()
      if (!this.formValid) return;

      const email = this.username.trim()
      const password = this.password.trim()
      const maxAttempts = 5

      const attemptsData = JSON.parse(localStorage.getItem("loginAttempts")) || {}

      // Checa se email está bloqueado
      console.log(email, password)
      if (attemptsData[email]?.blocked) {
        this.loginError = true
        this.errorMessage = "Este e-mail foi temporariamente bloqueado após 5 tentativas de login falhadas."
        return
      }

      this.loadingLogin = true

      const payload = {
        email,
        password
      }

      this.login(payload)
        .then(() => {
          delete attemptsData[email]
          localStorage.setItem("loginAttempts", JSON.stringify(attemptsData))

          this.$router.push("/tech-pharmacy");
        })
        .catch(() => {
          if (!attemptsData[email]) {
            attemptsData[email] = { count: 1 }
          } else {
            attemptsData[email].count += 1
          }

          if (attemptsData[email].count >= maxAttempts) {
            attemptsData[email].blocked = true
            this.errorMessage = "Muitas tentativas falhas. Este e-mail foi bloqueado temporariamente."
          } else {
            this.errorMessage = `Credenciais inválidas. Tentativas restantes: ${maxAttempts - attemptsData[email].count}`
          }

          localStorage.setItem("loginAttempts", JSON.stringify(attemptsData))
          this.loginError = true;
        })
        .finally(() => {
          this.loadingLogin = false
        })
    }
  },
  computed: {
    ...mapWritableState(useAuthStore, [
      "authenticated",
      "userId",
      "apiKey"
    ]),
    ...mapState(useAuthStore, [
      // "getBlotter",
      // "isBlotterFXSupplier",
      "isAuthenticated",
      "getUserId"
    ]),
    isPtBRLocale() {
      return this.$i18n.locale === "pt"
    },
  }
}
</script>

<style scoped>
.content {
  background-image: url("./../assets/white-background-login.svg");
  background-repeat: repeat;
  background-color: #000;
  flex: 1 0 auto;
}
.banner-title {
  background-color: #000;
  color: #fff;
  font-size: 60px;
  font-weight: 500;
  letter-spacing: .29px;
  line-height: 73px;
  max-height: 200px;
  max-width: 520px;
  margin-top: 90px;
  padding: 25px 50px;
  text-align: start;
  word-wrap: break-word;
}
.container-login {
  color: #fff;
  background-color: #000;
  /* margin-left: 13%; */
  max-width: 460px;
  font-size: 22px;
  text-align: start;
  max-height: 520px;
  height: 500px;
  font-weight: 300;
  padding: 0 10px 0 40px;
}
.footer-menu {
  cursor: pointer;
}
.footer-menu:hover {
  text-decoration: underline;
}
</style>
