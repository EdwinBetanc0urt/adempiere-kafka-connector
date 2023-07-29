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
package org.spin.eca56.util.support.kafka;

import java.util.HashMap;
import java.util.Map;

import org.spin.eca56.util.support.IGenericDocument;

/**
 * 	Default test document used for test connection
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class TestDocument implements IGenericDocument {

	//	Some default documents key
	public static final String KEY = "test";
	
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Map<String, Object> getValues() {
		Map<String, Object> document = new HashMap<>();
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", 1000000);
		documentDetail.put("code", "code-001");
		documentDetail.put("name", "A test for queue");
		document.put("test", documentDetail);
		return document;
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static TestDocument newInstance() {
		return new TestDocument();
	}
}
