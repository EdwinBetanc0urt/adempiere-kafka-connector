package org.spin.eca56.util;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaTest {
	public static void main (String [] args) throws InterruptedException {
		
		do {
			String host = "your_host";
			String topic =  "your_topic";
			int port = 29092;
			
			int intents = 4;
			if(intents == 0) {
				intents = 100;
			}
			
			String completeUrl = host + ":" + port;
			Properties config = new Properties();
			config.put("client.id", "erp2");
			config.put("group.id", "foo01");
			config.put("bootstrap.servers", completeUrl);
			config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			config.put("value.deserializer", MapDeserializer.class.getName());
			KafkaConsumer<String, Map<String , Object>> consumer = new KafkaConsumer<String, Map<String , Object>>(config);
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
			//	
			System.out.println("Waiting......");
			Thread.sleep(1000 * 10);
		} while(true);
	}
}
