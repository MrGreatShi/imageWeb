import io
import json
from typing import List
from fastapi import FastAPI, File, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image
from transformers import CLIPProcessor, CLIPModel
import torch

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

print("Loading CLIP model...")
model = CLIPModel.from_pretrained("openai/clip-vit-base-patch16")
processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch16")
print("Model loaded.")

@app.post("/predict_tags")
async def predict_tags(file: UploadFile = File(...), labels: str = Form(...)):
    try:
        # labels参数格式: [{"id":1,"title":"Nature"}, ...]
        labels_json = json.loads(labels)
        if not labels_json:
            return []

        ids = [item["id"] for item in labels_json]
        titles = [item["title"] for item in labels_json]

        image_bytes = await file.read()
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")

        # AI模型推理
        inputs = processor(
            text=titles,
            images=image,
            return_tensors="pt",
            padding=True
        )
        with torch.no_grad():
            outputs = model(**inputs)
            logits_per_image = outputs.logits_per_image[0]
            probs = logits_per_image.softmax(0).tolist()

        # 获得分数高于阈值的label id
        threshold = 0.20
        selected_ids = [id_ for id_, p in zip(ids, probs) if p >= threshold]
        # 如无高分，则返回分数最高的id
        if not selected_ids:
            max_idx = probs.index(max(probs))
            selected_ids = [ids[max_idx]]
        return selected_ids
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="0.0.0.0", port=5000, reload=False)

#python app.py