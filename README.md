# Thinbus Secure Remote Password SRP6a Authentication with Spring Security

Copyright (c) Simon Massey, 2015
 
This demo application uses Thinbus SRP to register and login users into a Spring MVC web application using Spring Security. Integrating the [SRP protocol](http://srp.stanford.edu/design.html) into Spring Security requires an extra initial request. The user enters their credentials and clicks on the submit button. The browser first uses the username to fetch their salt 's' together with a one-time server challenge 'B' via AJAX. It then generates a one-time ephemeral key 'A' and computes the password proof 'M1'. These values are concatenated together and substituted for the actual password.  Spring security then only needs to be configured with a custom AuthenticationProvider to check the 'M1' credential.  

## Building And Running

```sh
git clone git@bitbucket.org:simon_massey/thinbus-srp-spring-demo.git
cd thinbus-srp-spring-demo
mvn clean package jetty:run
```

## Notes

An additional implementation detail is that the server needs to hold state between the two browser requests. Spring Security does not expose the user HTTPSession to the AuthenticationProvider. This aligns to the principle of least privilege when applied to the security code of the stateless HTTP protocol. The demo code uses a Guava cache with a timeout to hold server session state for long enough to allow users to login. 

The codebase is an adaption of the sample application generated using [spring-mvc-quickstart version 1.0.0](https://github.com/kolorobot/spring-mvc-quickstart-archetype). The original generated codebase creates accounts, used standard password authentication and has a helpful 'remember me' feature. A brief overview of the changes made to the original codebase to replace plain text password authentication with SRP authentication are as follows:
 
1. Reconfigured the spring security logic to use the `SrpAuthenticationProvider`
1. Deleted the original `AccountController` which is replaced with `SrpAccountController`
1. Added Thinbus JavaScript to the sign-up page to register the user with a salt and verifier
1. Added Thinbus JavaScript to the sign-in page to perform the AJAX lookup of the salt and server challenge B. The value A and the computed password proof M1 are then substituted for the actual password. 
1. Added JPA code to encrypt the SRP verifier in the database that user accounts cannot be subjected to an offline dictionary attack using a leaked off-site database backup. 

A salient feature of the integration is that there are no changes to the standard spring security authentication path other than to configure the custom `SrpAuthenticationProvider`. At the browser the plain text password is simply substituted with the client password proof. This ensures that the login code path has the full protection of Spring Security in a future proof manner.

## License

```
   Copyright 2015 Simon Massey

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
   
End. 