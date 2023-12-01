#!/bin/bash

for i in $(seq 1 25) ; do
    num=$(printf "%02d" $i)
    touch src/main/resources/Day${num}.txt
    touch src/main/resources/Day${num}_test.txt
done
