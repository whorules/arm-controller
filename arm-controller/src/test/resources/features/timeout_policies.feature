Feature: Timeout policies for change window and maximum value
  As a user
  I want timeout changes to respect change window and maximum limits
  So that routes are not over-throttled or changed too frequently

  Background:
    Given the acceptable error rate is 2 percent
    And the maximum timeout is 1500 ms
    And the minimum change window is 2 minutes
    And the timeout step size is 100 ms

  Scenario: Do not change timeout when last change was too recent
    Given there is a route "route-1" with initial timeout 1000 ms
    And the last timeout change for "route-1" was 1 minutes ago
    And the current error rate for "route-1" is 10.0 percent
    When the scheduler checks Prometheus metrics
    Then no timeout change should be performed for "route-1"
    And the stored timeout for "route-1" should remain 1000 ms

  Scenario: Cap timeout at maximum value
    Given there is a route "route-1" with initial timeout 1490 ms
    And the current error rate for "route-1" is 10.0 percent
    When the scheduler checks Prometheus metrics
    Then the timeout for "route-1" should become 1500 ms
    And the stored timeout for "route-1" should not exceed 1500 ms

  Scenario: Do not increase timeout when already at maximum
    Given there is a route "route-1" with initial timeout 1500 ms
    And the current error rate for "route-1" is 10.0 percent
    When the scheduler checks Prometheus metrics
    Then no timeout change should be performed for "route-1"
    And the stored timeout for "route-1" should remain 1500 ms
