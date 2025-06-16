<template>
  <v-container class="pt-8">
    <v-row class="d-flex pb-8">
      <v-col class="d-flex justify-center text-h3 font-weight-light text-center primary--text">
        Meu Estoque de Medicamentos
      </v-col>
    </v-row>

    <v-row v-if="loading" justify="center" class="pa-12">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </v-row>

    <v-row v-else-if="Object.keys(groupedStock).length === 0" justify="center">
      <v-col cols="12" md="8">
        <v-alert type="info" prominent border="left" colored-border color="primary">
          Você ainda não possui nenhum medicamento em seu estoque.
          <router-link :to="{ name: 'home' }">Compre um agora!</router-link>
        </v-alert>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col cols="12" md="10" lg="8" class="mx-auto">
        <v-card v-for="(group, bulaId) in groupedStock" :key="bulaId" class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-avatar tile size="40" class="mr-4">
                <v-img :src="group.bula.fotoUrl" contain></v-img>
            </v-avatar>
            <span class="text-h6 font-weight-regular">{{ group.bula.nomeComercial }} - {{ group.bula.concentracao }}</span>
          </v-card-title>
          <v-divider></v-divider>
          
          <v-data-table
            :headers="headers"
            :items="group.items"
            hide-default-footer
            class="elevation-0"
            :items-per-page="-1"
          >
            <template slot="item.quantidadeComprimidos" slot-scope="{ item }">
              <v-chip :color="getQtdChipColor(item)">
                {{ item.quantidadeComprimidos }} / {{ item.quantidadeInicialComprimidos }}
              </v-chip>
            </template>
            
            <template slot="item.dataValidade" slot-scope="{ item }">
              <strong :class="getExpiryColor(item.dataValidade)">
                {{ formatDate(item.dataValidade) }}
              </strong>
            </template>

            <template slot="item.status" slot-scope="{ item }">
                <v-chip small :color="getStatusColor(item.status)">
                    {{ item.status }}
                </v-chip>
            </template>

            <template slot="item.dataAquisicao" slot-scope="{ item }">
              {{ formatDate(item.dataAquisicao) }}
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { useMedicationStore } from "@/store";
import { mapState, mapActions } from "pinia";

export default {
  name: 'InventoryView',
  data() {
    return {
      loading: true,
      headers: [
        { text: 'Comprimidos Restantes', value: 'quantidadeComprimidos', align: 'center' },
        { text: 'Data de Validade', value: 'dataValidade', sortable: true },
        { text: 'Status', value: 'status', align: 'center', sortable: true },
        { text: 'Data da Compra', value: 'dataAquisicao', sortable: true },
      ],
    };
  },
  computed: {
    ...mapState(useMedicationStore, ['userStock']),
    groupedStock() {
      if (!this.userStock || this.userStock.length === 0) {
        return {};
      }
      return this.userStock.reduce((acc, item) => {
        const bulaId = item.bula.id;
        if (!acc[bulaId]) {
          acc[bulaId] = {
            bula: item.bula,
            items: [],
          };
        }
        acc[bulaId].items.push(item);
        acc[bulaId].items.sort((a, b) => new Date(a.dataValidade) - new Date(b.dataValidade));
        return acc;
      }, {});
    },
  },
  methods: {
    ...mapActions(useMedicationStore, ['fetchUserStock']),
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      const date = new Date(dateString + 'T00:00:00Z');

      return new Intl.DateTimeFormat('pt-BR', {
        timeZone: 'America/Sao_Paulo',
        year: 'numeric', month: 'long', day: 'numeric'
      }).format(date);
    },
    getExpiryColor(dateString) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const expiryDate = new Date(dateString + 'T00:00:00');
      const diffTime = expiryDate - today;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays < 0) return 'red--text text--darken-2';
      if (diffDays <= 30) return 'orange--text text--darken-2';
      return 'green--text text--darken-2';
    },
    getQtdChipColor(item) {
        if (item.quantidadeComprimidos === 0) return 'grey';
        if (item.quantidadeComprimidos < item.quantidadeInicialComprimidos / 2) return 'orange';
        return 'green';
    },
    getStatusColor(status) {
        switch (status) {
            case 'DISPONIVEL': return 'success';
            case 'VAZIO': return 'grey';
            case 'VENCIDO': return 'error';
            default: return 'primary';
        }
    }
  },
  async mounted() {
    this.loading = true;
    await this.fetchUserStock();
    this.loading = false;
  }
};
</script>

<style scoped>
.v-data-table {
  background-color: transparent !important;
}
</style>