import React from 'react'
import ReactDOM from 'react-dom'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import AppBar from 'material-ui/AppBar';
import { Tabs, Tab, SelectField, MenuItem } from 'material-ui';
import SwipeableViews from 'react-swipeable-views';
import GenericInfo from './generic'
import LogicalPlan from './logical'
import PhysicalPlan from './physical'
import { getTheme } from './theme.js'
import logo from './logo.svg'

class Console extends React.Component {
    constructor(props) {
        super(props)
        this.state = {currentTab : 0, frequency : 0}
        this.references = {0 : React.createRef(), 1 : React.createRef(), 2 : React.createRef()}
    }

    onTabChange = (value) => {
        this.setState({currentTab : value, });
        // State change does not happen immediately. Hence calling handleRefresh after a timeout.
        setTimeout(this.handleRefresh, 50)
    }

    onFrequencyChange = (event, index, value) => {
        this.setState({frequency : value, })
        clearInterval(this.timerId)
        this.timerId = setInterval(this.handleRefresh, value * 1000);
    }

    handleRefresh = () => {
        this.references[this.state.currentTab].current.updateState()
    }

    render() {
        return (
            <MuiThemeProvider muiTheme={getTheme()} >
            <AppBar 
                title="Apache Apex Application Information" 
                iconElementLeft={<img src={logo} className="App-logo" alt="logo" />}>
                <SelectField
                    floatingLabelText="Update Frequency"
                    value={this.state.frequency}
                    onChange={this.onFrequencyChange} >
                        <MenuItem value={0} primaryText="Never" />
                        <MenuItem value={5} primaryText="5 sec" />
                        <MenuItem value={10} primaryText="10 sec" />
                        <MenuItem value={30} primaryText="30 sec" />
                        <MenuItem value={60} primaryText="60 sec" />
                </SelectField>
            </AppBar>
            <Tabs value={this.state.currentTab} onChange={this.onTabChange}>
                <Tab label={<b>Generic Information</b>} value={0} />
                <Tab label={<b>Logical Plan</b>} value={1} />
                <Tab label={<b>Physical Plan</b>} value={2} />
            </Tabs>

            <SwipeableViews
                index={this.state.currentTab}
                onChangeIndex={this.onTabChange}>
                <div><GenericInfo ref={this.references[0]}/></div>
                <div><LogicalPlan ref={this.references[1]}/></div>
                <div><PhysicalPlan ref={this.references[2]}/></div>
            </SwipeableViews>
            </MuiThemeProvider>
        )
    }
}

ReactDOM.render(
    <Console />,
    document.getElementById("root")
)
