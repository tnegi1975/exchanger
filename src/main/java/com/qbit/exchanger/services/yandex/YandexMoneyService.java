package com.qbit.exchanger.services.yandex;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import ru.yandex.money.api.ApiCommandsFacade;
import ru.yandex.money.api.ApiCommandsFacadeImpl;
import ru.yandex.money.api.CommandUrlHolder;
import ru.yandex.money.api.TestUrlHolder;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.enums.Destination;
import ru.yandex.money.api.response.ProcessPaymentResponse;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import ru.yandex.money.api.response.RequestPaymentResponse;
import ru.yandex.money.api.rights.Payment;
import ru.yandex.money.api.rights.Permission;

public class YandexMoneyService {

	private static final String CLIENT_ID = "469EAE5558DA3258EB004866286C7EE56E6BFDA2A9660E8D3F0343F1DA77F62C";
	private static final String REDIRECT_URI = "https://localhost:8443/yandex/tempCode";
	private static final String STORE_WALLET = "410011982465483";
	private static final String STORE_TOKEN = "410011982465483.6B199D52FE91DE3B45FFF4DA2DBC851E55228984B15A66B978B8CCA"
			+ "452FF36B8F3BC3BB1D6665CE4C34B816B666593F75CAFAE32AF77D0939EC328363162D70B2CCB2809FA425E8790585B471DB8604E"
			+ "1DFC19884B383DE6E8BA6426E5DC02916D2362AD633E24F3B59AFF335B3FE85A686414210A0DDF973CBA00C1DA29D15D";
	private static final String OPERATION_DESCRIPTION = "sample";

	private YandexMoney yandexMoney;
	
	private ApiCommandsFacade apiCommandsFacade; // use this for payments in test mode
	private TestUrlHolder urlHolder; // use this to chande test mode parameters

	public YandexMoneyService() {
		yandexMoney = new YandexMoneyImpl(CLIENT_ID);
		
		// !!! TEST MODE; one does not simply enable test mode in yamolib :(
		urlHolder = new TestUrlHolder();
		DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "yamolib");
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 4000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 60100);
		apiCommandsFacade = new ApiCommandsFacadeImpl(httpClient, urlHolder);
	}

	public String getAuthorizeUri(Boolean mobile, BigDecimal amount) {
		Collection<Permission> scope = getPaymentScope(amount);
		return yandexMoney.authorizeUri(scope, REDIRECT_URI, mobile);
	}

	public String getAuthorizeUri(Boolean mobile) {
		Collection<Permission> scope = getPaymentScope();
		return yandexMoney.authorizeUri(scope, REDIRECT_URI, mobile);
	}

	public void processPaymentIn(String token, BigDecimal amount) throws Exception {
	}

	private String receiveToken(String code) throws Exception {
		String token = null;
		try {
			ReceiveOAuthTokenResponse tokenResponse = yandexMoney.receiveOAuthToken(code, REDIRECT_URI);
			if (tokenResponse != null && tokenResponse.isSuccess()) {
				token = tokenResponse.getAccessToken();
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return token;
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
