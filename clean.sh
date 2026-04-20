#!/bin/bash

echo "Cleaning project..."
echo "==================="


mvn clean

rm -rf spa/node_modules
rm -rf spa/dist

echo ""
echo "==================="
echo "Project cleaned!"
