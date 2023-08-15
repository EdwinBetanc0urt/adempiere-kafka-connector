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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.core.domains.models.I_C_OrderLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.spin.eca56.util.support.IGenericDocument;

/**
 * 	the document class for Process senders
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Order implements IGenericDocument {

	//	Some default documents key
	public static final String KEY = "new";
	public static final String CHANNEL = "order";
	private Map<String, Object> document;
	
	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Map<String, Object> getValues() {
		return document;
	}
	
	public Order withOrder(MOrder order) {
		document = new HashMap<>();
		Map<String, Object> documentDetail = new HashMap<>();
		documentDetail.put("id", order.getC_Order_ID());
		documentDetail.put("uuid", order.getUUID());
		documentDetail.put("document_no", order.getDocumentNo());
		documentDetail.put("document_type_id", order.getC_DocTypeTarget_ID());
		documentDetail.put("description", order.getDescription());
		documentDetail.put("business_partner_id", order.getC_BPartner_ID());
		documentDetail.put("total_lines", order.getTotalLines());
		documentDetail.put("grand_total", order.getGrandTotal());
		
		//	Parameters
		List<MOrderLine> orderLines = Arrays.asList(order.getLines(true, I_C_OrderLine.COLUMNNAME_Line));
		List<Map<String, Object>> orderLinesDetail = new ArrayList<>();
		if(orderLines != null) {
			orderLines.forEach(orderLine -> {
				Map<String, Object> detail = new HashMap<>();
				detail.put("id", orderLine.getC_OrderLine_ID());
				detail.put("uuid", orderLine.getUUID());
				detail.put("name", orderLine.getName());
				detail.put("description", orderLine.getDescription());
				detail.put("product_id", orderLine.getM_Product_ID());
				detail.put("quantity_entered", orderLine.getQtyEntered());
				detail.put("quantity_ordered", orderLine.getQtyOrdered());
				detail.put("price_entered", orderLine.getPriceEntered());
				detail.put("price_actual", orderLine.getPriceLimit());
				detail.put("price_list", orderLine.getPriceList());
				detail.put("uom_id", orderLine.getC_UOM_ID());
				orderLinesDetail.add(detail);
			});
		}
		documentDetail.put("lines", orderLinesDetail);
		document.put(KEY, documentDetail);
		return this;
	}
	
	/**
	 * Default instance
	 * @return
	 */
	public static Order newInstance() {
		return new Order();
	}

	@Override
	public String getChannel() {
		return CHANNEL;
	}
	
	
}
