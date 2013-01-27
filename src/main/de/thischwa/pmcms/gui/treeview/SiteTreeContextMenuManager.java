/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://poormans.sourceforge.net
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
package de.thischwa.pmcms.gui.treeview;

import java.util.List;


import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.thischwa.pmcms.configuration.InitializationManager;
import de.thischwa.pmcms.configuration.resource.LabelHolder;
import de.thischwa.pmcms.gui.BrowserManager;
import de.thischwa.pmcms.gui.listener.ListenerAddLevelOrPageOrGalleryOrImage;
import de.thischwa.pmcms.gui.listener.ListenerDeletePersitentPojo;
import de.thischwa.pmcms.gui.listener.ListenerEditPage;
import de.thischwa.pmcms.gui.listener.ListenerEditPersistentPojoProperties;
import de.thischwa.pmcms.gui.listener.ListenerImageBulkImport;
import de.thischwa.pmcms.gui.listener.ListenerMoveOrderabelToPosition;
import de.thischwa.pmcms.gui.listener.ListenerUploadSiteWithoutExport;
import de.thischwa.pmcms.livecycle.PojoHelper;
import de.thischwa.pmcms.model.IOrderable;
import de.thischwa.pmcms.model.InstanceUtil;
import de.thischwa.pmcms.model.domain.OrderableInfo;
import de.thischwa.pmcms.model.domain.PoInfo;
import de.thischwa.pmcms.model.domain.PoStructurTools;
import de.thischwa.pmcms.model.domain.pojo.APoormansObject;
import de.thischwa.pmcms.model.domain.pojo.ASiteResource;
import de.thischwa.pmcms.model.domain.pojo.Gallery;
import de.thischwa.pmcms.model.domain.pojo.Image;
import de.thischwa.pmcms.model.domain.pojo.Level;
import de.thischwa.pmcms.model.domain.pojo.Macro;
import de.thischwa.pmcms.model.domain.pojo.Page;
import de.thischwa.pmcms.model.domain.pojo.Site;
import de.thischwa.pmcms.model.domain.pojo.Template;
import de.thischwa.pmcms.view.ViewMode;

/**
 * Manager for the context menu of the treeviewer.
 * 
 * @version $Id: SiteTreeContextMenuManager.java 2210 2012-06-17 13:01:49Z th-schwarz $
 * @author <a href="mailto:th-schwarz@users.sourceforge.net">Thilo Schwarz</a>
 */
public class SiteTreeContextMenuManager {
	private static Logger logger = Logger.getLogger(SiteTreeContextMenuManager.class);
	private Menu menu;
	private PojoHelper pojoHolder = new PojoHelper();
	private BrowserManager browserManager = InitializationManager.getBean(BrowserManager.class);
	private static APoormansObject<?> clipboardObject = null;

	public SiteTreeContextMenuManager(Menu menu) {
		this.menu = menu;
		for (MenuItem item : menu.getItems())
			item.dispose();
	}

	public void buildMenuForSite(final Site site) {
		logger.debug("Generate context menu for TYPE site."); //$NON-NLS-1$
		buildForPo(site);
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem menuItemAddLevel = new MenuItem(menu, SWT.PUSH);
		menuItemAddLevel.setText(LabelHolder.get("treecontextmenu.addlevel")); //$NON-NLS-1$
		menuItemAddLevel.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Level.class));
		if (CollectionUtils.isEmpty(site.getPages())) { // just one 'frontpage' is allowed
			MenuItem menuItemAddPage = new MenuItem(menu, SWT.PUSH);
			menuItemAddPage.setText(LabelHolder.get("treecontextmenu.addpage")); //$NON-NLS-1$
			menuItemAddPage.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Page.class));
			MenuItem menuItemAddGallery = new MenuItem(menu, SWT.PUSH);
			menuItemAddGallery.setText(LabelHolder.get("treecontextmenu.addgallery")); //$NON-NLS-1$
			menuItemAddGallery.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Gallery.class));
		}
		if (InitializationManager.isAdmin()) {
			new MenuItem(menu, SWT.SEPARATOR);
			MenuItem menuItemTransferWithoutExport = new MenuItem(menu, SWT.PUSH);
			menuItemTransferWithoutExport.setText(LabelHolder.get("treecontextmenu.transferwithoutexport")); //$NON-NLS-1$
			menuItemTransferWithoutExport.addSelectionListener(new ListenerUploadSiteWithoutExport(site));
		}
	}

	public void buildMenuForLevel(final Level level) {
		logger.debug("Generate context menu for TYPE level."); //$NON-NLS-1$
		buildForPo(level);
		buildForOrderable(level);
		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem menuItemAddLevel = new MenuItem(menu, SWT.PUSH);
		menuItemAddLevel.setText(LabelHolder.get("treecontextmenu.addlevel")); //$NON-NLS-1$
		menuItemAddLevel.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Level.class));
		MenuItem menuItemAddPage = new MenuItem(menu, SWT.PUSH);
		menuItemAddPage.setText(LabelHolder.get("treecontextmenu.addpage")); //$NON-NLS-1$
		menuItemAddPage.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Page.class));
		MenuItem menuItemAddGallery = new MenuItem(menu, SWT.PUSH);
		menuItemAddGallery.setText(LabelHolder.get("treecontextmenu.addgallery")); //$NON-NLS-1$
		menuItemAddGallery.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Gallery.class));
	}

	public void buildMenuForPageOrGallery(final Page page) {
		logger.debug("Generate context menu for TYPE page."); //$NON-NLS-1$
		buildForPo(page);
		MenuItem menuItemEditContent = new MenuItem(menu, SWT.PUSH);
		menuItemEditContent.setText(LabelHolder.get("treecontextmenu.editcontent")); //$NON-NLS-1$
		menuItemEditContent.addSelectionListener(new ListenerEditPage());
		buildForOrderable(page);
		if (InstanceUtil.isGallery(page)) {
			new MenuItem(menu, SWT.SEPARATOR);
			Gallery gallery = (Gallery) page;
			MenuItem menuItemAddImage = new MenuItem(menu, SWT.PUSH);
			menuItemAddImage.setText(LabelHolder.get("treecontextmenu.addimage")); //$NON-NLS-1$
			menuItemAddImage.addSelectionListener(new ListenerAddLevelOrPageOrGalleryOrImage(Image.class));
			MenuItem menuItemBulkImportImage = new MenuItem(menu, SWT.PUSH);
			menuItemBulkImportImage.setText(LabelHolder.get("treecontextmenu.imagebulkimport")); //$NON-NLS-1$
			menuItemBulkImportImage.addSelectionListener(new ListenerImageBulkImport(gallery));
		}
	}

	public void buildMenuForImage(final Image image) {
		logger.debug("Generate context menu for TYPE image."); //$NON-NLS-1$
		buildForPo(image);
		buildForOrderable(image);
	}

	private void buildForPo(final APoormansObject<?> po) {
		logger.debug("Generate context menu for TYPE Ipo."); //$NON-NLS-1$
		final TreeViewManager treeViewManager = InitializationManager.getBean(TreeViewManager.class);
		MenuItem menuItemEdit = new MenuItem(menu, SWT.PUSH);
		menuItemEdit.setText(LabelHolder.get("treecontextmenu.editproperties")); //$NON-NLS-1$
		menuItemEdit.addSelectionListener(new ListenerEditPersistentPojoProperties(po));
		MenuItem menuItemDelete = new MenuItem(menu, SWT.PUSH);
		menuItemDelete.setText(LabelHolder.get("treecontextmenu.delete")); //$NON-NLS-1$
		menuItemDelete.addSelectionListener(new ListenerDeletePersitentPojo(po));

		if (isCutAllowed(po) || isPastAllowed(po))
			new MenuItem(menu, SWT.SEPARATOR);
		if (isCutAllowed(po)) {
			MenuItem menuItemCut = new MenuItem(menu, SWT.PUSH);
			menuItemCut.setText(LabelHolder.get("treecontextmenu.cut")); //$NON-NLS-1$
			menuItemCut.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					logger.debug("SEL cut");
					APoormansObject<?> selectedPojo = treeViewManager.getSelectedTreeSitepo();
					if (selectedPojo == null || InstanceUtil.isSite(selectedPojo)) {
						logger.error("Can't handle object: " + selectedPojo);
						throw new IllegalArgumentException("Wrong object typ!");
					}
					setClipboardObject(selectedPojo);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		if (isPastAllowed(po)) {
			MenuItem menuItemPast = new MenuItem(menu, SWT.PUSH);
			menuItemPast.setText(LabelHolder.get("treecontextmenu.pasteof")
					.concat(" [").concat(clipboardObject.getDecorationString()).concat("]")); //$NON-NLS-1$ //$NON-NLS-2$
			menuItemPast.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					logger.debug("SEL paste");
					APoormansObject<?> parentpo = treeViewManager.getSelectedTreeSitepo();
					APoormansObject<?> cbo = getClipboardObject();
					setClipboardObject(null);
					PoStructurTools.changeParent(cbo, parentpo);

					pojoHolder.putpo(cbo);
					treeViewManager.fillAndExpands(cbo);
					if (InstanceUtil.isRenderable(cbo))
						browserManager.view(cbo, ViewMode.PREVIEW);

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
	}

	private void buildForOrderable(IOrderable<?> orderable) {
		@SuppressWarnings("unchecked")
		List<IOrderable<?>> family = (List<IOrderable<?>>) orderable.getFamily();
		int pos = family.indexOf(orderable);
		logger.debug("Generate context menu for TYPE IOrderables."); //$NON-NLS-1$
		if (OrderableInfo.hasPrevious(orderable) || OrderableInfo.hasNext(orderable))
			new MenuItem(menu, SWT.SEPARATOR);
		if (OrderableInfo.hasPrevious(orderable)) {
			MenuItem menuItemMoveUp = new MenuItem(menu, SWT.PUSH);
			menuItemMoveUp.setText(LabelHolder.get("treecontextmenu.moveup")); //$NON-NLS-1$
			menuItemMoveUp.addSelectionListener(new ListenerMoveOrderabelToPosition(orderable, pos-1));
		}
		int sisterCount = orderable.getFamily().size();
		if (sisterCount > 3) {
			Menu menuMoveTo = new Menu(menu);
			MenuItem menuItemSubPos = new MenuItem(menu, SWT.CASCADE);
			menuItemSubPos.setMenu(menuMoveTo);
			menuItemSubPos.setText(LabelHolder.get("treecontextmenu.moveto")); //$NON-NLS-1$
			for (int i = 0; i < sisterCount; i++) {
				if (i != pos) {
					MenuItem menuItemMoveToPos = new MenuItem(menuMoveTo, SWT.PUSH);
					menuItemMoveToPos.setText(String.valueOf(i));
					menuItemMoveToPos.addSelectionListener(new ListenerMoveOrderabelToPosition(orderable, i));
				}
			}
		}
		if (OrderableInfo.hasNext(orderable)) {
			MenuItem menuItemMoveDown = new MenuItem(menu, SWT.PUSH);
			menuItemMoveDown.setText(LabelHolder.get("treecontextmenu.movedown")); //$NON-NLS-1$
			menuItemMoveDown.addSelectionListener(new ListenerMoveOrderabelToPosition(orderable, pos+1));
		}
	}

	private boolean isPastAllowed(final APoormansObject<?> po) {
		APoormansObject<?> cbo = getClipboardObject();
		if (cbo == null)
			return false;
		return PoInfo.checkParentChildRelationship(po, cbo);
	}

	private boolean isCutAllowed(final APoormansObject<?> po) {
		return !(po instanceof Site);
	}

	private static APoormansObject<?> getClipboardObject() {
		return clipboardObject;
	}

	private static void setClipboardObject(APoormansObject<?> clipboardObject) {
		SiteTreeContextMenuManager.clipboardObject = clipboardObject;
	}

	public void buildForMacro() {
		logger.debug("Generate context menu for TYPE macro."); //$NON-NLS-1$
		if (menu.getItemCount() > 0)
			new MenuItem(menu, SWT.SEPARATOR);
		MenuItem menuItemAddMacro = new MenuItem(menu, SWT.PUSH);
		menuItemAddMacro.setText("add macro");
		menuItemAddMacro.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Macro macro = new Macro();
				macro.setParent(pojoHolder.getSite());
				browserManager.view(macro, ViewMode.EDIT);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});		
	}

	public void buildForTemplate() {
		logger.debug("Generate context menu for TYPE template."); //$NON-NLS-1$
		MenuItem menuItemAddTemplate = new MenuItem(menu, SWT.PUSH);
		menuItemAddTemplate.setText("add template");
		menuItemAddTemplate.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Template template = new Template();
				template.setParent(pojoHolder.getSite());
				browserManager.view(template, ViewMode.EDIT);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
	
	public void buildForSiteResource(ASiteResource siteResource) {
		MenuItem menuItemDelete = new MenuItem(menu, SWT.PUSH);
		menuItemDelete.setText("delete resource");
		menuItemDelete.addSelectionListener(new ListenerDeletePersitentPojo(siteResource));
	}
}
