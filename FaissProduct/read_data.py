import faiss
import pickle

FAISS_INDEX_PATH = "faiss.index"
PRODUCT_IDS_PATH = "product_ids.pkl"
DELETED_IDS_PATH = "deleted_ids.pkl"

# Đọc FAISS index
index = faiss.read_index(FAISS_INDEX_PATH)
print("FAISS index ntotal:", index.ntotal)

# Đọc product_ids
with open(PRODUCT_IDS_PATH, "rb") as f:
    product_ids = pickle.load(f)

# Đọc danh sách đã xóa
try:
    with open(DELETED_IDS_PATH, "rb") as f:
        deleted_ids = set(pickle.load(f))
except FileNotFoundError:
    deleted_ids = set()

print("Deleted IDs:", deleted_ids)

# In vector embedding của những product chưa xóa
for i, pid in enumerate(product_ids):
    if pid in deleted_ids:   # bỏ qua những product đã bị xóa
        continue

    vector = index.reconstruct(i)   # lấy lại vector tại vị trí i
    print(f"🔹 Product ID: {pid}")
    print(f"   Vector (first 10 dims): {vector[:10]}")  # in 10 giá trị đầu để dễ nhìn
    print(f"   Vector shape: {vector.shape}\n")
