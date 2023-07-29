/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * This program is free software; you can redistribute it and/or modify it    		 *
 * under the terms version 2 or later of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope   		 *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 		 *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           		 *
 * See the GNU General Public License for more details.                       		 *
 * You should have received a copy of the GNU General Public License along    		 *
 * with this program; if not, write to the Free Software Foundation, Inc.,    		 *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     		 *
 * For the text or an alternative of this public license, you may reach us    		 *
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpya.com				  		                 *
 *************************************************************************************/
package org.spin.eca56.util;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serializer for Entity Wrapper
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class MapSerializer implements Serializer<Map<String , Object>> {

	public MapSerializer() {
		
	}
	
	@Override
	public byte[] serialize(String topic, Map<String , Object> data) {
		byte[] serializedObject = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			serializedObject = objectMapper.writeValueAsString(data).getBytes();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return serializedObject;
	}
}
