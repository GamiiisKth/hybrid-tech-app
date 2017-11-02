import React from 'react';
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from 'material-ui/RaisedButton';
import { RadioButton, RadioButtonGroup } from 'material-ui/RadioButton';

const styles = {
  radioButton: {
    marginTop: 16
  }
};
export default class TargetEnvironment extends React.Component {
  state = {
    open: false,
    label: 'choose env'
  };

  handleOpen = () => {
    this.setState({ open: true });
  };

  handleClose = () => {
    this.setState({ open: false });
  };

  handleSelectedRadio= value => {
    this.setState({ label: value });
  };

  render() {
    const actions = [
      <FlatButton
        label="Cancel"
        primary
        onClick={this.handleClose}
      />,
      <FlatButton
        label="Submit"
        primary
        keyboardFocused
        onClick={this.handleClose}
      />
    ];
    // TODO import user targetEnvironment and replace int with radios
    const radios = [];
    for (let i = 0; i < 30; i++) {
      radios.push(
        <RadioButton
          key={i}
          value={`value${i + 1}`}
          label={`Option ${i + 1}`}
          style={styles.radioButton}
        />
      );
    }

    return (
      // TODO change label to choosen env from list {}
      <div>
        <RaisedButton label={this.state.label} onClick={this.handleOpen} />
        <Dialog
          titleStyle={{background: 'cyan' }}
          title="Available environment"
          actions={actions}
          modal={false}
          open={this.state.open}
          onRequestClose={this.handleClose}
          autoScrollBodyContent
        >
          <RadioButtonGroup name="shipSpeed" defaultSelected="not_light" onChange={event => this.handleSelectedRadio(event.target.value)}>
            {radios}
          </RadioButtonGroup>
        </Dialog>
      </div>
    );
  }
}
