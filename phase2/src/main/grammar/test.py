import requests

url = 'https://github.com/'

response = requests.get(url)

if response.status_code == 200:
    data = response.json()
    # ادامه کد برای پردازش داده‌ها
else:
    print("test", response.status_code)
    # response.json()