#!/usr/bin/env bash
# ══════════════════════════════════════════════════════════════
# M11-T2: Kubernetes Manifests Verification Script
#
# Run AFTER: kubectl apply -f k8s/
# Usage:     ./scripts/verify-k8s.sh
#
# This script is the "grading test" for the K8s exercise.
# All checks must pass for full marks.
# ══════════════════════════════════════════════════════════════

set -euo pipefail

NS="training"
DEPLOY="cloud-native-service"
PASS=0
FAIL=0

green()  { echo -e "\033[32m✅ $1\033[0m"; PASS=$((PASS+1)); }
red()    { echo -e "\033[31m❌ $1\033[0m"; FAIL=$((FAIL+1)); }
header() { echo -e "\n\033[1m── $1 ──\033[0m"; }

# ── Namespace ────────────────────────────────────────────────
header "Namespace"
if kubectl get namespace "$NS" &>/dev/null; then
  green "Namespace '$NS' exists"
else
  red "Namespace '$NS' not found — create it: kubectl create namespace $NS"
fi

# ── Deployment ───────────────────────────────────────────────
header "Deployment"
REPLICAS=$(kubectl get deploy "$DEPLOY" -n "$NS" -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "0")
if [ "$REPLICAS" -ge 2 ]; then
  green "Deployment has $REPLICAS replicas (>= 2)"
else
  red "Deployment replicas = $REPLICAS (expected >= 2)"
fi

READY=$(kubectl get deploy "$DEPLOY" -n "$NS" -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo "0")
if [ "${READY:-0}" -ge 2 ]; then
  green "All $READY pods are Ready"
else
  red "Only $READY pods ready — check: kubectl describe deploy $DEPLOY -n $NS"
fi

# ── ConfigMap injection ──────────────────────────────────────
header "ConfigMap"
APP_ENV=$(kubectl get configmap cloud-native-config -n "$NS" \
  -o jsonpath='{.data.APP_ENV}' 2>/dev/null || echo "")
if [ "$APP_ENV" = "staging" ]; then
  green "APP_ENV = staging (from ConfigMap)"
else
  red "APP_ENV = '$APP_ENV' (expected 'staging')"
fi

# ── Secret ───────────────────────────────────────────────────
header "Secret"
SECRET_EXISTS=$(kubectl get secret cloud-native-secret -n "$NS" &>/dev/null && echo "yes" || echo "no")
if [ "$SECRET_EXISTS" = "yes" ]; then
  green "Secret 'cloud-native-secret' exists"
  # Decode and verify
  DECODED=$(kubectl get secret cloud-native-secret -n "$NS" \
    -o jsonpath='{.data.DB_PASSWORD}' | base64 -d 2>/dev/null || echo "")
  if [ "$DECODED" = "supersecret123" ]; then
    green "DB_PASSWORD decoded correctly"
  else
    red "DB_PASSWORD decoded value is wrong (got: '$DECODED')"
  fi
else
  red "Secret 'cloud-native-secret' not found"
fi

# ── Probes ───────────────────────────────────────────────────
header "Health Probes"
READINESS_PATH=$(kubectl get deploy "$DEPLOY" -n "$NS" \
  -o jsonpath='{.spec.template.spec.containers[0].readinessProbe.httpGet.path}' 2>/dev/null || echo "")
if [ "$READINESS_PATH" = "/api/health" ]; then
  green "readinessProbe path = /api/health"
else
  red "readinessProbe path = '$READINESS_PATH' (expected /api/health)"
fi

LIVENESS_PATH=$(kubectl get deploy "$DEPLOY" -n "$NS" \
  -o jsonpath='{.spec.template.spec.containers[0].livenessProbe.httpGet.path}' 2>/dev/null || echo "")
if [ "$LIVENESS_PATH" = "/actuator/health" ]; then
  green "livenessProbe path = /actuator/health"
else
  red "livenessProbe path = '$LIVENESS_PATH' (expected /actuator/health)"
fi

# ── Resource limits ──────────────────────────────────────────
header "Resource Limits"
MEM_LIMIT=$(kubectl get deploy "$DEPLOY" -n "$NS" \
  -o jsonpath='{.spec.template.spec.containers[0].resources.limits.memory}' 2>/dev/null || echo "")
CPU_LIMIT=$(kubectl get deploy "$DEPLOY" -n "$NS" \
  -o jsonpath='{.spec.template.spec.containers[0].resources.limits.cpu}' 2>/dev/null || echo "")
if [ -n "$MEM_LIMIT" ] && [ -n "$CPU_LIMIT" ]; then
  green "Resource limits set: memory=$MEM_LIMIT cpu=$CPU_LIMIT"
else
  red "Resource limits not set — memory='$MEM_LIMIT' cpu='$CPU_LIMIT'"
fi

# ── HPA ──────────────────────────────────────────────────────
header "HorizontalPodAutoscaler"
HPA_MIN=$(kubectl get hpa cloud-native-hpa -n "$NS" \
  -o jsonpath='{.spec.minReplicas}' 2>/dev/null || echo "0")
HPA_MAX=$(kubectl get hpa cloud-native-hpa -n "$NS" \
  -o jsonpath='{.spec.maxReplicas}' 2>/dev/null || echo "0")
if [ "$HPA_MIN" -eq 2 ] && [ "$HPA_MAX" -eq 5 ]; then
  green "HPA: minReplicas=$HPA_MIN maxReplicas=$HPA_MAX"
else
  red "HPA: minReplicas=$HPA_MIN maxReplicas=$HPA_MAX (expected min=2 max=5)"
fi

# ── Service ──────────────────────────────────────────────────
header "Service"
SVC_TYPE=$(kubectl get svc "$DEPLOY" -n "$NS" \
  -o jsonpath='{.spec.type}' 2>/dev/null || echo "")
if [ "$SVC_TYPE" = "ClusterIP" ]; then
  green "Service type = ClusterIP"
else
  red "Service type = '$SVC_TYPE' (expected ClusterIP)"
fi

# ── Ingress ──────────────────────────────────────────────────
header "Ingress"
INGRESS_HOST=$(kubectl get ingress cloud-native-ingress -n "$NS" \
  -o jsonpath='{.spec.rules[0].host}' 2>/dev/null || echo "")
if [ "$INGRESS_HOST" = "cloud-native.training.local" ]; then
  green "Ingress host = cloud-native.training.local"
else
  red "Ingress host = '$INGRESS_HOST' (expected cloud-native.training.local)"
fi

# ── Summary ──────────────────────────────────────────────────
echo ""
echo "══════════════════════════════════════"
echo "  Results: $PASS passed, $FAIL failed"
echo "══════════════════════════════════════"

if [ "$FAIL" -gt 0 ]; then
  echo "Fix the issues above and re-run: ./scripts/verify-k8s.sh"
  exit 1
else
  echo "🎉 All checks passed! Open your PR."
  exit 0
fi
