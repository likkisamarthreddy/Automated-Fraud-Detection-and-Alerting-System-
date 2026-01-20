def send_alert(event,risk):
    print(f"[ALERT] User {event['user_id']} | Risk={risk}")
