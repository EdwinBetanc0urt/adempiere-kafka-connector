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
package org.spin.eca56.util.support.kafka;

import java.util.Map;

import org.adempiere.exceptions.AdempiereException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.eca56.util.support.IGenericSender;
import org.spin.eca56.util.KafkaLoader;
import org.spin.eca56.util.support.IGenericDocument;
import org.spin.model.MADAppRegistration;

/**
 * 	A implementation for kafka queue
 * 	@author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 */
public class Sender implements IGenericSender {
	/** Static Logger					*/
	private CLogger log = CLogger.getCLogger (Sender.class);
	/**	Host	*/
	private String host = null;
	/**	Port	*/
	private int port;
	/**	ADempier Token	*/
	private String defaultQueueName = null;
	/**	Registration Id	*/
	private int registrationId = 0;
	
	/**
	 * Validate connection
	 */
	private void validate() {
		if(getAppRegistrationId() <= 0) {
			throw new AdempiereException("@AD_AppRegistration_ID@ @NotFound@");
		}
		MADAppRegistration registration = MADAppRegistration.getById(Env.getCtx(), getAppRegistrationId(), null);
		defaultQueueName = registration.getValue().replaceAll("[^a-zA-Z0-9 -]", "").toLowerCase().trim();
		port = registration.getPort();
		host = registration.getHost();
	}

	@Override
	public String testConnection() {
		//	set time
		IGenericDocument defaultTestDocument = TestDocument.newInstance();
		send(defaultTestDocument, defaultQueueName);
		return defaultTestDocument.getValues().toString();
	}

	@Override
	public void setAppRegistrationId(int registrationId) {
		this.registrationId = registrationId;
		validate();
	}

	@Override
	public int getAppRegistrationId() {
		return registrationId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void send(IGenericDocument document, String channel) {
		String topicName = channel;
		if(Util.isEmpty(topicName)) {
			topicName = defaultQueueName;
		}
		if(Util.isEmpty(document.getKey())) {
			throw new AdempiereException("@Key@ @NotFound@");
		}
		log.fine("Command to Send: " + document);
		try {
			KafkaProducer producer = KafkaLoader.getInstance().getProducer(host + ":" + port, topicName);
			final ProducerRecord record = new ProducerRecord<String, Map<String , Object>>(topicName, document.getKey(), document.getValues());
			producer.send(record);
		} catch (Exception e) {
			throw new AdempiereException(e);
		}
	}
}
