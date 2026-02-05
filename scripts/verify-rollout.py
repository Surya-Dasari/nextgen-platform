import subprocess
import sys

deployments = [
    "apiservice",
    "authservice",
    "userservice",
    "nextgen-ui"
]

for d in deployments:
    print(f"⏳ Waiting for rollout: {d}")
    result = subprocess.run(
        ["oc", "rollout", "status", f"deployment/{d}", "--timeout=180s"]
    )
    if result.returncode != 0:
        print(f"❌ Rollout failed: {d}")
        sys.exit(1)

print("✅ All deployments rolled out successfully")

