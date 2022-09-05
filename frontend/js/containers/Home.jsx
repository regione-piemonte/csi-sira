const PropTypes = require('prop-types');
/**
 * Copyright 2016, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */
const React = require('react');
const {connect} = require('react-redux');
const {head} = require('lodash');
const Footer = require('../components/Footer');
const Header = require('../components/Header');
const { Modal, Button } = require("react-bootstrap");
const I18N = require('@mapstore/components/I18N/I18N');
const { getMetadataObjects, selectCategory, resetObjectAndView } = require('../actions/siracatalog');
const { resetUserIdentityError } = require('../actions/userprofile');
const {categorySelector} = require('../selectors/sira');
const Mosaic = connect(categorySelector)(require('../components/Mosaic'));
const LocaleUtils = require('@mapstore/utils/LocaleUtils');

const PlatformNumbers = connect((state) => ({
    siradecObject: state.platformnumbers.siradecObject,
    functionObjectMap: state.platformnumbers.functionObjectMap,
    functionObjectSearch: state.platformnumbers.functionObjectSearch,
    functionObjectView: state.platformnumbers.functionObjectView
}))(require('../components/PlatformNumbers'));

const SiraSearchBar = require('../components/SiraSearchBar');
const { handleKeyFocus } = require('../utils/SiraUtils');
const { HashLink } = require('react-router-hash-link');


class Home extends React.Component {
    static propTypes = {
        loadMetadata: PropTypes.func,
        match: PropTypes.shape({
            params: PropTypes.object
        }),
        selectCategory: PropTypes.func,
        allCategory: PropTypes.object,
        resetObjectAndView: PropTypes.func,
        profile: PropTypes.object,
        resetUserIdentityError: PropTypes.func
    };

    static contextTypes = {
        router: PropTypes.object,
        messages: PropTypes.object
    };

    state = {
        searchText: "",
        profileError: ''
    };

    componentDidMount() {
        document.body.className = "body_home";
        window.addEventListener('keyup', handleKeyFocus);
    }

    componentWillUnmount() {
        window.removeEventListener('keyup', handleKeyFocus);
    }

    renderProfileAlert = () => {
        return (<Modal show bsSize="small">
            <Modal.Header closeButton onClick={() => { this.props.resetUserIdentityError(); }}>
                <Modal.Title><I18N.Message msgId="Modal.InfoTitle"/></Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div className="mapstore-error"><I18N.Message msgId="Modal.NoRoleMessage"/></div>
            </Modal.Body>
            <Modal.Footer>
                <Button onClick={() => { this.props.resetUserIdentityError(); }}><I18N.Message msgId="Accessibility.button"/></Button>
            </Modal.Footer>
        </Modal>);
    };

    render() {
        let description = LocaleUtils.getMessageById(this.context.messages, "Homepage.appDescription");
        return (
            <div className="home-page">
                <div role="navigation" className="skip-navigation" aria-label="Navigazione veloce">
                    <HashLink to="/#main-content">Salta al contenuto principale</HashLink>
                </div>
                <Header />
                <h1 className="sr-only">{LocaleUtils.getMessageById(this.context.messages, "sr-only.homepage")}</h1>
                <div id="main-content"></div>
                <div className="container-fluid" role="search">
                    {this.props.profile.error === 'empty_roles' && this.renderProfileAlert()}
                    <div className="row-fluid sb-sx">
                        <div className="container search-home">
                            <div className="row">
                                <div className="col-md-7 col-xs-12 testo-home">
                                    <div>
                                        <span dangerouslySetInnerHTML={{ __html: description }} />
                                    </div>
                                </div>
                                <SiraSearchBar
                                    containerClasses="col-md-5 col-xs-12 ricerca-home catalog-search-container sira-ms2"
                                    searchClasses="home-search"
                                    addCategoriesSelector={false}
                                    onSearch={({text}) => {
                                        this.props.selectCategory(this.props.allCategory, 'objects');
                                        this.props.loadMetadata({params: {text}});
                                        if (this.props?.match?.params?.profile) {
                                            this.context.router.history.push(`/dataset/${this.props.match.params.profile}/`);
                                        } else {
                                            this.context.router.history.push('/dataset/');
                                        }
                                    }}
                                />
                            </div>
                        </div>
                    </div>
                </div>
                <Mosaic useLink={false} tileClick={this.selectCategory} />
                <PlatformNumbers />
                <Footer />
            </div>);
    }

    selectCategory = (category, subcat) => {
        this.props.resetObjectAndView();
        this.props.selectCategory(category, subcat);
        if (this.props?.match?.params?.profile) {
            this.context.router.history.push(`/dataset/${this.props.match.params.profile}/`);
        } else {
            this.context.router.history.push('/dataset/');
        }
    };
}

module.exports = connect((state) => {
    const {tiles} = categorySelector(state);
    return {
        allCategory: head(tiles.filter((t) => t.id === 999)),
        error: state.loadingError || (state.locale && state.locale.localeError) || null,
        locale: state.locale && state.locale.locale,
        messages: state.locale && state.locale.messages || {},
        profile: state.userprofile
    };
}, {
    loadMetadata: getMetadataObjects,
    selectCategory,
    resetObjectAndView,
    resetUserIdentityError
})(Home);
