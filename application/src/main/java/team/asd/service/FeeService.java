package team.asd.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.asd.dao.FeeDao;
import team.asd.entity.Fee;
import team.asd.exception.ValidationException;
import team.asd.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FeeService {
	private final FeeDao feeDao;

	public FeeService(@Autowired FeeDao feeDao) {
		this.feeDao = feeDao;
	}

	public Fee readById(Integer id) {
		ValidationUtil.validId(id);
		return feeDao.readById(id);
	}

	public List<Fee> readByParams(Integer feeType, Integer productId, String state) {
		if (productId != null && productId < 1) {
			throw new ValidationException("Incorrect product Id: not positive");
		}
		return feeDao.readFeesByParams(feeType, productId, state);
	}

	public List<Fee> readByDateRange(Integer productId, Date fromDate, Date toDate) {
		ValidationUtil.validId(productId);
		ValidationUtil.validDateRange(fromDate, toDate);
		return feeDao.readFeesByDateRange(productId, fromDate, toDate);
	}

	public List<Fee> readByMinValueSupplierId(Integer minValue, Integer supplierId) {
		if (minValue == null || supplierId == null || supplierId < 1) {
			throw new ValidationException("Incorrect integer value: null or negative");
		}
		return feeDao.readFeesByValueProductSupplierId(minValue, supplierId);
	}

	public Fee createFee(Fee fee) {
		validFee(fee, false);
		feeDao.saveFee(fee);
		return feeDao.readById(fee.getId());
	}

	public void createFees(List<Fee> fees) {
		CollectionUtils.filter(fees, Objects::nonNull);
		fees.forEach(fee -> validFee(fee, false));
		feeDao.saveFees(fees);
	}

	public Fee updateFee(Fee fee) {
		validFee(fee, true);
		feeDao.updateFee(fee);
		return feeDao.readById(fee.getId());
	}

	public void deleteFee(Integer id) {
		ValidationUtil.validId(id);
		feeDao.deleteFee(id);
	}

	private void validFee(Fee fee, boolean updatable) {
		if (fee == null) {
			throw new ValidationException("Fee is null");
		}
		if (updatable) {
			ValidationUtil.validId(fee.getId());
		} else {
			ValidationUtil.validFields(fee.getFeeType(), fee.getProductId(), fee.getState(),
					fee.getFromDate(), fee.getToDate(), fee.getTaxType(),
					fee.getUnit(), fee.getValue(), fee.getValueType(),
					fee.getCurrency());
		}
		ValidationUtil.validDates(fee.getFromDate(), fee.getToDate());
	}
}
