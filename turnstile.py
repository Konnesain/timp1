import argparse
import requests
import sys

DEFAULT_HOST = "localhost"
DEFAULT_PORT = "2001"

def main():
    parser = argparse.ArgumentParser(description="Турникет")
    parser.add_argument("employee_id", type=int, help="ID сотрудника")
    parser.add_argument("building_id", type=int, help="ID здания")
    parser.add_argument("action", help="Действие (ENTRY/EXIT)")
    parser.add_argument("-H", "--host", default=DEFAULT_HOST, help=f"IP адрес сервера (по умолчанию: {DEFAULT_HOST})")
    parser.add_argument("-p", "--port", default=DEFAULT_PORT, help=f"Порт сервера (по умолчанию: {DEFAULT_PORT})")
    args = parser.parse_args()

    action = args.action.upper()
    if action not in ("ENTRY", "EXIT"):
        print(f"Неизвестное действие: {args.action}. Используйте ENTRY или EXIT", file=sys.stderr)
        sys.exit(1)

    base_url = f"http://{args.host}:{args.port}"
    url = f"{base_url}/api/turnstile/{args.employee_id}/{args.building_id}/{action}"

    try:
        response = requests.post(url, timeout=5)
        data = response.json()
        print(data.get("message", data))
    except requests.exceptions.RequestException as e:
        print(f"Ошибка: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
