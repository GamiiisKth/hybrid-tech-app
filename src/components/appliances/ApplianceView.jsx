import React from 'react';
import { observer, inject } from 'mobx-react';

@inject('user')
@observer
export default class ApplianceView extends React.Component {



  render(){
    return (
      <h1>Hello</h1>
    )
  }
}