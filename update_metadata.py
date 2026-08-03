import json

with open("metadata.json", "r") as f:
    data = json.load(f)

data["name"] = "Derm-Ai"
data["description"] = "A clinical-soft AI dermatology and skincare tracking application."

with open("metadata.json", "w") as f:
    json.dump(data, f, indent=2)
