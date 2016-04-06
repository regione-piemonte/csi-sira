/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require('react');

const MapHeader = React.createClass({
    render() {
        return (
            <div id="header-servizio" className="container-fluid">
                <div className="row-fluid">
                    <div className="container">
                        <div className="row">
                            <div id="portalHeader">
                                <header className="wrap navbar navbar-default" id="t3-mainnav">
                                    <div className="header-section">
                                        <div className="navbar-header">
                                            <div className="col-lg-1 col-md-1 col-sm-1 col-xs-1 spiemonte">
                                                <h1><a href="http://www.sistemapiemonte.it/cms/privati/" title="Home
                                                    page Sistemapiemonte">SP</a></h1>
                                            </div>
                                            <div className="col-lg-8 col-md-8 col-sm-8 col-xs-8 titolo-interna"><h2>Siradec</h2></div>
                                            <div className="col-lg-2 col-md-2 col-sm-2 col-xs-2">
                                                <div data-toggle="buttons" className="btn-group map-list">
                                                    <label className="btn btn-primary ">
                                                        <input type="radio" checked="" autoComplete="off" id="option1"
                                                            name="options"/> Lista
                                                    </label>
                                                    <label className="btn btn-primary active">
                                                        <input type="radio" autoComplete="off" id="option2" name="options"/> Mappa
                                                        <span className="badge">4</span>
                                                    </label>
                                                </div>
                                            </div>
                                            <div className="col-lg-1 col-md-1 col-sm-1 col-xs-1 menu-righe">
                                                <button className="pimenu-navbar-toggle" type="button"
                                                    data-toggle="collapse" data-target=".pimenu-navbar-collapse">
                                                    <i className="fa fa-bars"></i>
                                                </button>
                                            </div>
                                        </div>

                                        <nav className="pimenu-navbar-collapse collapse">
                                            <ul className="nav navbar-nav">
                                                <li className="item-113"><a href="#">Link1</a></li>
                                                <li className="item-125"><a href="#">Link2</a></li>
                                            </ul>
                                        </nav>
                                    </div>
                                </header>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = MapHeader;
