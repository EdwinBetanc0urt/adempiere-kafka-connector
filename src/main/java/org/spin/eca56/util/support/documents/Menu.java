/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 or later of the                                  *
 * GNU General Public License as published                                    *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2023 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpya.com                                  *
 *****************************************************************************/
package org.spin.eca56.util.support.documents;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.core.domains.models.I_AD_Browse;
import org.adempiere.core.domains.models.I_AD_Form;
import org.adempiere.core.domains.models.I_AD_Menu;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Window;
import org.adempiere.core.domains.models.I_AD_Workflow;
import org.adempiere.model.MBrowse;
import org.compiere.model.MClientInfo;
import org.compiere.model.MForm;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.MRole;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.model.MWindow;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.compiere.wf.MWorkflow;
import org.spin.eca56.util.support.DictionaryDocument;

/**
 * 	The document class for Menu sender
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Menu extends DictionaryDocument {

	public static final String CHANNEL = "menu";
	public static final String KEY = "new";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	/**
	 * Add children to menu
	 * @param context
	 * @param builder
	 * @param node
	 */
	private void addChildren(MTreeNode node, Map<String, Object> parent) {
		Enumeration<?> childrens = node.children();
		List<Map<String, Object>> children = new ArrayList<>();
		while (childrens.hasMoreElements()) {
			MTreeNode childNode = (MTreeNode)childrens.nextElement();
			Map<String, Object> child = convertNode(childNode);
			addChildren(childNode, child);
			children.add(child);
		}
		parent.put("children", children);
	}
	
	private Map<String, Object> convertNode(MTreeNode node) {
		MMenu menu = MMenu.getFromId(Env.getCtx(), node.getNode_ID());
		Map<String, Object> detail = new HashMap<>();
		detail.put("id", menu.getAD_Menu_ID());
		detail.put("uuid", menu.getUUID());
		detail.put("name", menu.get_Translation(I_AD_Menu.COLUMNNAME_Name, getLanguage()));
		detail.put("description", menu.get_Translation(I_AD_Menu.COLUMNNAME_Description, getLanguage()));
		detail.put("parent_id", node.getParent_ID());
		detail.put("sequence", Integer.parseInt(node.getSeqNo()));
		detail.put("is_read_only", menu.isReadOnly());
		detail.put("is_sales_transaction", menu.isSOTrx());
		detail.put("is_summary", menu.isSummary());
		detail.put("action", menu.getAction());
		//	
		if(!Util.isEmpty(menu.getAction())) {
			if(menu.getAction().equals(MMenu.ACTION_Form)) {
				if(menu.getAD_Form_ID() > 0) {
					MForm form = new MForm(menu.getCtx(), menu.getAD_Form_ID(), null);
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", form.getAD_Form_ID());
					referenceDetail.put("uuid", form.getUUID());
					referenceDetail.put("name", form.get_Translation(I_AD_Form.COLUMNNAME_Name, getLanguage()));
					referenceDetail.put("description", form.get_Translation(I_AD_Form.COLUMNNAME_Description, getLanguage()));
					referenceDetail.put("help", form.get_Translation(I_AD_Form.COLUMNNAME_Help, getLanguage()));
					detail.put("form", referenceDetail);
					detail.put("action_id", form.getAD_Form_ID());
					detail.put("action_uuid", form.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Window)) {
				if(menu.getAD_Window_ID() > 0) {
					MWindow window = new MWindow(menu.getCtx(), menu.getAD_Window_ID(), null);
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", window.getAD_Window_ID());
					referenceDetail.put("uuid", window.getUUID());
					referenceDetail.put("name", window.get_Translation(I_AD_Window.COLUMNNAME_Name, getLanguage()));
					referenceDetail.put("description", window.get_Translation(I_AD_Window.COLUMNNAME_Description, getLanguage()));
					referenceDetail.put("help", window.get_Translation(I_AD_Window.COLUMNNAME_Help, getLanguage()));
					detail.put("window", referenceDetail);
					detail.put("action_id", window.getAD_Window_ID());
					detail.put("action_uuid", window.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Process)
				|| menu.getAction().equals(MMenu.ACTION_Report)) {
				if(menu.getAD_Process_ID() > 0) {
					MProcess process = MProcess.get(menu.getCtx(), menu.getAD_Process_ID());
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", process.getAD_Process_ID());
					referenceDetail.put("uuid", process.getUUID());
					referenceDetail.put("name", process.get_Translation(I_AD_Process.COLUMNNAME_Name, getLanguage()));
					referenceDetail.put("description", process.get_Translation(I_AD_Process.COLUMNNAME_Description, getLanguage()));
					referenceDetail.put("help", process.get_Translation(I_AD_Process.COLUMNNAME_Help, getLanguage()));
					detail.put("process", referenceDetail);
					detail.put("action_id", process.getAD_Process_ID());
					detail.put("action_uuid", process.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_SmartBrowse)) {
				if(menu.getAD_Browse_ID() > 0) {
					MBrowse smartBrowser = MBrowse.get(menu.getCtx(), menu.getAD_Browse_ID());
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", smartBrowser.getAD_Browse_ID());
					referenceDetail.put("uuid", smartBrowser.getUUID());
					referenceDetail.put("name", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Name, getLanguage()));
					referenceDetail.put("description", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Description, getLanguage()));
					referenceDetail.put("help", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Help, getLanguage()));
					detail.put("browse", referenceDetail);
					detail.put("action_id", smartBrowser.getAD_Browse_ID());
					detail.put("action_uuid", smartBrowser.getUUID());
				}
			} else if(menu.getAction().equals(MMenu.ACTION_WorkFlow)) {
				if(menu.getAD_Workflow_ID() > 0) {
					MWorkflow workflow = MWorkflow.get(menu.getCtx(), menu.getAD_Workflow_ID());
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", workflow.getAD_Workflow_ID());
					referenceDetail.put("uuid", workflow.getUUID());
					referenceDetail.put("name", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Name, getLanguage()));
					referenceDetail.put("description", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Description, getLanguage()));
					referenceDetail.put("help", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Help, getLanguage()));
					detail.put("workflow", referenceDetail);
					detail.put("action_id", workflow.getAD_Workflow_ID());
					detail.put("action_uuid", workflow.getUUID());
				}
			}
		}
		return detail;
	}
	
	@Override
	public DictionaryDocument withEntity(PO entity) {
		MMenu menu = (MMenu) entity;
		MClientInfo clientInfo = MClientInfo.get(menu.getCtx());
		int currentRoleId = Env.getAD_Role_ID(menu.getCtx());
		if(getRoleId() >= 0) {
			Env.setContext(Env.getCtx(), "#AD_Role_ID", getRoleId());
		}
		MRole role = MRole.getDefault(menu.getCtx(), true);
		int treeId = role.getAD_Tree_Menu_ID();
		if(treeId == 0) {
			treeId = clientInfo.getAD_Tree_Menu_ID();
		}
		MTree tree = new MTree(menu.getCtx(), treeId, false, false, null, null);
		Env.setContext(Env.getCtx(), "#AD_Role_ID", currentRoleId);
		MTreeNode rootNode = tree.getRoot();
		return withNode(rootNode.findNode(menu.getAD_Menu_ID()));
	}
	
	public Menu withNode(MTreeNode node) {
		if(node == null) {
			return this;
		}
		Enumeration<?> childrens = node.children();
		Map<String, Object> documentDetail = convertNode(node);
		List<Map<String, Object>> children = new ArrayList<>();
		while (childrens.hasMoreElements()) {
			MTreeNode childNode = (MTreeNode)childrens.nextElement();
			Map<String, Object> child = convertNode(childNode);
			//	Explode child
			addChildren(childNode, child);
			children.add(child);
		}
		documentDetail.put("children", children);
		putDocument(documentDetail);
		return this;
	}
	
	private Menu() {
		super();
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static Menu newInstance() {
		return new Menu();
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
}
