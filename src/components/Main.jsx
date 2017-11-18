import React from 'react'
import { inject, observer } from 'mobx-react'
import CircularProgress from 'material-ui/CircularProgress'
import ApplianceView from './appliances/ApplianceView.jsx'

@inject('user', 'loginService')
@observer
export default class Main extends React.Component {
  render () {
    const {user, loginService} = this.props
    window.codePush.sync(
      function (syncStatus) {
        switch (syncStatus) {
          // Result (final) statuses
          case SyncStatus.UPDATE_INSTALLED:
            user.setAppState('The update was installed successfully. For InstallMode.ON_NEXT_RESTART, the changes will be visible after application restart. ')
            user.setAppState('upToDate')
            break
          case SyncStatus.UP_TO_DATE:
            user.setAppState('upToDate')
            break
          case SyncStatus.UPDATE_IGNORED:
            user.setAppState('ignoredUpdate')
            break
          case SyncStatus.ERROR:
            user.setAppState('errorOccurredForUpdates')
            break

          // Intermediate (non final) statuses
          case SyncStatus.CHECKING_FOR_UPDATE:
            break
          case SyncStatus.AWAITING_USER_ACTION:
            console.log('Alerting user.')
            break
          case SyncStatus.DOWNLOADING_PACKAGE:
            console.log('Downloading package.')
            user.setAppState('downloading')

            break
          case SyncStatus.INSTALLING_UPDATE:
            console.log('Installing update')
            user.setAppState('installing')
            break
        }
      },
      {
        installMode: InstallMode.IMMEDIATE, updateDialog: true
      },
      function (downloadProgress) {
        console.log('Downloading ' + downloadProgress.receivedBytes + ' of ' + downloadProgress.totalBytes + ' bytes.')
      })

    if (user.appState !== 'upToDate' || user.appState === 'errorOccurredForUpdates'|| user.appState === 'ignoredUpdate') {
      return (
        <div>
          <CircularProgress/>
           {user.appState}
        </div>
      )
    } else {
      return (<ApplianceView/>)
    }

  }
}