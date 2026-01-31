FROM registry.access.redhat.com/ubi9/nodejs-18

WORKDIR /opt/app-root/src

COPY package.json ./
RUN npm install --omit=dev

COPY . .

ENV PORT=9080
EXPOSE 9080

CMD ["npm", "start"]

