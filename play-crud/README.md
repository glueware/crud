# car-advert introduction
This repository contains a RESTful web-service written in Scala. The service allows users to place new car adverts and view, modify and delete existing car adverts.
As techniques it uses Play framework version 2.4 with Slick as database access library. H2 is the underlying database.
It can be run and tested with `sbt` as build tool.

# Implementation

There are three packages:

* `controllers`
* `dao`
* `models`

The idea is to map the functionality of a semi-structured outside world (the web service interface) to a well typed logic. That means the `controllers` itself do not contain any logic, even error handling and validation is done within the `dao` package. The types of the logic world are defined in the models. 

## Models
In that simple case we have only one model, the model `car` for a car advertisement.
Car adverts contains the following fields:
* **id** (_required_): **int** ;
* **title** (_required_): **string**, e.g. _"Audi A4 Avant"_;
* **fuel** (_required_): gasoline or diesel, use some type which could be extended in the future by adding additional fuel types;
* **price** (_required_): **integer**;
* **new** (_required_): **boolean**, indicates if car is new or used;
* **mileage** (_only for used cars_): **integer**;
* **first registration** (_only for used cars_): **date** without time.  

## Controllers
By `request.body.validate[Car]` the "untyped" body of the Http request is mapped to to an instance of type `car`. It uses Play's `Json` mapping techniques. 
The `Int`s for the id do not have to be mapped. This works because of simplicity reasons a no special `Id` class is used. 
With that typed parameters methods of `dao` package are called. The return value is a `Future` where the error handling is already done. The `Future` contains in case of an error already the result of error response. To package the the error result into the `Future` a `ServerException` class is defined. 
`getResult`maps the result back to an "untyped" response. That is also the place where `Logging` should be defined.

## Data access object
The `dao` package provides an well typed interface using the case classes defined in `models`. Further validation beyond the type is also done in `dao` and the returned `Future` is the subtype `Failure` and contains a Play error result.
If we would have deeper structured models than the simple `car` class additional classes for each table in `dao` would have to be defined. 

