# 构建阶段
FROM node:20-alpine AS build-stage
WORKDIR /app
COPY . .
RUN npm install && npm run build

# 部署阶段
FROM nginx:stable-alpine AS production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]