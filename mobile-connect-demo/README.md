GSMA MobileConnect Java SDK Demo
==============================================================================================================

- This demo provides a complete code example for completing the authorization flow of MobileConnect.
- Demo code is only for example purposes

## Minimum Requirements

MobileConnect supports Java 6 and above, and is dependent upon Jackson Databind 2.6.3 and Apache HTTP Client 4.5.2.  It uses the SLF-4J logging framework and is dependent upon SLF-4J 1.7.21.

## Getting Started

You must have first registered an account on the [MobileConnect Developer Site](https://developer.mobileconnect.io) and created an application to get your sandbox credentials.
You should set your application redirectUrl to http://localhost:8001/mobileconnect.html

## Running the Demo Application
Ensure you have build the [mobile-connect-sdk](../mobile-connect-sdk) jar using [Maven](https://maven.apache.org/) repository.

```posh
cd r2-java-sdk
mvn clean install
```

Configure the MobileConnectConfig instance with your credentials from the [MobileConnect Developer Site](https://developer.mobileconnect.io) portal.  Copy the file `mobile-connect-demo/src/main/resources/config/mobile-connect-config.properties.example` to `mobile-connect-demo/src/main/resources/config/mobile-connect-config.properties` and fill in your details.

```posh
...
redirectUrl=http://localhost:8001/mobileconnect.html

clientId=<provide client id here>
clientSecret=<provide client secret here>
discoveryUrl=<provide discovery URL here>
...
```

Run the MobileConnect demo.

```posh
cd r2-java-sdk
mvn spring-boot:run -pl mobile-connect-demo
```

Then navigate to http://localhost:8001 in your browser.

`DemoAppConfiguration` shows how to configure and start the MobileConnect interface.  `DemoAppController` shows the interaction with the MobileConnect interface.

## Support

If you encounter any issues which are not resolved by consulting the resources below then [send us a message](https://developer.mobileconnect.io/content/contact-us)

## Resources

- [MobileConnect Discovery API Information](https://developer.mobileconnect.io/content/discovery-api-0)
- [MobileConnect Authentication API Information](https://developer.mobileconnect.io/content/mobile-connect-api)
