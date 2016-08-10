/*
 *  GeoServer Security Provider plugin with which doing authentication and authorization operations using CSI-Piemonte IRIDE Service.
 *  Copyright (C) 2016  Regione Piemonte (www.regione.piemonte.it)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.geoserver.security.iride.identity;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.geoserver.security.iride.identity.IrideIdentityFormatter.FormatStyle;
import org.geoserver.security.iride.identity.token.IrideIdentityToken;
import org.geotools.util.logging.Logging;
import org.junit.Before;
import org.junit.Test;

/**
 * <code>IRIDE</code> Digital Identity entity formatter <code>JUnit</code> Test Case.
 *
 * @author "Simone Cornacchia - seancrow76@gmail.com, simone.cornacchia@consulenti.csi.it (CSI:71740)"
 */
public final class IrideIdentityFormatterTest {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logging.getLogger(IrideIdentityTest.class);

    /**
     * <code>IRIDE</code> Digital Identity tokens.
     */
    private String[] tokens;

    /**
     * <code>IRIDE</code> Digital Identity entity instance.
     */
    private IrideIdentity irideIdentity;

    /**
     * <code>IRIDE</code> Digital Identity entity formatter.
     */
    private IrideIdentityFormatter formatter;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.tokens = new String[] { "AAAAAA00A11D000L", "CSI PIEMONTE", "DEMO 23", "IPA", "20160725152035", "2", "SQGMT++caXxGO/7s3Zu2ow==" };

        this.irideIdentity = new IrideIdentity(this.tokens);

        this.formatter = new IrideIdentityFormatter();
    }

    /**
     * Test method for {@link org.geoserver.security.iride.identity.IrideIdentityFormatter.FormatStyle#values()}.
     */
    @Test
    public void testIrideIdentityFormatterHasTwoFormatStyles() {
        LOGGER.entering(this.getClass().getName(), "testIrideIdentityFormatterHasTwoFormatStyles");
        try {
            final FormatStyle[] result = IrideIdentityFormatter.FormatStyle.values();

            assertThat(result, is(not(nullValue())));
            assertThat(result, is(arrayWithSize(2)));
        } finally {
            LOGGER.exiting(this.getClass().getName(), "testIrideIdentityFormatterHasTwoFormatStyles");
        }
    }

    /**
     * Test method for {@link org.geoserver.security.iride.identity.IrideIdentityFormatter#format(org.geoserver.security.iride.identity.IrideIdentityFormatter.FormatStyle)}.
     */
    @Test
    public void testFormatWithNullStyleReturnsNull() {
        final IrideIdentityFormatter.FormatStyle formatStyle = null;

        LOGGER.entering(this.getClass().getName(), "testFormatWithNullStyleReturnsNull", formatStyle);
        try {
            final String result = this.formatter.format(this.irideIdentity, formatStyle);

            assertThat(result, is(nullValue()));

            LOGGER.info("Formatted IrideIdentity: " + result);
        } finally {
            LOGGER.exiting(this.getClass().getName(), "testFormatWithNullStyleReturnsNull");
        }
    }

    /**
     * Test method for {@link org.geoserver.security.iride.identity.IrideIdentityFormatter#format(org.geoserver.security.iride.identity.IrideIdentityFormatter.FormatStyle)}.
     */
    @Test
    public void testFormatWithInternalRepresentationStyleReturnsValidDigitalRepresentation() {
        final IrideIdentityFormatter.FormatStyle formatStyle = IrideIdentityFormatter.FormatStyle.INTERNAL_REPRESENTATION;

        LOGGER.entering(this.getClass().getName(), "testFormatWithInternalRepresentationStyleReturnsValidDigitalRepresentation", formatStyle);
        try {
            final String result = this.formatter.format(this.irideIdentity, formatStyle);

            assertThat(result, is(not(nullValue())));
            assertThat(result, is(StringUtils.join(this.tokens, IrideIdentityToken.SEPARATOR)));

            LOGGER.info("Formatted IrideIdentity: " + result);
        } finally {
            LOGGER.exiting(this.getClass().getName(), "testFormatWithInternalRepresentationStyleReturnsValidDigitalRepresentation");
        }
    }

    /**
     * Test method for {@link org.geoserver.security.iride.identity.IrideIdentityFormatter#format(org.geoserver.security.iride.identity.IrideIdentityFormatter.FormatStyle)}.
     */
    @Test
    public void testFormatWithReflectionToStringStyle() {
        final IrideIdentityFormatter.FormatStyle formatStyle = IrideIdentityFormatter.FormatStyle.REFLECTION_TO_STRING;

        LOGGER.entering(this.getClass().getName(), "testFormatWithReflectionToStringStyle", formatStyle);
        try {
            final String result = this.formatter.format(this.irideIdentity, formatStyle);

            assertThat(result, is(not(nullValue())));

            LOGGER.info("Formatted IrideIdentity: " + result);
        } finally {
            LOGGER.exiting(this.getClass().getName(), "testFormatWithReflectionToStringStyle");
        }
    }

}
