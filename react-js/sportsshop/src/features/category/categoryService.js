import axios from "axios";
import {BASE_URL} from "../../utils/constant.js";


export const getCategories = async () => {
    const res = await axios.get(`${BASE_URL}/categories/root`);
    console.log('getCategories', res.data?.data);
    return res.data?.data;
};

export const getCategoryChildren = async (id) => {
    const res = await axios.get(`${BASE_URL}/categories/${id}/children`);
    return res.data?.data;
};

export const getSportCategories = async () => {
    const res = await axios.get(`${BASE_URL}/categories/sports`);
    return res.data?.data;
};