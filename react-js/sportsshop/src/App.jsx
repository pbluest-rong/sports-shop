import './App.css'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage.jsx";
import ProductDetailPage from "./pages/ProductDetailPage.jsx";
import ProductListPage from "./pages/ProductListPage.jsx";
import {useDispatch, useSelector} from "react-redux";
import {closeAddToCartSidebar} from "./features/product/productSlice.js";
import AddToCartSidebar from "./components/AddToCartSidebar.jsx";
import Header from "./components/Header.jsx";
import Footer from "./components/Footer.jsx";

function App() {
    const { openAddToCartSidebar, selectedProduct } = useSelector(
        (state) => state.product
    );
    const dispatch = useDispatch();

    const DefaultLayout = ({children}) => (
        <div className="flex flex-col min-h-screen">
            <Header/>
            <main className="flex-1">
                {children}
            </main>
            <Footer/>
        </div>
    )
  return (
      <>
          <Router>
              <Routes>
                  <Route path="/" element={<DefaultLayout><HomePage /></DefaultLayout>} />
                  <Route path="/c/*" element={<DefaultLayout><ProductListPage /></DefaultLayout>} />
                  <Route path="/search" element={<DefaultLayout><ProductListPage /></DefaultLayout>} />
                  <Route path="/p/:slug" element={<DefaultLayout><ProductDetailPage /></DefaultLayout>} />
              </Routes>
          </Router>
          {openAddToCartSidebar && (
              <>
                  <div
                      className="fixed inset-0 bg-black/70 z-50"
                      onClick={() => dispatch(closeAddToCartSidebar())}
                  ></div>
                  <AddToCartSidebar
                      slug={selectedProduct.slug}
                      title={selectedProduct.title}
                      brand={selectedProduct.brand}
                      firstVariant={selectedProduct.firstVariant}
                      onClose={() => dispatch(closeAddToCartSidebar())}
                      className="transition-transform duration-1000 ease-in-out transform translate-x-0"
                  />
              </>
          )}
      </>
  )
}
export default App
