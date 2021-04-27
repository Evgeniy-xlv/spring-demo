This project is a sample application that implements the ability to block method calls marked with the `@RateLimited` 
annotation from a specific IP address. 

The application is configured using the `src/main/resources/application.properties` file. The configuration 
allows you to adjust the allowable request rate(`com.example.demo.request.rate.limit`) and the period in minutes 
in which this rate will be considered (`com.example.demo.request.rate.limit.period.minutes`).