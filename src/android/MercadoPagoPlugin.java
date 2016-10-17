 data.getString(0);package com.mercadopago.cordova.sdk;


import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;


import com.google.gson.Gson;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;

import com.mercadopago.model.DecorationPreference;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.Token;
import com.mercadopago.core.MercadoPagoUI;

import com.mercadopago.util.JsonUtil;

import com.mercadopago.util.MercadoPagoUtil;


import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class MercadoPagoPlugin extends CordovaPlugin {
    private CallbackContext callback = null;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {


        if (action.equals("setPaymentPreference")) {
            Integer maxAcceptedInstallments = data.getInt(0);
            Integer defaultInstallments = data.getInt(1);
            JSONArray excludedPaymentMethodsJson = data.getJSONArray(2);
            JSONArray excludedPaymentTypesJson = data.getJSONArray(3);

            createPaymentPreference(maxAcceptedInstallments, defaultInstallments, excludedPaymentMethodsJson, excludedPaymentTypesJson, callbackContext);
            return true;

        } else if (action.equals("startSavedCards")) {

            String merchantBaseUrl = data.getString(0);
            String merchantGetCustomerUri = data.getString(1);
            String merchantAccessToken = data.getString(2);
            String color = data.getString(3);
            Boolean blackFont = data.getBoolean(4);
            String title = data.getString(5);
            String footerText = data.getString(6);
            String confirmPromptText = data.getString(7);
            String mode = data.getString(8);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(9), PaymentPreference.class);

            startSavedCards(merchantBaseUrl, merchantGetCustomerUri, merchantAccessToken, color, blackFont, title, footerText, confirmPromptText, mode, paymentPreference, callbackContext);
            return true;

        } else if (action.equals("startCardSelection")) {

            String publicKey = data.getString(0);
            String site = data.getString(1);
            BigDecimal amount = new BigDecimal(data.getDouble(2));
            String merchantBaseUrl = data.getString(3);
            String merchantGetCustomerUri = data.getString(4);
            String merchantAccessToken = data.getString(5);
            String color = data.getString(6);
            Boolean blackFont = data.getBoolean(7);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(8), PaymentPreference.class);

            startCardSelection(publicKey, site, amount, merchantBaseUrl, merchantGetCustomerUri, merchantAccessToken, color, blackFont, paymentPreference, callbackContext);
            return true;

        } else if (action.equals("startCheckout")) {

            String publicKey = data.getString(0);
            String prefid = data.getString(1);
            String color = data.getString(2);
            Boolean blackFont = data.getBoolean(3);
            startCheckout(publicKey, prefid, color, blackFont, callbackContext);
            return true;

        } else if (action.equals("showPaymentVault")) {

            String publicKey = data.getString(0);
            String site = data.getString(1);
            BigDecimal amount = new BigDecimal(data.getDouble(2));
            String color = data.getString(3);
            Boolean blackFont = data.getBoolean(4);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(5), PaymentPreference.class);
            startPaymentVault(publicKey, site, amount, color, blackFont, paymentPreference, callbackContext);
            return true;

        } else if (action.equals("showCardWithoutInstallments")) {

            String publicKey = data.getString(0);
            String color = data.getString(1);
            Boolean blackFont = data.getBoolean(2);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(3), PaymentPreference.class);

            startCardFormWithoutInstallments(publicKey, color, blackFont, paymentPreference, callbackContext);
            return true;

        } else if (action.equals("showCardWithInstallments")) {

            String publicKey = data.getString(0);
            String site = data.getString(1);
            BigDecimal amount = new BigDecimal(data.getDouble(2));
            String color = data.getString(3);
            Boolean blackFont = data.getBoolean(4);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(5), PaymentPreference.class);
            startCardFormWithInstallments(publicKey, site, amount, color, blackFont, paymentPreference, callbackContext);
            return true;

        } else if (action.equals("showPaymentMethods")) {

            String publicKey = data.getString(0);
            String color = data.getString(1);
            Boolean blackFont = data.getBoolean(2);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(3), PaymentPreference.class);
            startPaymentMethodsList(publicKey, color, blackFont, paymentPreference, callbackContext);
            return true;

        } else if (action.equals("showIssuers")) {
            String publicKey = data.getString(0);
            String color = data.getString(1);
            String paymentMethodId = data.getString(2);
            Boolean blackFont = data.getBoolean(3);
            startIssuersList(data, callbackContext);
            return true;

        } else if (action.equals("showInstallments")) {

            String publicKey = data.getString(0);
            String site = data.getString(1);
            BigDecimal amount = new BigDecimal(data.getDouble(2));
            String paymentMethodId = data.getString(3);
            Long issuerId = data.getLong(4);
            String color = data.getString(5);
            Boolean blackFont = data.getBoolean(6);
            PaymentPreference paymentPreference = JsonUtil.getInstance().fromJson(data.getString(7), PaymentPreference.class);
            startInstallmentsList(data, callbackContext);
            return true;

        } else if (action.equals("showBankDeals")) {

            String publicKey = data.getString(0);
            String color = data.getString(1);
            Boolean blackFont = data.getBoolean(2);
            startBankDealsList(publicKey, color, blackFont, callbackContext);
            return true;

        } else if (action.equals("showPaymentResult")) {

            String publicKey = data.getString(0);
            Payment payment = JsonUtil.getInstance().fromJson(data.getString(1), Payment.class);
            String paymentTypeId = data.getString(2);
            startPaymentResult(publicKey, payment, paymentTypeId, callbackContext);
            return true;

        } else if (action.equals("createPayment")) {

            String publicKey = data.getString(0);
            String itemId = data.getString(1);
            Integer itemQuantity = data.getInt(2);
            BigDecimal amount = new BigDecimal(data.getDouble(3));
            Long campaignId = data.getLong(4);
            String merchantAccessToken = data.getString(5);
            String merchantBaseUrl = data.getString(6);
            String merchantGetCustomerUri = data.getString(7);
            String paymentMethodId = data.getString(8);
            int installments = data.getInt(9);
            Long cardIssuerId = data.getLong(10);
            String token = data.getString(11);

            createPayment(publicKey, itemId, itemQuantity, amount, campaignId, merchantAccessToken, merchantBaseUrl, merchantGetCustomerUri, paymentMethodId, installments, cardIssuerId, token, callbackContext);
            return true;

        } else if (action.equals("getPaymentMethods")) {

            getPaymentMethods(data, callbackContext);
            return true;

        } else if (action.equals("getIssuers")) {

            getIssuers(data, callbackContext);
            return true;

        } else if (action.equals("getInstallments")) {

            getInstallments(data, callbackContext);
            return true;

        } else if (action.equals("getIdentificationTypes")) {

            getIdentificationTypes(data, callbackContext);
            return true;

        } else if (action.equals("createToken")) {

            createToken(data, callbackContext);
            return true;

        } else if (action.equals("getBankDeals")) {

            getBankDeals(data, callbackContext);
            return true;

        } else if (action.equals("getPaymentResult")) {

            getPaymentResult(data, callbackContext);
            return true;

        } else {
            return false;
        }
    }

    private void createPaymentPreference(Integer maxAcceptedInstallments, Integer defaultInstallments, JSONArray excludedPaymentMethodsJson, JSONArray excludedPaymentTypesJson, CallbackContext callbackContext) {
        PaymentPreference paymentPreference = new PaymentPreference();
        if (maxAcceptedInstallments != 0) {
            paymentPreference.setMaxAcceptedInstallments(maxAcceptedInstallments);
        }
        if (defaultInstallments != 0) {
            paymentPreference.setDefaultInstallments(defaultInstallments);
        }
        List<String> excludedPaymentTypes = new ArrayList();
        for (int i = 0; i < excludedPaymentMethodsJson.length(); i++) {
            excludedPaymentTypes.add(excludedPaymentMethodsJson.getString(i));
        }

        paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);

        List<String> excludedPaymentMethods = new ArrayList();
        for (int i = 0; i < excludedPaymentMethodsJson.length(); i++) {
            excludedPaymentMethods.add(excludedPaymentMethodsJson.getString(i));
        }

        paymentPreference.setExcludedPaymentMethodIds(excludedPaymentMethods);
        callbackContext.success(JsonUtil.getInstance().toJson(paymentPreference));
    }

    private void startSavedCards(String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, String color, Boolean blackFont, String title, String footerText, String confirmPromptText, String mode, PaymentPreference paymentPreference, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);

        DecorationPreference decorationPreference = new DecorationPreference();
        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        MercadoPagoUI.Activities.SavedCardsActivityBuilder builder = new MercadoPagoUI.Activities.SavedCardsActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setMerchantBaseUrl(merchantBaseUrl)
                .setMerchantGetCustomerUri(merchantGetCustomerUri)
                .setMerchantAccessToken(merchantAccessToken)
                .setPaymentPreference(paymentPreference)
                .setDecorationPreference(decorationPreference)
                .setTitle(title)
                .setFooter(footerText)
                .setSelectionConfirmPromptText(confirmPromptText);

        if (mode.equals("delete")) {
            builder.setSelectionImage(android.R.drawable.ic_delete);
        }

        builder.startActivity();
        callback = callbackContext;
    }

    private void startCardSelection(String publicKey, String site, BigDecimal amount, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, String color, Boolean blackFont, PaymentPreference merchantPaymentPreference, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);

        DecorationPreference decorationPreference = new DecorationPreference();
        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        List<String> excluded = new ArrayList();
        excluded.add(PaymentTypes.ACCOUNT_MONEY);
        excluded.add(PaymentTypes.ATM);
        excluded.add(PaymentTypes.BANK_TRANSFER);
        excluded.add(PaymentTypes.DIGITAL_CURRENCY);
        excluded.add(PaymentTypes.TICKET);

        PaymentPreference paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(excluded);

        if (merchantPaymentPreference != null) {
            paymentPreference.setExcludedPaymentMethodIds(merchantPaymentPreference.getExcludedPaymentMethodIds());
            paymentPreference.setDefaultInstallments(merchantPaymentPreference.getDefaultInstallments());
            paymentPreference.setMaxAcceptedInstallments(merchantPaymentPreference.getMaxInstallments());
        }

        MercadoPago.StartActivityBuilder mp = new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setAmount(amount)
                .setMerchantBaseUrl(merchantBaseUrl)
                .setMerchantGetCustomerUri(merchantGetCustomerUri)
                .setMerchantAccessToken(merchantAccessToken)
                .setPaymentPreference(paymentPreference)
                .setDecorationPreference(decorationPreference);

        if (site.toUpperCase().equals("ARGENTINA")) {
            mp.setSite(Sites.ARGENTINA);
        } else if (site.toUpperCase().equals("BRASIL")) {
            mp.setSite(Sites.BRASIL);
        } else if (site.toUpperCase().equals("CHILE")) {
            mp.setSite(Sites.CHILE);
        } else if (site.toUpperCase().equals("COLOMBIA")) {
            mp.setSite(Sites.COLOMBIA);
        } else if (site.toUpperCase().equals("MEXICO")) {
            mp.setSite(Sites.MEXICO);
        } else if (site.toUpperCase().equals("USA")) {
            mp.setSite(Sites.USA);
        } else if (site.toUpperCase().equals("VENEZUELA")) {
            mp.setSite(Sites.VENEZUELA);
        }
        mp.startPaymentVaultActivity();
        callback = callbackContext;
    }

    private void startCheckout(String publicKey, String prefid, String color, Boolean blackFont, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);

        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setDecorationPreference(decorationPreference)
                .setPublicKey(publicKey)
                .setCheckoutPreferenceId(prefid)
                .startCheckoutActivity();


        callback = callbackContext;
    }

    private void startPaymentVault(String publicKey, String site, BigDecimal amount, String color, Boolean blackFont, PaymentPreference paymentPreference, CallbackContext callbackContext) {
        DecorationPreference decorationPreference = new DecorationPreference();

        if (color) {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        cordova.setActivityResultCallback(this);

        MercadoPago.StartActivityBuilder mp = new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setDecorationPreference(decorationPreference)
                .setPaymentPreference(paymentPreference)
                .setAmount(amount);

        if (site.toUpperCase().equals("ARGENTINA")) {
            mp.setSite(Sites.ARGENTINA);
        } else if (site.toUpperCase().equals("BRASIL")) {
            mp.setSite(Sites.BRASIL);
        } else if (site.toUpperCase().equals("CHILE")) {
            mp.setSite(Sites.CHILE);
        } else if (site.toUpperCase().equals("COLOMBIA")) {
            mp.setSite(Sites.COLOMBIA);
        } else if (site.toUpperCase().equals("MEXICO")) {
            mp.setSite(Sites.MEXICO);
        } else if (site.toUpperCase().equals("USA")) {
            mp.setSite(Sites.USA);
        } else if (site.toUpperCase().equals("VENEZUELA")) {
            mp.setSite(Sites.VENEZUELA);
        }

        mp.startPaymentVaultActivity();

        callback = callbackContext;
    }

    private void startCardFormWithoutInstallments(String publicKey, String color, Boolean blackFont, PaymentPreference paymentPreference, CallbackContext callbackContext) {
        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setDecorationPreference(decorationPreference)
                .setPublicKey(publicKey)
                .setPaymentPreference(paymentPreference)
                .setInstallmentsEnabled(false)
                .startCardVaultActivity();

        cordova.setActivityResultCallback(this);
        callback = callbackContext;
    }

    private void startCardFormWithInstallments(String publicKey, String site, BigDecimal amount, String color, Boolean blackFont, PaymentPreference paymentPreference, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        BigDecimal amount = new BigDecimal(amount);
        MercadoPago.StartActivityBuilder mp = new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setDecorationPreference(decorationPreference)
                .setPaymentPreference(paymentPreference)
                .setInstallmentsEnabled(true)
                .setAmount(amount);

        if (site.toUpperCase().equals("ARGENTINA")) {
            mp.setSite(Sites.ARGENTINA);
        } else if (site.toUpperCase().equals("BRASIL")) {
            mp.setSite(Sites.BRASIL);
        } else if (site.toUpperCase().equals("CHILE")) {
            mp.setSite(Sites.CHILE);
        } else if (site.toUpperCase().equals("COLOMBIA")) {
            mp.setSite(Sites.COLOMBIA);
        } else if (site.toUpperCase().equals("MEXICO")) {
            mp.setSite(Sites.MEXICO);
        } else if (site.toUpperCase().equals("USA")) {
            mp.setSite(Sites.USA);
        } else if (site.toUpperCase().equals("VENEZUELA")) {
            mp.setSite(Sites.VENEZUELA);
        }
        mp.startCardVaultActivity();
    }

    private void startPaymentMethodsList(String publicKey, String color, Boolean blackFont, PaymentPreference paymentPreference, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;
        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setDecorationPreference(decorationPreference)
                .setPaymentPreference(paymentPreference, PaymentPreference.class))
                .startPaymentMethodsActivity();

    }

    private void startIssuersList(String publicKey, String paymentMethodId, String color, Boolean blackFont, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(paymentMethodId);

        new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setDecorationPreference(decorationPreference)
                .setPaymentMethod(paymentMethod)
                .startIssuersActivity();
    }

    private void startInstallmentsList(String publicKey, String site, BigDecimal amount, String paymentMethodId, Long issuerId, String color, Boolean blackFont, PaymentPreference paymentPreference, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;
        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);
        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(paymentMethodId);
        Issuer issuer = new Issuer();
        issuer.setId(issuerId);

        MercadoPago.StartActivityBuilder mp = new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setDecorationPreference(decorationPreference)
                .setPaymentPreference(paymentPreference)
                .setAmount(amount)
                .setIssuer(issuer)
                .setPaymentMethod(paymentMethod);

        if (site.toUpperCase().equals("ARGENTINA")) {
            mp.setSite(Sites.ARGENTINA);
        } else if (site.toUpperCase().equals("BRASIL")) {
            mp.setSite(Sites.BRASIL);
        } else if (site.toUpperCase().equals("CHILE")) {
            mp.setSite(Sites.CHILE);
        } else if (site.toUpperCase().equals("COLOMBIA")) {
            mp.setSite(Sites.COLOMBIA);
        } else if (site.toUpperCase().equals("MEXICO")) {
            mp.setSite(Sites.MEXICO);
        } else if (site.toUpperCase().equals("USA")) {
            mp.setSite(Sites.USA);
        } else if (site.toUpperCase().equals("VENEZUELA")) {
            mp.setSite(Sites.VENEZUELA);
        }
        mp.startInstallmentsActivity();
    }

    private void startBankDealsList(String publicKey, String color, Boolean blackFont CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        DecorationPreference decorationPreference = new DecorationPreference();

        if (color != "null") {
            decorationPreference.setBaseColor(color);

        }
        if (blackFont) {
            decorationPreference.enableDarkFont();
        }

        new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setDecorationPreference(decorationPreference)
                .startBankDealsActivity();
    }

    private void startPaymentResult(String publicKey, Payment payment, String paymentTypeId, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentTypeId(paymentTypeId);

        new MercadoPago.StartActivityBuilder()
                .setActivity(this.cordova.getActivity())
                .setPublicKey(publicKey)
                .setPayment(payment)
                .setPaymentMethod(paymentMethod)
                .startPaymentResultActivity();

    }


    //SERVICIOS

    private void createPayment(String publicKey, String itemId, Integer itemQuantity, BigDecimal amount, Long campaignId, String merchantAccessToken, String merchantBaseUrl, String merchantGetCustomerUri, String paymentMethodId, int installments, Long cardIssuerId, String token, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(paymentMethodId);

        if (paymentMethod != null) {

            Item item = new Item(itemId, itemQuantity, amount);

            String paymentMethodId = paymentMethod.getId();

            MerchantPayment payment = new MerchantPayment(item, installments,
                    cardIssuerId, token, paymentMethodId, campaignId, merchantAccessToken);

            // Enviar los datos a tu servidor
            MerchantServer.createPayment(this.cordova.getActivity(), merchantBaseUrl, merchantGetCustomerUri, payment, new Callback<Payment>() {
                @Override
                public void success(Payment payment) {

                    if (MercadoPagoUtil.isCard(paymentMethod.getPaymentTypeId())) {
                        Gson gson = new Gson();
                        String mpPayment = gson.toJson(payment);
                        String mpPaymentMethod = gson.toJson(paymentMethod);
                        JSONObject js = new JSONObject();
                        try {
                            js.put("payment", mpPayment);
                            js.put("payment_methods", mpPaymentMethod);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                        callback.success(js.toString());
                    } else {

                        Gson gson = new Gson();
                        String mpPayment = gson.toJson(payment);
                        String mpPaymentMethod = gson.toJson(paymentMethod);
                        JSONObject js = new JSONObject();
                        try {
                            js.put("payment", mpPayment);
                            js.put("payment_methods", mpPaymentMethod);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.success(js.toString());
                    }
                }

                @Override
                public void failure(ApiException apiException) {
                    callback.success(apiException.getError());

                }
            });
        } else {
            Toast.makeText(this.cordova.getActivity(), "Invalid payment method", Toast.LENGTH_LONG).show();
        }
    }

    private void getPaymentMethods(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                Gson gson = new Gson();
                String pm = gson.toJson(paymentMethods);
                callback.success(pm);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    private void getIssuers(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        String paymentMethodId = data.getString(1);
        String bin = data.getString(2);

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.getIssuers(paymentMethodId, bin, new Callback<List<Issuer>>() {
            @Override
            public void success(List<Issuer> issuers) {
                Gson gson = new Gson();
                String issuer = gson.toJson(issuers);
                callback.success(issuer);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    private void getInstallments(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        String paymentMethodId = data.getString(1);
        String bin = data.getString(2);
        Long issuerId = data.getLong(3);

        BigDecimal amount = new BigDecimal(data.getDouble(4));

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, new Callback<List<Installment>>() {
            @Override
            public void success(List<Installment> installments) {
                Gson gson = new Gson();
                String installment = gson.toJson(installments);
                callback.success(installment);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    private void getIdentificationTypes(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes) {
                Gson gson = new Gson();
                String identificationType = gson.toJson(identificationTypes);
                callback.success(identificationType);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    private void createToken(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        CardToken cardToken = new CardToken(data.getString(1), data.getInt(2), data.getInt(3), data.getString(4), data.getString(5), data.getString(6), data.getString(7));

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                Gson gson = new Gson();
                String mptoken = gson.toJson(token);
                callback.success(mptoken);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    private void getBankDeals(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(List<BankDeal> bankDeals) {
                Gson gson = new Gson();
                String bankDeal = gson.toJson(bankDeals);
                callback.success(bankDeal);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    private void getPaymentResult(JSONArray data, CallbackContext callbackContext) {
        cordova.setActivityResultCallback(this);
        callback = callbackContext;

        Long paymentId = data.getLong(1);
        String paymentTypeId = data.getString(2);

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(this.cordova.getActivity())
                .setPublicKey(data.getString(0))
                .build();

        mercadoPago.getPaymentResult(paymentId, paymentTypeId, new Callback<PaymentResult>() {
            @Override
            public void success(PaymentResult paymentResult) {

                Gson gson = new Gson();
                String mpPaymentResult = gson.toJson(paymentResult);
                callback.success(mpPaymentResult);
            }

            @Override
            public void failure(ApiException error) {
                callback.error(error.toString());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPagoUI.Activities.CUSTOMER_CARDS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Card card = JsonUtil.getInstance().fromJson(data.getStringExtra("card"), Card.class);
                if (card != null) {
                    Gson gson = new Gson();
                    callback.success(gson.toJson(card));
                } else {
                    callback.success("");
                }
            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }
        } else if (requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethod mppaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                Issuer mpissuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
                Token mptoken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
                PayerCost mppayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
                Gson gson = new Gson();
                String paymentMethod = gson.toJson(mppaymentMethod);
                String issuer = gson.toJson(mpissuer);
                String token = gson.toJson(mptoken);
                String payerCost = gson.toJson(mppayerCost);
                JSONObject js = new JSONObject();
                try {
                    js.put("payment_method", paymentMethod);
                    js.put("issuer", issuer);
                    js.put("token", token);
                    js.put("payer_cost", payerCost);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.success(js.toString());
            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }
        } else if (requestCode == MercadoPago.CARD_VAULT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethod mppaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                Issuer mpissuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
                Token mptoken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
                PayerCost mppayerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);


                Gson gson = new Gson();
                String paymentMethod = gson.toJson(mppaymentMethod);
                String issuer = gson.toJson(mpissuer);
                String token = gson.toJson(mptoken);
                String payerCost = gson.toJson(mppayerCost);
                JSONObject js = new JSONObject();
                try {
                    js.put("payment_method", paymentMethod);
                    js.put("issuer", issuer);
                    js.put("token", token);
                    js.put("payer_cost", payerCost);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.success(js.toString());

            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }
        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

                Gson gson = new Gson();
                callback.success(gson.toJson(paymentMethod));
            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }
        } else if (requestCode == MercadoPago.ISSUERS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);

                Gson gson = new Gson();
                callback.success(gson.toJson(issuer));

            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }
        } else if (requestCode == MercadoPago.INSTALLMENTS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);

                Gson gson = new Gson();
                callback.success(gson.toJson(payerCost));
            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }

        } else if (requestCode == MercadoPago.CHECKOUT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                // Listo! El pago ya fue procesado por MP.
                Payment payment = JsonUtil.getInstance().fromJson(data.getStringExtra("payment"), Payment.class);

                if (payment != null) {
                    Gson gson = new Gson();
                    callback.success(gson.toJson(payment));
                } else {
                    callback.success("El usuario no concretó el pago.");
                }

            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException mpException = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                    callback.error(mpException.getMessage());
                }
            }
        }

    }
}
