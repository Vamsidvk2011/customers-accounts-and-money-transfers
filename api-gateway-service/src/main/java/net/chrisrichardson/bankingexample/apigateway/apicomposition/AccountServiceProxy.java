package net.chrisrichardson.bankingexample.apigateway.apicomposition;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import net.chrisrichardson.bankingexample.accountservice.common.GetAccountResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class AccountServiceProxy {

  private RestTemplate restTemplate;

  public AccountServiceProxy(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @HystrixCommand(commandProperties = @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="1000"))
  public Optional<GetAccountResponse> getAccount(long accountId) {
    ResponseEntity<GetAccountResponse> response = restTemplate.getForEntity("http://ACCOUNT-SERVICE/api/accounts/" + accountId, GetAccountResponse.class);
    switch (response.getStatusCode()) {
      case OK:
        return Optional.of(response.getBody());
      case NOT_FOUND:
        return Optional.empty();
      default:
        throw new RuntimeException("Got bad status code: " + response.getStatusCode());
    }
  }

}
