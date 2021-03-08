FROM node:lts-buster

WORKDIR /usr/src/app/

COPY package.json /usr/src/app/

RUN npm install

COPY server.js /usr/src/app/
COPY views/index.html /usr/src/app/views/

EXPOSE 5000

CMD ["npm", "start"]
