import React from 'react'
import { inject, observer } from 'mobx-react'

@inject('user', 'loginService')
@observer
export default class ApplianceView extends React.Component {
  componentDidMount () {
    this.props.loginService.login()
  }

  render () {
    const {user} = this.props
    return (
      <div>
        <h4>{user.appState}</h4>

        <h3>getSessionKeyAsync</h3>
        <h6>{user.created}</h6>

        <h3>getApplianceAsync</h3>
        <h6>{user.appliances}</h6>

        <h3>subscribeAsync</h3>
        <h6>{user.subscribeAsync}</h6>

        <h3>subscribeApplianceStateAsync</h3>
        <h6>{user.subscribeApplianceStateAsync}</h6>
      </div>
    )
  }
}
