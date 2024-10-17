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

/**
 * 	The Stub class for reference
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class ReferenceValues {
	private String tableName;
	private String embeddedContextColumn;
	private int referenceId;
	
	private ReferenceValues(int referenceId, String tableName, String embeddedContextColumn) {
		this.referenceId = referenceId;
		this.tableName = tableName;
		this.embeddedContextColumn = embeddedContextColumn;
	}
	
	public static ReferenceValues newInstance(int referenceId, String tableName, String embeddedContextColumn) {
		return new ReferenceValues(referenceId, tableName, embeddedContextColumn);
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getEmbeddedContextColumn() {
		return embeddedContextColumn;
	}
	
	public int getReferenceId() {
		return referenceId;
	}

}
