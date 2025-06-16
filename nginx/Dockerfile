# Estágio 1: Build do Frontend Vue.js
FROM node:18-alpine AS vue-builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# Estágio 2: Configuração Final do Nginx
FROM nginx:stable-alpine
RUN rm /etc/nginx/conf.d/default.conf

# >>>>> ALTERAÇÃO PRINCIPAL AQUI <<<<<
# Especifique o caminho completo a partir da raiz do projeto.
COPY nginx/nginx.conf /etc/nginx/conf.d/

COPY --from=vue-builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]