import { action } from 'mobx';
import stateProvider from './StateProvider';

const mobilesdk = require('../../platforms/android/platform_www/plugins/ecp-plugin/www/mobilesdk');
 require('../../platforms/android/platform_www/cordova');
 require('../../platforms/android/platform_www/cordova_plugins');
const hint = 'hejhej1255521';

const configObject = {
  baseUrl: 'https://api.eu.apiconnect.ibmcloud.com/electrolux-europe-dev/uat/',
  apiKey: 'dXNlcjpwYXNz',
  clientId: 'a3322242-c54b-4903-b52c-c5ab6dc71686',
  apiVersion: '1',
  isOfflineMode: 'false'
};

class LoginService {

  @action
  executeLogin = () => {
    cordov
    return new Promise((resolve, reject) => {
      const { password } = stateProvider.user;
      // TODO call store to send the call to
      mobilesdk.EcpConfiguration.create(configObject, sucess => stateProvider.user.setToken(sucess), failed => stateProvider.user.setToken(failed));
      console.log(stateProvider.user.token);
      if (password === hint) {
        resolve(true);
      } else {
        stateProvider.user.setMessage({ email: 'please check email address', password: 'please check password' });
        reject(true);
      }
    });
  };

  @action
  login() {
    const loginAttempt = this.executeLogin();
    loginAttempt.then(resolve => {
      // TODO fix the routing to main page
      console.log('success ' + resolve);
      const token = Math.random().toString(36).substring(7);
      const time = Date.now();
      stateProvider.user.setToken(token);
      stateProvider.user.setTime(time);
      stateProvider.user.setAuthenticated(true);
      localStorage.setItem('authenticated', stateProvider.user.authenticated);
    }).catch(rejected => {
      console.log('failed ' + rejected);
    });
  }
}

const loginService = new LoginService();
export default loginService;
