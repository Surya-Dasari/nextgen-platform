# OpenShift Quotas & Limits

## Observed Issue
- ReplicaSet creation blocked
- Error: exceeded quota for replicasets.apps

## Root Cause
- Old ReplicaSets not cleaned
- Frequent image updates

## Resolution
- Manual cleanup of old ReplicaSets
- Understand rollout behavior

## Lesson
OpenShift enforces strict resource governance in enterprise clusters.
