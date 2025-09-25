package com.elasticsearch.search.digikala;

import com.elasticsearch.search.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DigikalaService {
    @Autowired
    DigikalaConfig digikalaConfig;
    public static String DIGIKALA_SRC = "Digikala";
    // TODO
    Long CommissionPercent = 5L;

    public Long getCommission(Product product) {
        if (product == null || product.getPrice() == null)
            return null;
        // TODO must get from digikala based on catId and brandId;
        return getCommission(product.getPrice(), product.getCatId(), product.getBrandId());
    }

    public Long getCommission(Long price, Long catId, Long brandId) {
        if (price == null)
            return null;
        return price * CommissionPercent / 100;
    }

    public List<Product> searchDigikalaProducts(String title) {
        List<ProductSearch.Product> products = searchProducts(title);
        List<ProductSearch.Product> result = new ArrayList<>();
        if (products != null && !products.isEmpty())
            for (ProductSearch.Product product : products) {
                ProductSearch.Product productD = getProduct(product.getId());
                result.add(productD);
            }
        return result.stream().map(this::convertDigikalaProductToProduct).collect(Collectors.toList());
    }

    public Product convertDigikalaProductToProduct(ProductSearch.Product product) {
        Product result = new Product();
        result.setTitle(product.getTitle_fa());
        result.setId(product.id);
        List<String> images = getImages(product);
        if (images != null && images.size() > 5)
            images = images.subList(0, 4);
        result.setImages(images);
        result.setSrc(DIGIKALA_SRC);
        result.setLink(getDetailUrl(product.id));
        if (product.getDefault_variant() != null && product.getDefault_variant().getPrice() != null) {
            result.setPrice(covertToTooman(product.getDefault_variant().getPrice().selling_price));
            result.setOriginalPrice(covertToTooman(product.getDefault_variant().getPrice().getRrp_price()));
            result.setAvailability(true);
        } else
            result.setAvailability(false);
        if (product.getCategory() != null) {
            result.setCat(product.getCategory().getTitle_fa());
            result.setCatId(product.getCategory().getId());
        }
        if (product.getBrand() != null) {
            result.setBrand(product.getBrand().getTitle_fa());
            result.setBrandId(product.getBrand().getId());
        }
        return result;
    }

    protected Long covertToTooman(Long rial) {
        if (rial != null)
            return rial / 10;
        return null;
    }

    public List<String> getImages(ProductSearch.Product product) {
        if (product == null || product.getImages() == null || product.getImages().getList() == null || product.getImages().getList().isEmpty())
            return new ArrayList<>();
        List<String> list = new ArrayList<>();
        if (product.getImages().getMain() != null)
            list.add(getImageLinkFromImage(product.getImages().main));
        list.addAll(product.getImages().getList().stream().map(this::getImageLinkFromImage).toList());


        return list;
    }

    public String getImageLinkFromImage(ProductSearch.Image image) {
        return image.webp_url.get(0);
    }

    public List<ProductSearch.Product> searchProducts(String title) {
        String url = digikalaConfig.productSearchUrl + title + "&size=10";
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);
        factory.setReadTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(factory);
        try {
            ProductSearch productSearch = restTemplate.getForObject(url, ProductSearch.class);
            if (productSearch != null && productSearch.status.equals(200))
                return productSearch.data.getProducts();
        } catch (Exception exception) {
            System.out.println("salam");
        }
        return null;
    }

    public ProductSearch.Product getProduct(String id) {
        String url = getDetailUrl(id);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        try {
            ProductDetailResult productDetailResult = restTemplate.getForObject(url, ProductDetailResult.class);
            if (productDetailResult != null && productDetailResult.status.equals(200))
                return productDetailResult.data.getProduct();
        } catch (Exception exception) {
            System.out.println("salam");
        }
        return null;
    }

    private String getDetailUrl(String id) {
        return digikalaConfig.productDetailUrl + id + "/";
    }
}
