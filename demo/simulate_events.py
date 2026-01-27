from app.pipeline import process_event

events=[
    {"user_id":"U1","amount":500,"device":"known"},
    {"user_id":"U1","amount":12000,"device":"new"},
    {"user_id":"U1","amount":18000,"device":"new"}
]

print("Starting fraud event simulation")

for e in events:
    print("Input event:", e)
    print("Decision output:", process_event(e))

print("Simulation completed")
