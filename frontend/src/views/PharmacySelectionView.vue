<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8" class="pt-8">
        
        <v-card>
          <v-overlay :value="isPageLoading" absolute>
            <v-progress-circular indeterminate size="64"></v-progress-circular>
          </v-overlay>

          <template v-if="medicamento">
            <v-card-title class="text-h4 font-weight-light primary--text d-flex align-center">
              <v-icon large left color="primary">mdi-pill</v-icon>
              Comprar: {{ medicamento.nome }}
            </v-card-title>
            <v-card-subtitle>Defina a quantidade e escolha a farmácia mais próxima.</v-card-subtitle>
            
            <v-divider class="mx-4"></v-divider>

            <v-card-text>
              <v-row>
                <v-col cols="12" md="6">
                  <h3 class="text-h6 font-weight-medium mb-3">Localização das Farmácias</h3>
                  <v-sheet rounded="lg" min-height="300" class="grey lighten-3 d-flex align-center justify-center">
                    <div v-if="isLoadingPharmacies" class="text-center">
                      <v-progress-circular indeterminate color="primary"></v-progress-circular>
                      <p class="mt-2 text-caption">Buscando...</p>
                    </div>
                    <div id="pharmacy-map" style="width: 100%; height: 300px;"></div>
                  </v-sheet>

                  <h3 class="text-h6 font-weight-medium mb-3 mt-6">Quantidade</h3>
                  <v-text-field
                    v-model.number="quantidade"
                    label="Nº de caixas"
                    type="number"
                    min="1"
                    outlined
                    dense
                    style="max-width: 200px;"
                  ></v-text-field>
                </v-col>

                <v-col cols="12" md="6">
                   <h3 class="text-h6 font-weight-medium mb-3">Farmácias Próximas</h3>
                   <v-alert v-if="!isLoadingPharmacies && pharmacySearchError" type="warning" dense text>
                     {{ pharmacySearchError }}
                   </v-alert>
                   <v-list v-else-if="!isLoadingPharmacies && farmacias.length > 0" class="pa-0">
                      <v-list-item
                        v-for="farmacia in farmacias"
                        :key="farmacia.id"
                        @click="abrirDialogConfirmacao(farmacia)"
                        class="mb-2"
                        link
                      >
                        <v-list-item-icon>
                          <v-icon color="primary">mdi-store-marker-outline</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                          <v-list-item-title class="font-weight-bold">{{ farmacia.name }}</v-list-item-title>
                          <v-list-item-subtitle>{{ farmacia.address }}</v-list-item-subtitle>
                        </v-list-item-content>
                        <v-list-item-action>
                           <div class="d-flex align-center">
                             <v-icon small color="amber">mdi-star</v-icon>
                             <span class="grey--text text--darken-2 ml-1">{{ farmacia.rating }}</span>
                           </div>
                        </v-list-item-action>
                      </v-list-item>
                   </v-list>
                </v-col>
              </v-row>
            </v-card-text>
          </template>

          <v-alert v-if="!isPageLoading && !medicamento" type="error" prominent>
            Não foi possível carregar os detalhes do medicamento. Por favor, volte para a
            <router-link to="/tech-pharmacy/home">página inicial</router-link> e tente novamente.
          </v-alert>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="dialogConfirmacao" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">Confirmar Pedido</v-card-title>
        <v-card-text class="text-body-1">
          Confirma o pedido de <strong>{{ quantidade }}x {{ medicamento?.nome }}</strong> na farmácia
          <br><strong>{{ farmaciaSelecionada?.name }}</strong>?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="dialogConfirmacao = false">Cancelar</v-btn>
          <v-btn color="success" @click="executarCompraMockada">Confirmar Pedido</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="dialogSucesso" persistent max-width="500px">
      <v-card>
        <v-card-title class="text-h5 success--text">
          <v-icon color="success" left>mdi-check-circle-outline</v-icon>
          Pedido Enviado!
        </v-card-title>
        <v-card-text>
          Seu pedido simulado foi enviado. Um entregador (imaginário) está a caminho!
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="fecharDialogSucesso">Ok</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </v-container>
</template>

<script>
/* global google */
import { mapState, mapActions } from 'pinia';
import { useMedicationStore } from '@/store';

export default {
  name: 'PharmacySelectionView',
  props: {
    medicationId: {
      type: [String, Number],
      required: true,
    },
  },
  data() {
    return {
      isPageLoading: true,
      quantidade: 1,
      dialogConfirmacao: false,
      dialogSucesso: false,
      farmaciaSelecionada: null,
      map: null, // Guarda a instância do mapa
    };
  },
  computed: {
    ...mapState(useMedicationStore, {
      farmacias: 'nearbyPharmacies',
      isLoadingPharmacies: 'isLoadingPharmacies',
      pharmacySearchError: 'pharmacySearchError',
      medicamentos: 'medicamentos',
      getMedicamentoById: 'getMedicamentoById',
    }),
    medicamento() {
      return this.getMedicamentoById(this.medicationId);
    },
  },
  methods: {
    ...mapActions(useMedicationStore, ['findNearbyPharmacies', 'fetchMedicamentos']),

    abrirDialogConfirmacao(farmacia) {
      this.farmaciaSelecionada = farmacia;
      this.dialogConfirmacao = true;
    },
    executarCompraMockada() {
      this.dialogConfirmacao = false;
      this.dialogSucesso = true;
    },
    fecharDialogSucesso() {
      this.dialogSucesso = false;
      this.$router.push('/tech-pharmacy/home');
    },
    
    // >>>>> MÉTODO CORRIGIDO E FUNCIONAL <<<<<
    renderMap() {
      if (this.farmacias.length === 0 || !window.google) {
        return;
      }

      // Pega a localização da primeira farmácia para centrar o mapa
      const centerLocation = this.farmacias[0].location;

      const mapElement = document.getElementById('pharmacy-map');
      if (!mapElement) return;

      // Cria a instância do mapa
      this.map = new google.maps.Map(mapElement, {
        center: centerLocation,
        zoom: 14, // Um bom zoom inicial
        mapId: 'TECH_PHARMACY_MAP', // Um ID para o mapa
      });
      
      const bounds = new google.maps.LatLngBounds();

      // Adiciona um marcador para cada farmácia
      this.farmacias.forEach(farmacia => {
        const marker = new google.maps.Marker({
          position: farmacia.location,
          map: this.map,
          title: farmacia.name,
        });
        // Estende os limites do mapa para incluir este marcador
        bounds.extend(marker.getPosition());
      });

      // Ajusta o zoom e o centro do mapa para mostrar todos os marcadores
      if (this.farmacias.length > 1) {
        this.map.fitBounds(bounds);
      }
    }
  },
  watch: {
    farmacias(newVal) {
      if (newVal && newVal.length > 0) {
        this.$nextTick(() => {
          this.renderMap();
        });
      }
    }
  },
  async mounted() {
    this.isPageLoading = true;
    
    if (this.medicamentos.length === 0) {
      await this.fetchMedicamentos();
    }

    await this.findNearbyPharmacies();
    
    this.isPageLoading = false;
  },
};
</script>

<style scoped>
#pharmacy-map {
  border-radius: 8px;
  overflow: hidden;
}
</style>