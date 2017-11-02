import user from '../models/User';
import loginService from './LoginService';

const stateProvider = {
  user,
  loginService
};

export default stateProvider;
