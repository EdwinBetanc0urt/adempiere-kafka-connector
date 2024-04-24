package org.spin.eca56.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaTest {
	public static void main (String [] args) throws InterruptedException {
		String host = "127.0.0.1";
		String port = "29092";
		String topic = "your_topic";
		if (args != null && args.length > 0) {
			host = args[0];
			port = args[1];
			topic = args[2];
		}

		String completeUrl = host + ":" + port;
		do {
			int intents = 4;
			if(intents == 0) {
				intents = 100;
			}

			HashMap<String, Object> consumerConfigs = new HashMap<String,Object>();
			consumerConfigs.put(ConsumerConfig.CLIENT_ID_CONFIG, "erp2");
			consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, completeUrl);
			consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, "foo01");
			consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
			consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MapDeserializer.class.getName());

			try (KafkaConsumer<String, Map<String , Object>> consumer = new KafkaConsumer<String, Map<String , Object>>(consumerConfigs)) {
				consumer.subscribe(Arrays.asList(topic));
				AtomicInteger iterate = new AtomicInteger(0);
				AtomicInteger errors = new AtomicInteger();
				while (iterate.incrementAndGet() < intents) {
					System.out.println("Intent: " + iterate.get());
					ConsumerRecords<String, Map<String , Object>> records = consumer.poll(Duration.ofSeconds(10));
					records.forEach(record -> {
						try {
							System.out.println(record.value());
						} catch (Exception e) {
							errors.addAndGet(1);
						}
					});
					consumer.commitSync();
				}
				consumer.unsubscribe();
				consumer.close(Duration.ofSeconds(1));
			}
			//	
			System.out.println("Waiting......");
			Thread.sleep(1000 * 10);
		} while(true);
	}
}
