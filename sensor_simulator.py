import requests
import random
import time
import argparse
from datetime import datetime

SENSOR_URL = "http://localhost:2001/api/sensors/{sensor_id}/readings"

def get_sensors(base_url: str) -> list:
    """Fetch all sensors from the server"""
    try:
        response = requests.get(f"{base_url}/api/sensors", timeout=5)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Error fetching sensors: {e}")
        return []

def send_reading(sensor_id: int, value: float, base_url: str) -> bool:
    """Send a sensor reading to the server"""
    try:
        payload = {"value": value}
        response = requests.post(
            SENSOR_URL.format(sensor_id=sensor_id),
            json=payload,
            timeout=5
        )
        if response.status_code == 200:
            return True
        else:
            print(f"Error sending reading for sensor {sensor_id}: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"Error sending reading: {e}")
        return False

def simulate_temperature(base_temp: float = 22.0, variance: float = 2.0) -> float:
    """Simulate realistic temperature with small fluctuations"""
    return base_temp + random.uniform(-variance, variance)

def main():
    parser = argparse.ArgumentParser(description="Sensor Simulator")
    parser.add_argument(
        "--interval", "-i",
        type=int,
        default=5,
        help="Interval between readings in seconds (default: 5)"
    )
    parser.add_argument(
        "--base-url",
        default="http://localhost:2001",
        help="Base URL of the server (default: http://localhost:2001)"
    )
    args = parser.parse_args()

    print("=" * 50)
    print("  Sensor Simulator")
    print("=" * 50)
    print(f"  Server: {args.base_url}")
    print(f"  Interval: {args.interval} seconds")
    print("=" * 50)

    sensors = get_sensors(args.base_url)
    if not sensors:
        print("No sensors found or server unreachable!")
        return

    print(f"\nFound {len(sensors)} sensor(s):")
    for sensor in sensors:
        sensor_type = sensor.get('type', 'Температура')
        print(f"  - Sensor #{sensor['id']}: {sensor['name']} ({sensor['buildingName']}) [{sensor_type}]")

    print(f"\nStarting simulation... (Ctrl+C to stop)")
    print("-" * 50)

    try:
        while True:
            for sensor in sensors:
                sensor_type = sensor.get('type', 'Температура')
                if sensor_type == 'Камера':
                    value = 1
                    display = "движение"
                else:
                    value = simulate_temperature()
                    display = f"{value:.1f}°C"
                
                if send_reading(sensor['id'], value, args.base_url):
                    timestamp = datetime.now().strftime("%H:%M:%S")
                    print(f"[{timestamp}] Sensor #{sensor['id']}: {display}")
            
            time.sleep(args.interval)
            
    except KeyboardInterrupt:
        print("\n\nSimulation stopped.")

if __name__ == "__main__":
    main()
