import React from 'react'

export default class PhysicalPlan extends React.Component {
    constructor(props) {
        super(props)
        this.state = {date : new Date()}
    }

    updateState = () => {
        this.setState({date : new Date()})
    }

    render() {
        return (
            <p>Physical Plan : {this.state.date.toLocaleTimeString()}</p>
        )
    }
}