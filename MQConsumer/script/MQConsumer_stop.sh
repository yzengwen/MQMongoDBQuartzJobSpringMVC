#!/bin/bash
PID=$(cat MQConsumer.pid)  
kill -9 $PID 