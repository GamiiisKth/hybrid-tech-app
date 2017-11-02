import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import TextField from 'material-ui/TextField';
import RaisedButton from 'material-ui/RaisedButton';
import elux from '../../style/img/elux.png';
import TargetEnvironment from './TargetEnvironment.jsx';

@inject('user', 'loginService')
@observer
class LoginComponent extends Component {


  componentWillMount() {
    this.handleSubmit = this.handleSubmit.bind(this);
    this.state = {
      isMountedAndCreatedByRouter: false
    };
  }

  handleSubmit(e) {
    e.preventDefault();
    this.props.loginService.login();
  }
  render() {
    const { user } = this.props;
    return (
      <div style={{ position: 'fixed', left: '0', top: '0', width: '100%', height: '100%', textAlign: 'center', backgroundColor: 'white', color: 'black' }}>
        <div style={{ position: 'relative', top: '50%', transform: 'translateY(-50%)' }}>
          <img src={elux} /><br />
          <TextField
            hintText="Hint Text"
            floatingLabelText="Email"
            onChange={e => user.setEmail(e.target.value)}
            errorText={user.message.email}
          />
          <br />
          <TextField
            hintText="Hint Text"
            type="password"
            floatingLabelText="Password"
            onChange={e => user.setPassword(e.target.value)}
            errorText={user.message.password}
          /><br />
          <TargetEnvironment />
          <RaisedButton label="Login" style={{ margin: '12' }} secondary onClick={e => this.handleSubmit(e)} />
        </div>
      </div>
    );
  }
}

export default LoginComponent;
