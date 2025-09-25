package com.elasticsearch.search.product;

import com.elasticsearch.search.match.DigikalaMatcherService;
import com.elasticsearch.search.match.MatchResultSum;
import com.elasticsearch.search.match.MatchService;
import com.elasticsearch.search.validation.AccessService;
import javassist.tools.web.BadHttpRequest;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    DigikalaMatcherService digikalaMatcherService;

    @Autowired
    MatchService matchService;

    @Autowired
    SmartValidator validator;
    @Autowired
    AccessService accessService;

    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public Product getProduct(@PathVariable  String id){
        return productService.getProductById(id);
    }

    @RequestMapping(value = "bulk", method = RequestMethod.POST)
    public List<Product> addProducts(String key, @RequestBody List<Product> products, BindingResult result) throws MethodArgumentNotValidException {
        accessService.checkKey(key);
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            productList.add(addProduct(key, product, result));
        }
        return productList;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Product addProduct(String key, @RequestBody Product product, BindingResult result) throws MethodArgumentNotValidException {
        accessService.checkKey(key);
        if (product.getImages() != null && product.getImages().size() > 0) {
            List<@URL String> images = product.getImages().stream().filter(ProductService::isValidURL).collect(Collectors.toList());
            if(images.size() > 5)
                images = images.subList(0,2);
            product.setImages(images);
        }
        validator.validate(product, result);
        if (result.hasErrors())
            throw new MethodArgumentNotValidException(null, result);
        Product saveProduct = productService.saveProduct(product);
        digikalaMatcherService.addProduct(saveProduct);
        return saveProduct;
    }

    @RequestMapping(value = "search/digikala", method = RequestMethod.GET)
    List<Product> searchDigikalaApi(String key, Product product) {
        accessService.checkKey(key);
        return digikalaMatcherService.searchProductOnDigikala(product);
    }
    @RequestMapping(value = "search", method = RequestMethod.GET)
    List<Product> search(String key, String title, String src, Pageable pageable) {
        accessService.checkKey(key);
        title = ProductService.convertPersianToEnglish(title);
        return productService.searchProducts(title, src, pageable.getPageNumber()* pageable.getPageSize(), pageable.getPageSize());
    }
}
