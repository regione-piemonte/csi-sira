/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

const msLayers = require('@mapstore/reducers/layers').default;
const assign = require('object-assign');
// const {isObject, head, findIndex, isEmpty, includes} = require('lodash');
const {isObject, head, findIndex} = require('lodash');
const {SHOW_SETTINGS, HIDE_SETTINGS, TOGGLE_NODE, addLayer} = require('@mapstore/actions/layers');
const {SELECT_FEATURES, SET_FEATURES, SELECT_ALL, SELECT_MLS} = require('../actions/featuregrid');
const {SET_ACTIVE_NODE} = require('../actions/siradec');
const {CONFIGURE_INFO_TOPOLOGY, CHANGE_MAPINFO_STATE, CHANGE_TOPOLOGY_MAPINFO_STATE} = require('../actions/mapInfo');
// const {TOGGLE_SIRA_CONTROL} = require("../actions/controls");
const ConfigUtils = require('@mapstore/utils/ConfigUtils');

const getVector = (state) => {
    return state?.flat?.length > 0 ? head(state.flat.filter((l) => l.id === "gridItems" )) : undefined;
};

const checkSelectLayer = (state, vector) => {
    if (state.flat && state.flat.length) {
        const mlsLayer = state.flat.filter(l => l.id === "selected_mls");
        const others = state.flat.filter(l => l.id !== "gridItems" && l.id !== "selected_mls");
        const newLayers = [...others, getVector(state) || vector, ...mlsLayer].filter(l => l);
        return {
            ...state,
            flat: newLayers
        };
    }
    return state;
};
const filterHiddenGroup = (state) => {
    if (state.groups) {
        return assign({}, state, {groups: state.groups.filter((g) => g.id !== "hidden")});
    }
    return state;
};
const checkState = (state, vector) => {
    return filterHiddenGroup(checkSelectLayer(state, vector));
};
const getAction = (layer, features) => {
    return {
        type: "CHANGE_LAYER_PROPERTIES",
        layer,
        newProperties: { features }
    };
};
const cloneLayer = (layer) => {
    let newLayer = {};
    Object.keys(layer).reduce((pr, next) => {
        pr[next] = isObject(layer[next]) ? assign({}, layer[next]) : layer[next];
        return pr;
    }, newLayer);
    return newLayer;
};

function layers(state = [], action) {
    switch (action.type) {
    case CHANGE_TOPOLOGY_MAPINFO_STATE:
    case CHANGE_MAPINFO_STATE: {
        let tmpState = msLayers(state, getAction("gridItems", []) );
        return msLayers(tmpState, getAction("topologyItems", []));
    }
    case SELECT_FEATURES:
        const ftLayers = state.flat.filter(l => /* includes(l.id, '_mls') || */ l.id === 'gridItems');
        return ftLayers.reduce((layersms, layer) => {
            const features = { features: action.features, style: {
                type: action.geometryType,
                radius: 10,
                stroke: {
                    width: 3,
                    color: 'rgba(0,0,255, 0.4)'
                },
                fill: {
                    color: 'rgba(0,0,255, 0.4)'
                }
            }};
            return msLayers(layersms, { type: "CHANGE_LAYER_PROPERTIES",
                layer: layer.id,
                newProperties: {
                    ...(layer.id === 'gridItems' && features) // ,
                    /* ...(includes(layer.id, '_mls') && {visibility: !isEmpty(action.features)}) */
                }
            });
        }, state);
    /* case TOGGLE_SIRA_CONTROL:
        if (action.control === "detail") {
            const ft2Layers = state.flat.filter(l => includes(l.id, '_mls') || l.id === 'gridItems');
            return ft2Layers.reduce((layersms, layer) => {
                return msLayers(layersms, { type: "CHANGE_LAYER_PROPERTIES",
                    layer: layer.id,
                    newProperties: {
                        ...(includes(layer.id, '_mls') && {visibility: false})
                    }
                });
            }, state);
        }
        return state; */
    case SET_FEATURES:
    case CONFIGURE_INFO_TOPOLOGY:
        return msLayers(state, getAction("topologyItems", action.features || action.infoTopologyResponse.features));
    case SHOW_SETTINGS: {
        return msLayers(msLayers(state, {
            type: TOGGLE_NODE,
            node: action.node,
            nodeType: "layers",
            status: true}), action);

    }
    case SET_ACTIVE_NODE: {
        return msLayers(msLayers(state, {
            type: TOGGLE_NODE,
            node: action.node,
            nodeType: "layers",
            status: true}), action);

    }
    case HIDE_SETTINGS: {
        return msLayers(msLayers(state, {
            type: 'TOGGLE_NODE',
            node: action.node,
            nodeType: "layers",
            status: false}), action);
    }
    case SELECT_ALL: {
        if (action.sldBody && action.featureTypeName) {
            let layer = head(state.flat.filter(l => l.name === `${action.featureTypeName}`));
            if (layer) {
                let allLayer = head(state.flat.filter(l => l.id === "selectAll"));
                if (allLayer) {
                    let params = {params: {...allLayer.params, SLD_BODY: action.sldBody}};
                    return msLayers(state, { type: "CHANGE_LAYER_PROPERTIES",
                        layer: allLayer.id,
                        newProperties: params
                    });
                }
                let newLayer = cloneLayer(layer);
                newLayer.id = "selectAll";
                newLayer.type = "wmspost";
                newLayer.visibility = true;
                delete newLayer.group;
                newLayer.params = assign({}, newLayer.params, {SLD_BODY: action.sldBody, viewparams: action.viewparams});
                return msLayers(state, { type: "ADD_LAYER", layer: newLayer});
            }
        }
        let allLayer = head(state.flat.filter(l => l.id === "selectAll"));
        return allLayer ? msLayers(state, { type: "REMOVE_NODE", nodeType: 'layers', node: 'selectAll'}) : msLayers(state, action);
    }
    case SELECT_MLS : {
        return action.layers.reduce((layersms, layer) => {
            let mlsLayer = head(state.flat.filter(l => l.name === `${layer.name}`));
            if (mlsLayer) {
                let params = {params: layer.params, visibility: true};
                return msLayers(layersms, { type: "CHANGE_LAYER_PROPERTIES",
                    layer: mlsLayer.id,
                    newProperties: params
                });
            }
            return msLayers(layersms, {type: 'ADD_LAYER', layer});
        }, state);
    }
    case 'THEMATIC_VIEW_CONFIG_LOADED': {
        // We exclude background layers and we add the rest
        const oldLayers = state.flat;
        if ( action.config && action.config.map && action.config && action.config.map.layers) {
            return action.config.map.layers.filter((l) => l.group !== 'background' && findIndex(oldLayers, (ol) => ol.name === l.name) === -1).reduce((st, layer) => {
                return msLayers(st, addLayer(ConfigUtils.setUrlPlaceholders(layer)));
            }, state);
        }
        return state;
    }
    case 'SIRA_ADD_LAYERS': {
        return action.layers.reduce((layersms, layer) => {
            return msLayers(layersms, {type: 'ADD_LAYER', layer});
        }, state);
    }
    default:
        return msLayers(state, action);
    }
}

function checkedLayers(state = [], action) {
    const newState = layers(state, action);
    if ( newState !== state) {
        const vector = getVector(state);
        return checkState(newState, vector);
    }
    return newState;
}

module.exports = checkedLayers;
