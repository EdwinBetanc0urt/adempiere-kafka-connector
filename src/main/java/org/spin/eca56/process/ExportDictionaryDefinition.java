/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.                                     *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net                                                  *
 * or https://github.com/adempiere/adempiere/blob/develop/license.html        *
 *****************************************************************************/

package org.spin.eca56.process;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.adempiere.core.domains.models.I_AD_Browse;
import org.adempiere.core.domains.models.I_AD_Form;
import org.adempiere.core.domains.models.I_AD_Menu;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Window;
import org.adempiere.model.MBrowse;
import org.compiere.model.MClientInfo;
import org.compiere.model.MForm;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.spin.eca56.util.queue.ApplicationDictionary;
import org.spin.queue.util.QueueLoader;

/** 
 * 	Generated Process for (Export Dictionary Definition)
 *  @author Yamel Senih
 *  @author Edwin Betancourt
 *  @version Release 3.9.4
 */
public class ExportDictionaryDefinition extends ExportDictionaryDefinitionAbstract {
	
	private AtomicInteger counter = new AtomicInteger();

	@Override
	protected String doIt() throws Exception {
		//	For menu
		if(isExportMenu()) {
			exportMenuDefinition();
		}

		//	For Process Definition
		if(isExportProcess()) {
			exportProcessesDefinition();
		}

		//	For Windows Definition
		if(isExportWindows()) {
			exportWindowsDefinition();
		}

		//	For Browsers Definition
		if(isExportBrowsers()) {
			exportBrowsersDefinition();
		}

		//	For Forms Definition
		if(isExportForms()) {
			exportFormsDefinition();
		}

		//	
		return "@Created@ " + counter.get();
	}


	private void exportMenuDefinition() {
		if (this.getMenuId() > 0) {
			//	For only specific Menu node
			MMenu menuNode = new Query(
				getCtx(),
				I_AD_Menu.Table_Name,
				I_AD_Menu.COLUMNNAME_AD_Menu_ID + " = ?",
				get_TrxName()
			)
				.setParameters(this.getMenuId())
				.first()
			;
			QueueLoader.getInstance()
				.getQueueManager(ApplicationDictionary.CODE)
				.withEntity(menuNode)
				.addToQueue()
			;
		} else {
			//	For all tree nodes
			MClientInfo clientInfo = MClientInfo.get(getCtx(), getAD_Client_ID());
			if(clientInfo.getAD_Tree_Menu_ID() > 0) {
				addLog("@AD_Menu_ID@");
				MTree tree = new MTree(getCtx(), clientInfo.getAD_Tree_Menu_ID(), false, false, null, null);
				MTreeNode rootNode = tree.getRoot();
				Enumeration<?> childrens = rootNode.children();
				while (childrens.hasMoreElements()) {
					MTreeNode childNode = (MTreeNode) childrens.nextElement();
					QueueLoader.getInstance()
						.getQueueManager(ApplicationDictionary.CODE)
						.withEntity(MMenu.getFromId(getCtx(), childNode.getNode_ID()))
						.addToQueue()
					;
				}
				addLog(tree.getName());
				counter.incrementAndGet();
			}
		}
	}


	private void exportProcessesDefinition() {
		addLog("@AD_Process_ID@");

		// Add filter a specific Process
		String whereClause = "";
		List<Object> filtersList = new ArrayList<>();
		if (this.getADProcessId() > 0) {
			whereClause = "AD_Process_ID = ?";
			filtersList.add(this.getADProcessId());
		}
		new Query(
				getCtx(),
				I_AD_Process.Table_Name,
				whereClause,
				get_TrxName()
		)
			.setOnlyActiveRecords(true)
			.setParameters(filtersList)
			.getIDsAsList()
			.forEach(processId -> {
				MProcess process = new MProcess(getCtx(), processId, get_TrxName());
				QueueLoader.getInstance()
					.getQueueManager(ApplicationDictionary.CODE)
					.withEntity(process)
					.addToQueue()
				;
				addLog(process.getValue() + " - " + process.getName());
				counter.incrementAndGet();
		});
	}


	private void exportWindowsDefinition() {
		addLog("@AD_Window_ID@");

		// AAdd filter a specific Window
		String whereClause = "";
		List<Object> filtersList = new ArrayList<>();
		if (this.getWindowId() > 0) {
			whereClause = "AD_Window_ID = ?";
			filtersList.add(this.getWindowId());
		}
		new Query(
				getCtx(),
				I_AD_Window.Table_Name,
				whereClause,
				get_TrxName()
		)
			.setOnlyActiveRecords(true)
			.setParameters(filtersList)
			.getIDsAsList()
			.forEach(windowId -> {
				MWindow window = new MWindow(getCtx(), windowId, get_TrxName());
				QueueLoader.getInstance()
					.getQueueManager(ApplicationDictionary.CODE)
					.withEntity(window)
					.addToQueue()
				;
				addLog(window.getName());
				counter.incrementAndGet();
			})
		;
	}

	private void exportBrowsersDefinition() {
		addLog("@AD_Browse_ID@");

		// Add filter a specific Browse
		String whereClause = "";
		List<Object> filtersList = new ArrayList<>();
		if (this.getBrowseId() > 0) {
			whereClause = "AD_Browse_ID = ?";
			filtersList.add(this.getBrowseId());
		}
		new Query(
				getCtx(),
				I_AD_Browse.Table_Name,
				whereClause,
				get_TrxName()
		)
			.setOnlyActiveRecords(true)
			.setParameters(filtersList)
			.getIDsAsList()
			.forEach(browseId -> {
				MBrowse browser = new MBrowse(getCtx(), browseId, get_TrxName());
				QueueLoader.getInstance()
					.getQueueManager(ApplicationDictionary.CODE)
					.withEntity(browser)
					.addToQueue()
				;
				addLog(browser.getValue() + " - " + browser.getName());
				counter.incrementAndGet();
			})
		;
	}


	private void exportFormsDefinition() {
		addLog("@AD_Form_ID@");

		// Add filter a specific Form
		String whereClause = "";
		List<Object> filtersList = new ArrayList<>();
		if (this.getFormId() > 0) {
			whereClause = "AD_Form_ID = ?";
			filtersList.add(this.getFormId());
		}
		new Query(
				getCtx(),
				I_AD_Form.Table_Name,
				whereClause,
				get_TrxName()
			)
			.setOnlyActiveRecords(true)
			.setParameters(filtersList)
			.getIDsAsList()
			.forEach(formId -> {
				MForm form = new MForm(getCtx(), formId, get_TrxName());
				QueueLoader.getInstance()
					.getQueueManager(ApplicationDictionary.CODE)
					.withEntity(form)
					.addToQueue()
				;
				addLog(form.getClassname() + " - " + form.getName());
				counter.incrementAndGet();
			})
		;
	}

}
