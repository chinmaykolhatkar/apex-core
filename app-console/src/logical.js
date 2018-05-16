import React from 'react'

export default class LogicalPlan extends React.Component {
    constructor(props) {
        super(props)
        this.state = {date : new Date()}
    }

    updateState = () => {
        this.setState({date : new Date()})
    }

    render() {
        return (
            <p>Logical Plan : {this.state.date.toLocaleTimeString()}</p>
        )
    }
}