cd /app
pip3 install -r requirements.txt
gunicorn --bind 0.0.0.0:5000 wsgi:app -w 4