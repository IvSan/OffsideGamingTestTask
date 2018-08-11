# OffsideGaming Test Task
## Requirements

Gas & Water Usage Monitoring Application
Create an application to monitor gas, cold and hot water usage. No UI needed, only REST API.
  
Two REST API methods should be implemented: 
+ submitting the current measurements for a given user,
+ getting the history of previously submitted measurements for a given user.  

Method inputs should be validated to reject incomplete or invalid data.
 
#### Technical Requirements
1. Use Java 1.8, Spring Framework, Hibernate and Maven.
2. Use other Java libraries as needed.
3. Use HSQLDB for storing data. It is ok NOT to persist data across application launches.
4. Try following all the good principles of writing qualitative and testable code.
5. Fill in missing requirements as you feel suitable.
6. Include a short README file describing how the application works and how to build and run the project.
 
## Implementation

#### Authentication  

Credential is existing: `user`. Password configure in `application.properties` file of chosen config profile.

#### Rest web service

+ **POST** on `/client` to add new client.   
No parameters required.  
Example: `POST /client`  
New client as the response.

+ **POST** on `/consumption` to add new consumption record.  
Parameters `id` (client id), `type` (supported types: gas, cold_water, hot_water), `value` are required.
Example: `POST /consumption?id=1&type=cold_water&value=55.20`  
Status 200 OK as the response.

+ **GET** on `/consumption` to receive consumption records.  
Parameters `id` (client id) is required.  
Example: `GET /consumption?id=1`  
Last consumption records for all three type of resources as the response.  
If you provide optional parameter `type` (supported types: gas, cold_water, hot_water):  
Example: `GET /consumption?id=1&type=cold_water`  
History of all records by this client and type as the response.
