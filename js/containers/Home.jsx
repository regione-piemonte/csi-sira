/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
const React = require('react');
const Debug = require('../../MapStore2/web/client/components/development/Debug');
const {connect} = require('react-redux');

const Localized = require('../../MapStore2/web/client/components/I18N/Localized');

const {Link} = require('react-router');

const {Glyphicon} = require('react-bootstrap');
const SearchBar = require('../../MapStore2/web/client/components/Search/SearchBar');

const Home = (props) => (
    <Localized messages={props.messages} locale={props.locale}>
        <div>
            <div className="homepage">
                <div className="header">
                    <div className="header-text">
                        Sistema della conoscenza dell'Ambiente
                    </div>
                    <div className="header-burger">
                        <Glyphicon glyph="glyphicon glyphicon-menu-hamburger"/>
                    </div>
                </div>
                <div className="header-2">
                    <p>
                        <span>
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit. In at urna lobortis libero viverra elementum et et nunc. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Ut a felis sit amet libero ultricies elementum a vitae quam...... scopri di più
                        </span>
                    </p>
                </div>
                <div className="header-3">
                    <div className="header-4">
                        <h2 className="search-title">Cerca</h2>
                        <SearchBar
                            className="siraSearchBar"
                            placeholder="Cerca dataset, applicazioni tematiche"/>
                    </div>
                </div>
                <div className="header-5">
                    <div className="box-left">
                        <Link to="/map/A">
                            <p><span style={{"color": "rgb(66, 66, 66)", "font-size": "24px", "font-weight": "bold"}}>AUA</span></p>
                            <p><span style={{"color": "#808080", "font-size": "16px"}}>PROFILO A</span></p>
                        </Link>
                    </div>
                    <div className="box-right">
                        <Link to="/map/B">
                            <p><span style={{"color": "rgb(66, 66, 66)", "font-size": "24px", "font-weight": "bold"}}>AUA</span></p>
                            <p><span style={{"color": "#808080", "font-size": "16px"}}>PROFILO B</span></p>
                        </Link>
                    </div>
                    <img className="header-5-bg" src="/assets/img/u0.png"></img>
                </div>
                <div className="main-home"></div>
            </div>
            <Debug/>
        </div>
    </Localized>
);

module.exports = connect((state) => {
    return {
        error: state.loadingError || (state.locale && state.locale.localeError) || null,
        locale: state.locale && state.locale.locale,
        messages: state.locale && state.locale.messages || {}
    };
})(Home);
