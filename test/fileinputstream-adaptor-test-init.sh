#!/bin/bash

echo "abcdefghijklmnopqrstuvwxyz" > /tmp/Saga-test-fileinputstream
for i in $(seq 1 10); do cat /tmp/Saga-test-fileinputstream /tmp/Saga-test-fileinputstream >> /tmp/Saga-test-fileinputstream_tmp; cat /tmp/Saga-test-fileinputstream_tmp /tmp/Saga-test-fileinputstream_tmp >> /tmp/Saga-test-fileinputstream; done
rm -rf /tmp/Saga-test-fileinputstream_tmp
