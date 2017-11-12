# Thinbus Secure Remote Password SRP6a Authentication with Spring Security

Copyright (c) Simon Massey, 2015 - 2017
 
This demo application uses [Thinbus SRP](https://bitbucket.org/simon_massey/thinbus-srp-js) to register and login users into a Spring MVC web application using Spring Security. 

The following sequence diagram shows how the user is registered using an email, salt and password verifier generated at the browser: 

![Thinbus SRP Register Diagram](http://simonmassey.bitbucket.io/thinbus/register.png "Thinbus SRP Register Diagram")

The following sequence diagram shows how a registered user is logged in: 

![Thinbus SRP Login Diagram](http://simonmassey.bitbucket.io/thinbus/login.png "Thinbus SRP Login Diagram")

In the diagram above the user is shown a standard login form. They enter their email and password and click the login button. 
JavaScript then makes an AJAX call using their email to load their `salt` and a one-time server challenge `B`. JavaScript creates 
a one-time client challenge `A` and uses all the information to compute a password proof `M1`. It then posts to the server 
the email, `A`+`M1` as the user's credentials. The user experience is that they enter their username and password and hit submit and are logged in; just like a normal login form. 

Note that the server has to remember the one-time ephemeral private key `b` that corresponds to the server challenge `B` that it gave to the browser in order to check the user's password proof `A`+`M1`. This requires that temporary authentication state within is held on the server between step1 and step2. This can be held in either the database, the server session or a server cache for the short duration of the login protocol. Spring Security does not expose the user `HTTPSession` to the `AuthenticationProvider`. So the demo code uses a Guava cache with a timeout to hold the state for the brief period of the login exchange. As of Thinbus 1.2.1 the state is serialisable so an alternative approach for a large stateless website would be to hold the temporary authentication state in the DB rather than in an in-memory cache.  

## Demo

The very latest code has been seen to work on IE8+, Edge, Chrome, FF, Safari: 

## Building And Running

```sh
git clone git@bitbucket.org:simon_massey/thinbus-srp-spring-demo.git
cd thinbus-srp-spring-demo
mvn clean package jetty:run
```

The last command starts a jetty webserver running the demo at http://localhost:8080/

## Notes

Please read the recommendations on the [Thinbus SRP](https://bitbucket.org/simon_massey/thinbus-srp-js) site before adopting this demo code. The codebase is an adaption of the sample application generated using [spring-mvc-quickstart version 1.0.0](https://github.com/kolorobot/spring-mvc-quickstart-archetype). The original mvc-quickstart app creates accounts, uses standard password authentication and has a helpful 'remember me' feature. A brief overview of the changes made to the original mvc-quickstart codebase to replace plain text password authentication with Thinbus SRP authentication is:
 
1. Replaced the original `AccountController` with `SrpController`
1. Whitelisted the `/challenge` path used by the ajax lookup within `SrpSecurityConfig`
1. Added Thinbus JavaScript to the sign-up page to register the user with a salt and verifier
1. Added Thinbus JavaScript to the sign-in page to perform the AJAX lookup of the salt and server challenge `B`. The password proof `A`+`M1` is then posted by the login form. 
1. Reconfigured the spring security logic to use `SrpAuthenticationProvider` which checks the password proof. 
1. Added JPA code to encrypt the SRP verifier in the database so that they cannot be subjected to an offline dictionary attack using a leaked database backup. 

A salient feature of the integration is that there are no changes to the standard spring security authentication data flow. At the browser the plain text password is simply substituted with the password proof. The `SrpAuthenticationProvider` simply has to use all the data to check the password proof in the `authenticate` method. This ensures that the data flow and logic has the full protection of Spring Security in a future proof manner.

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
