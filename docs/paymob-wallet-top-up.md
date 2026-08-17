# Paymob Wallet Top-Up

This app contains temporary direct-Android Paymob Intention plumbing for development and testing.

Required local-only configuration in `local.properties`:

```properties
PAYMOB_SECRET_KEY=<test secret key>
PAYMOB_PUBLIC_KEY=<test public key>
PAYMOB_CARD_INTEGRATION_ID=<test card integration id from the same Paymob merchant account>
PAYMOB_BASE_URL=https://accept.paymob.com
```

Do not commit real credentials. `local.properties` is ignored by Git.

`PAYMOB_CARD_INTEGRATION_ID` must be the Native Checkout card/Visa integration ID for the same test merchant account as `PAYMOB_SECRET_KEY` and `PAYMOB_PUBLIC_KEY`. If it is missing, the app falls back to `PAYMOB_INTEGRATION_ID` for backward compatibility, but wallet, iframe, or live-mode integration IDs will be rejected by Paymob and no CareNest credit will be added.

## Native Checkout SDK

The Paymob Native Checkout launcher is compiled against the official Android SDK AAR through a local Maven-style repository rooted at `presentation/libs`.

Required official SDK artifact:

```text
presentation/libs/com/paymob/sdk/Paymob-SDK/1.9.2/Paymob-SDK-1.9.2.aar
```

Use the official Paymob Native Android SDK AAR version `1.9.2` from Paymob's official distribution channel. A convenience copy may also exist at `presentation/libs/Paymob-SDK-1.9.2.aar`, but Gradle resolves the Maven-layout copy above so the declared SDK dependencies are available. Do not use reflection or guessed SDK APIs.

## Security Limitation

The Paymob Secret Key is temporarily shipped through the Android build and can be extracted from the APK. This implementation must not be distributed as a production-secure payment integration. Before production, Intention creation must be moved to a trusted server and the exposed key must be rotated.

## Runtime Behavior

The app does not create a Paymob Intention unless the Native Checkout adapter reports available. If Paymob succeeds but the CareNest wallet credit PATCH fails, the payment attempt is persisted as `CreditAddPending` and retry performs only the CareNest wallet `ADD`; it does not create a new Paymob payment.
