# Common OpenShift Issues Encountered

## 503 Service Unavailable
### Causes
- Service port mismatch
- Route targetPort mismatch
- Missing named ports
- No active endpoints

### Resolution
- Verify container port
- Match service targetPort
- Ensure service port has name
- Check endpoints object
