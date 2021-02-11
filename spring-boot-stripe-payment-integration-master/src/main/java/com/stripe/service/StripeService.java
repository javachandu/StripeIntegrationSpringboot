package com.stripe.service;

import java.util.HashMap;
import java.util.Map;

import com.stripe.param.ChargeCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;

@Service
public class StripeService {

	@Value("${stripe.key.secret}")
	private String API_SECET_KEY;

	public StripeService() {

	}

	public String createCustomer(String email, String token) {

		String id = null;
		Customer customer=null;

		try {
			Stripe.apiKey = API_SECET_KEY;
			CustomerCreateParams customerParams =
					CustomerCreateParams.builder()
							.setEmail(email)
							.setName("test")
							.setSource(token)
							.setAddress(
									CustomerCreateParams.Address.builder()
											.setLine1("510 Townsend St")
											.setPostalCode("98140")
											.setCity("San Francisco")
											.setState("CA")
											.setCountry("US")
											.build())
							.build();

			customer = Customer.create(customerParams);
			id = customer.getId();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public String createSubscription(String customerId, String plan, String coupon) {

		String subscriptionId = null;

		try {
			Stripe.apiKey = API_SECET_KEY;

			Map<String, Object> item = new HashMap<>();
			item.put("plan", plan);

			Map<String, Object> items = new HashMap<>();
			items.put("0", item);

			Map<String, Object> params = new HashMap<>();
			params.put("customer", customerId);
			params.put("items", items);

			if (!coupon.isEmpty()) {
				params.put("coupon", coupon);
			}

			Subscription subscription = Subscription.create(params);

			subscriptionId = subscription.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subscriptionId;
	}
	
	public boolean cancelSubscription(String subscriptionId) {
		
		boolean subscriptionStatus;
		
		try {
			Subscription subscription = Subscription.retrieve(subscriptionId);
			subscription.cancel();
			subscriptionStatus = true;	
		} catch (Exception e) {
			e.printStackTrace();
			subscriptionStatus = false;
		}
		return subscriptionStatus;
	}
	
	public Coupon retriveCoupon(String code) {
		try {
			Stripe.apiKey = API_SECET_KEY;
			return Coupon.retrieve(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String createCharge(String email, String token, int amount,String customerId) {
		
		String chargeId = null;
		
		try {
			Stripe.apiKey = API_SECET_KEY;
			
		/*	Map<String, Object> chargeParams = new HashMap<>();
			chargeParams.put("description","Charge for "+email);
			chargeParams.put("currency","usd");
			chargeParams.put("amount",amount);
			//chargeParams.put("source",token);
			chargeParams.put("customer",customerId);*/
			ChargeCreateParams chargeCreateParams=ChargeCreateParams.builder()
					.setAmount(1000L)
					.setCurrency("usd")
					.setCustomer(customerId)
					.setDescription("description:Charge for "+email)
					.build();
			
			Charge charge = Charge.create(chargeCreateParams);
			
		    chargeId = charge.getId();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chargeId;
	}

}
