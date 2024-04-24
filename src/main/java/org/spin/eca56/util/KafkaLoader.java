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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;

/**
 * Singleton for load kafka client
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class KafkaLoader {
	/**	Instance	*/
	private static KafkaLoader instance;

	/**	Producer	*/
	private HashMap<String, KafkaProducer<String, Object>> producers = new HashMap<>();


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
	private void createTopic(final String topic, final HashMap<String, Object> cloud) {
		final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
		try (final AdminClient adminClient = AdminClient.create(cloud)) {
			HashMap<String, String> topicConfigs = new HashMap<>();
			topicConfigs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, "20971520");
			topicConfigs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, "20971520");

			newTopic.configs(topicConfigs);
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
	public KafkaProducer<String, Object> getProducer(String url, String topic) throws Exception {
		String key = url + "|" + topic;
		KafkaProducer<String, Object> producer = producers.get(key);
		if (producer == null) {
			HashMap<String, Object> producerConfigs = new HashMap<>();
			producerConfigs.put(ProducerConfig.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
			producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, url);
			producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName());
			producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MapSerializer.class.getName());
			producerConfigs.put(ProducerConfig.ACKS_CONFIG, "all");

			producerConfigs.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1024 * 1024 * 3);

			createTopic(topic, producerConfigs);
			producer = new KafkaProducer<String, Object>(producerConfigs);
			producers.put(key, producer);
		}
		//	
		return producer;
	}
}
