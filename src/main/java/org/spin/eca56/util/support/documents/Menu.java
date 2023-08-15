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
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.compiere.wf.MWorkflow;
import org.spin.eca56.util.support.IGenericDictionaryDocument;

/**
 * 	The document class for Menu sender
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Menu implements IGenericDictionaryDocument {

	public static final String CHANNEL = "menu";
	public static final String KEY = "new";
	private Map<String, Object> document;
	private int clientId = -1;
	private int roleId = -1;
	private int userId = -1;
	private String language;
	
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Map<String, Object> getValues() {
		return document;
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
		detail.put("name", menu.get_Translation(I_AD_Menu.COLUMNNAME_Name, language));
		detail.put("description", menu.get_Translation(I_AD_Menu.COLUMNNAME_Description, language));
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
					referenceDetail.put("name", form.get_Translation(I_AD_Form.COLUMNNAME_Name, language));
					referenceDetail.put("description", form.get_Translation(I_AD_Form.COLUMNNAME_Description, language));
					referenceDetail.put("help", form.get_Translation(I_AD_Form.COLUMNNAME_Help, language));
					detail.put("form", referenceDetail);
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Window)) {
				if(menu.getAD_Window_ID() > 0) {
					MWindow window = new MWindow(menu.getCtx(), menu.getAD_Window_ID(), null);
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", window.getAD_Window_ID());
					referenceDetail.put("uuid", window.getUUID());
					referenceDetail.put("name", window.get_Translation(I_AD_Window.COLUMNNAME_Name, language));
					referenceDetail.put("description", window.get_Translation(I_AD_Window.COLUMNNAME_Description, language));
					referenceDetail.put("help", window.get_Translation(I_AD_Window.COLUMNNAME_Help, language));
					detail.put("window", referenceDetail);
				}
			} else if(menu.getAction().equals(MMenu.ACTION_Process)
				|| menu.getAction().equals(MMenu.ACTION_Report)) {
				if(menu.getAD_Process_ID() > 0) {
					MProcess process = MProcess.get(menu.getCtx(), menu.getAD_Process_ID());
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", process.getAD_Process_ID());
					referenceDetail.put("uuid", process.getUUID());
					referenceDetail.put("name", process.get_Translation(I_AD_Process.COLUMNNAME_Name, language));
					referenceDetail.put("description", process.get_Translation(I_AD_Process.COLUMNNAME_Description, language));
					referenceDetail.put("help", process.get_Translation(I_AD_Process.COLUMNNAME_Help, language));
					detail.put("process", referenceDetail);
				}
			} else if(menu.getAction().equals(MMenu.ACTION_SmartBrowse)) {
				if(menu.getAD_Browse_ID() > 0) {
					MBrowse smartBrowser = MBrowse.get(menu.getCtx(), menu.getAD_Browse_ID());
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", smartBrowser.getAD_Browse_ID());
					referenceDetail.put("uuid", smartBrowser.getUUID());
					referenceDetail.put("name", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Name, language));
					referenceDetail.put("description", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Description, language));
					referenceDetail.put("help", smartBrowser.get_Translation(I_AD_Browse.COLUMNNAME_Help, language));
					detail.put("browse", referenceDetail);
				}
			} else if(menu.getAction().equals(MMenu.ACTION_WorkFlow)) {
				if(menu.getAD_Workflow_ID() > 0) {
					MWorkflow workflow = MWorkflow.get(menu.getCtx(), menu.getAD_Workflow_ID());
					Map<String, Object> referenceDetail = new HashMap<>();
					referenceDetail.put("id", workflow.getAD_Workflow_ID());
					referenceDetail.put("uuid", workflow.getUUID());
					referenceDetail.put("name", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Name, language));
					referenceDetail.put("description", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Description, language));
					referenceDetail.put("help", workflow.get_Translation(I_AD_Workflow.COLUMNNAME_Help, language));
					detail.put("workflow", referenceDetail);
				}
			}
		}
		//	Generic Detail
		detail.put("language", language);
		if(clientId >= 0) {
			detail.put("client_id", clientId);
		}
		if(roleId >= 0) {
			detail.put("role_id", roleId);
		}
		if(userId >= 0) {
			detail.put("user_id", userId);
		}
		detail.put("index_value", getIndexValue());
		return detail;
	}
	
	public Menu withMenu(MMenu menu) {
		MClientInfo clientInfo = MClientInfo.get(menu.getCtx());
		int currentRoleId = Env.getAD_Role_ID(menu.getCtx());
		if(roleId >= 0) {
			Env.setContext(Env.getCtx(), "#AD_Role_ID", roleId);
		}
		MRole.getDefault(menu.getCtx(), true);
		MTree tree = new MTree(menu.getCtx(), clientInfo.getAD_Tree_Menu_ID(), false, false, null, null);
		Env.setContext(Env.getCtx(), "#AD_Role_ID", currentRoleId);
		MTreeNode rootNode = tree.getRoot();
		return withNode(rootNode.findNode(menu.getAD_Menu_ID()));
	}
	
	public Menu withNode(MTreeNode node) {
		if(node == null) {
			return this;
		}
		document = new HashMap<>();
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
		document.put(CHANNEL, documentDetail);
		return this;
	}
	
	private Menu() {
		language = Env.getAD_Language(Env.getCtx());
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
	
	private String getIndexValue() {
		StringBuffer channel = new StringBuffer(CHANNEL);
		if(!language.equals("en_US")) {
			channel.append("_").append(language);
		}
		if(clientId > 0) {
			channel.append("_").append(clientId);
		}
		if(roleId > 0) {
			channel.append("_").append(roleId);
		}
		if(userId > 0) {
			channel.append("_").append(userId);
		}
		return channel.toString().toLowerCase();
	}

	@Override
	public Menu withClientId(int clientId) {
		this.clientId = clientId;
		return this;
	}

	@Override
	public Menu withRoleId(int roleId) {
		this.roleId = roleId;
		return this;
	}

	@Override
	public Menu withUserId(int userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public Menu withLanguage(String language) {
		this.language = language;
		return this;
	}
	
	
}
