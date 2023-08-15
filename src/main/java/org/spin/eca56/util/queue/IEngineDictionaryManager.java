/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                      *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                      *
 * This program is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by              *
 * the Free Software Foundation, either version 3 of the License, or                 *
 * (at your option) any later version.                                               *
 * This program is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                     *
 * GNU General Public License for more details.                                      *
 * You should have received a copy of the GNU General Public License                 *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.eca56.util.queue;

import org.compiere.model.PO;
import org.spin.eca56.util.support.IGenericDictionaryDocument;

/**
 * 	A contract that will be implemented for each engine
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public interface IEngineDictionaryManager extends IEngineManager {
	
	public IGenericDictionaryDocument getDocumentManager(PO entity, String language);
	
	public IGenericDictionaryDocument getDocumentManagerByRole(PO entity, String language, int roleId);

	public IGenericDictionaryDocument getDocumentManagerByUser(PO entity, String language, int userId);
}
