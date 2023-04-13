package com.demokafkastreams.topology;

import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import com.demokafkastreams.Book;
import com.demokafkastreams.BookEnriched;
import com.demokafkastreams.Category;
import com.demokafkastreams.configuration.ApplicationConfig;
import com.demokafkastreams.processor.BookEnrichedProcessor;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.log4j.Log4j2;



@Service
@Log4j2
public class CustomMetricsTopology {

	private ApplicationConfig applicationConfig;
	

    private final KafkaProperties kafkaProperties;
    
    final SpecificAvroSerde<Book> bookSerde = new SpecificAvroSerde<>();
    
    final SpecificAvroSerde<Category> categorySerde = new SpecificAvroSerde<>();
    
    final SpecificAvroSerde<BookEnriched> bookEnrichedSerde = new SpecificAvroSerde<>();

    

    
    @Autowired
    public CustomMetricsTopology(ApplicationConfig applicationConfig,
                                      KafkaProperties kafkaProperties) {
        this.applicationConfig = applicationConfig;
        this.kafkaProperties = kafkaProperties;

        bookSerde.configure(serdeProperties(), false);
        
        categorySerde.configure(serdeProperties(), false);
        
        bookEnrichedSerde.configure(serdeProperties(), false);
        

    }
    
    private Map<String, Object> serdeProperties() {
        Map<String, Object> map = kafkaProperties.buildStreamsProperties();
        return map;
    }    

    @Autowired
    public void streamTopology(final StreamsBuilder streamsBuilder) {
    	
    	
        //STEP 1: create Global Ktables - OK
        createGlobalKTables(streamsBuilder);

        //STEP 2:  Get the stream of header sales - OK
        final KStream<String, Book> bookEventStream =
                streamsBuilder.stream(applicationConfig.getBookTopic(),
                        Consumed.with(Serdes.String(), bookSerde).withName("Kstream-source-input_book"));    	;    	
    	        
      bookEventStream.peek((key, value) -> log.info("Key [{}] - Value [{}]", key, value),Named.as("Kstream-book_print"));
      
     
      KStream<String,BookEnriched> bookEnrichedStream= bookEventStream.map((k,v)->
      {
      	BookEnriched result= BookEnriched.newBuilder()
        		.setIsbn(v.getIsbn())
        		.setAuthor(v.getAuthor())
        		.setPublisher(v.getPublisher())
        		.setTitle(v.getTitle())
        		.setCategoryId(v.getCategoryId())
    			.build();    	  
    	  KeyValue<String,BookEnriched> keyvalue=  KeyValue.pair(k, result) ;
    	  return keyvalue;
      } ,Named.as("Kstream-map-bookenriched"));
      
      bookEnrichedStream.to("intermediateTopic", Produced.with(Serdes.String(), bookEnrichedSerde)
  	.withName("Kstream-sink-bookenriched"));
      
//      bookEnrichedStream.process( ()-> new BookEnrichedProcessor(applicationConfig),
//    		  Named.as("Process-bookenriched"),new String[0]);

      Topology tpo =streamsBuilder.build();
      tpo.addSource("source1", "intermediateTopic")
      .addProcessor("procesor1", ()->new BookEnrichedProcessor(applicationConfig), "source1")
      .addSink("sink1", applicationConfig.getBookEnrichedTopic(), "procesor1");
      
      			
      					
      						
      
      
//    // join    
//    KStream<String,BookEnriched> bookEnriched= bookEventStream.leftJoin(globalKtableCategory, 
//    (booKey,bookValue) -> {
//          return bookValue.getCategoryId(); //num serie de sensor
//      },  
//    
//    (bookValue,categoryValue) -> { // both key and value params belong to inputStream.
//    	BookEnriched result= BookEnriched.newBuilder()
//        		.setIsbn(bookValue.getIsbn())
//        		.setAuthor(bookValue.getAuthor())
//        		.setPublisher(bookValue.getPublisher())
//        		.setTitle(bookValue.getTitle())
//        		.setCategoryName(categoryValue.getCategoryName())
//        		.setCategoryDescription(categoryValue.getDescription())
//    			.build();
//    	return result;
//    }
//   
//    		); 
    
     

//      bookEnrichedStream.to(applicationConfig.getBookEnrichedTopic(),
//    		  Produced.with(Serdes.String(), bookEnrichedSerde).withName("Kstream-sink-bookenriched"));
    
      bookEnrichedStream.peek((key, value) -> log.info("RESULT Key [{}] - Value [{}]", key, value),Named.as("Kstream-bookenriched_print"));


    }



	private void createGlobalKTables(StreamsBuilder streamsBuilder) {
        log.info("Creating GlobalKTables");


        log.info("Creating globalKtableCategory GlobalKTable");
        streamsBuilder.globalTable(applicationConfig.getCategoryTopic(),
        		Consumed.as("Global-Ktable-category_topic"),
                Materialized.<String, Category, KeyValueStore<Bytes, byte[]>>as(applicationConfig.getStateStoreCategoryTopic())
                        .withKeySerde(Serdes.String())
                        .withValueSerde(categorySerde));
   

        log.info("GlobalKTables created");		// TODO Auto-generated method stub
		
	}


}