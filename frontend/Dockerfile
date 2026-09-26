# --- Étape 1 : build Angular ---
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# --- Étape 2 : servir avec nginx ---
FROM nginx:alpine
# Angular 22 (esbuild builder) génère dist/<nom-projet>/browser
# Si ton build sort directement dans dist/<nom-projet> (sans /browser), adapte la ligne ci-dessous.
COPY --from=build /app/dist/*/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
