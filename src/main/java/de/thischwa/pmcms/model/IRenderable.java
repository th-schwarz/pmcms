package de.thischwa.pmcms.model;

import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.model.domain.pojo.TemplateType;

/**
 * Interface to identify the renderable pojos.
 */
public interface IRenderable {
	public Template getTemplate();
	public TemplateType getTemplateType();
}
