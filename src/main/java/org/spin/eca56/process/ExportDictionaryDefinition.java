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

import org.compiere.model.MClientInfo;
import org.compiere.model.MMenu;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.spin.eca56.util.queue.ApplicationDictionary;
import org.spin.queue.util.QueueLoader;

/** 
 * 	Generated Process for (Export Dictionary Definition)
 *  @author Yamel Senih
 *  @version Release 3.9.4
 */
public class ExportDictionaryDefinition extends ExportDictionaryDefinitionAbstract {

	@Override
	protected String doIt() throws Exception {
		MClientInfo clientInfo = MClientInfo.get(getCtx(), getAD_Client_ID());
		if(clientInfo.getAD_Tree_Menu_ID() > 0) {
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
			return "@AD_Menu_ID@ " + tree.getName();
		}
		return "@AD_Menu_ID@ @NotFound@";
	}
}