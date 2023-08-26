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

import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Window;
import org.adempiere.model.MBrowse;
import org.compiere.model.MClientInfo;
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
 *  @version Release 3.9.4
 */
public class ExportDictionaryDefinition extends ExportDictionaryDefinitionAbstract {
	
	private AtomicInteger counter = new AtomicInteger();

	@Override
	protected String doIt() throws Exception {
		//	For menu
		if(isExportMenu()) {
			MClientInfo clientInfo = MClientInfo.get(getCtx(), getAD_Client_ID());
			if(clientInfo.getAD_Tree_Menu_ID() > 0) {
				addLog("@AD_Menu_ID@");
				MTree tree = new MTree(getCtx(), clientInfo.getAD_Tree_Menu_ID(), false, false, null, null);
				MTreeNode rootNode = tree.getRoot();
				Enumeration<?> childrens = rootNode.children();
				while (childrens.hasMoreElements()) {
					MTreeNode childNode = (MTreeNode)childrens.nextElement();
					QueueLoader.getInstance()
					.getQueueManager(ApplicationDictionary.CODE)
					.withEntity(MMenu.getFromId(getCtx(), childNode.getNode_ID()))
					.addToQueue();
				}
				addLog(tree.getName());
				counter.incrementAndGet();
			}
		}
		//	For Process
		if(isExportProcess()) {
			addLog("@AD_Process_ID@");
			new Query(getCtx(), I_AD_Process.Table_Name, null, get_TrxName()).setOnlyActiveRecords(true).getIDsAsList().forEach(processId -> {
				MProcess process = new MProcess(getCtx(), processId, get_TrxName());
				QueueLoader.getInstance()
				.getQueueManager(ApplicationDictionary.CODE)
				.withEntity(process)
				.addToQueue();
				addLog(process.getValue() + " - " + process.getName());
				counter.incrementAndGet();
			});
		}
		//	For Windows
		if(isExportWindows()) {
			addLog("@AD_Window_ID@");
			new Query(getCtx(), I_AD_Window.Table_Name, null, get_TrxName()).setOnlyActiveRecords(true).getIDsAsList().forEach(windowId -> {
				MWindow window = new MWindow(getCtx(), windowId, get_TrxName());
				QueueLoader.getInstance()
				.getQueueManager(ApplicationDictionary.CODE)
				.withEntity(window)
				.addToQueue();
				addLog(window.getName());
				counter.incrementAndGet();
			});
		}
		//	For Browsers
		if(isExportBrowsers()) {
			addLog("@AD_Browse_ID@");
			new Query(getCtx(), I_AD_Process.Table_Name, null, get_TrxName()).setOnlyActiveRecords(true).getIDsAsList().forEach(browseId -> {
				MBrowse browser = new MBrowse(getCtx(), browseId, get_TrxName());
				QueueLoader.getInstance()
				.getQueueManager(ApplicationDictionary.CODE)
				.withEntity(browser)
				.addToQueue();
				addLog(browser.getValue() + " - " + browser.getName());
				counter.incrementAndGet();
			});
		}
		//	
		return "@Created@ " + counter.get();
	}
}