# Charlie- BE Code Assessment

Senior Backend Software Engineer

## Introduction
The parking-lot-service handle the task of managing a parking facility for 
vehicles of 3 sizes: motorcycle, cars and vans.

## Assumptions
1. The parking lot has 25 total spaces
2. The parking lot can hold motorcycles, cars and vans
3. The parking lot has motorcycle spots, compact car spots and regular spots
4. A car can park in a single compact spot, or a regular spot
5. A van can park, but it will take up 3 regular spots (for simplicity we don't need to make
   sure these spots are beside each other)
   
## Acceptance Criteria
1. Service endpoints
   a. Park vehicle (POST /park)
   b. Vehicle leaves parking lot (GET /unpark/{receiptNumber})
   c. Find how many spots are remaining (/spaces)
   d. Check if all parking spots are taken for a given vehicle type (/spaces/{vehicleType}) {motorcycle, car, van})

## Submission
Please submit your completed assessment by emailing a zip file containing your project files to
the recruiter or by sharing the GitHub repository link if you choose to use GitHub.


Sample OAuth Token
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFudGhvbnkgTmd1eWVuIiwiYWRtaW4iOmZhbHNlLCJpYXQiOjE1MTYyMzkwMjJ9.GYDOCQDABEZWqoa0ShIW3MiGnux241E1DrJbDHg1117FTfe-9ZI1UOWA3A3ysIt_S9dKZjPQXhb0l__g_qdBDgelNmTsKvNe42txs3chNLOrCbhzUvktOngc2TnpY0kxr8opet0Wbp4TYrF_f-PpCq_HZUIJI1yLiC01CiPVOaBWOwJ1CIxIFbD_0cWlS1gyT8sfZfYe0cCwqfXJTsYrBUWfsYTWdEVOyxTSZeZjGxIPGBgxbgPoUM9yKJ1FbjECy9YOgjybyklSuqKLD0BBbIXYzgNtQQmIRieFjxvAwwl5AAWJnvjJ7nnroMHbHG9VO5Kc7mIs_BItPRPFbw1jWQ

## Instruction to run the app

1. In the home folder /parking-lot-service, execute ./gradlew bootRun
2. Use the sample httpie command bellow to interact with the service.  (Use the link https://httpie.io/cli to install httpie if you don't have it on your system.)

## Sample httpie request
https://httpie.io/cli

This API request return the number of parking spaces remaining
http :8080/parkinglot/spaces

This API request return the number of parking spaces remaining for a VAN
http :8080/parkinglot/spaces/VAN

This API request return the receipt for a parked vehicle
http POST :8080/parkinglot/park licensePlate=4JCA123 type=CAR -A bearer -a eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFudGhvbnkgTmd1eWVuIiwiYWRtaW4iOmZhbHNlLCJpYXQiOjE1MTYyMzkwMjJ9.GYDOCQDABEZWqoa0ShIW3MiGnux241E1DrJbDHg1117FTfe-9ZI1UOWA3A3ysIt_S9dKZjPQXhb0l__g_qdBDgelNmTsKvNe42txs3chNLOrCbhzUvktOngc2TnpY0kxr8opet0Wbp4TYrF_f-PpCq_HZUIJI1yLiC01CiPVOaBWOwJ1CIxIFbD_0cWlS1gyT8sfZfYe0cCwqfXJTsYrBUWfsYTWdEVOyxTSZeZjGxIPGBgxbgPoUM9yKJ1FbjECy9YOgjybyklSuqKLD0BBbIXYzgNtQQmIRieFjxvAwwl5AAWJnvjJ7nnroMHbHG9VO5Kc7mIs_BItPRPFbw1jWQ

This API request return the receipt for a vehicle that leaves the parking lot
http :8080/parkinglot/unpark/1