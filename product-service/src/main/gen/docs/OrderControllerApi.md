# OrderControllerApi

All URIs are relative to *http://localhost:8083*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createOrder**](OrderControllerApi.md#createOrder) | **POST** /orders |  |


<a id="createOrder"></a>
# **createOrder**
> Object createOrder(orderRequestDTO)



### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OrderControllerApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8083");

    OrderControllerApi apiInstance = new OrderControllerApi(defaultClient);
    OrderRequestDTO orderRequestDTO = new OrderRequestDTO(); // OrderRequestDTO | 
    try {
      Object result = apiInstance.createOrder(orderRequestDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderControllerApi#createOrder");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **orderRequestDTO** | [**OrderRequestDTO**](OrderRequestDTO.md)|  | |

### Return type

**Object**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

