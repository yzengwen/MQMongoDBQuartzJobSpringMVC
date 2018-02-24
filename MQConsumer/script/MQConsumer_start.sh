#!/bin/bash
java -jar MQConsumer-0.0.1-SNAPSHOT.jar &
echo $! > MQConsumer.pid 
