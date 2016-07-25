/*
 *  GeoServer Security Provider plugin used for doing authentication and authorization operations using CSI-Piemonte IRIDE Service.
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
package org.geoserver.security.iride.exception;

/**
 * <code>IRIDE</code> Digital Identity base system exception class.
 *
 * @author "Simone Cornacchia - seancrow76@gmail.com, simone.cornacchia@consulenti.csi.it (CSI:71740)"
 */
public abstract class IrideSecuritySystemException extends RuntimeException {

    private static final long serialVersionUID = -4141465964677985419L;

    /**
     * Constructor.
     */
    protected IrideSecuritySystemException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message
     */
    protected IrideSecuritySystemException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     */
    protected IrideSecuritySystemException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     * @param cause
     */
    protected IrideSecuritySystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
