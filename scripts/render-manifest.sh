#!/bin/bash
# Usage: render-manifest.sh <yaml-file> <image-tag>

set -e

FILE=$1
TAG=$2

if [ -z "$FILE" ] || [ -z "$TAG" ]; then
  echo "Usage: render-manifest.sh <yaml-file> <image-tag>"
  exit 1
fi

sed "s|\${IMAGE_TAG}|$TAG|g" "$FILE"

