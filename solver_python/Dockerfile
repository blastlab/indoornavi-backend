FROM python:3
ADD main.py /
ADD solvers /solvers
ADD requirements.txt /
RUN pip install -r requirements.txt
CMD [ "python", "./main.py" ]
