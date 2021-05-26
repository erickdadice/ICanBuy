package com.icb.icanbuy;

import android.app.Application;

import com.icb.icanbuy.ui.pago.Constants;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class ICanBuy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CheckoutConfig checkoutConfig = new CheckoutConfig(
                this,
                Constants.PAYPAL_CLIENT_ID,
                Environment.SANDBOX,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true,
                        false
                )
        );
        PayPalCheckout.setConfig(checkoutConfig);
    }
}
