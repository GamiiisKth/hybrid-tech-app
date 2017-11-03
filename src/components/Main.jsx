import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';

@inject('user', 'loginService')
@observer
export default class Main extends React.Component {

  render() {
    const { user }= this.props;
    return (
      <h1>{user.created}</h1>
    );
  }
}
