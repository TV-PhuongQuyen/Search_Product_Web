import faiss
import numpy as np
import pickle
import os

FAISS_INDEX_PATH = "faiss.index"
PRODUCT_IDS_PATH = "product_ids.pkl"
DELETED_IDS_PATH = "deleted_ids.pkl"
DIMENSION = 512

# Load index
if os.path.exists(FAISS_INDEX_PATH):
    index = faiss.read_index(FAISS_INDEX_PATH)
else:
    index = faiss.IndexFlatL2(DIMENSION)

# Load product_ids
if os.path.exists(PRODUCT_IDS_PATH):
    with open(PRODUCT_IDS_PATH, "rb") as f:
        product_ids = pickle.load(f)
else:
    product_ids = []

# Load deleted_ids
if os.path.exists(DELETED_IDS_PATH):
    with open(DELETED_IDS_PATH, "rb") as f:
        deleted_ids = pickle.load(f)
else:
    deleted_ids = set()


def save_all():
    faiss.write_index(index, FAISS_INDEX_PATH)
    with open(PRODUCT_IDS_PATH, "wb") as f:
        pickle.dump(product_ids, f)
    with open(DELETED_IDS_PATH, "wb") as f:
        pickle.dump(deleted_ids, f)


def add_vector(embedding: np.ndarray, product_id: int):
    index.add(embedding)
    product_ids.append(product_id)
    save_all()


def soft_delete(product_id: int):
    """Đánh dấu product_id là đã xoá (soft delete)"""
    if product_id in product_ids:
        deleted_ids.add(product_id)

        save_all()
        return True
    return False


def search_vector(query_vector: np.ndarray, k: int = 5):
    distances, indices = index.search(query_vector, k * 2)  # lấy nhiều hơn để bù xoá
    results = []
    for idx, dist in zip(indices[0], distances[0]):
        if idx < len(product_ids):
            pid = product_ids[idx]
            if pid not in deleted_ids:  # bỏ qua sản phẩm bị xoá
                results.append({
                    "product_id": pid,
                    "distance": float(dist)
                })
        if len(results) >= k:
            break
    return results


def get_status():
    return {
        "ntotal": index.ntotal,
        "total_products": len(product_ids),
        "deleted_products": list(deleted_ids)
    }
