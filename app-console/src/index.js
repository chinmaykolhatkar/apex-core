import React from 'react'
import ReactDOM from 'react-dom'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import AppBar from 'material-ui/AppBar';
import { Tabs, Tab, SelectField, MenuItem, Avatar, SvgIcon } from 'material-ui';
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
    }

    onTabChange = (value) => {
        this.setState({currentTab : value, });
    }

    onFrequencyChange = (event, index, value) => {
        this.setState({frequency : value, })
        clearTimeout(this.timerId)
        this.timerId = setTimeout(this.handleRefresh, value);
    }

    handleRefresh = () => {
        
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
                <div><GenericInfo /></div>
                <div><LogicalPlan /></div>
                <div><PhysicalPlan /></div>
            </SwipeableViews>
            </MuiThemeProvider>
        )
    }
}

ReactDOM.render(
    <Console />,
    document.getElementById("root")
)
