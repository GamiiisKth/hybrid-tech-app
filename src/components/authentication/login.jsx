import React, { Component } from 'react'
import { inject, observer } from 'mobx-react'
import TextField from 'material-ui/TextField'
import RaisedButton from 'material-ui/RaisedButton'
import elux from '../../style/img/rsz_1rsz_elux-fallback.png'
import TargetEnvironment from './TargetEnvironment.jsx'
import Main from '../Main.jsx'

@inject('user')
@observer
class LoginComponent extends Component {
  componentWillMount () {
    this.handleSubmit = this.handleSubmit.bind(this)
    this.state = {
      isMountedAndCreatedByRouter: false
    }
  }

  handleSubmit (e) {
    e.preventDefault()
    this.setState({redirect: true})
  }

  render () {
    const {user} = this.props
    if (this.state.redirect) {
      return <Main/>
    }
    return (
      <div style={{
        position: 'fixed',
        left: '0',
        top: '0',
        width: '100%',
        height: '100%',
        textAlign: 'center',
        backgroundColor: '#131E51',
        color: 'white'
      }}>
        <div style={{position: 'relative', top: '50%', transform: 'translateY(-50%)'}}>
          <img src={elux}/>
          <br/>
          <TextField
            floatingLabelStyle={{color: '#ffffff'}}
            inputStyle={{fontColor: '#ffffff', color: '#ffffff'}}
            hintText="Enter your email"
            hintStyle={{color: '#ffffff'}}
            floatingLabelText="Email"
            onChange={(e) => user.setEmail(e.target.value)}
            errorText={user.message.email}
          />
          <br/>
          <TextField
            floatingLabelStyle={{color: '#ffffff'}}
            inputStyle={{fontColor: '#ffffff', color: '#ffffff'}}
            hintText="Enter your password"
            hintStyle={{color: '#ffffff'}}
            type="password"
            floatingLabelText="Password"
            onChange={(e) => user.setPassword(e.target.value)}
            errorText={user.message.password}
          />
          <br/>
          <TargetEnvironment/>
          <RaisedButton label="Login" style={{margin: '12'}} secondary onClick={(e) => this.handleSubmit(e)}/>

        </div>
      </div>

    )
  }
}

export default LoginComponent
