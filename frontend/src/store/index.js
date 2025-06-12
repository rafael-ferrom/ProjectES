import { defineStore } from "pinia"
import axiosInstance from "@/http.js"
const axios = axiosInstance

export let API_URL = process.env.VUE_APP_API_URL
export const API_USERS = `api/users`
const LOCAL_STORAGE_KEY = 'activeMedicationsV1';

export const useAuthStore = defineStore("auth", {
  state: () => ({
    authenticated: undefined,
    userId: undefined,
    apiKey: undefined
  }),
  getters: {
    isAuthenticated: (state) => state.authenticated,
    getUserId: (state) => state.userId,
    getApiKey: (state) => state.apiKey,
  },
  actions: {
    getAuthenticated () {
      this.loadAuthenticatedAndUserIdStateFromLocalStorage()
    },
    loadAuthenticatedAndUserIdStateFromLocalStorage() {
      const userId = localStorage.getItem("userId");
      if (userId) {
        this.authenticated = true;
        this.userId = userId;
      } else {
        this.authenticated = false;
        this.userId = null;
      }
    },
    saveSessionLocalStorage(data) {
      localStorage.setItem("userId", data.user_id);
    },
    removeSessionLocalStorage() {
      localStorage.removeItem("userId");
      localStorage.removeItem("apiKey");
    },
    getSessionFromLocalStorage () {
      const data = localStorage.getItem("userId")
      return data
    },
    registerUser(data) {
      const url = `${API_URL}/${API_USERS}/register`;
      return axios.post(url, data);
    },
    async login(data) {
      const url = `${API_URL}/${API_USERS}/login`;
      try {
        const response = await axios.post(url, data);
        // Se a requisição for bem-sucedida, atualize o estado AQUI
        this.saveSessionLocalStorage(response.data);
        this.authenticated = true;
        this.userId = response.data.user_id;
        return response; // Retorna a resposta para o componente
      } catch (error) {
        // Em caso de erro, garanta que o estado está como "não autenticado"
        this.authenticated = false;
        this.userId = null;
        throw error; // Lança o erro para o componente poder tratá-lo (ex: mostrar snackbar)
      }
    },
    logout() {
      this.removeSessionLocalStorage();
      this.authenticated = false;
      this.userId = null;
    },
  },
});

export const useDisplayStore = defineStore("display", {
  state: () => ({
    breakpoint: undefined,
    windowHeight: window.innerHeight,
    headerHeight: 153,
    // footerHeight: 30,
    marginLayout: 32,
  }),
  getters: {
    isMobile() {
      return this.mobile
    },
    currentBreakpoint () {
      return this.breakpoint
    },
    isXLarge () {
      return this.currentBreakpoint === "xl"
    },
    isLarge () {
      return this.currentBreakpoint === "lg"
    },
    isMedium () {
      return this.currentBreakpoint === "md"
    },
    isSmall () {
      return this.currentBreakpoint === "sm"
    },
    isXSmall () {
      return this.currentBreakpoint === "xs"
    },
    contentHeight () {
      const contentHeight = 
        this.windowHeight 
        - this.headerHeight 
        // - this.footerHeight 
        // - this.marginLayout // x axis
        // - this.marginLayout // y axis
      return `${contentHeight}`
    },
  },
  actions: {
  }
})

export const useMedicationStore = defineStore("medication", {
  state: () => ({
    medicamentos: [], // Master list of all available medications
    selectedMedicationDetails: null,
    userConfiguredMedicamentos: [], // New state to hold the user's configured medications
  }),
  getters: {
    getAllMedicamentos: (state) => state.medicamentos,
    getMedicamentoById: (state) => {
      return (id) => state.medicamentos.find((medicamento) => medicamento.id == id);
    },
  },
  actions: {
    transformBulaToMedicamento(bula) {
      return {
        id: bula.id,
        fotoLink: bula.fotoUrl,
        nome: `${bula.nomeComercial} ${bula.concentracao}`,
        descricao: `A medication based on ${bula.principioAtivo}. Presented in a ${bula.apresentacao.toLowerCase()}.`,
        informacoesDeUso: `Follow the medical instructions for ${bula.nomeComercial}.`
      };
    },
    async fetchMedicamentos() {
      if (this.medicamentos.length > 0) {
        return;
      }
      try {
        const response = await axios.get(`${API_URL}/api/bulas`);
        const transformedData = response.data.map(bula => this.transformBulaToMedicamento(bula));
        this.medicamentos = transformedData;
      } catch (error) {
        console.error("Failed to fetch medications:", error);
        this.medicamentos = [];
      }
    },

    /**
     * Fetches details for a single medication ('bula') from the API.
     * @param {string} id - The ID of the medication to fetch.
     */
    async fetchMedicationById(id) {
      try {
        const response = await axios.get(`${API_URL}/api/bulas/${id}`);
        this.selectedMedicationDetails = response.data;
        return response.data;
      } catch (error) {
        console.error(`Failed to fetch medication with id ${id}:`, error);
        this.selectedMedicationDetails = null;
      }
    },

    /**
     * Posts a new medication configuration to the backend.
     * @param {object} medicationPayload - The payload matching the MedicamentoDTO.
     */
    async addMedicationConfiguration(medicationPayload) {
      // The "auth" store is needed to get the userId
      const authStore = useAuthStore();
      if (!authStore.userId) {
        throw new Error("User is not authenticated.");
      }

      const payloadWithUser = {
        ...medicationPayload,
        userId: parseInt(authStore.userId, 10),
      };

      // The endpoint for adding a new medication configuration
      const url = `${API_URL}/api/medicamentos`;
      
      // Axios will automatically handle JSON stringification
      return axios.post(url, payloadWithUser);
    },

    /**
     * Fetches all configured medications for the currently authenticated user.
     */
    async fetchUserConfiguredMedicamentos() {
      const authStore = useAuthStore();
      const userId = authStore.userId;

      if (!userId) {
        console.error("Cannot fetch user medications without a userId.");
        this.userConfiguredMedicamentos = [];
        return;
      }

      try {
        const url = `${API_URL}/api/medicamentos/usuario/${userId}`;
        const response = await axios.get(url);
        this.userConfiguredMedicamentos = response.data;
      } catch (error) {
        console.error("Failed to fetch user configured medications:", error);
        this.userConfiguredMedicamentos = []; // Clear state on error
      }
    },

    /**
     * Calls the backend to record that a dose has been taken.
     * @param {number} medicationId - The ID of the configured medication.
     */
    async recordDose(medicationId) {
      const url = `${API_URL}/api/medicamentos/${medicationId}/doses`;
      return axios.post(url);
    }
  },
});

export const useActiveMedicationsStore = defineStore("activeMedications", {
  state: () => ({
    activeMedicamentos: [],
  }),
  getters: {
    getAllActiveMedicamentos: (state) => state.activeMedicamentos,
    hasActiveMedicamentos: (state) => state.activeMedicamentos.length > 0,
  },
  actions: {
    loadActiveMedicamentosFromLocalStorage() {
      const data = localStorage.getItem(LOCAL_STORAGE_KEY);
      if (data) {
        try {
          this.activeMedicamentos = JSON.parse(data);
        } catch (e) {
          console.error("Erro ao carregar medicamentos ativos do localStorage:", e);
          this.activeMedicamentos = [];
        }
      } else {
        this.activeMedicamentos = [];
      }
    },
    saveActiveMedicamentosToLocalStorage() {
      try {
        localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(this.activeMedicamentos));
      } catch (e) {
        console.error("Erro ao salvar medicamentos ativos no localStorage:", e);
      }
    },
    addActiveMedication(medicationData) {
      const newActiveMedication = {
        ...medicationData,
        configurationTimestamp: Date.now(),
        activeId: `active-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      };
      this.activeMedicamentos.push(newActiveMedication);
      this.saveActiveMedicamentosToLocalStorage();
    },
    removeActiveMedication(activeIdToRemove) {
      this.activeMedicamentos = this.activeMedicamentos.filter(
        (med) => med.activeId !== activeIdToRemove
      );
      this.saveActiveMedicamentosToLocalStorage();
    },
  },
});