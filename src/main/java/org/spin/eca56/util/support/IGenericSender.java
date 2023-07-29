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
package org.spin.eca56.util.support;

import org.spin.util.support.IAppSupport;

/**
 * 	A contract that will be implemented for each sender
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public interface IGenericSender extends IAppSupport {
	
	//	Some default topics
	/**	Dictionary topic for send all changes from Dictionary	*/
	public static final String DICTIONARY = "dictionary";
	/**	All related to storage and warehouses	*/
	public static final String DOCUMENT_MANAGEMENT = "document_management";
	
	/**
	 * Send command to device
	 * @param document
	 */
	public void send(IGenericDocument document, String channel);
}
