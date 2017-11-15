import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import CircularProgress from 'material-ui/CircularProgress';
import ApplianceView from './appliances/ApplianceView.jsx';

@inject('user')
@observer
export default class Main extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      completed: 0,
    };
  }


  inProgress=(downloadProgress)=>{
    if (downloadProgress.receivedBytes === downloadProgress.totalBytes) {
      this.setState({completed: 100});
    } else {
      let completed= (downloadProgress.receivedBytes /  downloadProgress.totalBytes) * 10;
      this.setState({completed});
    }
  };


  render() {
    const { user }= this.props;
    window.codePush.sync(
      function (syncStatus) {
        switch (syncStatus) {
          // Result (final) statuses
          case SyncStatus.UPDATE_INSTALLED:
            user.setAppState("The update was installed successfully. For InstallMode.ON_NEXT_RESTART, the changes will be visible after application restart. ");
            break;
          case SyncStatus.UP_TO_DATE:
            user.setAppState("upToDate");
            break;
          case SyncStatus.UPDATE_IGNORED:
            user.setAppState("The user decided not to install the optional update.");
            break;
          case SyncStatus.ERROR:
            user.setAppState("errorOccurredForUpdates");
            break;

          // Intermediate (non final) statuses
          case SyncStatus.CHECKING_FOR_UPDATE:
            console.log("Checking for update.");
            break;
          case SyncStatus.AWAITING_USER_ACTION:
            console.log("Alerting user.");
            break;
          case SyncStatus.DOWNLOADING_PACKAGE:
            console.log("Downloading package.");
            user.setAppState("downloading");

            break;
          case SyncStatus.INSTALLING_UPDATE:
            console.log("Installing update");
            user.setAppState("installing");
            break;
        }
      },
      {
        installMode: InstallMode.IMMEDIATE,updateDialog: true
      },
      function (downloadProgress) {
        console.log("Downloading " + downloadProgress.receivedBytes + " of " + downloadProgress.totalBytes + " bytes.");
        this.inProgress(downloadProgress);
        if(downloadProgress.receivedBytes === downloadProgress.totalBytes ){
          user.setAppState('upToDate')
        }
      });

return(
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
