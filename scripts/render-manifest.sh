#!/bin/bash
set -e

MANIFEST=$1
IMAGE_TAG=$2

sed "s|IMAGE_TAG|$IMAGE_TAG|g" "$MANIFEST"

