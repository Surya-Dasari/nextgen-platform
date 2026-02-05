#!/bin/bash
set -e

echo "Deploying with IMAGE_TAG=$IMAGE_TAG"

oc apply -f services/postgres/

./scripts/render-manifest.sh services/apiservice/openshift.yaml   "$IMAGE_TAG" | oc apply -f -
./scripts/render-manifest.sh services/authservice/openshift.yaml  "$IMAGE_TAG" | oc apply -f -
./scripts/render-manifest.sh services/userservice/openshift.yaml  "$IMAGE_TAG" | oc apply -f -
./scripts/render-manifest.sh services/frontend/openshift.yaml     "$IMAGE_TAG" | oc apply -f -

oc rollout status deployment/apiservice   --timeout=180s
oc rollout status deployment/authservice  --timeout=180s
oc rollout status deployment/userservice  --timeout=180s
oc rollout status deployment/nextgen-ui   --timeout=180s

