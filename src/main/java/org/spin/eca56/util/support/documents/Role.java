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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.core.domains.models.I_AD_Browse;
import org.adempiere.core.domains.models.I_AD_Form;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Window;
import org.adempiere.core.domains.models.I_AD_Workflow;
import org.adempiere.core.domains.models.I_PA_DashboardContent;
import org.compiere.model.MClientInfo;
import org.compiere.model.MRole;
import org.compiere.model.MTree;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.spin.eca56.util.support.DictionaryDocument;

/**
 * 	The document class for Role sender
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Role extends DictionaryDocument {

	public static final String CHANNEL = "role";
	public static final String KEY = "new";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	private Map<String, Object> convertRole(MRole role) {
		Map<String, Object> detail = new HashMap<>();
		detail.put("internal_id", role.getAD_Role_ID());
		detail.put("id", role.getUUID());
		detail.put("uuid", role.getUUID());
		detail.put("name", role.getName());
		detail.put("description", role.getDescription());
		MClientInfo clientInfo = MClientInfo.get(role.getCtx());
		int treeId = role.getAD_Tree_Menu_ID();
		if(treeId == 0) {
			treeId = clientInfo.getAD_Tree_Menu_ID();
		}
		MTree tree = MTree.get(role.getCtx(), treeId, null);
		detail.put("tree_id", treeId);
		detail.put("tree_uuid", tree.getUUID());
		detail.put("window_access", getWindowAccess(role));
		detail.put("process_access", getProcessAccess(role));
		detail.put("form_access", getFormAccess(role));
		detail.put("browser_access", getBrowserAccess(role));
		detail.put("workflow_access", getWorkflowAccess(role));
		detail.put("dashboard_access", getDashboardAccess(role));
		return detail;
	}
	
	private List<Integer> getWindowAccess(MRole role) {
		return new Query(role.getCtx(), I_AD_Window.Table_Name, "EXISTS(SELECT 1 FROM AD_Window_Access wa WHERE wa.AD_Window_ID = AD_Window.AD_Window_ID AND wa.AD_Role_ID = ?)", null)
				.setParameters(role.getAD_Role_ID())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getProcessAccess(MRole role) {
		return new Query(role.getCtx(), I_AD_Process.Table_Name, "EXISTS(SELECT 1 FROM AD_Process_Access wa WHERE wa.AD_Process_ID = AD_Process.AD_Process_ID AND wa.AD_Role_ID = ?)", null)
				.setParameters(role.getAD_Role_ID())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getFormAccess(MRole role) {
		return new Query(role.getCtx(), I_AD_Form.Table_Name, "EXISTS(SELECT 1 FROM AD_Form_Access wa WHERE wa.AD_Form_ID = AD_Form.AD_Form_ID AND wa.AD_Role_ID = ?)", null)
				.setParameters(role.getAD_Role_ID())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getBrowserAccess(MRole role) {
		return new Query(role.getCtx(), I_AD_Browse.Table_Name, "EXISTS(SELECT 1 FROM AD_Browse_Access wa WHERE wa.AD_Browse_ID = AD_Browse.AD_Browse_ID AND wa.AD_Role_ID = ?)", null)
				.setParameters(role.getAD_Role_ID())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getWorkflowAccess(MRole role) {
		return new Query(role.getCtx(), I_AD_Workflow.Table_Name, "EXISTS(SELECT 1 FROM AD_Workflow_Access wa WHERE wa.AD_Workflow_ID = AD_Workflow.AD_Workflow_ID AND wa.AD_Role_ID = ?)", null)
				.setParameters(role.getAD_Role_ID())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getDashboardAccess(MRole role) {
		return new Query(role.getCtx(), I_PA_DashboardContent.Table_Name, "EXISTS(SELECT 1 FROM AD_Dashboard_Access wa WHERE wa.PA_DashboardContent_ID = PA_DashboardContent.PA_DashboardContent_ID AND wa.AD_Role_ID = ?)", null)
				.setParameters(role.getAD_Role_ID())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	@Override
	public DictionaryDocument withEntity(PO entity) {
		MRole role = (MRole) entity;
		Map<String, Object> documentDetail = convertRole(role);
		putDocument(documentDetail);
		return this;
	}
	
	private Role() {
		super();
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static Role newInstance() {
		return new Role();
	}
	
	@Override
	public String getLanguage() {
		return null;
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
}
