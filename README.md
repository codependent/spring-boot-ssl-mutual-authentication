# spring-boot-ssl-mutual-authentication
Spring Boot Secure Server and Clients that requires mutual authentication

There are three projects in this repo:

 - secure-server: available at `https://localhost:8443`
 - secure-client: available at `http://localhost:8080`
 - secure-client2: available at `http://localhost:8282`

## Testing with self signed certificates:

 1. **Generating the Server Keystore:**
`keytool -genkeypair -alias secure-server -keyalg RSA -dname "CN=localhost,OU=myorg,O=myorg,L=mycity,S=mystate,C=es" -keypass secret -keystore server-keystore.jks -storepass secret`

 2. **Generating the Client Keystore:** 
`keytool -genkeypair -alias secure-client -keyalg RSA -dname "CN=codependent-client,OU=myorg,O=myorg,L=mycity,S=mystate,C=es" -keypass secret -keystore client-keystore.jks -storepass secret`

 3. **Import the supported client's public certificates intro the server truststore:**
  - **Export the client public certificate**: `keytool -exportcert -alias secure-client -file client-public.cer -keystore client-keystore.jks -storepass secret`
  - **Import it in the server truststore**: `keytool -importcert -keystore server-truststore.jks -alias clientcert -file client-public.cer -storepass secret`

 4. **Import the server's public certificate into the client truststores:**
   - **Export the server public certificate**: `keytool -exportcert -alias secure-server -file server-public.cer -keystore server-keystore.jks -storepass secret`
   - **Import it in the client truststore**: `keytool -importcert -keystore client-truststore.jks -alias servercert -file server-public.cer -storepass secret` 
  
To check the proyect start both Spring Boot applications and access secure-client -> `http://localhost:8080`. It will call `https://localhost:8443` and if everything goes OK you'll see *"Hello codependent-client1!"*

if you want to access `https://localhost:8443` from the browser you'll have to install the certificate in it. To export it: `keytool -importkeystore -srckeystore client-keystore.jks -destkeystore codependent-client.p12 -deststoretype PKCS12`


## Testing with signed certificates using a CA Certificate:

 1. The **server keystore** is the same generated in *Testing with self signed certificates step 1*

 2. The **client truststores** are the same from *Testing with self signed certificates step 4*

 3. **Create a CA**

 `openssl req -new  -x509  -keyout  codependent-ca-key.pem -out  codependent-ca.pem -days 365`
 ```
 Generating a 2048 bit RSA private key
 ..........................+++
 .......................................................................................+++
 writing new private key to 'codependent-ca-key.pem'
 Enter PEM pass phrase:
 Verifying - Enter PEM pass phrase:
 -----
 You are about to be asked to enter information that will be incorporated
 into your certificate request.
 What you are about to enter is what is called a Distinguished Name or a DN.
 There are quite a few fields but you can leave some blank
 For some fields there will be a default value,
 If you enter '.', the field will be left blank.
 -----
 Country Name (2 letter code) [AU]:es
 State or Province Name (full name) [Some-State]:mad
 Locality Name (eg, city) []:mad
 Organization Name (eg, company) [Internet Widgits Pty Ltd]:Codependent Ltd
 Organizational Unit Name (eg, section) []:dev
 Common Name (e.g. server FQDN or YOUR name) []:codependent-ca
 Email Address []:
 ```
 This generates two files codependent-ca.pem and codependent-ca-key.pem

 Now, **FOR EVERY CLIENT**:

 4. **Create the server truststore including the client CA:**

 `keytool -import -alias codependent-ca -file codependent-ca.pem -keystore server-truststore.jks -storepass secret`

 5. **Generate an unsigned client certificate for secure-client:**

 `keytool -genkeypair -alias secure-client -keyalg RSA -dname "CN=codependent-client1,OU=myorg,O=myorg,L=mycity,S=mystate,C=es" -keypass   secret -keystore client-keystore.jks -storepass secret`

 **NOTE: use CN=codependent-client1 for secure-client and cn=codependent-client2 for secure-client2**

 6. **Generate the Certificate Signing Request:**

 `keytool -keystore client-keystore.jks -certreq -alias secure-client -keyalg rsa -storepass secret`

 Paste the content in a unsigned-client.csr file

 7. **Generate a signed certificate for the associated Certificate Signing Request:**

 `openssl x509 -req -CA codependent-ca.pem -CAkey codependent-ca-key.pem -in unsigned-client.csr -out signed-client.cer -days 365 -CAcreateserial`

 You get a signed-client.cer file.

 8. **Import the CA and the client signed certificate into the client keystore:**

 `keytool -import -keystore client-keystore.jks -file codependent-ca.pem -alias codependent-ca`
 `keytool -import -keystore client-keystore.jks -file signed-client.cer -alias secure-client`

References: 

 - https://docs.oracle.com/cd/E19509-01/820-3503/ggeyj/index.html
 - https://docs.oracle.com/cd/E19509-01/820-3503/ggezy/index.html
