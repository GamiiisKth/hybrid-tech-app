import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';

@inject('user', 'loginService')
@observer
export default class Main extends React.Component {

  render() {
    const { user }= this.props;
    return (
      <div>
      <h1>{user.created}</h1>
      <h1>{user.appliances}</h1>
        <h2>---------------subscribeAsync--------------</h2>
        <h3>{user.subscribeAsync}</h3>
       <h2>---------------subscribeApplianceStateAsync--------------</h2>
        <h6>
          {user.subscribeApplianceStateAsync}
        </h6>
      </div>
    );
  }
}
