package com.qbit.exchanger.services.yandex;

import com.qbit.exchanger.services.core.OperationStatus;
import com.qbit.exchanger.services.core.OperationResult;
import com.qbit.exchanger.services.core.ProcessingException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.enums.Destination;
import ru.yandex.money.api.response.AccountInfoResponse;
import ru.yandex.money.api.response.ProcessPaymentResponse;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.response.RequestPaymentResponse;
import ru.yandex.money.api.rights.AccountInfo;
import ru.yandex.money.api.rights.IdentifierType;
import ru.yandex.money.api.rights.OperationDetails;
import ru.yandex.money.api.rights.OperationHistory;
import ru.yandex.money.api.rights.Payment;
import ru.yandex.money.api.rights.PaymentP2P;
import ru.yandex.money.api.rights.Permission;

public class YandexMoneyService {

	private static final String CLIENT_ID = "B83AF6B23CA9C5E0CA7AAFC2F1B98CDAEEAD59A49DED9A4BEE52B8F85A19D20B";
	private static final String REDIRECT_URI = "https://localhost:8443/exchanger/webapi/yandex/receive";
	private static final String STORE_WALLET = "41001954722279";
	private static final String STORE_TOKEN = "41001954722279.41FA1CFDB8228302CA23314BFEF423E2A3A719994AFE60A5"
			+ "6CB63DC438FE08B94CD8AFFC231EEE9404A19F0943D1D2B15E211561AD73A899951C62FE0C9891A761641F089F9C57D0"
			+ "6D5955FCDDB2DA609F6B7986E09EDFDFA580C5F543F4EA2091093BE448338B4D5E93D58D3F484BB804342607C48D1B7C5DF9AF1A86B271C6";
	private static final String OPERATION_DESCRIPTION = "test";

	private YandexMoney yandexMoney;

	public YandexMoneyService() {
		yandexMoney = new YandexMoneyImpl(CLIENT_ID);
	}

	public String getAuthorizeUri(Boolean mobile, BigDecimal amount) {
		Collection<Permission> scope = getPaymentScope(amount);
		return yandexMoney.authorizeUri(scope, REDIRECT_URI, mobile);
	}

	public String getAuthorizeUri(Boolean mobile) {
		Collection<Permission> scope = getPaymentScope();
		return yandexMoney.authorizeUri(scope, REDIRECT_URI, mobile);
	}

	public OperationResult receiveMoney(String code, BigDecimal amount) throws ProcessingException {
//		String token = receiveToken(code);
//        String accountNumber = parseToken(token);
//        List<Operation> operations = paymentRepository.findInProgress(accountNumber);
//        for(Operation operation : operations) {
//            RequestPaymentResponse p2pResponse = paymentP2P(token, STORE_WALLET, operation.getAmount(), operation.getDescription());
//            if (p2pResponse != null && p2pResponse.isSuccess()) {
//                ProcessPaymentResponse processResponse = processPayment(token, p2pResponse.getRequestId());
//                if(processResponse != null && processResponse.isSuccess()) {
//                    // TODO success
//                } else {
//                    // TODO error
//                }
//            } else {
//                // TODO error
//            }
//        }
		OperationResult result = new OperationResult(OperationStatus.IN_PROGRESS);
		String token = receiveToken(code);
		RequestPaymentResponse p2pResponse = requestPayment(token, STORE_WALLET, amount, OPERATION_DESCRIPTION);
		if (p2pResponse != null && p2pResponse.isSuccess()) {
			ProcessPaymentResponse processResponse = processPayment(token, p2pResponse.getRequestId());
			if (processResponse != null && processResponse.isSuccess()) {
				result.setStatus(OperationStatus.SUCCESS);
			} else {
				result.setStatus(OperationStatus.ERROR);
				result.setText(processResponse != null ? processResponse.getError().getCode() : "");
			}
		} else {
			result.setText(p2pResponse != null ? p2pResponse.getError().getCode() : "");
		}
		return result;
	}

	public OperationResult sendMoney(String wallet, BigDecimal amount) throws ProcessingException {
		OperationResult result = new OperationResult(OperationStatus.IN_PROGRESS);
		try {
			RequestPaymentResponse response = requestPayment(STORE_TOKEN, wallet, amount, OPERATION_DESCRIPTION);
			if (response != null && response.isSuccess()) {
				ProcessPaymentResponse paymentResponse = processPayment(STORE_TOKEN, response.getRequestId());
				if (paymentResponse != null && paymentResponse.isSuccess()) {
					result.setStatus(OperationStatus.SUCCESS);
				} else {
					result.setStatus(OperationStatus.ERROR);
					result.setText(paymentResponse != null ? paymentResponse.getError().getCode() : "");
				}
			} else {
				result.setStatus(OperationStatus.ERROR);
				result.setText(response != null ? response.getError().getCode() : "");
			}
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage());
		}
		return result;
	}

	public BigDecimal getBalanceInfo() throws ProcessingException {
		try {
			AccountInfoResponse response = yandexMoney.accountInfo(STORE_TOKEN);
			return response.getBalance();
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage());
		}
	}

	private String receiveToken(String code) throws ProcessingException {
		String token = null;
		try {
			ReceiveOAuthTokenResponse tokenResponse = yandexMoney.receiveOAuthToken(code, REDIRECT_URI);
			if (tokenResponse != null && tokenResponse.isSuccess()) {
				token = tokenResponse.getAccessToken();
			}
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage());
		}
		return token;
	}

	private RequestPaymentResponse requestPayment(String token, String wallet, BigDecimal amount, String description) throws ProcessingException {
		RequestPaymentResponse response = null;
		try {
			response = yandexMoney.requestPaymentP2PDue(token, wallet, IdentifierType.account, amount, description, description, null);
		} catch (Exception e) {
			Logger.getLogger(YandexMoneyService.class.getName()).log(Level.SEVERE, null, e);
			throw new ProcessingException(e.getMessage());
		}
		return response;
	}

	private ProcessPaymentResponse processPayment(String token, String requestId) throws ProcessingException {
		ProcessPaymentResponse response = null;
		try {
			response = yandexMoney.processPaymentByWallet(token, requestId);
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage());
		}
		return response;
	}

	/**
	 * Creates permissions for this application.
	 *
	 * @return
	 */
	private Collection<Permission> getAppPaymentScope() {
		List<Permission> permissions = new LinkedList<>();
		permissions.add(new PaymentP2P());
		permissions.add(new AccountInfo());
		permissions.add(new OperationDetails());
		permissions.add(new OperationHistory());
		return Collections.unmodifiableList(permissions);
	}

	private Collection<Permission> getPaymentScope(BigDecimal sum) {
		Permission scope = new Payment(Destination.toAccount, STORE_WALLET, sum.toString());
		return Collections.singletonList(scope);
	}

	private Collection<Permission> getPaymentScope() {
		Payment scope = new Payment();
		scope.toAccount(STORE_WALLET);
		Permission result = scope;
		return Collections.singletonList(result);
	}
}
