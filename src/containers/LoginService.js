import stateProvider from './StateProvider';
import {action} from 'mobx';
const hint = '1234';

const configObject = {
  baseUrl: 'https://api.eu.apiconnect.ibmcloud.com/electrolux-europe-dev/uat/',
  apiKey: 'dXNlcjpwYXNz',
  clientId: 'a3322242-c54b-4903-b52c-c5ab6dc71686',
  apiVersion: '1',
  isOfflineMode: 'false'
};


const tokenRequest = {
  "email": "angel1@test.com",
  "password": "Angel11111111",
  "countryName": "AU",
  "deviceId": "deviceId2"
};

const applianceNumber = {
  "elc":"00",
  "mac_address":"6014b31433e4",
  "pnc":"925060320",
  "serial_number":"73572472"
};

const applianceNumber2 = {
  "elc":"00",
  "mac_address":"6014B314345E",
  "pnc":"925060324",
  "serial_number":"35324026"
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

      mobilesdk.EcpUserManager.getSessionKeyAsync(tokenRequest,function (succ) {

        console.log("ECP getSessionKeyAsync "+JSON.stringify(succ));
        let successSessionKey= JSON.stringify(succ);
        stateProvider.user.setCreated(("getSessionKeyAsync :"+ successSessionKey))

        mobilesdk.EcpApplianceManager.getApplianceAsync(applianceNumber, function (succ) {
          console.log("ECP getApplianceAsync "+JSON.stringify(succ));
          let applianceAsync= JSON.stringify(succ);

          stateProvider.user.setAppliances(applianceAsync);


          mobilesdk.EcpRemoteMonitoringManager.subscribeAsync([3600, applianceNumber], function (succ) {

            stateProvider.user.setSubscribeAsync(succ);
            console.log("ECP subscribeAsync "+JSON.stringify(succ));

          },function (err) {
            console.log("ECP subscribeAsync "+JSON.stringify(err));
            let subscribeAsync= JSON.stringify(err);

            stateProvider.user.setSubscribeAsync(subscribeAsync);

          });

          mobilesdk.EcpApplianceStateMonitoringManager.subscribeApplianceStateAsync([3600, applianceNumber], function (succ) {

            if(!succ.includes("Successfully subscribed")){
              stateProvider.user.setSubscribeApplianceStateAsync(JSON.stringify(succ));
            }
            console.log("ECP subscribeApplianceStateAsync "+JSON.stringify(succ));

          },function (err) {
            console.log("ECP subscribeApplianceStateAsync "+JSON.stringify(err));
            let subscribeApplianceStateAsync= JSON.stringify(err);
            stateProvider.user.setSubscribeApplianceStateAsync(JSON.stringify(succ));
          });

        },function (err) {

          console.log("ECP getApplianceAsync "+JSON.stringify(err));
          let applianceAsync= JSON.stringify(err);
          stateProvider.user.setAppliances(applianceAsync)

        });

      },function (err) {
        console.log("ECP getSessionKeyAsync "+JSON.stringify(err));
        let successSessionKey= JSON.stringify(err);
        stateProvider.user.setCreated(("getSessionKeyAsync :"+ successSessionKey))
      });


      mobilesdk.EcpConfigurationManager.getConfigurationProfile(applianceNumber, function(succ){
        console.log("getConfigurationProfile........");
        var res = JSON.parse(succ);
        console.log("getConfigurationProfile"  +  JSON.stringify(succ));

        stateProvider.user.setConfigurationProfile(JSON.stringify(succ))

      }, function(err){
        console.log("getConfigurationProfile" + JSON.stringify(err));
        stateProvider.user.setConfigurationProfile(JSON.stringify(err))
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
    if(!stateProvider.user.appState === 'upToDate'){
      return;
    }
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
