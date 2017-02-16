# spring-boot-ssl-mutual-authentication
Spring Boot Secure Server and Client that requires mutual authentication

The embedded certificates were generated this way:

 - **Server Keystore:**
`keytool -genkeypair -alias secure-server -keyalg RSA -dname "CN=codependent,OU=myorg,O=myorg,L=mycity,S=mystate,C=es" -keypass secret -keystore keystore.jks -storepass secret`

 - **Client Keystore:** 
`keytool -genkeypair -alias secure-client -keyalg RSA -dname "CN=codependent-client,OU=myorg,O=myorg,L=mycity,S=mystate,C=es" -keypass secret -keystore client-keystore.jks -storepass secret`

 - **Server Truststore (contains the supported clients public certificates):**
  - First we **export the client public certificate**: `keytool -exportcert -alias secure-client -file client-public.cer -keystore client-keystore.jks -storepass secret`
  - Then we import it in the server truststore: `keytool -importcert -keystore server-truststore.jks -alias clientcert -file client-public.cer -storepass secret`
  
 - **Client Keystore (contains the server's public certificate):**
  - First we **export the server public certificate**: `keytool -exportcert -alias secure-server -file server-public.cer -keystore server-keystore.jks -storepass secret`
  - Then we import it in the client truststore: `keytool -importcert -keystore client-truststore.jks -alias servercert -file server-public.cer -storepass secret` 

To check the proyect start both Spring Boot applications and access secure-client -> `http://localhost:8080`. It will call `https://localhost:8443` and if everything goes OK you'll see *"Hello!"*
