export const formatPrice = (price, locale = "vi-VN", currency = "VND") => {
    if (isNaN(price)) return "";
    return new Intl.NumberFormat(locale, {
        style: "currency",
        currency,
        maximumFractionDigits: 0,
    }).format(price);
};