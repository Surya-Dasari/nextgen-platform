# Phase 2 – PostgreSQL Integration

## Changes
- H2 replaced with PostgreSQL
- Persistent storage
- Stateful backend service

## Benefits
- Data survives pod restarts
- Production-like behavior

## Challenges Faced
- JDBC config
- Pod startup failures due to YAML errors
- Connection pool tuning
