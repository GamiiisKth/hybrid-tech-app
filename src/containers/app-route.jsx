import React, { Component } from 'react';
import { Router, Route, hashHistory as history } from 'react-router';
import { observer, inject } from 'mobx-react';
import Login from '../components/authentication/login.jsx';
import Main from '../components/Main.jsx';
import Test from '../components/Test.jsx';

@inject('user')
@observer
export default class AppRouteComponent extends Component {
  componentWillMount() {
    this.checkAuth = this.checkAuth.bind(this);
    this.handleRedirect = this.handleRedirect.bind(this);
  }
  checkAuth(nextState, replace) {
    if (!this.props.user.authenticated) {
      replace('/login');
    }
  }

  handleRedirect(nextState, replace) {
    replace(this.props.user.authenticated ? '/main' : '/login');
  }

  render() {
    if (!this.props.user.authenticated) {
      return (<Login />);
    }

    return (
      <Router history={history}>
        <Route path="/main" component={Main} onEnter={this.checkAuth} />
        <Route path="/login" component={Login} />
        <Route path="/test" component={Test} />
        <Route path="*" onEnter={this.handleRedirect} />
      </Router>
    );
  }
}