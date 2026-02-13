Feature: Dynamic timeout adjustment based on error rate
  As a user
  I want the system to adjust timeouts based on current error rate
  So that clients receive fewer failed requests without overloading backend services

  Background:
    Given there is a route "route-1" with initial timeout 1000 ms
    And the acceptable error rate is 2 percent
    And the maximum timeout is 1500 ms
    And the minimum change window is 2 minutes
    And the timeout step size is 100 ms

  Rule: Timeout is increased only when error rate is above the acceptable threshold

  Scenario: Increase timeout when error rate is too high
    Given the current error rate for "route-1" is 5.0 percent
    When the scheduler checks Prometheus metrics
    Then the timeout for "route-1" should become 1100 ms
    And the last timeout change timestamp for "route-1" should be updated
    But the stored timeout for "route-1" should not exceed 1500 ms

  Scenario: Do not change timeout when error rate is acceptable
    Given the current error rate for "route-1" is 1.5 percent
    When I trigger the dynamic configuration check
    Then no timeout change should be performed for "route-1"
    And the stored timeout for "route-1" should remain 1000 ms
