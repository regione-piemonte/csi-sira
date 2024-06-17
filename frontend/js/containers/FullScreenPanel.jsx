const PropTypes = require('prop-types');
/**
 * Copyright 2017, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
const React = require('react');
// const {Button, Glyphicon} = require('react-bootstrap');

const {getWindowSize} = require('@mapstore/utils/AgentUtils');
const mapUtils = require('@mapstore/utils/MapUtils');
const CoordinateUtils = require('@mapstore/utils/CoordinatesUtils');
const {changeMapView} = require('@mapstore/actions/map');
const {connect} = require('react-redux');

const {mapSelector} = require('@mapstore/selectors/map');
const SideQueryPanel = require('../components/SideQueryPanel');
const Card = require('../components/template/Card');
const SideFeatureGrid = require('../components/SideFeatureGrid');
const {setProfile} = require('../actions/userprofile');
const Spinner = require('react-spinkit');
const { selectFeatures } = require('../actions/featuregrid');
const { handleKeyFocus } = require('../utils/SiraUtils');
const Header = require('../components/Header');
const { HashLink } = require('react-router-hash-link');

const {
    loadFeatureTypeConfig,
    expandFilterPanel
} = require('../actions/siradec');

const {toggleSiraControl} = require('../actions/controls');
require('../../assets/css/fullscreen.css');

const {addLayer} = require('@mapstore/actions/layers');

class FullScreen extends React.Component {
    static propTypes = {
        match: PropTypes.shape({
            params: PropTypes.object
        }),
        featureType: PropTypes.string,
        error: PropTypes.object,
        filterPanelExpanded: PropTypes.bool,
        onLoadFeatureTypeConfig: PropTypes.func,
        expandFilterPanel: PropTypes.func,
        toggleSiraControl: PropTypes.func,
        configLoaded: PropTypes.bool,
        profile: PropTypes.object,
        siraControls: PropTypes.object,
        selectFeatures: PropTypes.func,
        detailsConfig: PropTypes.object,
        gridConfig: PropTypes.object,
        loadCardTemplate: PropTypes.func,
        selectAllToggle: PropTypes.func,
        gridExpanded: PropTypes.bool,
        fTypeConfigLoading: PropTypes.bool,
        changeMapView: PropTypes.func,
        addLayer: PropTypes.func,
        srs: PropTypes.string,
        maxZoom: PropTypes.number,
        map: PropTypes.object,
        siraActiveConfig: PropTypes.object,
        layers: PropTypes.array
    };

    static contextTypes = {
        router: PropTypes.object
    };

    static defaultProps = {
        setProfile: () => {},
        onLoadFeatureTypeConfig: () => {},
        configLoaded: false,
        filterPanelExpanded: true,
        toggleSiraControl: () => {},
        srs: "EPSG:4326",
        maxZoom: 16
    };

    state = {
        loadList: true
    };

    componentWillMount() {
        this.setState({width: getWindowSize().maxWidth});
        if (window.addEventListener) {
            window.addEventListener('resize', this.setSize, false);
        }
    }

    componentDidMount() {
        document.body.className = "body_map sira-ms2";
        window.addEventListener('keyup', handleKeyFocus);
    }

    componentWillReceiveProps(nextProps) {
        const {map, filterPanelExpanded, siraControls, gridExpanded} = nextProps;
        if (this.props.map !== map) {
            if (siraControls.detail) {
                this.props.toggleSiraControl('detail');
            }
            if (filterPanelExpanded) {
                this.props.expandFilterPanel(false);
            }
            if (gridExpanded) {
                this.props.toggleSiraControl('grid');
            }
            this.props.selectFeatures([]);
            if (this.props?.match?.params?.profile) {
                this.context.router.history.push(`/map/${this.props.match.params.profile}`);
            } else {
                this.context.router.history.push('/map/');
            }
        }
    }

    componentWillUnmount() {
        if (window.removeEventListener) {
            window.removeEventListener('resize', this.setSize, false);
        }
        window.removeEventListener('keyup', handleKeyFocus);
    }

    renderQueryPanel = () => {
        return (
            <SideQueryPanel
                withMap={false}
                hideSpatialFilter
                // authkey: (this.props.profile.authParams && this.props.profile.authParams.authkey) ? this.props.profile.authParams.authkey : '')
                params={{authkey: this.props?.profile?.authParams?.authkey || ''}}
                toggleControl={this.toggleControl}/>
        );
    };

    renderGrid = () => {
        return (
            <SideFeatureGrid
                initWidth={this.state.width}
                withMap
                modeBackToDataset
                params={{authkey: this.props?.profile?.authParams?.authkey || ''}}
                profile={this.props.profile.profile}
                zoomToFeatureAction={this.zoomToFeature}
                backToDataset={this.backToDataset}
                fullScreen
                selectAll={false}/>
        );
    };

    render() {
        const {gridExpanded, profile, fTypeConfigLoading} = this.props;
        if (fTypeConfigLoading) {
            return (<div style={{position: "absolute", top: 0, left: 0, bottom: 0, right: 0, backgoroundColor: "rgba(125,125,125,.5)"}}><Spinner style={{position: "absolute", top: "calc(50%)", left: "calc(50% - 30px)", width: "60px"}} spinnerName="three-bounce" noFadeIn/></div>);
        }
        let comp;
        if (gridExpanded) {
            comp = this.renderGrid();
        } else {
            comp = this.renderQueryPanel();
        }
        return (
            <>
                <div role="navigation" className="skip-navigation" aria-label="Navigazione veloce">
                    <HashLink to="/dataset/#main-content">Salta al contenuto principale</HashLink>
                </div>
                <Header showCart="true" goToHome={this.goToHome} />
                <div id="main-content"></div>
                <div id="fullscreen-container" className="mappaSiraDecisionale">
                    {comp}
                    <Card draggable profile ={profile.profile} authParam={profile.authParams} withMap/>
                </div>
            </>
        );
    }

    goToHome = () => {
        this.context.router.history.push('/');
    };

    toggleControl = () => {
        this.props.expandFilterPanel(false);
        this.props.toggleSiraControl('grid');
        /* if (this.props?.match?.params?.profile) {
            this.context.router.history.push(`/dataset/${this.props.match.params.profile}/`);
        } else {
            this.context.router.history.push('/dataset/');
        } */
    };

    backToDataset = () => {
        this.props.expandFilterPanel(false);
        if (this.props?.match?.params?.profile) {
            this.context.router.history.push(`/dataset/${this.props.match.params.profile}/`);
        } else {
            this.context.router.history.push('/dataset/');
        }
    };

    zoomToFeature = (data) => {
        // old version I use caution, I do not delete ...
        // setTimeout(() => {this.changeMapView([data.geometry]); }, 0);
        // check if the layer is already present
        // this isn't the perfect place for this check but i can't modify the ms2 source ...
        if (this.props.layers.filter((l) => l.name === this.props.siraActiveConfig.layer.name ).length <= 0) {
            this.props.addLayer(this.props.siraActiveConfig.layer);
        }
        this.changeMapView([data.geometry]);
    };

    changeMapView = (geometries) => {
        let extent = geometries.reduce((prev, next) => {
            return CoordinateUtils.extendExtent(prev, CoordinateUtils.getGeoJSONExtent(next));
        }, CoordinateUtils.getGeoJSONExtent(geometries[0]));
        const center = mapUtils.getCenterForExtent(extent, "4326");
        this.props.changeMapView(center, 15, null, null, null, this.props.map.projection || "EPSG:3857");
    };

    setSize = () => {
        this.setState({width: window.innerWidth});
    };
}

module.exports = connect((state) => {
    const activeConfig = state.siradec.configOggetti[state.siradec.activeFeatureType] || {};
    return {
        profile: state.userprofile,
        error: state.loadingError || (state.locale && state.locale.localeError) || null,
        filterPanelExpanded: state.siradec.filterPanelExpanded,
        siraControls: state.siraControls,
        detailsConfig: activeConfig && activeConfig.card,
        gridConfig: activeConfig && activeConfig.featuregrid,
        featureType: activeConfig && activeConfig.featureTypeName,
        searchUrl: state.queryform.searchUrl || activeConfig?.queryform?.searchUrl,
        pagination: state.queryform.pagination,
        gridExpanded: state.siraControls.grid,
        fTypeConfigLoading: state.siradec.fTypeConfigLoading,
        map: mapSelector(state),
        siraActiveConfig: activeConfig,
        layers: state.layers.flat
    };
}, {
    setProfile,
    onLoadFeatureTypeConfig: loadFeatureTypeConfig,
    addLayer: addLayer,
    expandFilterPanel,
    toggleSiraControl,
    changeMapView,
    selectFeatures
})(FullScreen);
