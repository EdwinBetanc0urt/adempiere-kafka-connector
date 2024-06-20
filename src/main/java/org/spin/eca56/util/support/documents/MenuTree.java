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

import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.model.PO;
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
		Map<String, Object> detail = new HashMap<>();
		detail.put("node_id", node.getNode_ID());
		detail.put("parent_id", node.getParent_ID());
		detail.put("sequence", Integer.parseInt(node.getSeqNo()));
		return detail;
	}
	
	@Override
	public DictionaryDocument withEntity(PO entity) {
		MTree tree = (MTree) entity;
		MTreeNode rootNode = tree.getRoot();
		return withNode(tree, rootNode);
	}
	
	public MenuTree withNode(MTree tree, MTreeNode node) {
		if(node == null) {
			return this;
		}
		Enumeration<?> childrens = node.children();
		Map<String, Object> documentDetail = convertNode(node);
		documentDetail.put("internal_id", tree.getAD_Tree_ID());
		documentDetail.put("id", tree.getUUID());
		documentDetail.put("uuid", tree.getUUID());
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
