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
    // >>>>> ALTERAÇÃO AQUI <<<<<
    userStock: [],
    nearbyPharmacies: [],
    isLoadingPharmacies: false,
    pharmacySearchError: null,
  }),
  getters: {
    getAllMedicamentos: (state) => state.medicamentos,
    getMedicamentoById: (state) => {
      return (id) => state.medicamentos.find((medicamento) => medicamento.id == id);
    },
    getStockForBula: (state) => (bulaId) => {
        const today = new Date().toISOString().slice(0, 10);
        const relevantStock = state.userStock.filter(item => item.bula.id === bulaId && item.status === 'DISPONIVEL' && item.dataValidade >= today);
        const totalPills = relevantStock.reduce((acc, item) => acc + item.quantidadeComprimidos, 0);
        return {
            totalPills,
            boxes: relevantStock,
        };
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
        informacoesDeUso: `Siga as instruções médicas para ${bula.nomeComercial}.`,
        comprimidosPorCaixa: parseInt(bula.apresentacao.match(/\d+/)[0], 10) || 20 // Extrai número de comprimidos
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
      try {
        const response = await axios.post(url);
        await this.fetchUserStock();
        await this.fetchUserConfiguredMedicamentos();
        return response;
      } catch (error) {
        console.error("Erro ao registrar dose:", error.response?.data || error.message);
        throw new Error(error.response?.data?.message || "Não foi possível registrar a dose. Verifique seu estoque.");
      }
    },
    async fetchUserStock() {
        const authStore = useAuthStore();
        const userId = authStore.userId;
        if (!userId) {
            this.userStock = [];
            return;
        }
        try {
            const response = await axios.get(`/api/estoque/usuario/${userId}`);
            this.userStock = response.data;
        } catch (error) {
            console.error("Falha ao buscar estoque do usuário:", error);
            this.userStock = [];
        }
    },
    async purchaseMedication(purchasePayload) {
        const authStore = useAuthStore();
        if (!authStore.userId) {
            throw new Error("Usuário não está autenticado para comprar.");
        }
        const payload = {
            ...purchasePayload,
            userId: parseInt(authStore.userId, 10),
        };
        const url = `/api/estoque/comprar`;
        try {
          const response = await axios.post(url, payload);
          // Após a compra, atualiza o estoque local
          await this.fetchUserStock();
          return response;
        } catch (error) {
          console.error("Erro ao realizar compra:", error);
          throw error;
        }
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
        const userLocation = await this.getCurrentLocation();
        const center = new google.maps.LatLng(userLocation.lat, userLocation.lng);

        const request = {
          fields: ['displayName', 'formattedAddress', 'location', 'rating', 'businessStatus'],
          locationRestriction: {
            center: center,
            radius: 5000,
          },
          includedPrimaryTypes: ['pharmacy'],
          maxResultCount: 5,
          rankPreference: 'DISTANCE',
          language: 'pt-BR',
          region: 'br',
        };
        
        const { places } = await google.maps.places.Place.searchNearby(request);

        if (places.length) {
          this.nearbyPharmacies = places.map(place => ({
            id: place.id,
            name: place.displayName,
            address: place.formattedAddress,
            rating: place.rating || 'N/A',
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

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [],
    isDrawerOpen: false,
    snackbar: {
      show: false,
      text: '',
      color: 'info',
    }
  }),
  getters: {
    unreadCount: (state) => state.notifications.filter(n => !n.lida).length,
  },
  actions: {
    toggleDrawer() {
      this.isDrawerOpen = !this.isDrawerOpen;
      if(this.isDrawerOpen && this.unreadCount > 0) {
        this.markAllAsRead();
      }
    },
    async fetchNotifications() {
      const authStore = useAuthStore();
      if (!authStore.userId) return;
      try {
        const response = await axios.get(`/api/notificacoes/usuario/${authStore.userId}`);
        this.notifications = response.data;
      } catch (error) {
        console.error("Erro ao buscar notificações:", error);
      }
    },
    async addNotification(notificationData) {
      const authStore = useAuthStore();
      try {
        const payload = { ...notificationData, userId: authStore.userId };
        const response = await axios.post('/api/notificacoes', payload);
        this.notifications.unshift(response.data);
        
        this.snackbar.text = notificationData.mensagem;
        this.snackbar.color = notificationData.tipo === 'DOSE_ATRASADA' ? 'warning' : 'info';
        this.snackbar.show = true;

      } catch (error) {
        console.error("Erro ao salvar notificação:", error);
      }
    },
    async markAllAsRead() {
      const unreadIds = this.notifications.filter(n => !n.lida).map(n => n.id);
      if (unreadIds.length === 0) return;
      
      try {
        await axios.post('/api/notificacoes/marcar-como-lidas', unreadIds);
        this.notifications.forEach(n => {
          if (unreadIds.includes(n.id)) {
            n.lida = true;
          }
        });
      } catch (error) {
        console.error("Erro ao marcar notificações como lidas:", error);
      }
    },
  }
});