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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.compiere.util.DisplayType;
import org.compiere.util.Util;

/**
 * 	the document class for Process senders
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class ReferenceUtil {

	/**
	 * Validate reference
	 * TODO: Improve support to ID reference to get display column
	 * TODO: Add support to Resource Assigment reference to get display column
	 * @param referenceId
	 * @param referenceValueId
	 * @param columnName
	 * @return
	 */
	public static boolean isLookupReference(int referenceId) {
		if (DisplayType.isLookup(referenceId) || DisplayType.Account == referenceId
			|| DisplayType.ID == referenceId
			|| DisplayType.Location == referenceId || DisplayType.PAttribute == referenceId
			|| DisplayType.Locator == referenceId
			|| DisplayType.Image == referenceId) {
			return true;
		}

		return false;
	}
	
	/**
	 * Get Context column names from context
	 * @param context
	 * @return
	 * @return List<String>
	 */
	public static List<String> getContextColumnNames(String context) {
		if (Util.isEmpty(context, true)) {
			return new ArrayList<String>();
		}
		String START = "\\@";  // A literal "(" character in regex
		String END   = "\\@";  // A literal ")" character in regex

		// Captures the word(s) between the above two character(s)
		String patternValue = START + "(#|$){0,1}(\\w+)" + END;

		Pattern pattern = Pattern.compile(patternValue);
		Matcher matcher = pattern.matcher(context);
		Map<String, Boolean> columnNamesMap = new HashMap<String, Boolean>();
		while(matcher.find()) {
			columnNamesMap.put(matcher.group().replace("@", "").replace("@", ""), true);
		}
		return new ArrayList<String>(columnNamesMap.keySet());
	}
}
