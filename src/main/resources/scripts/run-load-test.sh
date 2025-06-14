#!/bin/bash

# Número de execuções simultâneas (5 usuários simulados)
PARALLEL=5
# Número de iterações por usuário (20 cada)
ITERATIONS=20

mkdir -p reports

for i in $(seq 1 $PARALLEL); do
  newman run flop.postman_collection.json \
    --iteration-count $ITERATIONS \
    --reporters cli,html \
    --reporter-html-export "reports/report_$i.html" &
done
