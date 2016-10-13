/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
const React = require('react');


require('../../assets/css/sira.css');
require('../../MapStore2/web/client/product/assets/css/viewer.css');

const {connect} = require('react-redux');
const assign = require('object-assign');

const SidePanel = require('./SidePanel');
const Card = require('../components/template/Card');
const Header = require('../components/MapHeader');

const {bindActionCreators} = require('redux');
const {toggleSiraControl} = require('../actions/controls');
const {setProfile} = require('../actions/userprofile');
const {configureInlineMap} = require('../actions/siradec');
const url = require('url');
const urlQuery = url.parse(window.location.href, true).query;

const authParams = {
    admin: {
        userName: "admin",
        authkey: "84279da9-f0b9-4e45-ac97-48413a48e33f"
    },
    A: {
        userName: "profiloa",
        authkey: "59ccadf2-963e-448c-bc9a-b3a5e8ed20d7"
    },
    B: {
        userName: "profilob",
        authkey: "d6e5f5a5-2d26-43aa-8af3-13f8dcc0d03c"
    },
    C: {
        userName: "profiloc",
        authkey: "0505bb64-21b6-436c-86f9-9c1280f15a6c"
    },
    D: {
        userName: "profilod",
        authkey: "4176ea85-9a9a-42a5-8913-8f6f85813dab"
    }
};

const { changeMousePointer} = require('../../MapStore2/web/client/actions/map');

const MapViewer = require('../../MapStore2/web/client/containers/MapViewer');

const {getFeatureInfo, purgeMapInfoResults, showMapinfoMarker, hideMapinfoMarker} = require('../actions/mapInfo');
const {loadGetFeatureInfoConfig, setModelConfig} = require('../actions/mapInfo');
const {selectFeatures, setFeatures} = require('../actions/featuregrid');

const GetFeatureInfo = connect((state) => ({
    siraFeatureTypeName: state.siradec.featureTypeName,
    siraFeatureInfoDetails: assign({}, state.siradec.featureinfo, {card: state.siradec.card}),
    siraTopology: state.siradec.topology,
    siraTopologyConfig: state.mapInfo.topologyConfig,
    infoEnabled: state.mapInfo && state.mapInfo.infoEnabled || false,
    topologyInfoEnabled: state.mapInfo && state.mapInfo.topologyInfoEnabled || false,
    htmlResponses: state.mapInfo && state.mapInfo.responses || [],
    htmlRequests: state.mapInfo && state.mapInfo.requests || {length: 0},
    infoFormat: state.mapInfo && state.mapInfo.infoFormat,
    detailsConfig: state.mapInfo.detailsConfig,
    // modelConfig: state.mapInfo.modelConfig,
    template: state.mapInfo.template,
    map: state.map && state.map.present,
    infoType: state.mapInfo.infoType,
    layers: state.layers && state.layers.flat || [],
    clickedMapPoint: state.mapInfo && state.mapInfo.clickPoint
}), (dispatch) => {
    return {
        actions: bindActionCreators({
            getFeatureInfo,
            purgeMapInfoResults,
            changeMousePointer,
            showMapinfoMarker,
            hideMapinfoMarker,
            loadGetFeatureInfoConfig,
            setFeatures,
            selectFeatures,
            setModelConfig
        }, dispatch)
    };
})(require('../components/identify/GetFeatureInfo'));

const {
    loadFeatureTypeConfig
} = require('../actions/siradec');

let MapInfoUtils = require('../../MapStore2/web/client/utils/MapInfoUtils');

MapInfoUtils.AVAILABLE_FORMAT = ['TEXT', 'JSON', 'HTML', 'GML3'];

const Sira = React.createClass({
    propTypes: {
        mode: React.PropTypes.string,
        params: React.PropTypes.object,
        loadMapConfig: React.PropTypes.func,
        reset: React.PropTypes.func,
        // featureGrigConfigUrl: React.PropTypes.string,
        featureTypeConfigUrl: React.PropTypes.string,
        error: React.PropTypes.object,
        loading: React.PropTypes.bool,
        // card: React.PropTypes.string,
        nsResolver: React.PropTypes.func,
        controls: React.PropTypes.object,
        toggleSiraControl: React.PropTypes.func,
        setProfile: React.PropTypes.func,
        onLoadFeatureTypeConfig: React.PropTypes.func,
        plugins: React.PropTypes.object,
        viewerParams: React.PropTypes.object,
        configureInlineMap: React.PropTypes.func,
        configLoaded: React.PropTypes.bool
    },
    getDefaultProps() {
        return {
            toggleSiraControl: () => {},
            setProfile: () => {},
            onLoadFeatureTypeConfig: () => {},
            mode: 'desktop',
            viewerParams: {mapType: "openlayers"},
            configLoaded: false
        };
    },
    componentWillMount() {
        if (urlQuery.map) {
            this.props.configureInlineMap(JSON.parse(urlQuery.map));
        }
        this.props.setProfile(this.props.params.profile, authParams[this.props.params.profile]);
    },
    componentDidMount() {
        if (!this.props.configLoaded && this.props.featureTypeConfigUrl) {
            this.props.onLoadFeatureTypeConfig(
                this.props.featureTypeConfigUrl, {authkey: authParams[this.props.params.profile].authkey});
        }
    },
    componentWillReceiveProps(props) {
        let fturl = props.featureTypeConfigUrl;
        if (fturl !== this.props.featureTypeConfigUrl) {
            this.props.onLoadFeatureTypeConfig(url, {authkey: authParams[this.props.params.profile].authkey});
        }
    },
    render() {
        return (
            <div className="mappaSiraDecisionale">
                <Header onBack={this.back} onHome={this.goHome}/>
                <div className="mapbody">
                    <span className={this.props.error && 'error' || !this.props.loading && 'hidden' || ''}>
                        {this.props.error && ("Error: " + this.props.error) || (this.props.loading)}
                    </span>
                    <div className="info">Profile: {this.props.params.profile}</div>
                    <SidePanel auth={authParams[this.props.params.profile]} profile={this.props.params.profile}/>
                    <MapViewer
                    plugins={this.props.plugins}
                    params={this.props.viewerParams}
                    />
                    <Card authParam={authParams[this.props.params.profile]}/>
                    <GetFeatureInfo
                        display={"accordion"}
                        params={{authkey: authParams[this.props.params.profile].authkey}}
                        profile={this.props.params.profile}
                        key="getFeatureInfo"/>
                </div>
            </div>
        );
    }
});

module.exports = connect((state) => {
    return {
        mode: 'desktop',
        loading: !state.config || !state.locale || false,
        error: state.loadingError || (state.locale && state.locale.localeError) || null,
        // card: state.cardtemplate,
        controls: state.siraControls,
        featureTypeConfigUrl: state.siradec && state.siradec.featureType && 'assets/' + state.siradec.featureType + '.json',
        configLoaded: state.siradec && state.siradec.card ? true : false
        // featureGrigConfigUrl: state.grid.featureGrigConfigUrl
    };
}, {
    toggleSiraControl,
    setProfile,
    onLoadFeatureTypeConfig: loadFeatureTypeConfig,
    configureInlineMap
})(Sira);
