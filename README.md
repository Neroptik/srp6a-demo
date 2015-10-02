# Thinbus Secure Remote Password SRP6a Authentication with Spring Security

Copyright (c) Simon Massey, 2015
 
This demo application uses [Thinbus SRP](https://bitbucket.org/simon_massey/thinbus-srp-js) to register and login users into a Spring MVC web application using Spring Security. Integrating the [SRP protocol](http://srp.stanford.edu/design.html) into Spring Security requires an extra initial AJAX request. The user first enters their credentials and clicks on the submit button. The browser then uses AJAX to pass the username to fetch the user salt `s` together with a one-time server challenge `B`. The browser then generates a one-time ephemeral key `A` and uses that and the password to compute the password proof `M1`. These values `A`+`M1` are concatenated together and substituted for the actual password to post the form login form to the server.  Spring security then only needs to be configured with a custom AuthenticationProvider to check the `A`+`M1` credential. The user experience is that they enter their username and password and hit submit and the login just like a normal application. 

## Building And Running

```sh
git clone git@bitbucket.org:simon_massey/thinbus-srp-spring-demo.git
cd thinbus-srp-spring-demo
mvn clean package jetty:run
```

The last command starts a jetty webserver running the demo at http://localhost:8080/

## Notes

Please read the recommendations on the [Thinbus SRP](https://bitbucket.org/simon_massey/thinbus-srp-js) before using this demo code. An additional implementation detail specific to Spring Security is that the server needs to hold state between the AJAX request and the browser post of the SRP credentials. Spring Security does not expose the user `HTTPSession` to the `AuthenticationProvider`. So demo code uses a Guava cache with a timeout to hold the `SRP6JavascriptServerSession` for the brief period of the login exchange. As of Thinbus 1.2.1 the session is serialisable so an alternative approach for a large stateless website would be to hold the `SRP6JavascriptServerSession` object in the DB rather than in an in-memory cache.  

The codebase is an adaption of the sample application generated using [spring-mvc-quickstart version 1.0.0](https://github.com/kolorobot/spring-mvc-quickstart-archetype). It creates accounts, uses standard password authentication and has a helpful 'remember me' feature. A brief overview of the changes made to the original codebase to replace plain text password authentication with SRP authentication in Spring Security are as follows:
 
1. Reconfigured the spring security logic to use the `SrpAuthenticationProvider`
1. Deleted the original `AccountController` which is replaced with `SrpAccountController`
1. Added Thinbus JavaScript to the sign-up page to register the user with a salt and verifier
1. Added Thinbus JavaScript to the sign-in page to perform the AJAX lookup of the salt and server challenge `B`. The value `A` and the computed password proof `M1` are then substituted for the actual password in the login form. 
1. Added JPA code to encrypt the SRP verifier in the database so that user accounts cannot be subjected to an offline dictionary attack using a leaked database backup. 

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