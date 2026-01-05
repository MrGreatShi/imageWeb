FROM python:3.10-slim

WORKDIR /app
COPY . /app

# 安装依赖（根据你的需要修改 requirements.txt 路径）
RUN pip install --no-cache-dir -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt

EXPOSE 5000
# 假设用 Flask 或 FastAPI
CMD ["python", "app.py"]