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
 * Copyright (C) 2003-2024 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Edwin Betancourt, EdwinBetanc0urt@outlook.com              *
 *****************************************************************************/
package org.spin.eca56.util.support.documents;

/**
 * 	A stub class for nodes
 *	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class TreeNodeReference {
	private int nodeId;
	private int parentId;
	private int sequence;

	public static TreeNodeReference newInstance() {
		return new TreeNodeReference();
	}

	public int getNodeId() {
		return nodeId;
	}
	public TreeNodeReference withNodeId(int nodeId) {
		this.nodeId = nodeId;
		return this;
		
	}

	public int getParentId() {
		return parentId;
	}
	public TreeNodeReference withParentId(int parentId) {
		this.parentId = parentId;
		return this;
	}

	public int getSequence() {
		return sequence;
	}
	public TreeNodeReference withSequence(int sequence) {
		this.sequence = sequence;
		return this;
	}

}
