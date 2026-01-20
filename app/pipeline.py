from engine.rules import run_rules
from engine.risk_score import score_risk
from alerting.alert_service import send_alert
from audit.audit_log import log_event

def process_event(event):
    signals=run_rules(event)
    risk=score_risk(signals)

    if risk>=70:
        send_alert(event,risk)

    log_event(event,signals,risk)
