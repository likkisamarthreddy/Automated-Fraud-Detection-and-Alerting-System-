ALERT_THRESHOLD=70

def process_event(event):
    signals=run_rules(event)
    risk=score_risk(signals)

    if risk>=ALERT_THRESHOLD:
        send_alert(event,risk)

    log_event(event,signals,risk)
