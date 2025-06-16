/* global google */
import { defineStore } from "pinia"
import axiosInstance from "@/http.js"
const axios = axiosInstance

// export let API_URL = process.env.VUE_APP_API_URL
export const API_USERS = `/api/users`
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
      const url = `${API_USERS}/register`;
      return axios.post(url, data);
    },
    async login(data) {
      const url = `${API_USERS}/login`;
      try {
        const response = await axios.post(url, data);
        this.saveSessionLocalStorage(response.data);
        this.authenticated = true;
        this.userId = response.data.user_id;
        return response;
      } catch (error) {
        this.authenticated = false;
        this.userId = null;
        throw error;
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
    medicamentos: [],
    selectedMedicationDetails: null,
    userConfiguredMedicamentos: [],
    nearbyPharmacies: [],
    isLoadingPharmacies: false,
    pharmacySearchError: null,
  }),
  getters: {
    getAllMedicamentos: (state) => state.medicamentos,
    getMedicamentoById: (state) => {
      return (id) => state.medicamentos.find((medicamento) => medicamento.id == id);
    },
    getNearbyPharmacies: (state) => state.nearbyPharmacies,
    getIsLoadingPharmacies: (state) => state.isLoadingPharmacies,
  },
  actions: {
    transformBulaToMedicamento(bula) {
      return {
        id: bula.id,
        fotoLink: bula.fotoUrl,
        nome: `${bula.nomeComercial} ${bula.concentracao}`,
        descricao: `Um medicamento baseado em ${bula.principioAtivo}. Apresentado em uma ${bula.apresentacao.toLowerCase()}.`,
        informacoesDeUso: `Siga as instruções médicas para ${bula.nomeComercial}.`
      };
    },
    async fetchMedicamentos() {
      if (this.medicamentos.length > 0) {
        return;
      }
      try {
        const response = await axios.get(`/api/bulas`);
        const transformedData = response.data.map(bula => this.transformBulaToMedicamento(bula));
        this.medicamentos = transformedData;
      } catch (error) {
        console.error("Falha ao buscar medicamentos:", error);
        this.medicamentos = [];
      }
    },
    async fetchMedicationById(id) {
      try {
        const response = await axios.get(`/api/bulas/${id}`);
        this.selectedMedicationDetails = response.data;
        return response.data;
      } catch (error) {
        console.error(`Falha ao buscar medicamento com id ${id}:`, error);
        this.selectedMedicationDetails = null;
      }
    },
    async addMedicationConfiguration(medicationPayload) {
      const authStore = useAuthStore();
      if (!authStore.userId) {
        throw new Error("Usuário não está autenticado.");
      }

      const payloadWithUser = {
        ...medicationPayload,
        userId: parseInt(authStore.userId, 10),
      };

      const url = `/api/medicamentos`;
      
      return axios.post(url, payloadWithUser);
    },
    async fetchUserConfiguredMedicamentos() {
      const authStore = useAuthStore();
      const userId = authStore.userId;

      if (!userId) {
        console.error("Não é possível buscar medicamentos do usuário sem um userId.");
        this.userConfiguredMedicamentos = [];
        return;
      }

      try {
        const url = `/api/medicamentos/usuario/${userId}`;
        const response = await axios.get(url);
        this.userConfiguredMedicamentos = response.data;
      } catch (error) {
        console.error("Falha ao buscar medicamentos configurados pelo usuário:", error);
        this.userConfiguredMedicamentos = [];
      }
    },
    async recordDose(medicationId) {
      const url = `/api/medicamentos/${medicationId}/doses`;
      return axios.post(url);
    },
    getCurrentLocation() {
      return new Promise((resolve, reject) => {
        if (!navigator.geolocation) {
          reject(new Error("Geolocalização não é suportada por este navegador."));
        } else {
          navigator.geolocation.getCurrentPosition(
            (position) => {
              resolve({
                lat: position.coords.latitude,
                lng: position.coords.longitude,
              });
            },
            () => {
              reject(new Error("Não foi possível obter a localização do usuário."));
            }
          );
        }
      });
    },
    async findNearbyPharmacies() {
      this.isLoadingPharmacies = true;
      this.nearbyPharmacies = [];
      this.pharmacySearchError = null;

      try {
        // 1. Obter a localização do usuário
        const userLocation = await this.getCurrentLocation();
        const center = new google.maps.LatLng(userLocation.lat, userLocation.lng);

        // 2. Preparar a requisição para a API do Places
        const request = {
          fields: ['displayName', 'formattedAddress', 'location', 'rating', 'businessStatus'],
          locationRestriction: {
            center: center,
            radius: 5000, // Busca num raio de 5km
          },
          includedPrimaryTypes: ['pharmacy'],
          maxResultCount: 5,
          rankPreference: 'DISTANCE', // Essencial para ordenar por distância
          language: 'pt-BR',
          region: 'br',
        };
        
        // 3. Chamar a API
        const { places } = await google.maps.places.Place.searchNearby(request);

        // 4. Formatar e salvar o resultado no estado da store
        if (places.length) {
          this.nearbyPharmacies = places.map(place => ({
            id: place.id, // O ID do Google Place
            name: place.displayName,
            address: place.formattedAddress,
            rating: place.rating || 'N/A', // Nem todos os lugares têm avaliação
            location: {
              lat: place.location.lat(),
              lng: place.location.lng(),
            }
          }));
        }

      } catch (error) {
        console.error("Erro ao buscar farmácias:", error);
        this.nearbyPharmacies = [];
        this.pharmacySearchError = error.message;
      } finally {
        this.isLoadingPharmacies = false;
      }
    },
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