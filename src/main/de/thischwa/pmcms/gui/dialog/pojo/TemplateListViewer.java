/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://pmcms.sourceforge.net
 * Copyright (C) 2004-2013 by Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 ******************************************************************************/
package de.thischwa.pmcms.gui.dialog.pojo;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.thischwa.pmcms.model.domain.pojo.Template;

/**
 * Object to encapsulating the SWT ListViewer.
 *
 * @version $Id$
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class TemplateListViewer {

	private final ListViewer listViewer;
	
	public TemplateListViewer(Composite parant, Object layoutData) {		
		listViewer = new ListViewer(parant, SWT.V_SCROLL | SWT.BORDER);
		listViewer.setContentProvider(new TemplateContentProvider());
		listViewer.setLabelProvider(new TemplateLabelProvider());
		listViewer.getList().setLayoutData(layoutData);
	}
	
	public void setInput(List<Template> templates) {
		listViewer.setInput(templates);
	}

	public Template getSelectedTemplate() {
		IStructuredSelection selection = (IStructuredSelection)listViewer.getSelection();
		if(selection.getFirstElement() != null)
			return (Template) selection.getFirstElement();
		return null;
	}
	
	public void setSelectedTemplate(Template selectedTemplate) {
		if(selectedTemplate == null)
			return;
		List<Template> list = new ArrayList<Template>(1);
		list.add(selectedTemplate);
		listViewer.setSelection(new StructuredSelection(list));
	}
	
	private class TemplateContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer arg0, Object oldObj, Object newObj) {
		}

		@Override
		public Object[] getElements(Object obj) {
			return (obj == null) ? null : ((List<?>)obj).toArray();
		}
	}
	
	private class TemplateLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener arg0) {		
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {		
		}

		@Override
		public Image getImage(Object obj) {
			return null;
		}

		@Override
		public String getText(Object obj) {
			return ((Template)obj).getName();
		}
	}
}
