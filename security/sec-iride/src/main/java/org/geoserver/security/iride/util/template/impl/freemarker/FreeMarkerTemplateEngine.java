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
package org.geoserver.security.iride.util.template.impl.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.geoserver.security.iride.util.IrideSecurityUtils;
import org.geoserver.security.iride.util.template.TemplateEngine;
import org.geoserver.security.iride.util.template.exception.TemplateEngineException;
import org.geotools.util.logging.Logging;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <a href="http://freemarker.org/"><code>FreeMarker</code></a> {@link TemplateEngine} implementation.
 *
 * @author "Simone Cornacchia - seancrow76@gmail.com, simone.cornacchia@consulenti.csi.it (CSI:71740)"
 */
public final class FreeMarkerTemplateEngine implements TemplateEngine {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logging.getLogger(FreeMarkerTemplateEngine.class);

    /**
     * <code>FreeMarker</code> Template {@link Configuration}.
     */
    private Configuration templateConfiguration;

    /**
     * <code>FreeMarker</code> template file extension, could be {@code null}.
     */
    private String templateExtension;

    /**
     * @return the templateConfiguration
     */
    public Configuration getTemplateConfiguration() {
        return this.templateConfiguration;
    }

    /**
     * @param templateConfiguration the templateConfiguration to set
     */
    public void setTemplateConfiguration(Configuration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }

    /**
     * @return the templateExtension
     */
    public String getTemplateExtension() {
        return this.templateExtension;
    }

    /**
     * @param templateExtension the templateExtension to set
     */
    public void setTemplateExtension(String templateExtension) {
        this.templateExtension = templateExtension;
    }

    /* (non-Javadoc)
     * @see org.geoserver.security.iride.util.template.TemplateEngine#process(java.lang.String, java.lang.Object, java.io.Writer)
     */
    @Override
    public void process(String template, Object context, Writer writer) throws IOException {
        final Template freeMarkerTemplate = this.getTemplate(template);

        LOGGER.fine("Processing template '" + template + "':\n" + freeMarkerTemplate + "\nwith context:\n" + context);

        try {
            freeMarkerTemplate.process(context == null ? TemplateModel.NOTHING : context, writer);
        } catch (TemplateException e) {
            /*
             * Rethrow any FreeMarker exception thrown here
             * encapsulated in a SimpleTemplateEngine runtime exception,
             * since there's nothing we can do if anything goes wrong at this point
             */
            throw new TemplateEngineException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.geoserver.security.iride.util.template.TemplateEngine#process(java.lang.String, java.lang.Object)
     */
    @Override
    public String process(String template, Object context) throws IOException {
        final StringWriter writer = new StringWriter();

        this.process(template, context, writer);

        final String output = writer.toString();

        LOGGER.fine("Processed template '" + template + "':\n" + output);

        return output;
    }

    /**
     * Returns the {@link Template} instance for the given template name.<br />
     * Subsequent calls with the same template name will return the same {@link Template} instance,
     * thanks to <code>FreeMarker</code> internal caching mechanism.
     *
     * @return the {@link Template} instance for the given template name
     * @throws IOException if anything goes wrong during {@link Template} instance resolution,
     *                     for example when there is no template with the given name to be found
     */
    private Template getTemplate(String templateName) throws IOException {
        if (this.templateConfiguration == null) {
            throw new IllegalStateException("FreeMarker Configuration is not defined");
        }

        return this.getTemplateConfiguration().getTemplate(
            IrideSecurityUtils.ensureFileWithExtension(templateName, this.getTemplateExtension()),
            this.getTemplateConfiguration().getDefaultEncoding()
        );
    }

}
