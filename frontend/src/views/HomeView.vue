<template>
  <div>
    <v-row no-gutters class="mb-8 px-8">
      <v-col xs="0" sm="0" md="0" lg="1" xl="1"></v-col>
      <v-col xs="12" sm="12" md="12" lg="10" xl="10" class="pt-8">
        <div class="d-flex flex-column">
          <v-row class="d-flex pb-8">
            <v-col class="d-flex justify-center text-h3 font-weight-light text-center primary--text">
              {{ $t('medication.title') }}
            </v-col>
          </v-row>
          <v-row no-gutters>
            <!-- <v-col cols="1" sm="0" md="0" lg="1" xl="1"></v-col> -->
            <v-col cols="10" sm="12" md="12" lg="12" xl="12">
              <v-row v-if="loading" class="justify-center">
                 <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
              </v-row>
              <v-row v-else-if="!medicamentos.length" class="justify-center">
                <v-alert type="info" border="left" colored-border color="primary">
                  Nenhum medicamento encontrado. Por favor, tente novamente mais tarde.
                </v-alert>
              </v-row>
              <v-row v-else no-gutters class="justify-center">
                <v-col v-for="medicamento in medicamentos" :key="medicamento.id" cols="12" sm="6" md="6" lg="3"
                  class="pa-2 d-flex" style="min-width: 310px">
                  <v-card class="d-flex flex-column flex-grow-1" hover >
                    <div @click="viewMedicationDetails(medicamento)" style="cursor: pointer;">
                      <v-img :src="medicamento.fotoLink" height="200px" contain class="grey lighten-2">
                        <v-chip
                          v-if="getStockForBula(medicamento.id).totalPills > 0"
                          class="ma-2"
                          color="blue"
                          text-color="white"
                          absolute
                          top
                          right
                        >
                          <v-icon left>mdi-package-variant-closed</v-icon>
                          {{ getStockForBula(medicamento.id).totalPills }} em estoque
                        </v-chip>
                        <template v-slot:placeholder>
                          <v-row class="fill-height ma-0" align="center" justify="center">
                            <v-icon size="60" color="grey lighten-1">mdi-pill</v-icon>
                          </v-row>
                        </template>
                      </v-img>
                      <v-card-title class="text-subtitle-1">{{ medicamento.nome }}</v-card-title>
                      <v-card-text class="text-body-2 flex-grow-1">{{ medicamento.descricao }}</v-card-text>
                    </div>

                    <v-spacer></v-spacer>

                    <v-card-actions>
                      <v-btn
                        text
                        color="primary"
                        @click="viewMedicationDetails(medicamento)"
                      >
                        <v-icon left>mdi-medical-bag</v-icon>
                        Tratamento
                      </v-btn>
                      <v-spacer></v-spacer>
                      <v-btn
                        color="primary"
                        @click="comprarRemedio(medicamento.id)"
                      >
                        <v-icon left>mdi-cart-plus</v-icon>
                        Comprar
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-col>
              </v-row>
            </v-col>
            <!-- <v-col cols="1" sm="0" md="0" lg="1" xl="1"></v-col> -->
          </v-row>
        </div>
      </v-col>
      <v-col xs="0" sm="0" md="0" lg="1" xl="1"></v-col>
    </v-row>
  </div>
</template>

<script>
import {
  useDisplayStore,
  useAuthStore,
  useMedicationStore
} from "../store/index"
import { mapState, mapActions, mapGetters } from "pinia"
import * as utils from "../utils.js"

export default {
  components: {},
  filters: {
    formatDate(timestamp) {
      return utils.getLocaleFormattedDateTime(timestamp);
    },
    formatPrice(price) {
      return utils.formatPrice(price);
    },
  },
  data() {
    return {
      medicines: [],
      loading: false,
    };
  },
  computed: {
    ...mapState(useDisplayStore, [
      "isXSmall", "isSmall", "isMedium", "isLarge", "isXLarge"
    ]),
    ...mapState(useMedicationStore, [
      "getAllMedicamentos"
    ]),
    // >>>>> ALTERAÇÃO AQUI <<<<<
    ...mapGetters(useMedicationStore, ['getStockForBula']),
    medicamentos() {
      return this.getAllMedicamentos;
    }
  },
  async mounted() {
    this.loading = true;
    // >>>>> ALTERAÇÃO AQUI <<<<<
    await Promise.all([
      this.fetchMedicamentos(),
      this.fetchUserStock()
    ]);
    this.loading = false;
  },
  methods: {
    ...mapActions(useAuthStore, [
      "getUserRoleFromLocalStorage"
    ]),
    ...mapActions(useMedicationStore, [
      "ensureMedicamentosLoaded",
      "fetchMedicamentos",
      // >>>>> ALTERAÇÃO AQUI <<<<<
      "fetchUserStock",
    ]),
    viewMedicationDetails(medicamento) {
      this.$router.push({
        name: 'configure-medication',
        params: {
          id: medicamento.id.toString()
        }
      });
    },
    comprarRemedio(id) {
      this.$router.push({ name: 'PharmacySelection', params: { medicationId: id } })
    },
  }
}
</script>

<style scoped>
::v-deep div.v-radio.disabled.v-radio--is-disabled.theme--dark>div>i {
  color: #303030;
}

.font-size-rate {
  font-size: 22px;
}

.line-height {
  line-height: 26px;
}

.relative-container {
  position: relative;
}

.center-text {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.v-input--radio-group__input {
  display: block
}

.d-flex.flex-column.flex-grow-1 {
  height: 100%;
}

.pa-2 {
  padding: 8px !important;
}
</style>