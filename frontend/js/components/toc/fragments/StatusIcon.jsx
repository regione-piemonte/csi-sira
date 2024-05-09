/**
 * Copyright 2015, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require('react');
const {Glyphicon} = require('react-bootstrap');
const PropTypes = require('prop-types');

class StatusIcon extends React.Component {
    static propTypes = {
        node: PropTypes.object,
        onClick: PropTypes.func
    };

    static inheritedPropTypes = ['node', 'expanded'];

    static defaultProps = {
        node: null,
        onClick: () => {}
    };

    render() {
        let expanded = (this.props.node.expanded !== undefined) ? this.props.node.expanded : true;
        return (
            <Glyphicon tabIndex="0" style={{marginRight: "8px"}} glyph={expanded ? "chevron-up" : "chevron-down"} />
        );
    }
}

module.exports = StatusIcon;
