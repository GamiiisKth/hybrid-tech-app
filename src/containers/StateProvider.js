import user from '../models/User';
import loginService from './LoginService';
import Main from '../components/Main.jsx';

const stateProvider = {
  user,
  loginService,
};

export default stateProvider;
