import { action, observable } from 'mobx';

class User {

  @observable email;
  @observable password;
  @observable authenticated;
  @observable token;
  @observable time;
  @observable message= {
    email: '',
    password: '',
  };

  @observable targetEnvironment = [];
  @observable created;
  @observable appliances;
  @observable subscribeAsync;
  @observable subscribeApplianceStateAsync;
  @observable configurationProfile;

  @action
  setEmail(email) {
    let current = email;
    if (current !== this.email && this.message.email) {
      this.setMessage({ email: '', password: '' });
    }
    this.email = email;
    current = '';
  }

  @action
  setPassword(password) {
    let current = password;
    if (current !== this.password && this.message.password) {
      this.setMessage({ email: '', password: '' });
    }
    this.password = password;
    current = '';
  }

  @action
  setAuthenticated(authenticated) {
    this.authenticated = authenticated;
  }

  @action
  setToken(token) {
    this.token = token;
  }

  @action
  setTime(time) {
    this.time = time;
  }

  @action
  setMessage(message) {
    this.message = message;
  }

  @action
  setCreated(created) {
    this.created = created;
  }

  @action
  setAppliances(appliances) {
    this.appliances = appliances;
  }

  @action
  setSubscribeAsync(subscribeAsync) {
    this.subscribeAsync = subscribeAsync;
  }

  @action
  setSubscribeApplianceStateAsync(subscribeApplianceStateAsync) {
    this.subscribeApplianceStateAsync = subscribeApplianceStateAsync;
  }

  @action
  setConfigurationProfile(configurationProfile) {
    this.configurationProfile = configurationProfile;
  }
}

const user = new User();
export default user;
