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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MTree;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.spin.eca56.util.support.DictionaryDocument;

/**
 * 	The document class for Menu Tree sender
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class MenuTree extends DictionaryDocument {

	public static final String CHANNEL = "menu_tree";
	public static final String KEY = "new";

	@Override
	public String getKey() {
		return KEY;
	}

	private Map<String, Object> convertNode(TreeNodeReference node) {
		Map<String, Object> detail = new HashMap<>();
		detail.put("node_id", node.getNodeId());
		detail.put("internal_id", node.getNodeId());
		detail.put("parent_id", node.getParentId());
		detail.put("sequence", node.getSequence());
		return detail;
	}

	@Override
	public DictionaryDocument withEntity(PO entity) {
		MTree tree = (MTree) entity;
		return withNode(tree);
	}

	private List<TreeNodeReference> getChildren(int treeId, int parentId) {
		String tableName = MTree.getNodeTableName(MTree.TREETYPE_Menu);
		final String sql = "SELECT tn.Node_ID, tn.SeqNo "
				+ "FROM " + tableName + " tn "
				+ "WHERE tn.AD_Tree_ID = ? "
				+ "AND tn.Parent_ID = ?";
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(treeId);
		parameters.add(parentId);
		List<TreeNodeReference> nodeIds = new ArrayList<TreeNodeReference>();
		DB.runResultSet(null, sql, parameters, resulset -> {
			while (resulset.next()) {
				nodeIds.add(TreeNodeReference.newInstance()
					.withNodeId(
						resulset.getInt("Node_ID")
					)
					.withParentId(parentId)
					.withSequence(
						resulset.getInt("SeqNo"))
					)
				;
			}
		}).onFailure(throwable -> {
			throw new AdempiereException(throwable);
		});
		return nodeIds;
	}

	public MenuTree withNode(MTree tree) {
		List<TreeNodeReference> children = getChildren(tree.getAD_Tree_ID(), 0);
		Map<String, Object> documentDetail = convertNode(TreeNodeReference.newInstance());
		documentDetail.put("internal_id", tree.getAD_Tree_ID());
		documentDetail.put("id", tree.getUUID());
		documentDetail.put("uuid", tree.getUUID());
		List<Map<String, Object>> childrenAsMap = new ArrayList<>();
		children.forEach(child -> {
			Map<String, Object> nodeAsMap = convertNode(child);
			//	Explode child
			addChildren(tree.getAD_Tree_ID(), child, nodeAsMap);
			childrenAsMap.add(nodeAsMap);
		});
		documentDetail.put("children", childrenAsMap);
		putDocument(documentDetail);
		return this;
	}

	/**
	 * Add children to menu
	 * @param context
	 * @param builder
	 * @param node
	 */
	private void addChildren(int treeId, TreeNodeReference node, Map<String, Object> parent) {
		List<TreeNodeReference> children = getChildren(treeId, node.getNodeId());
		List<Map<String, Object>> childrenAsMap = new ArrayList<>();
		children.forEach(child -> {
			Map<String, Object> nodeAsMap = convertNode(child);
			//	Explode child
			addChildren(treeId, child, nodeAsMap);
			childrenAsMap.add(nodeAsMap);
		});
		parent.put("children", childrenAsMap);
	}

	private MenuTree() {
		super();
	}

	/**
	 * Default instance
	 * @return
	 */
	public static MenuTree newInstance() {
		return new MenuTree();
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
