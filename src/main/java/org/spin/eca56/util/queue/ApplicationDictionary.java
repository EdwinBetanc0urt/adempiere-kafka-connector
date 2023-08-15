/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2013 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Yamel Senih www.erpcya.com                                 *
 *****************************************************************************/
package org.spin.eca56.util.queue;

import java.util.List;

import org.adempiere.core.domains.models.I_AD_Language;
import org.adempiere.core.domains.models.I_AD_Menu;
import org.adempiere.core.domains.models.I_AD_Process;
import org.adempiere.core.domains.models.I_AD_Role;
import org.adempiere.core.domains.models.I_AD_User;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MLanguage;
import org.compiere.model.MMenu;
import org.compiere.model.MProcess;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.eca56.util.support.IGenericDictionaryDocument;
import org.spin.eca56.util.support.IGenericDocument;
import org.spin.eca56.util.support.IGenericSender;
import org.spin.eca56.util.support.documents.Menu;
import org.spin.eca56.util.support.documents.Process;
import org.spin.queue.model.MADQueue;
import org.spin.queue.util.QueueManager;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * Just a test for service  
 */
public class ApplicationDictionary extends QueueManager implements IEngineDictionaryManager {

	public static final String CODE = "ADM";
	
	@Override
	public void add(int queueId) {
		logger.fine("Queue Added: " + queueId);
		try {
			send(queueId);
			MADQueue queue = new MADQueue(getContext(), queueId, getTransactionName());
			queue.setProcessed(true);
			queue.saveEx();
		} catch (Throwable e) {
			logger.warning(e.getLocalizedMessage());
		}
	}

	@Override
	public void process(int queueId) {
		send(queueId);
	}
	
	public void send(int queueId) {
		PO entity = getEntity();
		if(entity != null) {
			IGenericSender sender = DefaultEngineQueueUtil.getEngineManager();
			if(sender != null) {
				getLanguages().forEach(languageId -> {
					MLanguage language = new MLanguage(getContext(), languageId, getTransactionName());
					IGenericDictionaryDocument documentByLanguage = getDocumentManager(entity, language.getAD_Language());
					if(documentByLanguage != null) {
						sender.send(documentByLanguage, documentByLanguage.getChannel());
					}
					getRoles().forEach(roleId -> {
						IGenericDictionaryDocument documentByRole = getDocumentManagerByRole(entity, language.getAD_Language(), roleId);
						if(documentByRole != null) {
							sender.send(documentByRole, documentByRole.getChannel());
						}
					});
					getUsers().forEach(userId -> {
						IGenericDictionaryDocument documentByUser = getDocumentManagerByUser(entity, language.getAD_Language(), userId);
						if(documentByUser != null) {
							sender.send(documentByUser, documentByUser.getChannel());
						}
					});
				});
			} else {
				throw new AdempiereException("@AD_AppRegistration_ID@ @NotFound@");
			}
			logger.fine("Queue Processed: " + queueId);
		}
	}
	
	private List<Integer> getRoles() {
		return new Query(getContext(), I_AD_Role.Table_Name, null, getTransactionName())
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getLanguages() {
		return new Query(getContext(), I_AD_Language.Table_Name, "(IsBaseLanguage = 'Y' OR IsSystemLanguage = 'Y')", getTransactionName())
				.setOnlyActiveRecords(true)
				.getIDsAsList();
	}
	
	private List<Integer> getUsers() {
		return new Query(getContext(), I_AD_User.Table_Name, "(EXISTS(SELECT 1 FROM AD_WindowCustom wc WHERE wc.AD_User_ID = AD_User.AD_User_ID AND wc.IsActive = 'Y') "
				+ "OR EXISTS(SELECT 1 FROM AD_ProcessCustom wc WHERE wc.AD_User_ID = AD_User.AD_User_ID AND wc.IsActive = 'Y') "
				+ "OR EXISTS(SELECT 1 FROM AD_BrowseCustom wc WHERE wc.AD_User_ID = AD_User.AD_User_ID AND wc.IsActive = 'Y'))", getTransactionName())
				.setOnlyActiveRecords(true)
				.setClient_ID()
				.getIDsAsList();
	}

	@Override
	public IGenericDocument getDocumentManager(PO entity) {
		if(entity == null) {
			return null;
		}
		String tableName = entity.get_TableName();
		if(Util.isEmpty(tableName)) {
			return null;
		}
		if(tableName.equals(I_AD_Process.Table_Name)) {
			return Process.newInstance().withProcess((MProcess) entity);
		} else if(tableName.equals(I_AD_Menu.Table_Name)) {
			return Menu.newInstance().withMenu((MMenu) entity);
		}
		return null;
	}

	@Override
	public IGenericDictionaryDocument getDocumentManager(PO entity, String language) {
		return Menu.newInstance().withLanguage(language).withClientId(Env.getAD_Client_ID(entity.getCtx())).withMenu((MMenu) entity);
	}

	@Override
	public IGenericDictionaryDocument getDocumentManagerByRole(PO entity, String language, int roleId) {
		return Menu.newInstance().withLanguage(language).withClientId(Env.getAD_Client_ID(entity.getCtx())).withRoleId(roleId).withMenu((MMenu) entity);
	}

	@Override
	public IGenericDictionaryDocument getDocumentManagerByUser(PO entity, String language, int userId) {
		return Menu.newInstance().withLanguage(language).withClientId(Env.getAD_Client_ID(entity.getCtx())).withUserId(userId).withMenu((MMenu) entity);
	}
}
