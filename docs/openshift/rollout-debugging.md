# Rollout Debugging Guide

## Symptoms
- rollout status stuck
- no new pods created
- old pod still running

## Commands Used
- oc rollout status
- oc rollout restart
- oc describe deployment
- oc get events

## Key Insight
Quota errors can silently block new ReplicaSets.
