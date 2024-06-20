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
package org.spin.eca56.util.support;

import java.util.HashMap;
import java.util.Map;

import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * 	Interface for determinate if is a document to send, note that this is a mapping of values.
 * 	For Dictionary exists some variants like Client, Role, User and Language
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public abstract class DictionaryDocument implements IGenericDictionaryDocument {

	private String clientId;
	private String roleId;
	private String userId;
	private String language;
	private static final String KEY = "new";
	private String channel = "none";
	private Map<String, Object> document;

	public DictionaryDocument() {
		withLanguage(Env.getAD_Language(Env.getCtx()));
		document = new HashMap<>();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getChannel() {
		return channel;
	}

	@Override
	public Map<String, Object> getValues() {
		return document;
	}

	public void putDocument(Map<String, Object> document) {
		//	Generic Detail
		if(!Util.isEmpty(getLanguage())) {
			document.put("language", getLanguage());
		}
		if(!Util.isEmpty(getClientId())) {
			document.put("client_id", getClientId());
		}
		if(!Util.isEmpty(getRoleId())) {
			document.put("role_id", getRoleId());
		}
		if(!Util.isEmpty(getUserId())) {
			document.put("user_id", getUserId());
		}
		document.put("index_value", getIndexValue());
		this.document.put("document", document);
	}

	public DictionaryDocument withClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public DictionaryDocument withRoleId(String roleId) {
		this.roleId = roleId;
		return this;
	}

	public DictionaryDocument withUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public DictionaryDocument withLanguage(String language) {
		this.language = language;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public String getRoleId() {
		return roleId;
	}

	public String getUserId() {
		return userId;
	}

	public String getLanguage() {
		return language;
	}

	private String getIndexValue() {
		StringBuffer channel = new StringBuffer(getChannel());
		if(!Util.isEmpty(getLanguage())) {
			channel.append("_").append(getLanguage());
		}
		if(!Util.isEmpty(getClientId())) {
			channel.append("_").append(getClientId());
		}
		if(!Util.isEmpty(getRoleId())) {
			channel.append("_").append(getRoleId());
		}
		if(!Util.isEmpty(getUserId())) {
			channel.append("_").append(getUserId());
		}
		return channel.toString().toLowerCase();
	}

	public DictionaryDocument withEntity(PO entity) {
		channel = entity.get_TableName().toLowerCase();
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", entity.get_ID());
		documentDetail.put("uuid", entity.get_UUID());
		documentDetail.put("display_value", entity.getDisplayValue());
		putDocument(documentDetail);
		return this;
	}

}
