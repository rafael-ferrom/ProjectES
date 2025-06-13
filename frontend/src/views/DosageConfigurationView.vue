<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="10" md="8" lg="7" xl="6" class="pt-16">
        <div v-if="loading" class="text-center pa-12">
          <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
          <p class="mt-4">Carregando detalhes do medicamento...</p>
        </div>

        <v-alert v-else-if="error" type="error" border="left" colored-border prominent>
          <div class="text-h6">Erro ao carregar medicamento</div>
          Não foi possível carregar os detalhes do medicamento solicitado. Por favor, tente novamente.
        </v-alert>

        <template v-else-if="medicationDetails">
          <v-card>
            <v-card-title class="headline font-weight-bold primary--text text-center d-block pt-4 pb-2">
              Configurar Tratamento: {{ medicationDetails.nomeComercial }}
            </v-card-title>
            
            <v-card-text class="text-center mb-4 text-body-1">
              Princípio Ativo: {{ medicationDetails.principioAtivo }} ({{ medicationDetails.concentracao }})
            </v-card-text>

            <v-divider></v-divider>

            <v-form ref="configForm" v-model="isFormValid">
              <v-card-text>
                <h3 class="text-h6 font-weight-medium mb-4 primary--text">Detalhes do Tratamento</h3>
                <v-row>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="config.nome"
                      label="Nome do Medicamento (para sua lista)"
                      outlined dense
                      :rules="[rules.required]"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="config.dosagem"
                      label="Dosagem"
                      outlined dense
                      :rules="[rules.required]"
                    ></v-text-field>
                  </v-col>
                </v-row>

                <h3 class="text-h6 font-weight-medium mb-4 mt-2 primary--text">Frequência e Duração</h3>
                <v-row>
                   <v-col cols="12" sm="6">
                     <v-menu v-model="dateMenus.start" :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto">
                       <template v-slot:activator="{ on, attrs }">
                         <v-text-field v-model="config.dataInicio" label="Data de Início" prepend-icon="mdi-calendar" readonly v-bind="attrs" v-on="on" outlined dense :rules="[rules.required]"></v-text-field>
                       </template>
                       <v-date-picker v-model="config.dataInicio" @input="dateMenus.start = false"></v-date-picker>
                     </v-menu>
                   </v-col>
                   <v-col cols="12" sm="6">
                      <v-text-field v-model="config.duracaoDias" label="Duração do tratamento (dias)" type="number" outlined dense :rules="[rules.required, rules.positiveNumber]" min="1"></v-text-field>
                   </v-col>
                </v-row>
                <v-select v-model="config.vezesPorDia" :items="frequencyOptions" item-text="text" item-value="value" label="Doses por dia" outlined dense class="mb-3" :rules="[rules.required]"></v-select>

                <h3 class="text-h6 font-weight-medium mb-4 mt-2 primary--text">Instruções</h3>
                <v-textarea v-model="config.instrucoes" label="Instruções pessoais (opcional)" outlined dense rows="3" placeholder="Ex.: Tomar com comida, evitar luz solar..."></v-textarea>
              </v-card-text>

              <v-card-actions class="pa-4">
                <v-spacer></v-spacer>
                <v-btn color="success" large @click="submitConfiguration" :loading="submitting" :disabled="!isFormValid">
                  <v-icon left>mdi-content-save</v-icon>
                  Salvar Tratamento
                </v-btn>
              </v-card-actions>
            </v-form>
          </v-card>
        </template>
      </v-col>
    </v-row>
     <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="4000">
        {{ snackbar.text }}
     </v-snackbar>
  </v-container>
</template>

<script>
import { useMedicationStore } from "@/store/index.js";
import { mapActions } from 'pinia';

export default {
  name: 'ConfigureMedicationView',
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      loading: true,
      submitting: false,
      error: false,
      isFormValid: false,
      medicationDetails: null,
      config: {
        nome: '',
        dosagem: '',
        dataInicio: new Date().toISOString().substr(0, 10),
        duracaoDias: 7,
        vezesPorDia: null,
        instrucoes: '', 
      },
      dateMenus: {
        start: false,
      },
      frequencyOptions: [
        { text: '1 vez por dia', value: 1 },
        { text: '2 vezes por dia', value: 2 },
        { text: '3 vezes por dia', value: 3 },
        { text: '4 vezes por dia', value: 4 },
        { text: '6 vezes por dia', value: 6 },
      ],
      rules: {
        required: value => !!value || 'Este campo é obrigatório.',
        positiveNumber: value => (value === null || value === '' || parseFloat(value) > 0) || 'O valor deve ser maior que zero.',
      },
      snackbar: {
        show: false,
        text: '',
        color: 'success'
      }
    };
  },
  async created() {
    this.medicationDetails = await this.fetchMedicationById(this.id);
    if (this.medicationDetails) {
      this.config.nome = this.medicationDetails.nomeComercial;
      this.config.dosagem = this.medicationDetails.concentracao;

      if (this.medicationDetails.instrucoes && this.medicationDetails.instrucoes.length > 0) {
        this.config.instrucoes = this.medicationDetails.instrucoes.join('\n');
      }

    } else {
      this.error = true;
    }
    this.loading = false;
  },
  methods: {
    ...mapActions(useMedicationStore, ['fetchMedicationById', 'addMedicationConfiguration']),
    
    async submitConfiguration() {
      this.$refs.configForm.validate();
      if (!this.isFormValid) return;

      this.submitting = true;

      const startDate = new Date(this.config.dataInicio);
      const endDate = new Date(startDate.getTime());
      endDate.setDate(endDate.getDate() + parseInt(this.config.duracaoDias, 10));

      const payload = {
        nome: this.config.nome,
        dosagem: this.config.dosagem,
        tipo: this.medicationDetails.formaFarmaceutica.toLowerCase(),
        dataInicio: this.config.dataInicio,
        dataTermino: endDate.toISOString().substr(0, 10),
        vezesPorDia: this.config.vezesPorDia,
        instrucoes: this.config.instrucoes.split('\n').filter(line => line.trim() !== ''),
        bulaId: parseInt(this.id, 10)
      };

      try {
        await this.addMedicationConfiguration(payload);
        this.snackbar.text = 'Tratamento salvo com sucesso!';
        this.snackbar.color = 'success';
        this.snackbar.show = true;
        setTimeout(() => this.$router.push({ name: 'home' }), 1500);

      } catch (error) {
        console.error("Erro ao salvar tratamento:", error);
        this.snackbar.text = 'Erro ao salvar tratamento. Tente novamente.';
        this.snackbar.color = 'error';
        this.snackbar.show = true;
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>
