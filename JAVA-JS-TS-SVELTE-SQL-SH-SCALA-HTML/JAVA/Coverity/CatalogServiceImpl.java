/**
 * 26/05/2020
 */
package it.poste.fdr.ecomm.be.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.poste.fdr.ecomm.be.client.fdr.ProductKind;
import it.poste.fdr.ecomm.be.model.ErrorCode;
import it.poste.fdr.ecomm.be.model.Result;
import it.poste.fdr.ecomm.be.model.catalog.Condition;
import it.poste.fdr.ecomm.be.model.catalog.ConditionListResponse;
import it.poste.fdr.ecomm.be.model.catalog.Product;
import it.poste.fdr.ecomm.be.model.catalog.ProductListResponse;
import it.poste.fdr.ecomm.be.model.catalog.ProductResponse;
import it.poste.fdr.ecomm.be.service.helper.CatalogHelper;
import it.poste.fdr.ecomm.be.service.helper.ConditionHelper;
import it.poste.fdr.ecomm.persistence.exception.FdrDbException;

/**
 * 
 * @author BacchiniM
 *
 */
@Service
public class CatalogServiceImpl implements CatalogService {
	// logger
    private static final Logger log = LogManager.getLogger(CatalogServiceImpl.class);

    @Autowired
    private CatalogHelper catalogHelper;
    
    @Autowired
    private ConditionHelper conditionHelper;
    
	@Override
	public ProductListResponse getProducts() {
		ProductListResponse productResponse = new ProductListResponse();
		
		log.debug("getProducts() ");
		
		List<Product> products = null;;
		try {
			products = catalogHelper.getProducts();
		} catch (FdrDbException e) {
			log.error("DB Error",e);
			productResponse.setResult(Result.ERROR.getValue());
			return productResponse;
		}
		
		productResponse.setProducts(products);
		
		productResponse.setResult(Result.SUCCESS.getValue());
		
		return productResponse;
	}

	@Override
	public ProductResponse getProductByType(ProductKind kind) {
		ProductResponse productResponse = new ProductResponse();
		
		log.debug("getProductByType() ");
		
		Product product = null;
		try {
			product = catalogHelper.getProductByTypeName(kind.getValue());
		} catch (FdrDbException e) {
			log.error("DB Error",e);
			productResponse.setResult(Result.ERROR.getValue());
			return productResponse;
		}
		
		productResponse.setProduct(product);
		productResponse.setResult(Result.SUCCESS.getValue());
		
		return productResponse;
	}

	@Override
	public ConditionListResponse getConditions() {
		ConditionListResponse conditionResponse = new ConditionListResponse();
		
		log.debug("getConditions() ");
		
		List<Condition> conditions = null;
		try {
			conditions = conditionHelper.getConditions();
			
			conditionResponse.setConditions(conditions);
			conditionResponse.setResult(Result.SUCCESS.getValue());
		} catch (FdrDbException e) {
			log.error("DB Error",e);
			conditionResponse.setResult(Result.ERROR.getValue());
			conditionResponse.setErrorDescription(e.getMessage());
			conditionResponse.setErrorCode(ErrorCode.ERR_DATABASE.getValue());
			
			return conditionResponse;
		}
		
		return conditionResponse;
	}

}
