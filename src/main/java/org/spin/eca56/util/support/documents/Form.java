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

import java.util.HashMap;
import java.util.Map;

import org.adempiere.core.domains.models.I_AD_Form;
import org.compiere.model.MForm;
import org.compiere.model.PO;
import org.compiere.util.Util;
import org.spin.eca56.util.support.DictionaryDocument;

/**
 * 	the document class for Process senders
 *	@author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class Form extends DictionaryDocument {

	//	Some default documents key
	public static final String KEY = "new";
	public static final String CHANNEL = "form";


	@Override
	public String getKey() {
		return KEY;
	}


	@Override
	public DictionaryDocument withEntity(PO entity) {
		MForm form = (MForm) entity;
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", form.getAD_Form_ID());
		documentDetail.put("uuid", form.getUUID());
		documentDetail.put("name", form.get_Translation(I_AD_Form.COLUMNNAME_Name, getLanguage()));
		documentDetail.put("description", form.get_Translation(I_AD_Form.COLUMNNAME_Description, getLanguage()));
		documentDetail.put("help", form.get_Translation(I_AD_Form.COLUMNNAME_Help, getLanguage()));

		String fileName = form.getClassname();
		if (!Util.isEmpty(fileName, true)) {
			int beginIndex = fileName.lastIndexOf(".");
			if (beginIndex == -1) {
				beginIndex = 0;
			} else {
				beginIndex++;
			}
			fileName = form.getClassname().substring(beginIndex, fileName.length());
			documentDetail.put("file_name", fileName);
		}

		putDocument(documentDetail);
		return this;
	}

	private Form() {
		super();
	}

	/**
	 * Default instance
	 * @return
	 */
	public static Form newInstance() {
		return new Form();
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
}
