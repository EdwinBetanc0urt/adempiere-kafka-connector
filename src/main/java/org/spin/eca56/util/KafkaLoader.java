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

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.TopicExistsException;

/**
 * Singleton for load kafka client
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class KafkaLoader {
	/**	Instance	*/
	private static KafkaLoader instance;
	/**	Producer	*/
	@SuppressWarnings("rawtypes")
	private Map<String, KafkaProducer> producers = new HashMap<>();
	
	/**
	 * default instance
	 * @return
	 */
	public static KafkaLoader getInstance() {
		if(instance == null) {
			instance = new KafkaLoader();
		}
		return instance;
	}

	/**
	 * Create topic if not exist
	 * @param topic
	 * @param cloud
	 */
	private void createTopic(final String topic, final Properties cloud) {
		final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
		try (final AdminClient adminClient = AdminClient.create(cloud)) {
			adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
		} catch (final InterruptedException | ExecutionException e) {
			// Ignore if TopicExistsException, which may be valid if topic exists
			if (!(e.getCause() instanceof TopicExistsException)) {
				throw new RuntimeException(e);
			}
		}
	 }
	
	/**
	 * Get current producer
	 * @param url
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public KafkaProducer getProducer(String url, String topic) throws Exception {
		String key = url + "|" + topic;
		KafkaProducer producer = producers.get(key);
		if(producer == null) {
			Properties config = new Properties();
			config.put("client.id", InetAddress.getLocalHost().getHostName());
			config.put("bootstrap.servers", url);
			config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			config.put("value.serializer", MapSerializer.class.getName());
			config.put("acks", "all");
			createTopic(topic, config);
			producer = new KafkaProducer(config);
			producers.put(key, producer);
		}
		//	
		return producer;
	}
}
