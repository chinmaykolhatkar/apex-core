import React from 'react'

export default class GenericInfo extends React.Component {
    constructor(props) {
        super(props)
        this.state = {date : new Date()}
    }

    updateState = () => {
        this.setState({date : new Date()})
    }

    render() {
        return (
            <p>Generic Information : {this.state.date.toLocaleTimeString()}</p>
        )
    }
}