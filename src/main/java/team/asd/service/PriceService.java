package team.asd.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.asd.dao.PriceDao;
import team.asd.entity.Price;
import team.asd.exception.ValidationException;

import java.util.Date;

@Service
public class PriceService {

	private final PriceDao priceDao;

	public PriceService(@Autowired PriceDao priceDao) {
		this.priceDao = priceDao;
	}

	public Price readById(Integer id) {
		validId(id);
		return priceDao.readById(id);
	}

	public Price createPrice(Price price) {
		validPrice(price, false);
		priceDao.savePrice(price);
		return priceDao.readById(price.getId());
	}

	public Price updatePrice(Price price) {
		validPrice(price, true);
		priceDao.updatePrice(price);
		return priceDao.readById(price.getId());
	}

	public void deletePrice(Integer id) {
		validId(id);
		priceDao.deletePrice(id);
	}

	public void validId(Integer id) {
		if (id == null || id < 1) {
			throw new ValidationException("Id is not valid");
		}
	}

	public void validPrice(Price price, boolean updatable) {
		if (price == null) {
			throw new ValidationException("Price is null");
		}
		if (updatable && ObjectUtils.anyNull(price.getId())) {
			throw new ValidationException("Id field is null");
		} else if (!updatable && ObjectUtils.anyNull(
				price.getId(), price.getEntityType(), price.getEntityId(),
				price.getName(), price.getFromDate(), price.getToDate(),
				price.getValue(), price.getCurrency()
		)) {
			throw new ValidationException("Required field is null");
		}

		if (!updatable && !price.getFromDate().before(price.getToDate())) {
			throw new ValidationException("From_date have to be earlier then To_date");
		} else if (!ObjectUtils.allNull(price.getFromDate(), price.getToDate())) {
			if (price.getFromDate() == null && !new Date().before(price.getToDate())) {
				throw new ValidationException("To_date can not be earlier then current");
			} else if (price.getToDate() == null && !price.getFromDate().before(new Date())) {
				throw new ValidationException("From_date can not be later then current");
			} else if (!price.getFromDate().before(price.getToDate())) {
				throw new ValidationException("From_date have to be earlier then To_date");
			}
		}
	}

}
