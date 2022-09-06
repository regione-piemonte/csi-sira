/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

const axios = require('@mapstore/libs/ajax');
const ConfigUtils = require('@mapstore/utils/ConfigUtils');

const SET_PROFILE = 'SET_PROFILE';
const SET_USER_IDENTITY_ERROR = 'SET_USER_IDENTITY_ERROR';
const RESET_USER_IDENTITY_ERROR = 'RESET_USER_IDENTITY_ERROR';
const SET_USER_IDENTITY = 'LOADED_USER_IDENTITY';
const RESET_USER_IDENTITY = 'RESET_USER_IDENTITY';
const SHOW_LOGIN_PANEL = 'SHOW_LOGIN_PANEL';
const HIDE_LOGIN_PANEL = 'HIDE_LOGIN_PANEL';

function showLoginPanel() {
    return {
        type: SHOW_LOGIN_PANEL,
        showLoginPanel: true
    };
}

function hideLoginPanel() {
    return {
        type: HIDE_LOGIN_PANEL,
        showLoginPanel: false
    };
}

function setProfile(profile, authParams) {
    return {
        type: SET_PROFILE,
        profile: profile,
        authParams: authParams
    };
}

function userIdentityLoaded(data) {
    return {
        type: SET_USER_IDENTITY,
        roles: data.roles,
        user: data.user,
        error: ''
    };
}

function resetUserIdentity() {
    return {
        type: RESET_USER_IDENTITY,
        roles: null,
        user: null,
        error: ''
    };
}

function userIdentityError(err) {
    return {
        type: SET_USER_IDENTITY_ERROR,
        roles: '',
        userIdentity: '',
        error: err
    };
}

function resetUserIdentityError() {
    return {
        type: RESET_USER_IDENTITY_ERROR
    };
}

function loadUserIdentity(serviceUrl = 'services/iride/getRolesForDigitalIdentity') {
    if (window.location.href.indexOf('auth') === -1) {
        return () => { };
    }
    return (dispatch) => {
        return axios.get(serviceUrl).then((response) => {
            // response example
            // response.data = { "roles": [{ "code": "PA_GEN_DECSIRA", "domain": "REG_PMN", "mnemonic": "PA_GEN_DECSIRA@REG_PMN", "description": "PA Generica" }], "userIdentity": { "codFiscale": "AAAAAA00B77B000F", "nome": "CSI PIEMONTE", "cognome": "DEMO 20", "idProvider": "ACTALIS_EU" } };
            // response.data = { "roles": [{ "code": "BDN_EG_01", "domain": "REG_PMN", "mnemonic": "BDN_EG_01@REG_PMN", "description": "BDN - Ente di gestione delle aree protette delle Alpi Cozie" }], "userIdentity": { "codFiscale": "AAAAAA00A11K000S", "nome": "CSI PIEMONTE", "cognome": "DEMO 30", "idProvider": "ACTALIS_EU" } };
            // response.data = { "roles": [{ "code": "PA_GEN_DECSIRA", "domain": "REG_PMN", "mnemonic": "PA_GEN_DECSIRA@REG_PMN", "description": "PA Generica" }, { "code": "PA_SPEC_AUT_DECSIRA", "domain": "REG_PMN", "mnemonic": "PA_SPEC_AUT_DECSIRA@REG_PMN", "description": "PA Specialistico Autorita' Regionale" }], "userIdentity": { "codFiscale": "AAAAAA00A11B000J", "nome": "CSI PIEMONTE", "cognome": "DEMO 21", "idProvider": "ACTALIS_EU" } };
            // response.data = { "roles": null, "userIdentity": null };
            // response.data = { "roles": [], "userIdentity": { "codFiscale": "MRLFNC68A56H456Q", "nome": "FRANCESCA", "cognome": "MORELLI", "idProvider": "ACTALIS_EU" } };
            // response.data = { "roles": [{ "code": "PA_SPEC_CONS_DECSIRA", "domain": "REG_PMN", "mnemonic": "PA_SPEC_CONS_DECSIRA@REG_PMN", "description": "Pubblica Amministrazione - Specialistico su ambito regionale" }, { "code": "PA_SPEC_DECSIRA", "domain": "REG_PMN", "mnemonic": "PA_SPEC_DECSIRA@REG_PMN", "description": null }], "userIdentity": { "codFiscale": "AAAAAA00A11E000M", "nome": "CSI PIEMONTE", "cognome": "DEMO 24", "idProvider": "ACTALIS_EU" } };
            if (typeof response.data === 'object') {
                let user = {
                    profile: []
                };
                if (response.data.userIdentity && response.data.roles && response.data.roles.length > 0) {
                    // there is a logged user, geoserverUrl = secureGeoserverUrl
                    ConfigUtils.setConfigProp('geoserverUrl', ConfigUtils.getConfigProp('secureGeoserverUrl'));
                    ConfigUtils.setConfigProp('gsdownloadUrl', ConfigUtils.getConfigProp('secureGsdownloadUrl'));
                    response.data.profile = [];
                    Array.from(response.data.roles).forEach(function(val) {
                        if (val && val.mnemonic) {
                            response.data.profile.push(val.mnemonic);
                        }
                    });

                    if (response.data.userIdentity) {
                        user = {
                            name: response.data.userIdentity.nome + " " + response.data.userIdentity.cognome,
                            surname: response.data.userIdentity.cognome,
                            cf: response.data.userIdentity.nome,
                            idProvider: response.data.userIdentity.idProvider,
                            profile: response.data.profile,
                            roles: response.data.roles
                        };
                    }
                }
                if (response.data.roles.length === 0) {
                    dispatch(userIdentityError('empty_roles'));
                }
                response.data.user = user;
                dispatch(userIdentityLoaded(response.data));
            } else {
                try {
                    dispatch(userIdentityLoaded(JSON.parse(response.data)));
                } catch (e) {
                    dispatch(userIdentityError('Error in getRolesForDigitalIdentity: ' + e.message));
                }
            }
        }).catch((e) => {
            dispatch(userIdentityError(e.message));
        });
    };
}

module.exports = {
    SET_PROFILE,
    SET_USER_IDENTITY_ERROR,
    RESET_USER_IDENTITY_ERROR,
    SET_USER_IDENTITY,
    RESET_USER_IDENTITY,
    SHOW_LOGIN_PANEL,
    HIDE_LOGIN_PANEL,
    showLoginPanel,
    hideLoginPanel,
    loadUserIdentity,
    userIdentityLoaded,
    resetUserIdentity,
    userIdentityError,
    setProfile,
    resetUserIdentityError
};
