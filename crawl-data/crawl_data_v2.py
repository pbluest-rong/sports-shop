import requests
from bs4 import BeautifulSoup
import json
import time
import re
from bson import ObjectId
from slugify import slugify
from datetime import datetime

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
}

BASE_URL = "https://samba.vn"

# ----------- Danh sách category mapping -----------
CATEGORY_MAPPING = {
    "quan-ao-bong-da": "64f1d2f77bcf86cd79943901",
    "gym-yoga": "64f1d2f77bcf86cd79943902",
    "giay-chay-bo": "64f1d2f77bcf86cd79943903",
    "quan-ao-the-thao": "64f1d2f77bcf86cd79943904",
    "pickleball": "64f1d2f77bcf86cd79943905",
    "quan-ao-bong-chuyen": "64f1d2f77bcf86cd79943906",
    "bong-ro": "64f1d2f77bcf86cd79943907",
    "do-boi": "64f1d2f77bcf86cd79943908",
    "bong-ban": "64f1d2f77bcf86cd79943909"
}

# ----------- Hàm parse giá về int -----------
def parse_price(price_text):
    if not price_text:
        return 0
    try:
        return int(re.sub(r"\D", "", price_text))  # loại bỏ ký tự không phải số
    except:
        return 0

# ----------- Lấy danh sách link sản phẩm -----------
def get_product_links(category_url):
    try:
        response = requests.get(category_url, headers=HEADERS)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, "html.parser")
        products = soup.select("h3.product-name a")

        links = []
        for a in products:
            href = a.get("href")
            if href and not href.startswith("http"):
                href = BASE_URL + href
            links.append(href)
        return links
    except Exception as e:
        print(f"Lỗi khi lấy link sản phẩm: {e}")
        return []

# ----------- Lấy chi tiết sản phẩm -----------
def get_product_detail(url):
    try:
        res = requests.get(url, headers=HEADERS)
        res.raise_for_status()
        res.encoding = "utf-8"
        soup = BeautifulSoup(res.text, "html.parser")

        # Tên sản phẩm
        title = soup.select_one("h1.title-product").get_text(strip=True)

        # Giá sản phẩm
        price_element = soup.select_one("span.price.product-price")
        price = price_element.get_text(strip=True) if price_element else "0"

        # Mô tả
        description = soup.select_one(".product-summary .rte").get_text(" ", strip=True) if soup.select_one(".product-summary .rte") else ""

        # Ảnh sản phẩm
        images = ["https:" + a["href"] for a in soup.select("#lightgallery a")]
        images.reverse()

        # Thương hiệu và tình trạng
        brand = soup.select_one(".a-vendor").get_text(strip=True) if soup.select_one(".a-vendor") else ""
        status = soup.select_one(".a-stock").get_text(strip=True) if soup.select_one(".a-stock") else "Còn hàng"

        # Map tên thuộc tính (color / size) từ HTML
        option_labels = {}
        for opt in soup.select('.swatch'):
            idx = opt.get('data-option-index')
            label = opt.select_one('.header').get_text(strip=True).lower() if opt.select_one('.header') else ""
            if "màu" in label:
                option_labels[idx] = "color"
            elif "size" in label or "kích" in label:
                option_labels[idx] = "size"

        # Map mỗi màu với 2 ảnh liên tiếp
        color_image_map = {}
        image_index = 0

        # Danh sách variants
        variants = []
        for option in soup.select("#product-selectors option"):
            text = option.get_text(strip=True)
            value = option.get("value")
            if " - " in text:
                parts = text.split(" - ")
                attr_values = parts[0].split(" / ")
                price_variant = parse_price(parts[1].strip())

                # Gán color/size theo option_labels
                attrs = {}
                for idx, val in enumerate(attr_values):
                    key = option_labels.get(str(idx), "")
                    if key and val.strip():
                        attrs[key] = val.strip()

                # Map ảnh theo màu (nếu có color)
                color = attrs.get("color")
                if color and color not in color_image_map:
                    color_image_map[color] = images[image_index:image_index+2]
                    image_index += 2

                variant_images = color_image_map.get(color, images)

                variants.append({
                    "variant_id": value,
                    "attributes": attrs,
                    "price": price_variant,
                    "images": variant_images
                })

        return {
            "title": title,
            "price": price,
            "description": description,
            "brand": brand,
            "status": "ACTIVE",
            "images": images,
            "variants": variants,
            "url": url
        }

    except Exception as e:
        print(f"Lỗi khi lấy chi tiết sản phẩm {url}: {e}")
        return None

# ----------- Chuẩn hóa dữ liệu để lưu MongoDB (bỏ price + stock ở variants) -----------
def normalize_product(detail, category_slug):
    slug = slugify(detail.get("title", ""))

    category_id = CATEGORY_MAPPING.get(category_slug, "507f1f77bcf86cd799439012")

    normalized = {
        "_id": str(ObjectId()),
        "slug": slug,
        "title": detail.get("title", ""),
        "description": detail.get("description", ""),
        "brand": detail.get("brand", ""),
        "status": detail.get("status", "Còn hàng"),
        "sold": 10,
        "categoryId": {"$oid": category_id},
        "attributes": {
            "colors": [],
            "sizes": []
        },
        "variants": [],
        "rating": {
            "avg": 0,
            "count": 0
        },
        "createdAt": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "updatedAt": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    }

    variants = []
    colors_set = set()
    sizes_set = set()

    for v in detail.get("variants", []):
        attrs = v.get("attributes", {})
        color = attrs.get("color")
        size = attrs.get("size")

        if color:
            colors_set.add(color)
        if size:
            sizes_set.add(size)

        variants.append({
            "sku": f"SKU-{v.get('variant_id', '')}",
            "id": v.get("variant_id", ""),
            "attributes": attrs,
            "images": v.get("images", detail.get("images", []))
        })

    # Nếu không có variants, tạo mặc định
    if not variants:
        variant_id = str(int(time.time() * 1000))
        variants.append({
            "sku": f"SKU-{variant_id}",
            "id": variant_id,
            "attributes": {},
            "images": detail.get("images", [])
        })

    product_attributes = {}
    if colors_set:
        product_attributes["colors"] = list(colors_set)
    if sizes_set:
        product_attributes["sizes"] = list(sizes_set)

    normalized["attributes"] = product_attributes
    normalized["variants"] = variants

    return normalized

# ----------- Sinh SQL cho inventories -----------
def generate_inventory_sql(product, detail, default_quantity=20):
    sql_statements = []
    now = datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S')

    for v in product.get("variants", []):
        sku = v.get("sku")
        variant_id = v.get("id")
        product_id = product["_id"]

        raw_variant = next((x for x in detail.get("variants", []) if x["variant_id"] == variant_id), None)
        if raw_variant:
            price = raw_variant.get("price", 0)
            quantity = default_quantity
        else:
            # fallback sang price gốc của sản phẩm
            price = parse_price(detail.get("price", "0"))
            quantity = default_quantity

        sql = f"""
        INSERT INTO inventories
        (created_at, min_stock_level, price, product_id, quantity, reserved_quantity, sku, updated_at, variant_id, version)
        VALUES
        ('{now}', 0, {price}, '{product_id}', {quantity}, 0, '{sku}', '{now}', '{variant_id}', 0);
        """
        sql_statements.append(sql.strip())
    return sql_statements

# ----------- Crawl theo danh mục -----------
def crawl_category(category_url, category_slug, start_page=1, end_page=1):
    all_products = []
    all_sql = []

    for page in range(start_page, end_page + 1):
        page_url = f"{category_url}?page={page}"
        print(f"Đang crawl trang: {page_url}")
        product_links = get_product_links(page_url)
        print(f"  -> Tìm thấy {len(product_links)} sản phẩm")

        for link in product_links:
            detail = get_product_detail(link)
            if detail:
                normalized = normalize_product(detail, category_slug)
                all_products.append(normalized)

                sqls = generate_inventory_sql(normalized, detail)
                all_sql.extend(sqls)

                print(f"   ✓ Đã crawl: {normalized['title']} - Category: {category_slug}")

    return all_products, all_sql

# ----------- Chạy crawl và lưu file -----------
category_slug = "bong-ban"
c1 = "quan-ao-bong-da"
c2 = "gym-yoga"
c3 = "giay-chay-bo"
c4 = "quan-ao-the-thao"
c5 = "pickleball"
c6 = "quan-ao-bong-chuyen"
c7 = "bong-ro"
c8 = "do-boi"
c9 = "bong-ban"
category_slug = c1
CATEGORY_URL = f"https://samba.vn/{category_slug}"

products, inventories_sql = crawl_category(CATEGORY_URL, category_slug, start_page=1, end_page=1)

with open("products/" + category_slug + ".json", "w", encoding="utf-8") as f:
    json.dump(products, f, indent=4, ensure_ascii=False)

with open("products/inventories.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(inventories_sql))

print(f"\n✅ Đã lưu {len(products)} sản phẩm vào products/{category_slug}.json")
print(f"✅ Đã sinh {len(inventories_sql)} câu lệnh INSERT vào products/inventories.sql")