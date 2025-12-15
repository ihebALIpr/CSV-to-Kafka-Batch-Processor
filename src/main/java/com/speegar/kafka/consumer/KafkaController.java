//package com.speegar.kafka;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.speegar.kafka.config.KafkaProducer;
//
//@RestController
//@RequestMapping("/kafka")
//public class KafkaController {
//
//    private final KafkaProducer producer;
//
//    public KafkaController(KafkaProducer producer) {
//        this.producer = producer;
//    }
//
//    @GetMapping("/send")
//    public String send(@RequestParam String msg) {
//        producer.sendMessage(msg);
//        return "Message sent: " + msg;
//    }
//    }
 


