package com.demokafkastreams.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.streams.StreamsMetrics;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import com.demokafkastreams.BookEnriched;
import com.demokafkastreams.Category;
import com.demokafkastreams.configuration.ApplicationConfig;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class BookEnrichedProcessor implements Processor<String, BookEnriched, Void, Void> {


    private TimestampedKeyValueStore<String, Category> categoryStateStore;



    private final ApplicationConfig applicationConfig;
    
    private StreamsMetrics streamsMetrics;
    private Sensor sensorStartTs;
    


    public BookEnrichedProcessor(final ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public void init(final ProcessorContext<Void, Void> processorContext) {
    	categoryStateStore = processorContext.getStateStore(applicationConfig.getStateStoreCategoryTopic());
  
        streamsMetrics = processorContext.metrics();
        
        Map<String, String> metricTags = new HashMap<String, String>();
        metricTags.put("metricTagKey", "metricsTagVal");
        


        MetricName metricName = new MetricName("my-process-time", "stream-processor-node-metrics","description",metricTags);
        
        sensorStartTs = streamsMetrics.addSensor("start_ts", Sensor.RecordingLevel.INFO);
        
        sensorStartTs.add(metricName, new Avg());

    	
    }

    @Override
    public void process(Record<String, BookEnriched> bookEnriched) {
    	

    	long iniTime = System.currentTimeMillis();
        if (Objects.isNull(bookEnriched)) {
            log.error("retailjJdaSapSitesStore is NULL");
            return;
        }

        String categoryId = bookEnriched.value().getCategoryId();
        if (Objects.isNull(categoryId)) {
            log.error("categoryId is NULL");
            return;
        }

        ValueAndTimestamp<Category> category = categoryStateStore.get(categoryId);
        if (Objects.isNull(category)) {
            log.error("TaxStatusProcessor - Store=[{}] NOT Found!", categoryId);
            return;
        }
        

        bookEnriched.value().setCategoryName(category.value().getCategoryName());
        bookEnriched.value().setCategoryDescription(category.value().getDescription());
        long elapsedTime = System.currentTimeMillis() - iniTime;
        System.out.println("ElapsedTime - " + elapsedTime);
    	sensorStartTs.record(Long.valueOf(elapsedTime).doubleValue());
    	
    	
    	

        
    }

  

}
