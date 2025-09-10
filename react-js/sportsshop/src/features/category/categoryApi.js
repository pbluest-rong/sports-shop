import axios from "axios";
import {BASE_URL} from "../../utils/constant.js";


export const getCategories = async () => {
    const res = await axios.get(`${BASE_URL}/categories/root`);
    return res.data;
};

export const getCategoryChildren = async (id) => {
    const res = await axios.get(`${BASE_URL}/categories/${id}/children`);
    return res.data;
};

export const getSportCategories = async () => {
    const res = await axios.get(`${BASE_URL}/categories/sports`);
    return res.data;
};