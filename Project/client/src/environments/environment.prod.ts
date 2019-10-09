const remoteAddress = 'http://localhost:8080';

export const environment = {
  production: true,
  baseEndpoint: remoteAddress,
  baseWsEndpoint: remoteAddress+'/ws'
};
