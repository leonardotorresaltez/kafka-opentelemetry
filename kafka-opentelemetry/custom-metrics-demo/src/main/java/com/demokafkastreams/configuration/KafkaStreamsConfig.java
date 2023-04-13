package com.demokafkastreams.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.demokafkastreams.Book;
import com.demokafkastreams.Category;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class KafkaStreamsConfig<V> {

    private final  KafkaClientCustomProperties kafkaCustomProperties;

    @Autowired
    public KafkaStreamsConfig(final KafkaClientCustomProperties kafkaCustomProperties) {
        super();
        this.kafkaCustomProperties = kafkaCustomProperties;
    }
    
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;


    @Bean
    public KafkaTemplate<String, Book> kafkaTemplateBook(ProducerFactory<String, Book> producerFactory) {
    	return createKafkaTemplate(producerFactory);    
    }    
    
    @Bean
    public KafkaTemplate<String, Category> kafkaTemplateCategory(ProducerFactory<String, Category> producerFactory) {
        return createKafkaTemplate2(producerFactory);        
    }     
    
 
    
    private KafkaTemplate createKafkaTemplate(ProducerFactory producerFactory){
        Map<String, Object> configOverrides1 = new HashMap<>();
        configOverrides1.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configOverrides1.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        return new KafkaTemplate<>(producerFactory, configOverrides1);
    	
    }
    
    private KafkaTemplate<String,Category> createKafkaTemplate2(ProducerFactory<String,Category> producerFactory){
        Map<String, Object> configOverrides1 = new HashMap<>();
        configOverrides1.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configOverrides1.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        return new KafkaTemplate<String,Category>(producerFactory, configOverrides1);
    	
    }    
    
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig(KafkaProperties properties) {
    	
    	
    	Map<String, Object> config = properties.buildStreamsProperties();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaCustomProperties.getMaxPollRecords());
        config.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, kafkaCustomProperties.getStandByReplicas());



        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaCustomProperties.getSessionTimeOut());
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaCustomProperties.getHeartbeatInterval());

        //producer
        config.put(ProducerConfig.LINGER_MS_CONFIG, kafkaCustomProperties.getLinger());
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaCustomProperties.getRequestTimeout());
        config.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, kafkaCustomProperties.getNumThreads());

        // rocksdb
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, kafkaCustomProperties.getCacheMaxBytesBuffering());
        config.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, BoundedMemoryRocksDBConfig.class);
        config.put(StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE);
        config.put(StreamsConfig.WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG, kafkaCustomProperties.getLogAdditionalRetentionTime());

        return new KafkaStreamsConfiguration(config);
    }


    public static class BoundedMemoryRocksDBConfig implements RocksDBConfigSetter {

        // See #1 below
        //1MB
        private static final org.rocksdb.Cache cache = new org.rocksdb.LRUCache(1 * 1024 * 1024L, -1, false, 0);
        //1MB
        private static final org.rocksdb.WriteBufferManager writeBufferManager = new org.rocksdb.WriteBufferManager(1 * 1024 * 1024L, cache);

        @Override
        public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {

            BlockBasedTableConfig tableConfig = (BlockBasedTableConfig) options.tableFormatConfig();

            // These three options in combination will limit the memory used by RocksDB to the size passed to the block cache (TOTAL_OFF_HEAP_MEMORY)
            tableConfig.setBlockCache(cache);
            tableConfig.setCacheIndexAndFilterBlocks(true);
            options.setWriteBufferManager(writeBufferManager);

            // These options are recommended to be set when bounding the total memory
            // See #2 below
            tableConfig.setCacheIndexAndFilterBlocksWithHighPriority(true);
            tableConfig.setPinTopLevelIndexAndFilter(true);
            // See #3 below
            tableConfig.setBlockSize(2048L);
            options.setMaxWriteBufferNumber(2);
            options.setWriteBufferSize(1 * 1024 * 1024L);
            options.setIncreaseParallelism(7);
            // Enable compression (optional). Compression can decrease the required storage
            // and increase the CPU usage of the machine. For CompressionType values, see
            // https://javadoc.io/static/org.rocksdb/rocksdbjni/6.4.6/org/rocksdb/CompressionType.html.
//		    options.setCompressionType(CompressionType.LZ4_COMPRESSION);

            options.setTableFormatConfig(tableConfig);
        }

        @Override
        public void close(final String storeName, final Options options) {
            // Cache and WriteBufferManager should not be closed here, as the same objects are shared by every store instance.
        }
    }
}
