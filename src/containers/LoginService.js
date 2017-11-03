import stateProvider from './StateProvider';
import {action} from 'mobx';
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
    return new Promise((resolve, reject) => {
      const { password } = stateProvider.user;
      // TODO call store to send the call to

      mobilesdk.EcpConfiguration.create(configObject, function(succ){
        console.log("ECP succ"+JSON.stringify(succ));
        stateProvider.user.setCreated("succ")
      }, function(err){
        console.log("ECP err"+JSON.stringify(err));
        stateProvider.user.setCreated("err")
      });

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
      stateProvider.user.setToken(stateProvider.user.token);
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
