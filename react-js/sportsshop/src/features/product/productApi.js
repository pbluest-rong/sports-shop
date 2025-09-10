import axios from "axios";


export const getProducts = async (url) => {
    const res = await axios.get(url);
    return res.data.data.content || [];
}

