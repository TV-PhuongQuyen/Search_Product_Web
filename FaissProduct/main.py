from fastapi import FastAPI, File, UploadFile, Form
import clip, torch, io
from PIL import Image
import numpy as np
from faiss_store import add_vector, search_vector, get_status, soft_delete

app = FastAPI()

# Load CLIP
device = "cuda" if torch.cuda.is_available() else "cpu"
model, preprocess = clip.load("ViT-B/32", device=device)


def encode_image_to_vector(image_file: bytes):
    image = Image.open(io.BytesIO(image_file)).convert("RGB")
    image = preprocess(image).unsqueeze(0).to(device)
    with torch.no_grad():
        embedding = model.encode_image(image).cpu().numpy().astype("float32")
    return embedding


@app.post("/encode")
async def encode(product_id: int = Form(...), file: UploadFile = File(...)):
    image_bytes = await file.read()
    embedding = encode_image_to_vector(image_bytes)
    add_vector(embedding, product_id)
    return {"status": "ok", "product_id": product_id}


@app.post("/search_image_product")
async def search_image(file: UploadFile = File(...), k: int = 5):
    image_bytes = await file.read()
    query_vector = encode_image_to_vector(image_bytes)
    results = search_vector(query_vector, k)
    return {"status": "ok", "results": results}


# @app.get("/check_index")
# async def check_index():
#     return get_status()
#

@app.delete("/delete_product/{product_id}")
async def delete_product(product_id: int):
    success = soft_delete(product_id)
    if success:
        return {
            "status": "ok",
            "message": f"Product {product_id} marked as deleted",
            "deleted_products": list(get_status()["deleted_products"])  # thêm danh sách id đã xoá
        }
    return {
        "status": "error",
        "message": "Product ID not found",
        "deleted_products": list(get_status()["deleted_products"])
    }

