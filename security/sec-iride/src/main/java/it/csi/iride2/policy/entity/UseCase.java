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
package it.csi.iride2.policy.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <code>IRIDE</code> "UseCase" entity object.
 *
 * @author "Simone Cornacchia - seancrow76@gmail.com, simone.cornacchia@consulenti.csi.it (CSI:71740)"
 */
public final class UseCase {

    /**
     * <code>IRIDE</code> "UseCase" string representation.
     */
    private static final String TO_STRING_FORMAT = "UseCase[%s,%s]";

    /**
     * Application.
     */
    private Application appId;

    /**
     * Use Case id.
     */
    private String id;

    /**
     * Constructor.
     */
    public UseCase() {
        /* NOP */
    }

    /**
     * Constructor.
     *
     * @param applicationId
     * @param id
     */
    public UseCase(String applicationId, String id) {
        this(new Application(applicationId), id);
    }

    /**
     * Constructor.
     *
     * @param applicationId
     * @param id
     */
    public UseCase(Application applicationId, String id) {
        this.appId = applicationId;
        this.id = id;
    }

    /**
     * @return the applicationId
     */
    public Application getAppId() {
        return this.appId;
    }

    /**
     * @param appId the applicationId to set
     */
    public void setAppId(Application appId) {
        this.appId = appId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.appId)
               .append(this.id);

        return builder.toHashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        final UseCase other = (UseCase) obj;

        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.appId, other.appId)
               .append(this.id, other.id);

        return builder.isEquals();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, this.appId, this.id);
    }

}
